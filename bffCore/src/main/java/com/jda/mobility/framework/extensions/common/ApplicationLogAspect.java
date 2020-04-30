/**
 * 
 */
package com.jda.mobility.framework.extensions.common;

import java.util.Arrays;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

/**
 * @author HCL Technologies Ltd.
 *
 */
@Aspect
@Component
public class ApplicationLogAspect {
    @Pointcut("within(com.jda.mobility.framework.extensions.service.impl..* ||"
    		+ "com.jda.mobility.framework.extensions.transformation..* ||"
    		+ "com.jda.mobility.framework.extensions.controller..* ||"
    		+ "com.jda.mobility.framework.extensions.repository..*)")
    private void inMethodInvocation() {
    	//defining PointCut invocation expression

    }
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Before("inMethodInvocation()")
    public void logMethodInvocation(JoinPoint jp) {
        Class<?> classObj = jp.getSourceLocation().getWithinType();
        String methodName = jp.getSignature().getName();
        String messageString = Arrays.toString(jp.getArgs());

        Logger logger = LogManager.getLogger(classObj);
        logger.log(Level.TRACE, "--------------------------------------------------------------------------------");
        logger.log(Level.TRACE, "Invoking: {}.{} with parameters {}", classObj.getSimpleName(), methodName, messageString);
    }
    
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @AfterReturning(pointcut = "inMethodInvocation()", returning = "result")
    public void logMethodExit(JoinPoint jp, Object result) {
        Class<?> classObj = jp.getSourceLocation().getWithinType();
        String methodName = jp.getSignature().getName();

        Logger logger = LogManager.getLogger(classObj);
        logger.log(Level.TRACE, "Exiting: {}.{}", classObj.getSimpleName(), methodName);
        logger.log(Level.TRACE, "--------------------------------------------------------------------------------");
    }
    
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @AfterThrowing(pointcut = "inMethodInvocation()", throwing = "exp")
    public void logMethodException(JoinPoint jp, Exception exp) {
        Class<?> classObj = jp.getSourceLocation().getWithinType();
        String methodName = jp.getSignature().getName();

        Logger logger = LogManager.getLogger(classObj);
		logger.log(Level.ERROR, "Exception occured for {} method {}", classObj.getSimpleName(), methodName, exp);
    }
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @AfterThrowing(pointcut = "inMethodInvocation()", throwing = "exp")
    public void logMethodDbException(JoinPoint jp, DataAccessException exp) {
        Class<?> classObj = jp.getSourceLocation().getWithinType();
        String methodName = jp.getSignature().getName();

        Logger logger = LogManager.getLogger(classObj);
        logger.log(Level.ERROR, "Exception occured for {} method {}", classObj.getSimpleName(), methodName, exp);
    }
   
}
