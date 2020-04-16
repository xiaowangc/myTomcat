package testmain;

import server.Server;

public class TestMain {
    public static void main(String[] args) {
        System.out.println("启动服务器：");
        new Server().ServerToBrowser();
    }
}
