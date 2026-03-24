package com.ming.controller;

import com.ming.dto.ArticleInsertDTO;
import com.ming.dto.ArticlePageDTO;
import com.ming.dto.ArticleUpdateDTO;
import com.ming.vo.ArticleSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ming.entity.Article;
import com.ming.service.ArticleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 新闻表 控制层。
 *
 * @author Ming
 * @since v1.0.0
 */
@RestController
@Tag(name = "新闻表接口")
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Operation(summary = "新增 - 单条新增", description = "新增一条新闻记录")
    @PostMapping("insert")
    public boolean insert(@Validated @RequestBody ArticleInsertDTO dto) {
        return articleService.insert(dto);
    }

    @Operation(summary = "查询 - 单条查询", description = "按主键查询一条新闻记录")
    @GetMapping("select/{id}")
    public Article select(@PathVariable("id") Long id) {
        return articleService.select(id);
    }

    @Operation(summary = "查询 - 简单列表", description = "查询全部新闻记录，仅返回简单信息")
    @GetMapping("simpleList")
    public List<ArticleSimpleListVO> simpleList() {
        return articleService.simpleList();
    }

    @Operation(summary = "查询 - 分页查询", description = "分页查询新闻记录")
    @GetMapping("page")
    public Page<Article> page(@Validated ArticlePageDTO dto) {
        return articleService.page(dto);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条新闻记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody ArticleUpdateDTO dto) {
        return articleService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条新闻记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return articleService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除新闻记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return articleService.deleteBatch(ids);
    }
    @Operation(summary = "查询 - 前N条记录", description = "查询前N条新闻记录")
    @GetMapping("top/{n}")
    public List<Article> top(@PathVariable("n") Long n) {
        return articleService.top(n);
    }
}
