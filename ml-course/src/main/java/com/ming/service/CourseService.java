package com.ming.service;

import com.ming.dto.CourseInsertDTO;
import com.ming.dto.CoursePageDTO;
import com.ming.dto.CourseUpdateDTO;
import com.ming.vo.CourseSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Course;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 课程表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface CourseService extends IService<Course> {
    boolean insert(CourseInsertDTO dto);
    Course select(Long id);
    List<CourseSimpleListVO> simpleList();
    Page<Course> page(CoursePageDTO dto);
    boolean update(CourseUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 上传课程封面图片
     *
     * @param newFile 封面图片文件
     * @param id      课程主键
     * @return 文件名
     */
    String uploadCover(MultipartFile newFile, Long id);
    /**
     * 上传课程摘要图片
     *
     * @param newFile 摘要图片文件
     * @param id      课程主键
     * @return 文件名
     */
    String uploadSummary(MultipartFile newFile, Long id);
}
