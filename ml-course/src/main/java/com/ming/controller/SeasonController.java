package com.ming.controller;

import com.ming.dto.SeasonInsertDTO;
import com.ming.dto.SeasonPageDTO;
import com.ming.dto.SeasonUpdateDTO;
import com.ming.vo.SeasonSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ming.entity.Season;
import com.ming.service.SeasonService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 季次表 控制层。
 *
 * @author Ming
 * @since v1.0.0
 */
@RestController
@Tag(name = "季次表接口")
@RequestMapping("/season")
public class SeasonController {

    @Resource
    private SeasonService seasonService;

    @Operation(summary = "新增 - 单条新增", description = "新增一条季次记录")
    @PostMapping("insert")
    public boolean insert(@Validated @RequestBody SeasonInsertDTO dto) {
        return seasonService.insert(dto);
    }

    @Operation(summary = "查询 - 单条查询", description = "按主键查询一条季次记录")
    @GetMapping("select/{id}")
    public Season select(@PathVariable("id") Long id) {
        return seasonService.select(id);
    }

    @Operation(summary = "查询 - 简单列表", description = "查询全部季次记录，仅返回简单信息")
    @GetMapping("simpleList")
    public List<SeasonSimpleListVO> simpleList() {
        return seasonService.simpleList();
    }

    @Operation(summary = "查询 - 分页查询", description = "分页查询季次记录")
    @GetMapping("page")
    public Page<Season> page(@Validated SeasonPageDTO dto) {
        return seasonService.page(dto);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条季次记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody SeasonUpdateDTO dto) {
        return seasonService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条季次记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return seasonService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除季次记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return seasonService.deleteBatch(ids);
    }
}
