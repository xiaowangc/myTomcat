package server;

import java.io.*;

/**
 * 目的：存储响应信息
 */


public class HTTPServletResponse {

    private StringBuilder responseContent = new StringBuilder();

    void write(String str){
        this.responseContent.append(str);
    }
    //让response读取文件，文件的内容是响应给浏览器的信息
    public void sentRedirect(String path){
        File file = new File("src//file//" + path);
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader br = new BufferedReader(fileReader);
            String message = br.readLine();
            while(message!=null){
                this.responseContent.append(message);
                message = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    String getResponseContent(){
        return this.responseContent.toString();
    }

}
