package cn.lyn4ever.learn.boot.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信公众号 “小鱼与Java”
 *
 * @date 2020/4/16
 * @auther Lyn4ever
 */
@Configuration
public class UserDetailsServiceImpl implements UserDetailsService {
    /**
     * 这个方法要返回一个UserDetails对象
     * 其中包括用户名，密码，授权信息等
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        /**
         * 将我们的登录逻辑写在这里
         * 我是直接在这里写的死代码，其实应该从数据库中根据用户名去查
         */
        if (username == null) {
            //返回null时，后边就会抛出异常，就会登录失败。但这个异常并不需要我们处理
            return null;
        }
        if (username.equals("lyn4ever")) {
            //这是构造用户权限组的代码
            //但是这个权限上加了ROLE_前缀，而在之前的配置上却没有加。
            //与其说这不好理解，倒不如说这是他设计上的一个小缺陷
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
            List<SimpleGrantedAuthority> list = new ArrayList<>();
            list.add(authority);
            //这个user是UserDetails的一个实现类
            //用户密码实际是lyn4ever,前边加{noop}是不让SpringSecurity对密码进行加密，使用明文和输入的登录密码比较
            //如果不写{noop},它就会将表表单密码进行加密，然后和这个对比
            User user = new User("lyn4ever", "{noop}lyn4ever", list);
            return user;
        }
        if (username.equals("admin")) {
            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
            SimpleGrantedAuthority authority1 = new SimpleGrantedAuthority("ROLE_ADMIN");
            List<SimpleGrantedAuthority> list = new ArrayList<>();
            list.add(authority);
            list.add(authority1);
            User user = new User("admiin", "{noop}admin", list);
            return user;
        }

        //其他返回null
        return null;
    }
}
