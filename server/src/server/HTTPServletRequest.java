package server;

import java.util.HashMap;

/**
 *  目的： 为了存储浏览器发送请求时携带的所有信息
 */
public class HTTPServletRequest {

    private String content;
    private HashMap<String,String> params;
    public HTTPServletRequest(){}
    public HTTPServletRequest(String content,HashMap<String,String> params){
        this.content = content;
        this.params = params;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getParams(String key) {
        return params.get(key);
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }
}
