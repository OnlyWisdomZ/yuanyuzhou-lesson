package com.ming.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** @author Ming */
@Schema(name = "横幅全查VO")
@Data
public class BannerSimpleListVO implements Serializable {
    @Schema(description = "主键")  
    private Long id;  
    @Schema(description = "轮播图")  
    private String url;  
}