package com.ming.controller;

import com.ming.dto.CoursePageDTO;
import com.ming.es.CourseDoc;
import com.ming.service.CourseService;
import com.ming.vo.CoursePageVO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author Ming */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @GetMapping("search")
    public CoursePageVO<CourseDoc> search(@Validated CoursePageDTO dto) {
        return courseService.search(dto);
    }
}
