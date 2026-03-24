package com.ming.controller;

import com.ming.dto.EpisodeInsertDTO;
import com.ming.dto.EpisodePageDTO;
import com.ming.dto.EpisodeUpdateDTO;
import com.ming.result.Result;
import com.ming.util.EasyExcelUtil;
import com.ming.vo.EpisodeSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ming.entity.Episode;
import com.ming.service.EpisodeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 集次表 控制层。
 *
 * @author Ming
 * @since v1.0.0
 */
@RestController
@Tag(name = "集次表接口")
@RequestMapping("/episode")
public class EpisodeController {

    @Resource
    private EpisodeService episodeService;

    @Operation(summary = "新增 - 单条新增", description = "新增一条集次记录")
    @PostMapping("insert")
    public boolean insert(@Validated @RequestBody EpisodeInsertDTO dto) {
        return episodeService.insert(dto);
    }

    @Operation(summary = "查询 - 单条查询", description = "按主键查询一条集次记录")
    @GetMapping("select/{id}")
    public Episode select(@PathVariable("id") Long id) {
        return episodeService.select(id);
    }

    @Operation(summary = "查询 - 简单列表", description = "查询全部集次记录，仅返回简单信息")
    @GetMapping("simpleList")
    public List<EpisodeSimpleListVO> simpleList() {
        return episodeService.simpleList();
    }

    @Operation(summary = "查询 - 分页查询", description = "分页查询集次记录")
    @GetMapping("page")
    public Page<Episode> page(@Validated EpisodePageDTO dto) {
        return episodeService.page(dto);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条集次记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody EpisodeUpdateDTO dto) {
        return episodeService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条集次记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return episodeService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除集次记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return episodeService.deleteBatch(ids);
    }
    @Operation(summary = "修改 - 集次封面", description = "按主键修改集次的封面图片")
    @PostMapping("/uploadVideoCover/{id}")
    public Result<String> uploadVideoCover(@RequestParam("videoCoverFile") MultipartFile videoCoverFile,
                                           @PathVariable("id") Long id) {
        return new Result<>(episodeService.uploadVideoCover(videoCoverFile, id));
    }
    @Operation(summary = "修改 - 集次视频", description = "按主键修改集次的视频")
    @PostMapping("/uploadVideo/{id}")
    public Result<String> uploadVideo(@RequestParam("videoFile") MultipartFile videoFile,
                                      @PathVariable("id") Long id) {
        return new Result<>(episodeService.uploadVideo(videoFile, id));
    }
    @Operation(summary = "查询 - 报表打印", description = "打印集次相关的报表数据")
    @GetMapping("excel")
    public void excel(HttpServletResponse response) {
        EasyExcelUtil.download(response, "集次统计表", episodeService.getExcelData());
    }
}
