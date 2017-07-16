package me.yeliang.web.controller;


import me.yeliang.util.BeanUtils;
import me.yeliang.util.RequestMappingMap;
import me.yeliang.util.ScanClassUtil;
import me.yeliang.web.WebContext.WebContext;
import me.yeliang.web.annotation.Controller;
import me.yeliang.web.annotation.RequestMapping;
import me.yeliang.web.view.DispatchActionConstant;
import me.yeliang.web.view.View;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * 注解处理器
 */
public class AnnotationHandleServlet extends HttpServlet {

    private String pareRequestURI(HttpServletRequest request) {
        String path = request.getContextPath() + "/";
        String requestUri = request.getRequestURI();
        String midUrl = requestUri.replaceFirst(path, "");
        String lasturl = midUrl.substring(0, midUrl.lastIndexOf("."));
        return lasturl;
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.excute(request, response);
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        this.excute(request, response);
    }

    private void excute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //将当前线程中HttpServletRequest对象存储到ThreadLocal中，以便在Controller类中使用
        WebContext.requestHodler.set(request);
        //将当前线程中HttpServletResponse对象存储到ThreadLocal中，以便在Controller类中使用
        WebContext.responseHodler.set(response);
        //解析url
        String lasturl = pareRequestURI(request);
        //获取要使用的类
        Class<?> clazz = RequestMappingMap.getRequesetMap().get(lasturl);
        //创建类的实例
        Object classInstance = BeanUtils.instanceClass(clazz);
        //获取类中定义的方法
        Method[] methods = BeanUtils.findDeclaredMethods(clazz);
        Method method = null;
        for (Method m : methods) {//循环方法，找匹配的方法进行执行
            if (m.isAnnotationPresent(RequestMapping.class)) {
                String anoPath = m.getAnnotation(RequestMapping.class).value();
                if (anoPath != null && !"".equals(anoPath.trim()) && lasturl.equals(anoPath.trim())) {
                    //找到要执行的目标方法
                    method = m;
                    break;
                }
            }
        }
        try {
            if (method != null) {
                //执行目标方法处理用户请求
                Object retObject = method.invoke(classInstance);
                //如果方法有返回值，那么就表示用户需要返回视图
                if (retObject != null) {
                    View view = (View) retObject;
                    //判断要使用的跳转方式
                    if (view.getDispathAction().equals(DispatchActionConstant.FORWARD)) {
                        //使用服务器端跳转方式
                        request.getRequestDispatcher(view.getUrl()).forward(request, response);
                    } else if (view.getDispathAction().equals(DispatchActionConstant.REDIRECT)) {
                        //使用客户端跳转方式
                        response.sendRedirect(request.getContextPath() + view.getUrl());
                    } else {
                        request.getRequestDispatcher(view.getUrl()).forward(request, response);
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        super.init(config);
        System.out.println("---初始化开始---");
        //获取web.xml中配置的要扫描的包
        String basePackage = config.getInitParameter("basePackage");
        //如果配置了多个包，例如：<param-value>me.gacl.web.controller,me.gacl.web.UI</param-value>
        if (basePackage.indexOf(",") > 0) {
            //按逗号进行分隔
            String[] packageNameArr = basePackage.split(",");
            for (String packageName : packageNameArr) {
                initRequestMapingMap(packageName);
            }
        } else {
            initRequestMapingMap(basePackage);
        }
        System.out.println("----初始化结束---");
    }

    /**
     * 添加使用了Controller注解的Class到RequestMapingMap中
     */
    private void initRequestMapingMap(String packageName) {
        Set<Class<?>> setClasses = ScanClassUtil.getClasses(packageName);
        for (Class<?> clazz : setClasses) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                Method[] methods = BeanUtils.findDeclaredMethods(clazz);
                for (Method m : methods) {//循环方法，找匹配的方法进行执行
                    if (m.isAnnotationPresent(RequestMapping.class)) {
                        String anoPath = m.getAnnotation(RequestMapping.class).value();
                        if (anoPath != null && !"".equals(anoPath.trim())) {//路径非空
                            if (RequestMappingMap.getRequesetMap().containsKey(anoPath)) {
                                throw new RuntimeException("RequestMapping映射的地址不允许重复！");
                            }
                            RequestMappingMap.put(anoPath, clazz);//储存路径-方法
                        }
                    }
                }
            }
        }
    }
}