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
    OperateCategoryEnum category() default OperateCategoryEnum.UNKNOW;

    /**
     * 和category含义相同，代表操作类型，优先级高于category，方便客户端自定义扩展
     *
     * @return
     */
    String categoryStr() default "";

    /**
     * 操作业务对象唯一标识
     *
     * @return
     */
    String bizId() default "";

    /**
     * 操作详情模版内容
     *
     * @return
     */
    String template() default "";

}
