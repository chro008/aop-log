package com.aispeech.aistore.tp.admin.log.aop;

import com.aispeech.aistore.tp.admin.log.LogRecordExpressionEvaluator;
import com.aispeech.aistore.tp.admin.log.annotations.LogRecordAnnotation;
import com.aispeech.aistore.tp.admin.log.dto.OperatorDto;
import com.aispeech.aistore.tp.admin.log.po.OperateLogPo;
import com.aispeech.aistore.tp.admin.log.service.IOperateLogService;
import com.aispeech.aistore.tp.admin.log.service.IOperatorService;
import com.alibaba.fastjson.JSON;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.expression.EvaluationContext;
import org.springframework.util.StringUtils;

@Aspect
public class LogRecordAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogRecordAspect.class);

    private LogRecordExpressionEvaluator expressionEvaluator = new LogRecordExpressionEvaluator();

    @Autowired
    IOperatorService operatorService;

    @Autowired(required = false)
    IOperateLogService operateLogService;

    @Value("${spring.application.name:unknown}")
    private String applicationName;

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

        // 接口请求出错，将错误抛出，如接口执行成功则记录日志
        if (Objects.nonNull(methodException)) {
            // todo 测试完毕后删除
            doLog(joinPoint, requestTime);
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
            logger.debug("logRecord parse expression start, express is {}", annotation.template());
            String logContent = getTemplateParseResult(joinPoint, annotation.template());
            if (!StringUtils.hasText(logContent)) {
                logger.warn("logRecord empty logContent");
                return;
            }
            OperateLogPo operateLogPo = new OperateLogPo();
            OperatorDto operator = operatorService.getOperator();
            operateLogPo.setSystem(applicationName);
            operateLogPo.setTenantId(0L);
            operateLogPo.setUserId(operator.getId());
            operateLogPo.setUserName(operator.getName());
            operateLogPo.setOrgIdPath(operator.getOrgIdPath());
            operateLogPo.setOrgNamePath(operator.getOrgNamePath());
            operateLogPo.setModuleId(0L);
            operateLogPo.setModuleName("");
            operateLogPo.setBizId(annotation.bizId());
            if (StringUtils.hasText(annotation.categoryStr())) {
                operateLogPo.setCategory(annotation.categoryStr());
            } else {
                operateLogPo.setCategory(annotation.category().getName());
            }
            operateLogPo.setDetail(logContent);
            operateLogPo.setTime(requestTime);
            logger.info("logRecord a new operate log: {}", JSON.toJSONString(operateLogPo));
            operateLogService.createLog(operateLogPo);
        } catch (Exception e) {
            logger.error("logRecord error: " + e.getMessage(), e);
        }
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
