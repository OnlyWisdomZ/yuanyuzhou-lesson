package com.ming.vo;

import com.ming.dto.PageDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/** @author Ming */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class CoursePageVO<T> extends PageDTO {
    private Long totalRow;
    private Integer totalPage;
    private List<T> records;
}
