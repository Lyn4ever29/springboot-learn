package cn.lyn4ever.boot.learn.config;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信公众号 “小鱼与Java”
 *
 * @date 2020/4/22
 * @auther Lyn4ever
 */
@Component
public class MyErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        //调用父类的方法，会自动获取内置的那些属性，如果你不想要，可以不调用这个
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);

        //添加自定义的属性
        errorAttributes.put("reason","完了，你写的代码又产生了一次线上事故");
        // 你可以看一下这个方法的参数webRequest这个对象，我相信你肯定能发现好东西

        return errorAttributes;
    }
}
