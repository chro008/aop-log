package com.aispeech.aistore.tp.admin.log.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class OperatorDto implements Serializable {
    String id;
    String name;
    String orgIdPath;
    String orgNamePath;
}
