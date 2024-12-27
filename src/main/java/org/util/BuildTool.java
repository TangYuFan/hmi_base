package org.util;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *   @desc : 输入项目路径，自动 pom 中解析入口类、含依赖 jar 名称，然后创建打包配置进行打包
 *   @auth : tyf
 *   @date : 2024-12-19 15:52:12
*/
public class BuildTool {


    // pom 解析 maven-assembly-plugin 插件中的 mainClass 和 descriptorRef 元素的值
    public static String[] parseMavenAssemblyPlugin(String xmlPath) throws Exception {
        // 定义返回值，index0: mainClass，index1: descriptorRef
        String[] values = new String[2];
        // 创建文档构建器
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // 启用命名空间支持
        DocumentBuilder builder = factory.newDocumentBuilder();

        // 解析 XML 文件
        Document doc = builder.parse(new File(xmlPath));
        doc.getDocumentElement().normalize();

        // 查找 maven-assembly-plugin 插件
        NodeList pluginList = doc.getElementsByTagName("plugin");
        for (int i = 0; i < pluginList.getLength(); i++) {
            Node pluginNode = pluginList.item(i);
            // 如果该插件是 maven-assembly-plugin
            if (pluginNode.getNodeType() == Node.ELEMENT_NODE) {
                Element pluginElement = (Element) pluginNode;
                NodeList artifactIdList = pluginElement.getElementsByTagName("artifactId");
                for (int j = 0; j < artifactIdList.getLength(); j++) {
                    Node artifactIdNode = artifactIdList.item(j);
                    if (artifactIdNode.getTextContent().equals("maven-assembly-plugin")) {
                        // 找到 maven-assembly-plugin 插件后，获取 mainClass 和 descriptorRef
                        NodeList mainClassList = pluginElement.getElementsByTagNameNS("*", "mainClass");
                        if (mainClassList.getLength() > 0) {
                            values[0] = mainClassList.item(0).getTextContent().trim();
                        }
                        NodeList descriptorRefList = pluginElement.getElementsByTagNameNS("*", "descriptorRef");
                        if (descriptorRefList.getLength() > 0) {
                            values[1] = descriptorRefList.item(0).getTextContent().trim();
                        }
                        return values;  // 找到后立即返回
                    }
                }
            }
        }
        return values; // 如果没有找到，返回空值
    }



    // 打包
    public static void build(String projectPath) throws Exception{

        System.out.println("---------------------------------");
        // target
        String targetPath = new File(projectPath,"target").getAbsolutePath();
        System.out.println("target："+targetPath);
        // pom.xml
        String pomPath = new File(projectPath,"pom.xml").getPath();

        String jrePath = System.getenv("JAVA_HOME");
        System.out.println("jdk："+jrePath);
        System.out.println("---------------------------------");


        // 解析 pom.xml 中 org.apache.maven.plugins 插件的 <mainClass> 和 <descriptorRef> 标签（含有依赖的jar名称）
        String parse[] = parseMavenAssemblyPlugin(pomPath);
        String mainClass = parse[0];
        String descriptorRef = parse[1];


        System.out.println("mainClass："+mainClass);
        System.out.println("descriptorRef："+descriptorRef);
        System.out.println("---------------------------------");

        // 待打包的 jar
        String filter = descriptorRef;
        String jar = Arrays.stream(new File(targetPath).listFiles()).filter(n->n.getName().contains(filter)&&n.getName().contains(".jar")).collect(Collectors.toList()).get(0).getAbsolutePath();

        // 生成同名的 exe 文件
        String appName = new File(jar).getName().replace(".jar","");
        String appFile = new File(jar.replace(".jar",".exe")).getAbsolutePath();
        if(new File(appFile).exists()){
            new File(appFile).delete();
        }

        // 配置模板
        String tamplate = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exe4j version=\"8.0\" transformSequenceNumber=\"3\">\n" +
                "  <directoryPresets config=\"${directoryPresets}\" />\n" +
                "  <application name=\"${appName}\" distributionSourceDir=\"${distributionSourceDir}\" />\n" +
                "  <executable name=\"${appName}\" wrapperType=\"embed\" executableDir=\".\" executableMode=\"gui\" />\n" +
                "  <java mainClass=\"${mainClass}\" preferredVM=\"client\" minVersion=\"1.5\">\n" +
                "    <searchSequence>\n" +
                "      <registry />\n" +
                "      <envVar name=\"JAVA_HOME\" />\n" +
                "      <envVar name=\"JDK_HOME\" />\n" +
                "      <directory location=\"${jrePath}\" />\n" +
                "    </searchSequence>\n" +
                "    <classPath>\n" +
                "      <archive location=\"${jarPath}\" failOnError=\"false\" />\n" +
                "    </classPath>\n" +
                "  </java>\n" +
                "</exe4j>";
        String config = tamplate.replace("${directoryPresets}",targetPath).
                replace("${distributionSourceDir}",targetPath).
                replace("${appName}",appName).
                replace("${mainClass}",mainClass).
                replace("${jrePath}",jrePath).
                replace("${jarPath}",jar);
        // 创建打包配置文件
        File configFile = new File(new File(targetPath),"build.exe4j");
        if(configFile.exists()){
            configFile.delete();
        }
        // 写入配置到文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(config);
        } catch (IOException e) {
            e.getMessage();
        }
        System.out.println("打包配置：");
        System.out.println(config);
        System.out.println("---------------------------------");
        System.out.println(configFile);
        System.out.println(jar);
        System.out.println(appFile);

