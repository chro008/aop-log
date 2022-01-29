package com.aispeech.aistore.tp.admin.log.enums;

import lombok.Getter;

/**
 * 操作类型枚举类
 */
@Getter
public enum OperateCategoryEnum {
    /**
     * name为操作中文释义
     */
    ADD("新增"),
    DELETE("删除"),
    MODIFY("修改"),
    PUBLISH("发布"),
    LOGIN("登录"),
    EXPORT("导出"),
    IMPORT("导入"),
    DISTRIBUTE("复核分配"),
    REFRESH("刷新结果"),
    REVIEW("人工复核"),
    EXAMINE("审核"),
    UNKNOW("未知");

    private String name;

    OperateCategoryEnum(String name) {
        this.name = name;
    }
}
