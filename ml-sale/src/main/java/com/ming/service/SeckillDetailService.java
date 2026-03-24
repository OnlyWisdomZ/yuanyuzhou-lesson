package com.ming.service;

import com.ming.dto.SeckillDetailInsertDTO;
import com.ming.dto.SeckillDetailPageDTO;
import com.ming.dto.SeckillDetailUpdateDTO;
import com.ming.vo.SeckillDetailSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.SeckillDetail;

import java.util.List;

/**
 * 秒杀明细表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface SeckillDetailService extends IService<SeckillDetail> {
    boolean insert(SeckillDetailInsertDTO dto);
    SeckillDetail select(Long id);
    List<SeckillDetailSimpleListVO> simpleList();
    Page<SeckillDetail> page(SeckillDetailPageDTO dto);
    boolean update(SeckillDetailUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
}
