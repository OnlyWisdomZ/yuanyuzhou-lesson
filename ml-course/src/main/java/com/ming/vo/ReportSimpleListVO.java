package com.ming.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** @author Ming */
@Schema(name = "举报全查VO")
@Data
public class ReportSimpleListVO implements Serializable {
    @Schema(description = "主键")  
    private Long id;  
    @Schema(description = "举报内容")  
    private String content;  
}