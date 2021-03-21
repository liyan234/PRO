package com.ly.tomcat;

import com.ly.standard.Servlet;
import com.ly.standard.ServletException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Context {
    /** 解析配置类 */
    private final ConfigReader reader;
    /** 项目名称 */
    private final String name;
    /** 配置类中的对应的 servletNameToServletClassNameMap 和  urlPatternToServletNameMap */
    private Config config;

    /** 自己的类加载器去加载这个类 */
    private final ClassLoader webappClassLoader = Context.class.getClassLoader();


    public Context(ConfigReader reader, String name) {
        this.reader = reader;
        this.name = name;
    }

    public String getName () {
        return name;
    }

    // 获取到具体的配置信息
    public void readConfigFile() throws IOException {
        this.config = reader.read(name);
    }

    // 加载需要的ServletClass<?> 对象
    // 利用反射加载对象
    List<Class<?>> servletClassList = new ArrayList<>();
    /** 加载类 */
    public void loadServletClass() throws ClassNotFoundException {
        // 找到对应的webapps中需要加载的对象的路径存储于Set中
        Set<String> servletClassNames = new HashSet<>(config.servletNameToServletClassNameMap.values());
        // 然后用独自的ClassLoader类进行类加载工作
        for (String className : servletClassNames) {
            Class<?> servletClass = webappClassLoader.loadClass(className);
            servletClassList.add(servletClass);
        }
     }

     // 实列化对象集
     List<Servlet> servletList = new ArrayList<>();
     /** 实例化成Servlet对象*/
    public void instantiateServletObject() throws IllegalAccessException, InstantiationException {
        for (Class<?> c : servletClassList) {
            // newInstance 其实实例化成一个Object
            // 强转成Servlet对象
            Servlet servlet = (Servlet)( c.newInstance());
            servletList.add(servlet);
        }
    }

    public void initializeServletObject() throws ServletException {
        for (Servlet s : servletList) {
            s.init();
        }
    }

    /** 销毁servlet对象*/
    public void destroyServlets() {
        for (Servlet s : servletList) {
            s.destroy();
        }
    }

    /** 根据我们解析配置文件的两个map找到对应Servlet对象 */
    public Servlet getServlet(String servletPath) {
        String servletName = config.urlPatternToServletNameMap.get(servletPath);
        String servletClassName = config.servletNameToServletClassNameMap.get(servletName);
        for (Servlet servlet : servletList) {
            String className = servlet.getClass().getCanonicalName();
            if (className.equals(servletClassName)) {
                return servlet;
            }
        }
        return null;
    }
}
