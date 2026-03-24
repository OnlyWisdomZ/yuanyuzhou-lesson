package com.ming.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** @author Ming */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class CoursePageDTO extends PageDTO {

    @Size(min = 1, max = 42, message = "搜索关键字必须在1~42之间")
	@Schema(description = "搜索关键字，比如课程名称或作者名称")
	private String keyword;
}
