package com.gane.maple.order.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description TODO
 * @Date 2020/4/18 17:40
 * @Created by 王弘博
 */
@Controller
public class CASController {

    @Value("${casClientLogoutUrl}")
    private String clientLogoutUrl;

    @RequestMapping("index")
    public String index(ModelMap map) {
        map.addAttribute("name", "clien B");
        return "index";
    }

    @RequestMapping("hello")
    public String hello() {
        return "hello";
    }

    @RequestMapping("logout")
    public String logout() {
        return "redirect:" + clientLogoutUrl;
    }

}
