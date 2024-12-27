package org.util;

import org.pmw.tinylog.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FileTools {

    // 读取文件内容为字符串
    public static String readContent(String fileName) {
        URL resourceUrl = FileTools.class.getClassLoader().getResource(fileName);
        if (resourceUrl == null) {
            Logger.error("资源不存在：" + fileName);
            return null;
        }
        try {
            // 判断资源是否在 JAR 文件中
            if ("jar".equals(resourceUrl.getProtocol())) {
                // 获取 JAR 文件路径
                String jarFilePath = resourceUrl.getFile().substring(5, resourceUrl.getFile().indexOf("!"));
                try (JarFile jarFile = new JarFile(jarFilePath)) {
                    // 获取文件的 JarEntry
                    JarEntry jarEntry = jarFile.getJarEntry(fileName);
                    if (jarEntry != null) {
                        // 读取文件内容
                        try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                            return new String(readBytes(inputStream), StandardCharsets.UTF_8);
                        }
                    } else {
                        Logger.error("在 JAR 文件中找不到资源：" + fileName);
                    }
                }
            } else {
                // 直接从文件系统读取
                return new String(readBytes(new FileInputStream(new File(resourceUrl.toURI()))));
            }
        } catch (Exception e) {
            Logger.error("读取资源文件出错：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // 读取文件内容为字节数组
    public static byte[] readBytes(String fileName){
        URL resourceUrl = FileTools.class.getClassLoader().getResource(fileName);
        if (resourceUrl == null) {
            Logger.error("资源不存在：" + fileName);
            return null;
        }
        try {
            // 判断资源是否在 JAR 文件中
            if ("jar".equals(resourceUrl.getProtocol())) {
                // 获取 JAR 文件路径
                String jarFilePath = resourceUrl.getFile().substring(5, resourceUrl.getFile().indexOf("!"));
                try (JarFile jarFile = new JarFile(jarFilePath)) {
                    // 获取文件的 JarEntry
                    JarEntry jarEntry = jarFile.getJarEntry(fileName);
                    if (jarEntry != null) {
                        // 读取文件内容
                        try (InputStream inputStream = jarFile.getInputStream(jarEntry)) {
                            return readBytes(inputStream);
                        }
                    } else {
                        Logger.error("在 JAR 文件中找不到资源：" + fileName);
                    }
                }
            } else {
                // 直接从文件系统读取
                return readBytes(new FileInputStream(new File(resourceUrl.toURI())));
            }
        } catch (Exception e) {
            Logger.error("读取资源文件出错：" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // 将 resource 下指令目录中的文件，读取内容然后返回成 map，key 是文件名称、value是内容文本内容
    public static Map<String, String> resourceDirectoryRead(String resourceFolderPath, String filter) {
        Map<String, String> fileContents = new HashMap<>();
        URL resourceUrl = FileTools.class.getClassLoader().getResource(resourceFolderPath);
        if (resourceUrl == null) {
            Logger.error("资源目录不存在：" + resourceFolderPath);
            return fileContents;
        }
        Logger.info("资源协议：" + resourceUrl.getProtocol());
        try {
            // 判断资源是否是 jar 文件中的内容
            if ("jar".equals(resourceUrl.getProtocol())) {
                String jarFilePath = resourceUrl.getFile().substring(5, resourceUrl.getFile().indexOf("!"));
                try (JarFile jarFile = new JarFile(jarFilePath)) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        // 只处理资源路径匹配的条目，并且文件名包含 filter 字符串
                        if (entry.getName().startsWith(resourceFolderPath) && entry.getName().contains(filter)) {
                            // 获取文件输入流
                            try (InputStream inputStream = jarFile.getInputStream(entry)) {
                                byte[] bytes = readBytes(inputStream);
                                String content = new String(bytes, StandardCharsets.UTF_8); // 转换为字符串
                                // 文件名作为 key，内容作为 value
                                String fileName = entry.getName().substring(entry.getName().lastIndexOf("/") + 1);
                                fileContents.put(fileName, content);
//                                Logger.info("读取文件：" + fileName);
                            }
                        }
                    }
                }
            } else {
                // 直接从文件系统读取
                File sourceFolder = new File(resourceUrl.toURI());
                if (sourceFolder.exists() && sourceFolder.isDirectory()) {
                    Files.walkFileTree(sourceFolder.toPath(), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            String fileName = file.getFileName().toString();
                            // 只处理文件名包含 filter 字符串的文件
                            if (fileName.contains(filter)) {
                                byte[] bytes = Files.readAllBytes(file); // 使用 Files.readAllBytes()
                                String content = new String(bytes, StandardCharsets.UTF_8); // 转换为字符串
                                fileContents.put(fileName, content);
//                                Logger.info("读取文件：" + fileName);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            Logger.info("无法访问文件：" + file);
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
            }
        } catch (IOException | URISyntaxException e) {
            Logger.error("读取资源文件出错：" + e.getMessage());
        }
        return fileContents;
    }

    // 将 resource 下指定目录，复制到另外一个磁盘目录中
    public static void resourceDirectoryCopy(String resourcePath, String directory) {
        Logger.info("文件迁移：" + resourcePath + " => " + directory);

        // 目标目录
        File destinationFolder = new File(directory);
        if (!destinationFolder.exists()) {
            boolean created = destinationFolder.mkdirs();
            if (!created) {
                Logger.error("目标目录创建失败：" + directory);
                return;
            }
        }

        try {
            // 获取资源路径对应的 URL
            String resourceFolderPath = resourcePath.startsWith("/") ? resourcePath :  resourcePath;
            URL resourceUrl = FileTools.class.getClassLoader().getResource(resourceFolderPath);

            if (resourceUrl == null) {
                Logger.info("资源不存在：" + resourcePath);
                return;
            }
            Logger.info("文件协议："+resourceUrl.getProtocol());

            // 如果资源是 JAR 文件内的内容
            if ("jar".equals(resourceUrl.getProtocol())) {
                // 从 JAR 文件中提取资源
                String jarFilePath = resourceUrl.getFile().substring(5, resourceUrl.getFile().indexOf("!"));
                try (JarFile jarFile = new JarFile(jarFilePath)) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();

                        // 只处理资源路径匹配的条目
                        if (entry.getName().startsWith(resourcePath)) {
                            // 获取当前条目的输入流
                            try (InputStream inputStream = jarFile.getInputStream(entry)) {
                                // 目标文件路径
                                File targetFile = new File(destinationFolder, entry.getName().substring(resourcePath.length()));

                                // 确保目标目录存在
                                if (entry.isDirectory()) {
                                    targetFile.mkdirs();
                                } else {
                                    // 确保父目录存在
                                    targetFile.getParentFile().mkdirs();
                                    // 复制文件
                                    try (OutputStream outputStream = new FileOutputStream(targetFile)) {
                                        byte[] buffer = new byte[1024];
                                        int length;
                                        while ((length = inputStream.read(buffer)) != -1) {
                                            outputStream.write(buffer, 0, length);
                                        }
                                    }
//                                    Logger.info("复制文件：" + entry.getName() + " => " + targetFile.getAbsolutePath());
                                }
                            } catch (IOException e) {
                                Logger.error("复制文件出错：" + e.getMessage());
                            }
                        }
                    }
                }
            } else {
                // 如果资源不在 JAR 中，直接读取文件系统中的资源
                File sourceFolder = new File(resourceUrl.toURI());
                if (sourceFolder.exists() && sourceFolder.isDirectory()) {
                    Files.walkFileTree(sourceFolder.toPath(), new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            // 获取源文件相对路径
                            Path relativePath = sourceFolder.toPath().relativize(file);
                            // 创建目标路径
                            Path destinationPath = Paths.get(destinationFolder.getAbsolutePath(), relativePath.toString());
                            // 确保目标目录存在
                            if (Files.notExists(destinationPath.getParent())) {
                                Files.createDirectories(destinationPath.getParent());
                            }
                            // 复制文件
                            Files.copy(file, destinationPath, StandardCopyOption.REPLACE_EXISTING);
//                            Logger.info("复制文件：" + file + " => " + destinationPath);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                            Logger.error("无法访问文件：" + file);
                            return FileVisitResult.CONTINUE;
                        }

                        @Override
                        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                            return FileVisitResult.CONTINUE;
                        }
                    });
                }
            }
        } catch (Exception e) {
            Logger.error("资源目录读取过程出错：" + e.getMessage());
        }
    }

    // 递归删除目录及其内容
    public static boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            // 获取目录下的所有文件和子目录
            String[] files = directory.list();
            if (files != null) {
                for (String file : files) {
                    File fileToDelete = new File(directory, file);
                    // 递归删除子目录及文件
                    deleteDirectory(fileToDelete);
                }
            }
        }
        // 删除空目录或文件
        return directory.delete();
    }


    // 读取文件
    private static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, length);
        }
        return byteArrayOutputStream.toByteArray();
    }

}
