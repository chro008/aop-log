package com.aispeech.aistore.tp.admin.log.service;

import com.aispeech.aistore.tp.admin.log.annotations.LogRecordBizBean;
import com.aispeech.aistore.tp.admin.log.dto.OperatorDto;
import com.aispeech.aistore.tp.admin.log.enums.OperateCategoryEnum;
import com.aispeech.aistore.tp.admin.log.po.OperateLogPo;
import com.alibaba.fastjson.JSON;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LogAspectService {

    private static Logger logger = LoggerFactory.getLogger(LogAspectService.class);

    @Autowired
    IOperatorService operatorService;

    @Autowired(required = false)
    IOperateLogService operateLogService;

    @Value("${spring.application.name:unknown}")
    private String applicationName;

    public void log(OperateCategoryEnum category, Object bizObj) {
        try {
            Date requestTime = new Date();
            LogRecordBizBean bizBean = bizObj.getClass().getAnnotation(LogRecordBizBean.class);
            if (Objects.isNull(bizBean)) {
                return;
            }
            String nameField = bizBean.nameField();
            String nameFlag = "";
            try {
                Field field = bizObj.getClass().getDeclaredField(nameField);
                field.setAccessible(true);
                nameFlag = field.get(bizObj).toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(category.getName()).append(bizBean.moduleShortName());
            if (StringUtils.hasText(nameFlag)) {
                stringBuilder.append("\"").append(nameFlag).append("\"");
            }
            String logContent = stringBuilder.toString();
            persistantLog(category, logContent, requestTime);
        } catch (Exception e) {
            logger.error("operate log record error: " + e.getMessage(), e);
        }
    }

    public void persistantLog(OperateCategoryEnum category, String logContent, Date requestTime) {
        OperateLogPo operateLogPo = generateOperateLogPo();
        operateLogPo.setCategory(category.getName());
        operateLogPo.setDetail(logContent);
        operateLogPo.setTime(requestTime);

        operateLogPo.setTenantId(0L);
        operateLogPo.setModuleId(0L);
        operateLogPo.setModuleName("");
        //operateLogPo.setBizId(annotation.bizId());

        logger.info("logRecord a new operate log: {}", JSON.toJSONString(operateLogPo));
        operateLogService.createLog(operateLogPo);
    }

    private OperateLogPo generateOperateLogPo() {
        OperateLogPo operateLogPo = new OperateLogPo();
        OperatorDto operator = operatorService.getOperator();
        operateLogPo.setSystem(applicationName);

        operateLogPo.setUserId(operator.getId());
        operateLogPo.setUserName(operator.getName());
        operateLogPo.setOrgIdPath(operator.getOrgIdPath());
        operateLogPo.setOrgNamePath(operator.getOrgNamePath());

        return operateLogPo;
    }


}
