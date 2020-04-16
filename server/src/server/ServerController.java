package server;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

/**
 * 1.这个类的目的是为了管理 findController方法
 * 2.因为findController方法与之前的服务器serverHandler做的事情不一致, 抽离出来
 * 3.每一次找寻Controller 类的时候都需要参考一下web.properties
 *      读取效率比较低，所以增加了一个缓存机制
 * 4.每一个Controller类都是由findController方法来找寻的
 *      找到了Controller类是为了执行里面的service方法
 *      为了服务器方便找到类下的service方法，所有定义了一个规则统一方法名
 * 5.发现Controller类与之前的Service和Dao相似    只有方法执行 没有属性
 *      所有让Controller类的对象变成单例模式，对象只有一份，但多个人同时操作该对象调用方法时
 *      只是方法产生多个而已
 */

class ServerController {
    //利用一个集合当做缓冲机制来存放配置文件里的东西
    private static HashMap<String,String> contentAndRealClass = new HashMap<>();
    //利用一个集合，用来存放Controller类中的所有对象，采用的是生命周期托管延迟加载的方式
    //并且每一个Controller类对象都只有一份，实现了单例模式
    private static HashMap<String,HTTPServlet> controllerObjectMap = new HashMap<>();
    //立即加载的方式可能会导致一开始读取存入很对对象信息但有些用不到，造成空间时间上的浪费
    static {
        //参考配置文件，获取一个请求资源对应控制层中的类全名
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("src//controller//web.properties"));
            Enumeration en = properties.propertyNames();
            while(en.hasMoreElements()){
                String content = (String)en.nextElement();
                String realClassName = properties.getProperty(content);
                contentAndRealClass.put(content,realClassName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    //找人干活 --- 利用反射找某一个控制层来干活
    static void findController(HTTPServletRequest request, HTTPServletResponse response){
        String content = request.getContent();
        try {
            HTTPServlet controllerObject = controllerObjectMap.get(content);
            //如果controller对象不存在，则证明之前还没用过
            //运用到了生命周期托管的方式，将controllerObject放到一个集合里
            //将这个对象交给一个集合管理，要用的时候去集合里拿就行
            if(controllerObject == null){
                String realControllerName = contentAndRealClass.get(content);
                //如果controller对象不在集合中并且存在这个controller类，我们就new一个controller对象并将这个对象放入集合中
                if(realControllerName != null){
                    //如果realControllerName在配置文件写错了，会抛出ClassNotFoundException
                    Class clazz = Class.forName(realControllerName);
                    Constructor con = clazz.getConstructor();
                    controllerObject = (HTTPServlet)con.newInstance();
                    controllerObjectMap.put(content,controllerObject);
                }
                //如果realControllerName == null，会抛出一个异常NoSuchMethodException,在方法执行那里出现71行
            }
            //---------------以上54-65行确保controllerObject对象肯定存在
            //接下来就可以执行控制层对象里的方法了
            Class controllerClass = controllerObject.getClass();
            Method method = controllerClass.getMethod("service",HTTPServletRequest.class,HTTPServletResponse.class);
            method.invoke(controllerObject,request,response);
        }catch (ClassNotFoundException e){
            response.write("请求的" + content+ "controller不存在");
        }catch (NoSuchMethodException e){
            //可能方法名写错了(如service写错了),与服务器中HTTPServlet定义的规则不一样
            response.write("405 没有可以执行的方法");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}
