package com.aispeech.aistore.tp.admin.log.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogRecordBizBean {

    /**
     * 模块名称
     *
     * @return
     */
    String moduleName() default "";

    /**
     * 模块简称
     *
     * @return
     */
    String moduleShortName() default "";

    /**
     * 业务名称字段
     *
     * @return
     */
    String nameField() default "";

    /**
     * 租户id字段
     *
     * @return
     */
    String tenantIdField() default "";

}
