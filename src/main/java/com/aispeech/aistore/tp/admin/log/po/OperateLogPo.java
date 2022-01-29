package com.aispeech.aistore.tp.admin.log.po;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class OperateLogPo implements Serializable {

    /**
     * keyword 日志所属系统
     */
    private String system;

    /**
     * long 多租户ID
     */
    private long tenantId;

    /**
     * keyword 用户ID
     */
    private String userId;

    /**
     * keyword 用户姓名
     */
    private String userName;

    /**
     * keyword 用户组织架构ID路径
     */
    private String orgIdPath;

    /**
     * keyword 用户组织架构名称路径
     */
    private String orgNamePath;

    /**
     * long 操作模块ID
     */
    private long moduleId;

    /**
     * keyword 模块名称
     */
    private String moduleName;

    /**
     * keyword 操作对象业务ID
     */
    private String bizId;

    /**
     * keyword 操作种类
     */
    private String category;

    /**
     * text 操作详细信息
     */
    private String detail;

    /**
     * date 操作时间
     */
    private Date time;

}
