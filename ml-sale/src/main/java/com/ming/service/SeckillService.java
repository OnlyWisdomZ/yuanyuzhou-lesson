package com.ming.service;

import com.ming.dto.SeckillInsertDTO;
import com.ming.dto.SeckillPageDTO;
import com.ming.dto.SeckillUpdateDTO;
import com.ming.vo.SeckillSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Seckill;

import java.util.List;

/**
 * 秒杀表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface SeckillService extends IService<Seckill> {
    boolean insert(SeckillInsertDTO dto);
    Seckill select(Long id);
    List<SeckillSimpleListVO> simpleList();
    Page<Seckill> page(SeckillPageDTO dto);
    boolean update(SeckillUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 查询距离当前时间最近的前N条秒杀活动记录，根据开始时间升序
     *
     * @param n 前N条
     * @return 前N条秒杀活动记录
     */
    List<Seckill> near(Long n);
}
