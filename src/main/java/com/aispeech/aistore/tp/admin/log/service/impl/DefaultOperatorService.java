package com.aispeech.aistore.tp.admin.log.service.impl;

import com.aispeech.aistore.tp.admin.log.dto.OperatorDto;
import com.aispeech.aistore.tp.admin.log.service.IOperatorService;

import org.slf4j.MDC;

/**
 * 默认的操作人实现类
 */
public class DefaultOperatorService implements IOperatorService {

    @Override
    public OperatorDto getOperator() {
        String operatorId = MDC.get("__operator_id");
        String operatorName = MDC.get("__operator_name");
        String orgIdPath = MDC.get("__operator_org_id_path");
        String orgNamePath = MDC.get("__operator_org_name_path");
        OperatorDto operatorDto = new OperatorDto();
        operatorDto.setId(operatorId);
        operatorDto.setName(operatorName);
        operatorDto.setOrgIdPath(orgIdPath);
        operatorDto.setOrgNamePath(orgNamePath);
        return operatorDto;
    }
}
