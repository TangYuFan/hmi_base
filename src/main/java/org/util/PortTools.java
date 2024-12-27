package org.util;


import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 *   @desc : 端口工具
 *   @auth : tyf
 *   @date : 2024-12-25 16:43:36
*/
public class PortTools {

    private static int minPort = 8000;
    private static int maxPort = 9000;

    // 获取指定数量的可用端口
    public static int[] getAvailablePorts(int count){
        if (count <= 0 || minPort <= 0 || maxPort > 65535 || minPort > maxPort) {
            throw new IllegalArgumentException("参数无效！");
        }
        List<Integer> availablePorts = new ArrayList<>();
        for (int port = minPort; port <= maxPort && availablePorts.size() < count; port++) {
            if (isPortAvailable(port)) {
                availablePorts.add(port);
            }
        }
        return availablePorts.stream().mapToInt(Integer::intValue).toArray();
    }

    // 检查指定端口是否可用
    private static boolean isPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static void main(String[] args) {
        // 获取 5 个可用端口
        int[] ports = getAvailablePorts(5);
        System.out.println("可用端口：" + java.util.Arrays.toString(ports));
    }
}
