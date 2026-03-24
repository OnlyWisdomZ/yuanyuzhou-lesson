package com.ming.service;

import com.ming.dto.CouponsInsertDTO;
import com.ming.dto.CouponsPageDTO;
import com.ming.dto.CouponsUpdateDTO;
import com.ming.vo.CouponsSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Coupons;

import java.util.List;

/**
 * 优惠卷表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface CouponsService extends IService<Coupons> {
    boolean insert(CouponsInsertDTO dto);
    Coupons select(Long id);
    List<CouponsSimpleListVO> simpleList();
    Page<Coupons> page(CouponsPageDTO dto);
    boolean update(CouponsUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 根据兑换口令查询优惠卷
     *
     * @param code 兑换口令
     * @return 优惠卷
     */
    Coupons selectByCode(String code);
}
