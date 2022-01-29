package com.aispeech.aistore.tp.admin.log;

import com.aispeech.aistore.tp.admin.log.register.CustomerFunctionRegister;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;

/**
 * spring表达式解析类
 */
public class LogRecordExpressionEvaluator extends CachedExpressionEvaluator {

    private ParameterNameDiscoverer paramNameDiscoverer = new DefaultParameterNameDiscoverer();

    private Map<ExpressionKey, Expression> expressionCache = new ConcurrentHashMap<>(64);

    private Map<AnnotatedElementKey, Method> methodCache = new ConcurrentHashMap<>(64);

    public String parseExpression(String template, AnnotatedElementKey elementKey, EvaluationContext evaluationContext) {
        return getExpression(expressionCache, elementKey, template).getValue(evaluationContext, String.class);
    }

    public EvaluationContext createEvaluationContext(Object object, Class<?> targetClass, Method method, Object[] args) {
        AnnotatedElementKey elementKey = new AnnotatedElementKey(method, targetClass);
        Method targetMethod = methodCache.get(elementKey);
        if (Objects.isNull(targetMethod)) {
            targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            methodCache.put(elementKey, targetMethod);
        }
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(object, targetMethod, args, paramNameDiscoverer);
        // 注入自定义函数
        CustomerFunctionRegister.register(context);
        return context;
    }

}
