package server;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;


public class ServerHandler extends Thread {

    private Socket socket;
    ServerHandler(Socket socket){
        this.socket = socket;
    }
    @Override
    public void run() {
        this.readMessage();
        //解析信息
        //找人干活
        //返回响应
    }
    private void readMessage(){
        try {
            InputStream is = socket.getInputStream();//字节流
            InputStreamReader isr = new InputStreamReader(is);//将字节流转换成字符流
            BufferedReader br = new BufferedReader(isr);
            String contentAndParams = br.readLine();//读取一行消息，就是浏览器发送过来的请求资源
            this.paresMessage(contentAndParams);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void paresMessage(String contentAndParams){
        //解析浏览器发送过来的请求资源
        //利用两个变量存储： String类型存储 资源名   HashMap<String,String>存储key=value值
        String content = null;
        HashMap<String,String> paramMap = null;
        //找到问号所在的索引值
        int questionMarkIndex = contentAndParams.indexOf("?");
        //判断是否携带了参数
        if(questionMarkIndex != -1){
            content = contentAndParams.substring(0,questionMarkIndex);
            paramMap = new HashMap<>();
            String params = contentAndParams.substring(questionMarkIndex + 1);
            String[] keyAndValues = params.split("&");
            for(String keyAndValue : keyAndValues){
                String[] kAndV = keyAndValue.split("=");
                paramMap.put(kAndV[0],kAndV[1]);
            }

        }else{
            content = contentAndParams;
        }
        //服务器里面有两个对象，HTTPServletRequest用来存储浏览器发送过来的请求资源
        //HTTPServletResponse用来存储从控制层响应返回的资源信息
        HTTPServletRequest request = new HTTPServletRequest(content,paramMap);
        //创建时对象是空的，在Controller执行完毕后就被填满了
        HTTPServletResponse response = new HTTPServletResponse();
        //将解析后得到的资源名，键值参数交给控制层处理
        ServerController.findController(request,response);
        //上面的findController方法执行完毕之后，真实的Controller里面的那个service方法就执行完了
        //response对象里面也有响应信息了
        this.responseToBrowser(response);
    }

    //将响应信息返回给浏览器
    private void responseToBrowser(HTTPServletResponse response){
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            out.println(response.getResponseContent());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
