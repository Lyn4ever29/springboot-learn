package cn.lyn4ever.learn.springboot.aop.basic;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 切面类，用来写切入点和通知方法
 */
@Component
@Aspect
public class AdvisorBean {
    /*
    切入点
     */
    @Pointcut("execution(* teach*(..))")
    public void teachExecution() {
    }

    /************以下是配置通知类型，可以是多个************/
    @Around("teachExecution()")
    public Object beforeAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        args[0] = ".....你们体育老师生病了，我们开始上英语课";
        Object proceed = joinPoint.proceed(args);

        return proceed;
    }
}
