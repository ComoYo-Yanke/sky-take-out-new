package com.sky.aspect;

import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Aspect
@Component
@Slf4j
public class ToolAspect {
    
    @Pointcut("execution(* com.sky.controller.admin.*.*(..))")
    public void toolPointCut(){}
    
    @After("toolPointCut()")
    public void cue(){
        log.info("当前操作信息: id-{},time-{}",
                BaseContext.getCurrentId(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
