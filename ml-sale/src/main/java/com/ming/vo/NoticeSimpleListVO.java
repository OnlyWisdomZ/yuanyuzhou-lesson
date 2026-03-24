package com.ming.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/** @author Ming */
@Schema(name = "通知全查VO")
@Data
public class NoticeSimpleListVO implements Serializable {
    @Schema(description = "主键")
    private Long id;
    @Schema(description = "通知内容")
    private String content;
}