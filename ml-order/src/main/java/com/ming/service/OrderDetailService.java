package com.ming.service;

import com.ming.dto.OrderDetailExcelDTO;
import com.ming.dto.OrderDetailInsertDTO;
import com.ming.dto.OrderDetailPageDTO;
import com.ming.dto.OrderDetailUpdateDTO;
import com.ming.vo.OrderDetailSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.OrderDetail;

import java.util.List;

/**
 * 订单明细表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface OrderDetailService extends IService<OrderDetail> {
    boolean insert(OrderDetailInsertDTO dto);
    OrderDetail select(Long id);
    List<OrderDetailSimpleListVO> simpleList();
    Page<OrderDetail> page(OrderDetailPageDTO dto);
    boolean update(OrderDetailUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 获取订单明细记录的Excel数据
     *
     * @return 订单明细的Excel数据列表
     */
    List<OrderDetailExcelDTO> getExcelData();
}
