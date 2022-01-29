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
    BATCH_EXPORT("批量导出"),
    ALL_EXPORT("全部导出"),
    DOWNLOAD("下载"),
    IMPORT("导入"),
    UPLOAD("上传"),
    DISTRIBUTE("分配"),
    CANCEL_DISTRIBUTE("取消分配"),
    REFUSE("拒绝"),
    SUBMIT("提交"),
    REFRESH("刷新结果"),
    REVIEW("人工复核"),
    EXAMINE("审核");

    private String name;

    OperateCategoryEnum(String name) {
        this.name = name;
    }
}
