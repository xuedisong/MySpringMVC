# MySpringMVC
模仿springMVC实现的简单web框架


## 使用说明
将源码导入lib中,即可类比spirngMVC使用@Controller和@RequestMapping注解,在web.xml文件中配置servlet之后,即可以Controller中配置的路径访问对应jsp页面.

##原理简介
1.首先是注解的定义,在annotion包中定义Controller和RequestMapping注解的类型
2.AnnotationHandleServlet作为控制器,服务器开启后init方法被调用,从web.xml文件中读取相关配置,扫描被注解的类,并读取到ThreadLocal中.
3.服务器接收到用户的get或post请求,在execute方法中解析请求路径,并与ThreadLocal中存储的路径比对,加载并实例化对应的类,调用从定向方法,返回对应的jsp页面.
4.util包是对扫描,存储,和一些反射方法的封装.
