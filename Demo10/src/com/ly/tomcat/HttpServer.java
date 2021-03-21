package com.ly.tomcat;

import com.ly.standard.ServletException;
import com.ly.tomcat.sever.DefaultServlet;
import com.ly.tomcat.sever.NotFoundServlet;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/** 服务器启动 */
public class HttpServer {
    public static DefaultServlet defaultServlet = new DefaultServlet();
    public static NotFoundServlet notFoundServlet = new NotFoundServlet();

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, ServletException, IllegalAccessException {
        // 1.初始化工作 找到所有的Servlet对象进行初始化
        initServer();
        // 2. 处理服务器的逻辑 相当于Service方法 处理HTTP的请求响应
        startServer();
        // 3. 找到所有的 Servlet对象， 进行销毁
        destroyServer();

    }

    // 相当于service方法
    private static void startServer() throws IOException {
        System.out.println("startSever");
        // 创建线程池
        ExecutorService es = Executors.newFixedThreadPool(10);
        // 我们的自建HTTP服务器在哪个端口
        ServerSocket serverSocket = new ServerSocket(8080);

        // 循环处理请求
        // 一次处理一个请求
        while (true) {
            // 三次握手成功后执行accept方法
            Socket socket = serverSocket.accept();
            Runnable task = new RequestResponseTask(socket);
            es.execute(task);
        }
    }

    private static void destroyServer() {
        defaultServlet.destroy();
        notFoundServlet.destroy();
        System.out.println("最后一步  destroy 销毁所有的任务");
        for (Context context : contextList) {
            context.destroyServlets();
        }
    }

    /** 五步走 进行初始化工作*/
    private static void initServer() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, ServletException {
        /**扫描出所有的context对象 相当于webapps中每一个web项目*/
        scanContexts();
        /**读取并且解析各自的web配置文件*/
        parseContextConf();
        /**加载需要的ServletClass表现为Class<?> 对象 利用反射*/
        loadServletClasses();
        /** 类加载完成之后 就去实例化所需要的Servlet对象*/
        instantiateServletObjects();
        /**执行Servlet的初始化工作*/
        initializeServletObjects();
    }

    private static void initializeServletObjects() throws ServletException {
        System.out.println("init第五步 执行Servlet的初始化工作");
        for (Context context : contextList) {
            context.initializeServletObject();
        }
        defaultServlet.init();
        notFoundServlet.init();
    }


    private static void instantiateServletObjects() throws InstantiationException, IllegalAccessException {
        System.out.println("init第四步 实例化加载完成的类对象");
        for (Context context : contextList) {
            context.instantiateServletObject();
        }
    }

    //使用“反射”技术，进行类的加载工作
    //引入了 ClassLoader 类进行类加载
    //主要目的是不同的 context 之间的类加载器(ClassLoader）需要各不相同——隔离的目的，防止有版本冲突时，类加载错误
    private static void loadServletClasses() throws ClassNotFoundException {
        System.out.println("init的第三步 加载需要的ServletClass<?> 对象");
        for (Context context : contextList) {
            context.loadServletClass();
        }
    }

    private static void parseContextConf() throws IOException {
        System.out.println("init 的第二步 读取并且解析各自的web配置文件");

        // 设计一个 Config配置类来专门解析配置文件
        for (Context context : contextList) {
            // 获取到具体的config配置信息
            context.readConfigFile();
        }


    }


    public static final List<Context> contextList = new ArrayList<>();
    public static final String WEBAPPS_BASE = "C:\\JavaEE_study\\Demo10\\src\\webapps";
    public static final ConfigReader configReader = new ConfigReader();
    public static final DefaultContext defaultContext = new DefaultContext(configReader);
    private static void scanContexts() {
        System.out.println("init第一步 扫描出每一个的context内容");
        File webappsRoot = new File(WEBAPPS_BASE);
        // 罗列出每一个文件
        File[] files = webappsRoot.listFiles();
        if(files == null) {
            //当文件列表为空的时候
            throw new RuntimeException("Webapps下文件为空");
        }
        for (File f : files) {
            if (!f.isDirectory()) {
                //不是目录的情况
                continue;
            }
            // 解析每一个目录的名字 其实也就是web的名称
            String contextName = f.getName();
            System.out.println(contextName);
            // 每一个项目利用ConfigReader 来解析他的配置
            Context context = new Context(configReader, contextName);
            contextList.add(context);
        }
    }
}
