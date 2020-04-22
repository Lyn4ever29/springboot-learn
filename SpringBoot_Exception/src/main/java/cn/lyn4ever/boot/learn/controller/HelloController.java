package cn.lyn4ever.boot.learn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 微信公众号 “小鱼与Java”
 *
 * @date 2020/4/22
 * @auther Lyn4ever
 */
@Controller
@RequestMapping("hello")
public class HelloController {

    /**
     * 返回一个错误页面
     * @return
     */
    @RequestMapping("page")
    public String helloPage(Model model){
        model.addAttribute("text","文字");
        return "hello";
    }

    /**
     * 返回json数据
     * @return
     */
    @RequestMapping("json")
    @ResponseBody
    public Object json(){
        String[] arrs = {"hello","world","SpringBoot"};
        return arrs;
    }

    /**
     * 造成一个错误页面
     * @return
     */
    @RequestMapping("haserror")
    @ResponseBody
    public Object myError(){
        int i =10/0;
        return "something is error";
    }
}
