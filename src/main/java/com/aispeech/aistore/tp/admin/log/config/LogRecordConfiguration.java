package com.aispeech.aistore.tp.admin.log.config;

import com.aispeech.aistore.tp.admin.log.aop.LogRecordAspect;
import com.aispeech.aistore.tp.admin.log.register.CustomerFunctionRegister;
import com.aispeech.aistore.tp.admin.log.service.IOperateLogService;
import com.aispeech.aistore.tp.admin.log.service.IOperatorService;
import com.aispeech.aistore.tp.admin.log.service.impl.DefaultOperateLogService;
import com.aispeech.aistore.tp.admin.log.service.impl.DefaultOperatorService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class LogRecordConfiguration {

    @Bean
    @ConditionalOnMissingBean(IOperatorService.class)
    IOperatorService operatorService() {
        return new DefaultOperatorService();
    }

    @Bean
    @ConditionalOnMissingBean
    IOperateLogService operateLogService() {
        return new DefaultOperateLogService();
    }

    @Bean
    public CustomerFunctionRegister registrar() {
        return new CustomerFunctionRegister();
    }

    @Bean
    public LogRecordAspect logRecordAspect() {
        return new LogRecordAspect();
    }
}
