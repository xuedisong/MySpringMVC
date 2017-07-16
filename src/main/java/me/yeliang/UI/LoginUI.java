package me.yeliang.UI;

import me.yeliang.web.annotation.Controller;
import me.yeliang.web.annotation.RequestMapping;
import me.yeliang.web.view.View;


@Controller
public class LoginUI {
    //使用RequestMapping注解指明
    @RequestMapping("LoginUI/Login1")
    public View forward1() {
        return new View("/jsp/login1.jsp");
    }
    @RequestMapping("LoginUI/Login2")
    public View forward2() {
        return new View("/jsp/login2.jsp");
    }

}
