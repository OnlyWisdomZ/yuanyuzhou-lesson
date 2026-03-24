package com.ming.controller;

import com.ming.dto.NoticeInsertDTO;
import com.ming.dto.NoticePageDTO;
import com.ming.dto.NoticeUpdateDTO;
import com.ming.vo.NoticeSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ming.entity.Notice;
import com.ming.service.NoticeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 通知表 控制层。
 *
 * @author Ming
 * @since v1.0.0
 */
@RestController
@Tag(name = "通知表接口")
@RequestMapping("/notice")
public class NoticeController {

    @Resource
    private NoticeService noticeService;

    @Operation(summary = "新增 - 单条新增", description = "新增一条通知记录")
    @PostMapping("insert")
    public boolean insert(@Validated @RequestBody NoticeInsertDTO dto) {
        return noticeService.insert(dto);
    }

    @Operation(summary = "查询 - 单条查询", description = "按主键查询一条通知记录")
    @GetMapping("select/{id}")
    public Notice select(@PathVariable("id") Long id) {
        return noticeService.select(id);
    }

    @Operation(summary = "查询 - 简单列表", description = "查询全部通知记录，仅返回简单信息")
    @GetMapping("simpleList")
    public List<NoticeSimpleListVO> simpleList() {
        return noticeService.simpleList();
    }

    @Operation(summary = "查询 - 分页查询", description = "分页查询通知记录")
    @GetMapping("page")
    public Page<Notice> page(@Validated NoticePageDTO dto) {
        return noticeService.page(dto);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条通知记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody NoticeUpdateDTO dto) {
        return noticeService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条通知记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return noticeService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除通知记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return noticeService.deleteBatch(ids);
    }
    @Operation(summary = "查询 - 前N条记录", description = "查询前N条通知记录")
    @GetMapping("top/{n}")
    public List<Notice> top(@PathVariable("n") Long n) {
        return noticeService.top(n);
    }
}
