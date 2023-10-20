package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 自定义切面、实现公共字段自动填充功能逻辑
 */
@Aspect
@Component    //交给IOC容器管理
@Slf4j
public class AutoFillAspect {
    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {

    }
    /**
     * 前置通知：在通知中进行公共字段的填充
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) { //连接点joinPoint暗含了方法执行书的相关信息
        log.info("公共字段的填充!");

        //1.获取当前被拦截的方法上的数据库操作类型
        /**
         * 之前弄得注解就相当于一个标识，现在通过反射来拿到注解的标识
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();//方法签名对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);//获取方法上的注解对象
        OperationType operationType = autoFill.value(); //获取数据库的操作类型

        //2.获取当前被拦截的方法的参数-实体对象   //约定参数的第一个为实体对象
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0) return;
        Object entity = args[0];

        //3.准备赋值的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //4.根据不同的操作类型，为对应的属性通过反射来赋值
        if(operationType == OperationType.INSERT){
            //插入操作、四个字段赋值
            try {
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpDateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpDateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                //通过反射赋值
                setCreateTime.invoke(entity,now);
                setCreateUser.invoke(entity,currentId);
                setUpDateUser.invoke(entity,currentId);
                setUpDateTime.invoke(entity,now);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (operationType == OperationType.UPDATE) {
            //更新操作、两个字段赋值
            try {
                Method setUpDateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpDateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
                setUpDateUser.invoke(entity,currentId);
                setUpDateTime.invoke(entity,now);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
