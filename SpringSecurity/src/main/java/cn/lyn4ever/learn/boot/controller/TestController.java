package cn.lyn4ever.learn.boot.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信公众号 “小鱼与Java”
 *
 * @date 2020/4/15
 * @auther Lyn4ever
 */
@RestController
public class TestController {
    @RequestMapping("admin/{id}")
    public String admin(@PathVariable("id") String id) {
        return "admin/" + id;
    }

    @RequestMapping("visitor/{id}")
    public String visitor(@PathVariable("id") String id) {
        return "visitor/" + id;
    }

    @RequestMapping("user/{id}")
    public String user(@PathVariable("id") String id) {
        return "user/" + id;
    }
}
