package com.ming.service;

import com.ming.dto.EpisodeExcelDTO;
import com.ming.dto.EpisodeInsertDTO;
import com.ming.dto.EpisodePageDTO;
import com.ming.dto.EpisodeUpdateDTO;
import com.ming.vo.EpisodeSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Episode;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 集次表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface EpisodeService extends IService<Episode> {
    boolean insert(EpisodeInsertDTO dto);
    Episode select(Long id);
    List<EpisodeSimpleListVO> simpleList();
    Page<Episode> page(EpisodePageDTO dto);
    boolean update(EpisodeUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 上传视频封面图片
     *
     * @param newFile 封面图片文件
     * @param id      集次主键
     * @return 文件名
     */
    String uploadVideoCover(MultipartFile newFile, Long id);
    /**
     * 上传集次的视频文件
     *
     * @param newFile 视频文件
     * @param id      集次主键
     * @return 文件名
     */
    String uploadVideo(MultipartFile newFile, Long id);
    /**
     * 获取集次记录的Excel数据
     *
     * @return 用户集次的Excel数据列表
     */
    List<EpisodeExcelDTO> getExcelData();
}