        System.out.println("---------------------------------");


        // 执行打包脚本
//        String cmd = "exe4j" + " " + "\"" + new File(new File(targetPath),"build.exe4j").getAbsolutePath() + "\"";
        String cmd = "exe4jc" + " " + "\"" + new File(new File(targetPath),"build.exe4j").getAbsolutePath() + "\"";
        System.out.println("启动打包：");
        System.out.println(cmd);
        //执行命令并打印输出
        Process process = Runtime.getRuntime().exec(cmd);
        InputStream in = process.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("GBK")));
        String line = br.readLine();
        while(line!=null) {
            line = br.readLine();
            if(line!=null){
                System.out.println(line);
            }
        }

    }


    public static void main(String[] args) throws Exception{


        // 项目中打包工具需要两个，一个是依赖全部打包到 jar、另外一个是 install 完成后自动执行java函数来调用当前工具打包成 exe

        // <build>
        //        <plugins>
        //
        //            <!--含有依赖的 jar 打包-->
        //            <plugin>
        //                <groupId>org.apache.maven.plugins</groupId>
        //                <artifactId>maven-assembly-plugin</artifactId>
        //                <version>3.3.0</version>
        //                <configuration>
        //                    <archive>
        //                        <manifest>
        //                            <mainClass>org.Main</mainClass>
        //                        </manifest>
        //                    </archive>
        //                    <descriptorRefs>
        //                        <descriptorRef>jar-with-dependencies</descriptorRef>
        //                    </descriptorRefs>
        //                </configuration>
        //                <executions>
        //                    <execution>
        //                        <id>make-assembly</id>
        //                        <phase>package</phase>
        //                        <goals>
        //                            <goal>single</goal>
        //                        </goals>
        //                    </execution>
        //                </executions>
        //            </plugin>
        //
        //            <plugin>
        //                <groupId>org.apache.maven.plugins</groupId>
        //                <artifactId>maven-compiler-plugin
        //                </artifactId>
        //                <configuration>
        //                    <source>8</source>
        //                    <target>8</target>
        //                </configuration>
        //            </plugin>
        //
        //            <!--执行自定义 java main 函数运行 exe4j 打包为 exe-->
        //            <plugin>
        //                <groupId>org.codehaus.mojo</groupId>
        //                <artifactId>exec-maven-plugin</artifactId>
        //                <version>3.1.0</version>
        //                <executions>
        //                    <execution>
        //                        <!-- 在安装阶段执行 -->
        //                        <phase>install</phase>
        //                        <goals>
        //                            <goal>java</goal>
        //                        </goals>
        //                        <configuration>
        //                            <mainClass>org.Build</mainClass>
        //                        </configuration>
        //                    </execution>
        //                </executions>
        //            </plugin>
        //
        //        </plugins>
        //    </build>



        // 项目路径
        // 解析 pom 中 maven-assembly-plugin 插件指定入口类和含有依赖jar名称
        // <mainClass>org.Main</mainClass>
        // <descriptorRef>jar-with-dependencies</descriptorRef>

        // 获取当前项目路径
        String projectPath = System.getProperty("user.dir");


        // 启动 exe4j 打包软件，直接点完成即可
        build(projectPath);

    }





}
