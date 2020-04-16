package controller;

import server.HTTPServlet;
import server.HTTPServletRequest;
import server.HTTPServletResponse;

public class LoginController extends HTTPServlet {
    @Override
    public void service(HTTPServletRequest request, HTTPServletResponse response) {
        String name = request.getParams("name");
        String password = request.getParams("password");
        System.out.println(name + "----" + password);
    }
}
