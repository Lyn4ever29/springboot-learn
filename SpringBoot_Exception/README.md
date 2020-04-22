# SpringBoot定制404、500页面

## 一、错误分析

使用SpringBoot创建的web项目中，当我们请求的页面不存在(http状态码为404)，或者器发生异常(http状态码一般为500)时，SpringBoot就会给我们返回错误信息。

也就是说，在SpringBoot的web项目中，会自动创建一个/error的错误接口，来返回错误信息。但是针对不同的访问方式，会有以下两种不同的返回信息。这主要取决于你访问时的http头部信息的```Accept```这个值来指定你可以接收的类型有哪些

* 使用浏览器访问时的头信息及其返回结果

```json
Accept: text/html
```

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200422215925.png)

* 使用其他设备，如手机客户端等访问时头部信息及其返回结果（一般是在前后端分离的架构中）

```Accept: 
Accept: */*
```

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200422215908.png)

## 二、进行错误处理

处理异常主要有两种方式：

### 1. 使用SpringBoot的自动配置原理进行异常处理

SpringBoot自动配置了一个类```ErrorMvcAutoConfiguration```来处理处理异常，有兴趣的可以去看一下，然后在这个类中定义一个错误的BasicErrorController类，主要代码有如下：

```java
@Controller
@RequestMapping({"${server.error.path:${error.path:/error}}"})
public class BasicErrorController extends AbstractErrorController {

  	/**
  	 * 错误的页面响应 
  	 */
    @RequestMapping(produces = {"text/html"})
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = this.getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
      	// 得到一个modelAndView对象
        ModelAndView modelAndView = this.resolveErrorView(request, response, status, model);
        return modelAndView != null ? modelAndView : new ModelAndView("error", model);
    }
		
  /**
   * 错误的json响应
   */
    @RequestMapping
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = this.getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity(status);
        } else {
            Map<String, Object> body = this.getErrorAttributes(request, this.isIncludeStackTrace(request, MediaType.ALL));
            return new ResponseEntity(body, status);
        }
    }
}
```

多的代码就不深究了，感兴趣的可以去看一下。上边的代码也就是说，针对不同的请求方式，会返回不同的结果，其关键在于``` @RequestMapping```注解的```produces = {"text/html"}```属性上

#### 1）、返回一个错误页面，如404、500等。

* #####  有模板引擎的情况（可以用于渲染页面）

项目中使用的了模板引擎，如：thymeleaf 、freemarker等做为页面的渲染时。在templates创建/error文件夹并添加错误的状态码对应的.html文件，如下图：

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200422225905.png)

这里的404和500就是确定的错误状态码，而4xx表示其他的4开头的错误，如400，401等。当然可以为每一个状态码都设置对应的错误页面，但是这样做，并没有什么好处，所以就直接使用4xx.html这样的泛指代替了。

可以在我们错误页面中获取到如下信息（就是ModelAndView对象中的内容）：

| 字段名    | 说明       |
| --------- | ---------- |
| timstamp  | 时间戳     |
| status    | 错误状态码 |
| error     | 错误提示   |
| exception | 异常对象   |
| message   | 异常消息   |
| path      | 页面路径   |

> 细心的小伙伴会发现，这个其实就是当你用手机请求时返回的json内容

比如：在代码中加入上边信息，然后在在后端写一个错误代码：

```java
@RequestMapping("haserror")
@ResponseBody
public Object myError(){
  int i =10/0;
  return "something is error";
}
```

```html
这是一个错误页面：
<ul>
    <li>错误状态码：[[${status}]]</li>
    <li>错误消息：[[${error}]]</li>
    <li>异常对象：[[${exception}]]</li>
    <li>异常消息：[[${message}]]</li>
    <li>当前时间：[[${timestamp}]]</li>
</ul>
```

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200422231618.png)



* ##### 没有模板引擎的情况

当项目中没有使用模板引擎的时候，就将整个error文件夹移到static文件夹下就可以了。

不过此时并不能获取上边的那些信息了，因为这本就是静态资源，没有模板引擎进行渲染

#### 2）、返回对应的json串

这个并没有什么好说的，返回的就是一个json字符串。格式如下：

```json
{
"timestamp": "2020-04-22T16:13:37.506+0000",
"status": 500,
"error": "Internal Server Error",
"message": "/ by zero",
"path": "/hello/haserror",
"reason": "完了，你写的代码又产生了一次线上事故"
}
```

#### 3）、自定义页面返回信息

这才是最重要的内容，因为这个信息不仅是做为json返回的，也是可以在上边的错误页面中拿到，也可以直接返回一个json。其实也很简单，就是在Spring容器中添加一个```ErrorAttributes```对象就可以了，这里我选择继承它的一个子类。

```java
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
```

这就可以了，用两种请求方式分别测试一个我们的这个自定义属性是否可用：

![](https://gitee.com/lyn4ever/picgo-img/raw/master/img/20200422234555.png)

### 2. 使用AOP的异常通知进行处理（推荐）

它的原理就是获取一个全局的异常通知，然后进行处理。我们只需要在项目中写下边代码就可以了（其实上边也只是写了一个自定义异常信息的类）

```java
@ControllerAdvice
public class ErrroAcvice {

    /**
     * 全局捕获异常的切面类
     * @param request 请求对象，可不传
     * @param response 响应对象，可不传
     * @param e 异常类(这个要和你当前捕获的异常类是同一个)
     */
    @ExceptionHandler(Exception.class) //也可以只对一个类进行捕获
    public void errorHandler(HttpServletRequest request, HttpServletResponse response,Exception e){
      	/*
      	 * You can do everything you want to do
         * 这里你拿到了request和response对象，你可以做任何你想做的事
         * 比如：
         *	1.用request从头信息中拿到Accept来判断是请求方可接收的类型从而进行第一个方法的判断
         *	2.如果你也想返回一个页面，使用response对象进行重定向到自己的错误页面就可以了
         *  3.你甚至还拿到了异常对象
      	 */
      
        String accept = request.getHeader("Accept");
				// 根据这个字符串来判断做	
      
        try {
            response.setStatus(500);
            response.getWriter().write("hello");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
      
    }
}
```

 ### 3. 两种方法对比：

* 第一种方法，就是在当前项目中放置一些错误状态码的页面让SpringBoot去查找。也支持自定义返回的错误信息
* 第二种方法，就是直接使用AOP的思想，进行异常通知处理，自由性很大。
* 我个人建议使用第二种方法，因为自由度很高，可以根据自己的业务逻辑进行随时改变，而且还有一个很大的用处。下一篇文章会有个很好的例子
* 使用了第二种方式后，通过第一种方式放置的错误页面和自定义错误信息全部失效