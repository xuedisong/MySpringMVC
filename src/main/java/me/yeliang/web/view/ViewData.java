package me.yeliang.web.view;

import me.yeliang.web.WebContext.WebContext;

import javax.servlet.http.HttpServletRequest;


public class ViewData {

    private HttpServletRequest request;

    public ViewData() {
        initRequest();
    }

    private void initRequest(){
        //从requestHodler中获取request对象
        this.request = WebContext.requestHodler.get();
    }

    public void put(String name,Object value){
        this.request.setAttribute(name, value);
    }
}
