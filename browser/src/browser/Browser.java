package browser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class Browser {

    private Scanner scanner = new Scanner(System.in);
    private Socket socket = null;
    private String ip;
    private Integer port;

    //设计一个方法，打开一个浏览器
    public void openBrowser(){
        System.out.println("URL:");
        //获取地址栏输入的url链接
        String url = scanner.nextLine();
        //url链接分为:  ip:port/资源名?key=value&key=value
        //接着对url进行解析，找出ip,port,资源文件
        this.paresUrl(url);
    }
    //设计一个方法，用来解析url地址
    private void paresUrl(String url){
        int maohao = url.indexOf(":");
        int xiegan = url.indexOf("/");
        ip = url.substring(0,maohao);
        port = Integer.parseInt(url.substring(maohao+1,xiegan));
        String source = url.substring(xiegan+1);
        this.sendRequest(ip,port,source);
    }
    //设计一个方法，创建一个socket，浏览器将source请求发送给服务端
    private void sendRequest(String ip,Integer port,String source){
        try {
            //通过ip,port创建一个socket
            socket= new Socket(ip,port);
            //将source发送出去给浏览器
            PrintWriter pw = new PrintWriter(socket.getOutputStream());
            pw.println(source);
            pw.flush();
            //浏览器等待响应消息
            this.getResponseContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //获取服务器响应的信息
    private void getResponseContent(){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String responseContent = br.readLine();//响应信息
            //解析响应信息并展示出来
            this.parseResponseContentAndShow(responseContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //这个方法大部分代码实现类似于浏览器内核，将获得的响应信息解析成html的形式展示到页面
    private void parseResponseContentAndShow(String responseContent){
        String content = null;//存放的是新的请求
//        <input name="xxx" value="">  集合的key值为name上的xxx,value为用户输入的值
        HashMap<String,String> requestAndParams = null;
        //解析服务器响应过来的内容responseContent
        responseContent = responseContent.replace("<br>","\r\n");
        while(true){
            int lessThenIndex = responseContent.indexOf("<");
            int greaterThenIndex = responseContent.indexOf(">");
            //如果两个符号成对，则存在一个有意义的标签
            if(lessThenIndex != -1 && greaterThenIndex != -1 && lessThenIndex < greaterThenIndex){
                System.out.println(responseContent.substring(0,lessThenIndex));
                //获取标签，类似<input name="" value="">
                String tag = responseContent.substring(lessThenIndex,greaterThenIndex + 1);
                if(tag.contains("input")){
                    String value = scanner.nextLine();
                    if(requestAndParams == null){
                        requestAndParams = new HashMap<>();
                    }//<input name="" value="">
                    String[] keyAndValues = tag.split(" ");
                    for(String keyAndValue : keyAndValues){
                        if(keyAndValue.contains("=")){
                            String[] KV = keyAndValue.split("=");
                            if("name".equals(KV[0])){
                                requestAndParams.put(KV[1].substring(1,KV[1].length()-1),value);
                            }
                        }
                    }
                }else  if(tag.contains("form")){
                    String[] keyAndValues = tag.split(" ");
                    for(String keyAndValue : keyAndValues){
                        if(keyAndValue.contains("=")){
                            String[] KV = keyAndValue.split("=");
                            if("action".equals(KV[0])){
                                content = KV[1].substring(1,KV[1].length()-1);
                            }
                        }
                    }
                }
                //截取标签后的内容
                responseContent = responseContent.substring(greaterThenIndex + 1);

            }else{//如果符号不成对 证明不存在成对标签
                //直接输出全部内容
                System.out.println(responseContent);
                break;
            }
        }
        //----------至此将所有的标签解析完毕
        //如果遇到了form标签表示还有一次新的请求
        this.sendNewRequest(content,requestAndParams);
    }

    private void sendNewRequest(String content,HashMap<String,String> paramsMap){
        if(content!=null){
            StringBuilder url = new StringBuilder(ip);
            url.append(":");
            url.append(port);
            url.append("/");
            url.append(content);
            if(paramsMap != null){
                url.append("?");
                Iterator<String> it = paramsMap.keySet().iterator();
                while(it.hasNext()){
                    String key = it.next();
                    String value = paramsMap.get(key);
                    url.append(key);
                    url.append("=");
                    url.append(value);
                    url.append("&");
                }
                url.delete(url.length()-1,url.length());
            }

            this.paresUrl(url.toString());
        }

    }

}
