package cn.lyn4ever.learn.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class SpringbootLearnApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringbootLearnApplication.class, args);
	}

}
