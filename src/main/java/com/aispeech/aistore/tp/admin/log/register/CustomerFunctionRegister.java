package com.aispeech.aistore.tp.admin.log.register;

import com.aispeech.aistore.tp.admin.log.annotations.LogRecordFunction;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomerFunctionRegister implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CustomerFunctionRegister.class);

    private static List<Method> functions = new ArrayList<>();

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        logger.debug("regist customer functions start...");
        Map<String, Object> customerFunctionMap = applicationContext.getBeansWithAnnotation(LogRecordFunction.class);
        for (Map.Entry<String, Object> entry : customerFunctionMap.entrySet()) {
            String className = entry.getKey();
            Arrays.stream(entry.getValue().getClass().getMethods()).filter(item -> item.isAnnotationPresent(LogRecordFunction.class))
                    .forEach(item -> {
                        logger.debug("regist customer functions -> {}-{}", className, item.getName());
                        functions.add(item);
                    });
        }
        logger.debug("regist customer functions finish...");
    }

    public static void register(StandardEvaluationContext context) {
        functions.forEach(item -> context.registerFunction(item.getName(), item));
    }

}
