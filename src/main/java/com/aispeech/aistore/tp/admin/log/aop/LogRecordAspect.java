package com.aispeech.aistore.tp.admin.log.aop;

import com.aispeech.aistore.tp.admin.log.LogRecordExpressionEvaluator;
import com.aispeech.aistore.tp.admin.log.annotations.LogRecordAnnotation;
import com.aispeech.aistore.tp.admin.log.annotations.LogRecordBizBean;
import com.aispeech.aistore.tp.admin.log.enums.OperateCategoryEnum;
import com.aispeech.aistore.tp.admin.log.service.LogAspectService;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StringUtils;

@Aspect
public class LogRecordAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogRecordAspect.class);

    private LogRecordExpressionEvaluator expressionEvaluator = new LogRecordExpressionEvaluator();

    @Autowired
    LogAspectService logAspectService;

    @Pointcut("@annotation(com.aispeech.aistore.tp.admin.log.annotations.LogRecordAnnotation)")
    public void logRecord() {
        // no logic
    }

    @Around(value = "logRecord()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Date requestTime = new Date();
        logger.debug("logRecord arount in...");
        Object result = null;
        Exception methodException = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            methodException = e;
        }

        // 接口请求出错，将错误抛出，否则表示接口执行成功则记录日志
        if (Objects.nonNull(methodException)) {
            throw methodException;
        } else {
            doLog(joinPoint, requestTime);
            return result;
        }
    }

    /**
     * 解析模版日志，持久化
     *
     * @param joinPoint
     */
    private void doLog(ProceedingJoinPoint joinPoint, Date requestTime) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            LogRecordAnnotation annotation = signature.getMethod().getAnnotation(LogRecordAnnotation.class);
            // 日志模版
            String template = annotation.template();
            // 业务对象
            Class<?> bizBeanClass = annotation.bizBeanClass();
            // 日志内容优先从日志模板解析，否则就按默认规则拼接操作日志
            String logContent = null;
            OperateCategoryEnum category = annotation.category();
            if (StringUtils.hasText(template)) {
                logger.debug("logRecord parse expression start, express is {}", template);
                logContent = getTemplateParseResult(joinPoint, template);
            } else if (bizBeanClass.isAnnotationPresent(LogRecordBizBean.class)) {
                logger.debug("logRecord parse bizeBean start, bean is {}", bizBeanClass.getName());
                logContent = getBizBeanParseResult(joinPoint, annotation, bizBeanClass);
            }

            if (!StringUtils.hasText(logContent)) {
                logger.warn("logRecord empty logContent");
                return;
            }

            logAspectService.persistantLog(category, logContent, requestTime);
        } catch (Exception e) {
            logger.error("logRecord error: " + e.getMessage(), e);
        }
    }

    /**
     * 解析业务对象
     *
     * @param joinPoint
     * @param annotation
     * @param bizBeanClass
     * @return
     */
    private String getBizBeanParseResult(ProceedingJoinPoint joinPoint, LogRecordAnnotation annotation, Class<?> bizBeanClass) {
        LogRecordBizBean bizBean = bizBeanClass.getAnnotation(LogRecordBizBean.class);
        String nameField = bizBean.nameField();
        String nameFlag = "";
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (!bizBeanClass.isAssignableFrom(arg.getClass())) {
                continue;
            }
            try {
                Field field = arg.getClass().getDeclaredField(nameField);
                field.setAccessible(true);
                nameFlag = field.get(arg).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        OperateCategoryEnum category = annotation.category();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(category.getName()).append(bizBean.moduleShortName());
        if (StringUtils.hasText(nameFlag)) {
            stringBuilder.append("\"").append(nameFlag).append("\"");
        }
        return stringBuilder.toString();
    }

    /**
     * 解析模版，返回日志内容
     *
     * @param template
     * @return
     */
    private String getTemplateParseResult(JoinPoint joinPoint, String template) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = target.getClass();
        AnnotatedElementKey annotatedElementKey = new AnnotatedElementKey(method, targetClass);
        EvaluationContext context = expressionEvaluator.createEvaluationContext(target, targetClass, method, args);
        return expressionEvaluator.parseExpression(template, annotatedElementKey, context);
    }
}
