package server;

public abstract class HTTPServlet {
    public abstract void service(HTTPServletRequest request,HTTPServletResponse response);
}
