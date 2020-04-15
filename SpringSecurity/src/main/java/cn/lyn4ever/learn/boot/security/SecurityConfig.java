package cn.lyn4ever.learn.boot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;


/**
 * 微信公众号 “小鱼与Java”
 * <p>
 * SpringSecurity的配置
 *
 * @date 2020/4/15
 * @auther Lyn4ever
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * 配置用户权限组和接口路径的关系
     * 和一些其他配置
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()     // 对请求进行验证
                .antMatchers("/visitor/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")     // 必须有ADMIN权限
                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")       //有任意一种权限
                .anyRequest()     //任意请求（这里主要指方法）
                .authenticated()   //// 需要身份认证
                .and()   //表示一个配置的结束
                .formLogin().permitAll()    //开启SpringSecurity内置的表单登录，会提供一个/login接口
                .and()
                .logout().permitAll()  //开启SpringSecurity内置的退出登录，会为我们提供一个/logout接口
                .and()
                .csrf().disable();    //关闭csrf跨站伪造请求
    }
}
