package com.ming.service;

import com.ming.dto.CoursePageDTO;
import com.ming.es.CourseDoc;
import com.ming.vo.CoursePageVO;

/** @author Ming */
public interface CourseService {

    /**
     * 分页搜索课程记录
     *
     * @param dto  课程搜索DTO
     * @return 搜索结果
     */
    CoursePageVO<CourseDoc> search(CoursePageDTO dto);
}
