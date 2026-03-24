package com.ming.service;

import com.ming.dto.CartInsertDTO;
import com.ming.dto.CartPageDTO;
import com.ming.dto.CartUpdateDTO;
import com.ming.vo.CartSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Cart;

import java.util.List;

/**
 * 购物车表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface CartService extends IService<Cart> {
    boolean insert(CartInsertDTO dto);
    Cart select(Long id);
    List<CartSimpleListVO> simpleList();
    Page<Cart> page(CartPageDTO dto);
    boolean update(CartUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 根据用户主键计算购物车记录总金额
     *
     * @param userId 用户主键
     * @return 购物车记录总金额
     */
    double totalAmountByUserId(Long userId);
    /**
     * 根据用户主键清空购物车记录
     *
     * @param userId 用户主键
     * @return true 成功，false 失败
     */
    boolean clearByUserId(Long userId);
    /**
     * 根据用户主键和课程主键列表删除购物车记录
     *
     * @param userId    用户主键
     * @param courseIds 课程主键列表
     * @return true 成功，false 失败
     */
    boolean deleteByUserIdAndCourseIds(Long userId, List<Long> courseIds);
}
