package com.ming.service;

import com.ming.dto.NoticeInsertDTO;
import com.ming.dto.NoticePageDTO;
import com.ming.dto.NoticeUpdateDTO;
import com.ming.vo.NoticeSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Notice;

import java.util.List;

/**
 * 通知表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface NoticeService extends IService<Notice> {
    boolean insert(NoticeInsertDTO dto);
    Notice select(Long id);
    List<NoticeSimpleListVO> simpleList();
    Page<Notice> page(NoticePageDTO dto);
    boolean update(NoticeUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 查看前N条通知记录，根据序号升序，序号相同根据ID降序
     *
     * @param n 前N条
     * @return 前N条通知记录
     */
    List<Notice> top(Long n);
}
