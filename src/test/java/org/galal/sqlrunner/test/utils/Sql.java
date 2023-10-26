package org.galal.sqlrunner.test.utils;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Runs a sql script before or after test methods , based on spring boot's @Sql annotation, but uses CDI
 * injection instead.
 * LIMITATIONS: for some reason, it only intercept when applied on methods, the interceptor doesn't work in it is
 * applied on the bean class.
 * */
@Inherited
@InterceptorBinding
@Target({METHOD, TYPE})
@Retention(RUNTIME)
@Repeatable(Sql.List.class)
public @interface Sql {
    //MUST USE  @Nonbinding , or the interceptor won't work for some reason
    @Nonbinding
    ExecutionPhase executionPhase() default ExecutionPhase.BEFORE_TEST_METHOD;
    @Nonbinding
    String[] scripts() default {};

    @Target({ TYPE, METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    @interface List {
        Sql[] value();
    }



    enum ExecutionPhase {
        /**
         * The configured SQL scripts and statements will be executed
         * <em>before</em> the corresponding test method.
         */
        BEFORE_TEST_METHOD,

        /**
         * The configured SQL scripts and statements will be executed
         * <em>after</em> the corresponding test method.
         */
        AFTER_TEST_METHOD
    }
}
