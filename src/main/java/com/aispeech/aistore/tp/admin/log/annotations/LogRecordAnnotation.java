package com.aispeech.aistore.tp.admin.log.annotations;

import com.aispeech.aistore.tp.admin.log.enums.OperateCategoryEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志记录注解类
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecordAnnotation {


    /**
     * 操作类型
     *
     * @return
     */
    OperateCategoryEnum category();

    /**
     * 操作业务对象唯一标识
     *
     * @return
     */
    String bizId() default "";

    /**
     * 日志记录业务对象，需要在业务对象上有 LogRecordBizBean 注解
     *
     * @return
     */
    Class<?> bizBeanClass() default Void.class;

    /**
     * 操作详情模版内容
     *
     * @return
     */
    String template() default "";

}
