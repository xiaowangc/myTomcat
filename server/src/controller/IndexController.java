package controller;

import server.HTTPServlet;
import server.HTTPServletRequest;
import server.HTTPServletResponse;

public class IndexController  extends HTTPServlet {

    @Override
    public void service(HTTPServletRequest request, HTTPServletResponse response) {
        //1.获取请求发送过来携带的参数

        //2.找到某一个业务层的方法 做事

        //3.将最终业务层执行完毕的结果 交还给服务器，服务器在交给浏览器渲染出来
        response.sentRedirect("index.view");
    }
}
