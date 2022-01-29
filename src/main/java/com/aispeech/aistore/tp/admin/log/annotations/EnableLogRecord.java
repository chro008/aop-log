package com.aispeech.aistore.tp.admin.log.annotations;

import com.aispeech.aistore.tp.admin.log.config.LogRecordConfiguration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LogRecordConfiguration.class)
public @interface EnableLogRecord {
}
