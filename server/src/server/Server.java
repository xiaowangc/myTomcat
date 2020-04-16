package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {


    public void ServerToBrowser(){
        ServerSocket serverSocket;
        try {
            //创建一个服务器，提供一个9999的端口给浏览器连接
            serverSocket = new ServerSocket(9999);
            //接受浏览器，产生一个socket连接
            //服务器只有一份，但是socket却有好多份，因为不止一个人在连接服务器
            //所以这里是多线程的
            while (true){
                Socket socket = serverSocket.accept();
                //创建多线程用来处理多个浏览器发送的请求
                new ServerHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
