package com.temesoft.jbench;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for monitoring duration metrics of methods within classes annotated with @JBench
 * or
 */
@Component
@Aspect
public class JMethodMonitorAspect
{

    static
    {
        System.out.println(String.format(
                "%s - Monitoring duration metrics of methods within classes annotated with @%s",
                JMethodMonitorAspect.class.getSimpleName(),
                JMethodMonitor.class.getName()));
    }

    private static final JMethodMonitorService jMethodMonitorService = new JMethodMonitorService();

    @Around(value = "@annotation(annotation)")
    public Object meter(final ProceedingJoinPoint joinPoint, final JMethodMonitor annotation) throws Throwable
    {
        long ms = System.currentTimeMillis();
        final Object o = joinPoint.proceed(); //continue on the intercepted method
        ms = System.currentTimeMillis() - ms;
        if (joinPoint.getThis() != null)
        {
            String className = joinPoint.getThis().getClass().getName();

            int dollarIndex = className.indexOf("$$");
            if (dollarIndex > 0)
            {
                className = className.substring(0, dollarIndex);
            }
            jMethodMonitorService.addNewValues(
                    className,
                    joinPoint.getSignature().getName(),
                    ms);
        }
        return o;
    }

    @Around(value = "publicMethodInsideAClassMarkedWithJMethodMonitor() || publicStaticMethodInsideAClassMarkedWithJMethodMonitor()")
    public Object validateClassLevel(final ProceedingJoinPoint joinPoint) throws Throwable
    {
        if (joinPoint.getTarget() != null)
        {
            JMethodMonitor annotation = joinPoint.getTarget().getClass().getAnnotation(JMethodMonitor.class);
            if (annotation != null)
            {
                return meter(joinPoint, annotation);
            }
            else
            {
                return joinPoint.proceed();
            }
        }
        else
        {
            return joinPoint.proceed();
        }
    }

    // Pointcut all classes with annotation
    @Pointcut("within(@com.temesoft.jbench.JMethodMonitor *)")
    public void beanAnnotatedWithJMethodMonitor() {} // pointcuts must be empty

    // Pointcut all public methods
    @Pointcut("execution(public * *(..))")
    public void publicMethod() {} // pointcuts must be empty

    // Pointcut all public static methods
    @Pointcut("execution(static public * *(..))")
    public void publicStaticMethod() {} // pointcuts must be empty

    // Pointcut combination of beanAnnotatedWithVerifyRequiredServices and its publicMethod
    @Pointcut("publicMethod() && beanAnnotatedWithJMethodMonitor()")
    public void publicMethodInsideAClassMarkedWithJMethodMonitor() {} // pointcuts must be empty

    // Pointcut combination of beanAnnotatedWithVerifyRequiredServices and its publicMethod
    @Pointcut("publicStaticMethod() && beanAnnotatedWithJMethodMonitor()")
    public void publicStaticMethodInsideAClassMarkedWithJMethodMonitor() {} // pointcuts must be empty
}
