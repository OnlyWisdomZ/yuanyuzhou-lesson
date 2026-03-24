package com.ming.controller;

import com.ming.dto.SeckillDetailInsertDTO;
import com.ming.dto.SeckillDetailPageDTO;
import com.ming.dto.SeckillDetailUpdateDTO;
import com.ming.vo.SeckillDetailSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ming.entity.SeckillDetail;
import com.ming.service.SeckillDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 秒杀明细表 控制层。
 *
 * @author Ming
 * @since v1.0.0
 */
@RestController
@Tag(name = "秒杀明细表接口")
@RequestMapping("/seckillDetail")
public class SeckillDetailController {

    @Resource
    private SeckillDetailService seckillDetailService;

    @Operation(summary = "新增 - 单条新增", description = "新增一条秒杀明细记录")
    @PostMapping("insert")
    public boolean insert(@Validated @RequestBody SeckillDetailInsertDTO dto) {
        return seckillDetailService.insert(dto);
    }

    @Operation(summary = "查询 - 单条查询", description = "按主键查询一条秒杀明细记录")
    @GetMapping("select/{id}")
    public SeckillDetail select(@PathVariable("id") Long id) {
        return seckillDetailService.select(id);
    }

    @Operation(summary = "查询 - 简单列表", description = "查询全部秒杀明细记录，仅返回简单信息")
    @GetMapping("simpleList")
    public List<SeckillDetailSimpleListVO> simpleList() {
        return seckillDetailService.simpleList();
    }

    @Operation(summary = "查询 - 分页查询", description = "分页查询秒杀明细记录")
    @GetMapping("page")
    public Page<SeckillDetail> page(@Validated SeckillDetailPageDTO dto) {
        return seckillDetailService.page(dto);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条秒杀明细记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody SeckillDetailUpdateDTO dto) {
        return seckillDetailService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条秒杀明细记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return seckillDetailService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除秒杀明细记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return seckillDetailService.deleteBatch(ids);
    }


}
