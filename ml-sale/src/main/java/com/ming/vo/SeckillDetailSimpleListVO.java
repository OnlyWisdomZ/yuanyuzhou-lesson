package com.ming.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** @author Ming */
@Schema(name = "秒杀活动明细全查VO")
@Data
public class SeckillDetailSimpleListVO implements Serializable {
    @Schema(description = "主键")  
    private Long id;  
    @Schema(description = "课程标题")  
    private String courseTitle;  
}