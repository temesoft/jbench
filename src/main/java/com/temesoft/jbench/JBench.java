package com.temesoft.jbench;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a class for benchmarking
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JBench {
    /**
     * Defines the number of benchmark iterations for given method
     * Default number of iterations is 1 for individual single benchmark execution
     *
     * @return - long
     */
    long maxIterations() default 1;

}
