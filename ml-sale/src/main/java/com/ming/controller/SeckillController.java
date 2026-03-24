package com.ming.controller;

import com.ming.dto.SeckillInsertDTO;
import com.ming.dto.SeckillPageDTO;
import com.ming.dto.SeckillUpdateDTO;
import com.ming.vo.SeckillSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ming.entity.Seckill;
import com.ming.service.SeckillService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 秒杀表 控制层。
 *
 * @author Ming
 * @since v1.0.0
 */
@RestController
@Tag(name = "秒杀表接口")
@RequestMapping("/seckill")
public class SeckillController {

    @Resource
    private SeckillService seckillService;

    @Operation(summary = "新增 - 单条新增", description = "新增一条秒杀记录")
    @PostMapping("insert")
    public boolean insert(@Validated @RequestBody SeckillInsertDTO dto) {
        return seckillService.insert(dto);
    }

    @Operation(summary = "查询 - 单条查询", description = "按主键查询一条秒杀记录")
    @GetMapping("select/{id}")
    public Seckill select(@PathVariable("id") Long id) {
        return seckillService.select(id);
    }

    @Operation(summary = "查询 - 简单列表", description = "查询全部秒杀记录，仅返回简单信息")
    @GetMapping("simpleList")
    public List<SeckillSimpleListVO> simpleList() {
        return seckillService.simpleList();
    }

    @Operation(summary = "查询 - 分页查询", description = "分页查询秒杀记录")
    @GetMapping("page")
    public Page<Seckill> page(@Validated SeckillPageDTO dto) {
        return seckillService.page(dto);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条秒杀记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody SeckillUpdateDTO dto) {
        return seckillService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条秒杀记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return seckillService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除秒杀记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return seckillService.deleteBatch(ids);
    }
    @Operation(summary = "查询 - 近N条记录", description = "查询近N条秒杀记录")
    @GetMapping("near/{n}")
    public List<Seckill> near(@PathVariable("n") Long n) {
        return seckillService.near(n);
    }
}
