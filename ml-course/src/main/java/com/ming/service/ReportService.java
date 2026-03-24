package com.ming.service;

import com.ming.dto.ReportInsertDTO;
import com.ming.dto.ReportPageDTO;
import com.ming.dto.ReportUpdateDTO;
import com.ming.vo.ReportSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Report;

import java.util.List;

/**
 * 举报表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface ReportService extends IService<Report> {
    boolean insert(ReportInsertDTO dto);
    Report select(Long id);
    List<ReportSimpleListVO> simpleList();
    Page<Report> page(ReportPageDTO dto);
    boolean update(ReportUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 根据用户主键删除举报
     *
     * @param userId 用户主键
     * @return true 删除成功，false 删除失败
     */
    boolean deleteByUserId(Long userId);
    /**
     * 根据用户主键列表删除举报
     *
     * @param userIds 用户主键列表
     * @return true 删除成功，false 删除失败
     */
    boolean deleteByUserIds(List<Long> userIds);
}
