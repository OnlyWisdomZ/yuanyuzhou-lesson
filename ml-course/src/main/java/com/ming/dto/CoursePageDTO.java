package com.ming.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** @author Ming */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(name = "课程分页DTO")
@Data
public class CoursePageDTO extends PageDTO {
    @Schema(description = "标题")
    private String title;
    @Schema(description = "类别ID，类别表外键")
    private Long fkCategoryId;
}