package com.ming.feign;
import com.ming.entity.Course;
import com.ming.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** @author Ming */
@FeignClient("ml-course")
public interface CourseFeign {

    @GetMapping("/api/v1/course/select/{id}")
    Result<Course> select(@PathVariable("id") Long id);
}