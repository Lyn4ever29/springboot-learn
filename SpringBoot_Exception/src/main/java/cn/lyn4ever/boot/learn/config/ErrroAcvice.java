package cn.lyn4ever.boot.learn.config;

/**
 * 微信公众号 “小鱼与Java”
 *
 * @date 2020/4/22
 * @auther Lyn4ever
 */

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 方法二的用法
 */
@ControllerAdvice
public class ErrroAcvice {

    /**
     * 全局捕获异常的切而类
     *
     * @param request  请求对象，可不传
     * @param response 响应对象，可不传
     * @param e        异常类
     */
    @ExceptionHandler(Exception.class)
    public void errorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        String accept = request.getHeader("Accept");
        System.out.println(accept);
//        try {
//            response.setStatus(500);
////            response.getWriter().write("hello");
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }

    }

}
