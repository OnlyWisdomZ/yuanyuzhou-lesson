package com.ming.controller;

import com.ming.dto.CourseInsertDTO;
import com.ming.dto.CoursePageDTO;
import com.ming.dto.CourseUpdateDTO;
import com.ming.result.Result;
import com.ming.vo.CourseSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ming.entity.Course;
import com.ming.service.CourseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程表 控制层。
 *
 * @author Ming
 * @since v1.0.0
 */
@RestController
@Tag(name = "课程表接口")
@RequestMapping("/course")
public class CourseController {
    @Resource
    private CourseService courseService;

    @Operation(summary = "新增 - 单条新增", description = "新增一条课程记录")
    @PostMapping("insert")
    public boolean insert(@Validated @RequestBody CourseInsertDTO dto) {
        return courseService.insert(dto);
    }

    @Operation(summary = "查询 - 单条查询", description = "按主键查询一条课程记录")
    @GetMapping("select/{id}")
    public Course select(@PathVariable("id") Long id) {
        return courseService.select(id);
    }

    @Operation(summary = "查询 - 简单列表", description = "查询全部课程记录，仅返回简单信息")
    @GetMapping("simpleList")
    public List<CourseSimpleListVO> simpleList() {
        return courseService.simpleList();
    }

    @Operation(summary = "查询 - 分页查询", description = "分页查询课程记录")
    @GetMapping("page")
    public Page<Course> page(@Validated CoursePageDTO dto) {
        return courseService.page(dto);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条课程记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody CourseUpdateDTO dto) {
        return courseService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条课程记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return courseService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除课程记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return courseService.deleteBatch(ids);
    }
    @Operation(summary = "修改 - 课程封面", description = "按主键修改课程的封面图片")
    @PostMapping("/uploadCover/{id}")
    public Result<String> uploadCover(@RequestParam("coverFile") MultipartFile coverFile,
                                      @PathVariable("id") Long id) {
        return new Result<>(courseService.uploadCover(coverFile, id));
    }
    @Operation(summary = "修改 - 课程摘要", description = "按主键修改课程的摘要图片")
    @PostMapping("/uploadSummary/{id}")
    public Result<String> uploadSummary(@RequestParam("summaryFile") MultipartFile summaryFile,
                                        @PathVariable("id") Long id) {
        return new Result<>(courseService.uploadSummary(summaryFile, id));
    }

}
