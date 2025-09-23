package com.example.code.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
/// 权限校验
public @interface AuthCheck {

    /**
     * 必须有某个角色
     */
    String mustRole() default "";
}
