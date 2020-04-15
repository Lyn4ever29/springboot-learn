#  在SpringBoot中使用SpringSecurity

> 本教程是基于SpringMVC而创建的，不适用于WebFlux。（如果你不知道这两者，可以忽略这句提示）

##	提出一个需求

所有的技术是为了解决实际问题而出现的，所以我们并不空谈，也不去讲那么多的概念。在这样一个系统中，有三个接口，需要授权给三种权限的人使用，如下表：

| 接口地址     | 需要的权限描述                       | 可访问的权限组名称 |
| ------------ | ------------------------------------ | ------------------ |
| visitor/main | 不需要权限，也不用登录，谁都可以访问 |                    |
| admin/main   | 必须登录，只有管理员可以访问         | ADMIN              |
| user/main    | 必须登录，管理员和用户权限都能访问   | USER和ADMIN        |

##	解决方案：

*  在Controller中判断用户是否登录和用户的权限组判断是否可以访问

   这是最不现实的解决方案，可是我刚进公司时的项目就是这样设计的，当时我还觉得很高大尚呢。

*  使用Web应用的三大组件中和过滤器（Filter）进行判断

   这是正解，SpringSecurity也正是用的这个原理。如果你的项目足够简单，建议你直接使用这种方式就可以了，并不需要集成SpringSecurity。这部分的示例在代码中有演示，自己下载代码查看即可。
   
*  我们可以直接使用SpringSecurity框架来解决这个问题

## 使用SpringSecurity进行解决

​	网上的教程那么多，但是讲的都不清不楚。所以，请仔细阅读下段这些话，这要比后边的代码重要。

​	SpringSecurity主要有两部分内容：

- 认证 （你是谁，说白了就是一个用户登录的功能，帮我们验证用户名和密码）
- 授权 （你能干什么，就是根据当前登录用户的权限，说明你能访问哪些接口，哪些不能访问。）

> 这里的登录是对于浏览器访问来说的，因为如果是前后端分离时，使用的是Token进行授权的，也可以理解为登录用户，这个后边会讲。这里只是为了知识的严谨性才提到了这点

### SpringSecurity和SpringBoot结合

#### 1. 首先在pom.xml中引入依赖：

```xml
<!-- 不用写版本，继承Springboot的版本-->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

```

#### 2. 配置用户角色和接口的权限关系

> 是支持使用xml进行配置的，但是在SpringBoot中更建议使用Java注解配置

```java
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
                .antMatchers("/admin/**").hasRole("ROLE_ADMIN")     // 必须有ADMIN权限
                .antMatchers("/user/**").hasAnyRole("ROLE_USER", "ROLE_ADMIN")       //有任意一种权限
                .anyRequest()     //任意请求（这里主要指方法）
                .authenticated()   //// 需要身份认证
                .and()   //表示一个配置的结束
                .formLogin().permitAll()  //开启SpringSecurity内置的表单登录，会提供一个/login接口
                .and()
                .logout().permitAll()  //开启SpringSecurity内置的退出登录，会为我们提供一个/logout接口
                .and()
                .csrf().disable();    //关闭csrf跨站伪造请求
    }

}
```

上边的配置主要内容有两个:

1. 配置访问三个接口（实际上不仅仅是3个，/**是泛指）需要的权限；
2. 配置了使用SpringSecurity的内置/login和/loginout接口（这个是完全可以自定义的）
3. 权限被拒绝后的返回结果也可以自定义，它当权限被拒绝后，会抛出异常

说明：

1. 上边的配置中，其实就是调用http的这个对象的方法；
2. 使用.and()只为了表示一上配置结束，并满足链式调用的要求，不然之前的对象可能并不能进行链式调用
3. 这个配置在SpringBoot应用启动的时候就会调用，也就是会将这些配置加载进内存，当用户调用对应的接口的时候，就会判断它的角色是否可以调用这个接口，流程图如下（我觉得图要比文字更能说明过程）：

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200415235232.png)

#### 3. 配置用户名和密码

​	配置了上边的接口和用户权限角色的关系后，就是要配置我们的用户名和密码了。如果没有正确的用户名和密码，神仙也登录不上去。

​	关于这个，网上的教程有各种各样的配置，其实就一个接口，我们只需要实现这个接口中的方法就可以了。接口代码如下：

```java
package org.springframework.security.core.userdetails;

public interface UserDetailsService {
  	/**
  	 * 在登录的时候，就会调用这个方法，它的返回结果是一个UserDetails接口类
  	 */
    UserDetails loadUserByUsername(String var1) throws UsernameNotFoundException;
}
```

​	来看一下这个接口，如果想扩展，可以自己写一个实现类，也可以使用SpringSecurity提供的实现

```java
public interface UserDetails extends Serializable {
  	// 用户授权集合
    Collection<? extends GrantedAuthority> getAuthorities();
    String getPassword();
    String getUsername();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();
}
```

​	UserDetailsServicer接口的实现类

```java
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
```

#### 4.进行测试

​	分别访问上边三个接口，可以看到访问结果和上边的流程是一样的。

### 总结：

* 仔细阅读上边的那个流程图，是理解SpringSecurity最重要的内容，代码啥的都很简单；上边也就两个类，一个配置接口与角色的关系，一个实现了UserDetailsService类中的方法。
* 前边说了，SpringSecurity主要就是两个逻辑：
  * 用户登录后，将用户的角色信息保存在服务器（session中）；
  * 用户访问接口后，从session中取出用户信息，然后和配置的角色和权限进行比对是否有这个权限访问
* 上述方法中，我们只重写了用户登录时的逻辑。而根据访问接口来判断当前用户是否拥有这个接口的访问权限部分，我们并没有进行修改。所以这只适用于可以使用session的项目中。
* 对于前后端分离的项目，一般是利用JWT进行授权的，所以它的主要内容就在判断token中的信息是否有访问这个接口的权限，而并不在用户登录这一部分。
* 解决访问的方案有很多种，选择自己最适合自己的才是最好了。SpringSecurity只是提供了一系列的接口，他自己内部也有一些实现，你也可以直接使用。
* 上边配置和用户登录逻辑部分的内容是完全可以从数据库中查询出来进行配置的。

