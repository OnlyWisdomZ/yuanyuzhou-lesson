package com.ming.controller;

import com.ming.dto.CartInsertDTO;
import com.ming.dto.CartPageDTO;
import com.ming.dto.CartUpdateDTO;
import com.ming.vo.CartSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.ming.entity.Cart;
import com.ming.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;

/**
 * 购物车表 控制层。
 *
 * @author Ming
 * @since v1.0.0
 */
@RestController
@Tag(name = "购物车表接口")
@RequestMapping("/cart")
public class CartController {

    @Resource
    private CartService cartService;

    @Operation(summary = "新增 - 单条新增", description = "新增一条购物车记录")
    @PostMapping("insert")
    public boolean insert(@Validated @RequestBody CartInsertDTO dto) {
        return cartService.insert(dto);
    }

    @Operation(summary = "查询 - 单条查询", description = "按主键查询一条购物车记录")
    @GetMapping("select/{id}")
    public Cart select(@PathVariable("id") Long id) {
        return cartService.select(id);
    }

    @Operation(summary = "查询 - 简单列表", description = "查询全部购物车记录，仅返回简单信息")
    @GetMapping("simpleList")
    public List<CartSimpleListVO> simpleList() {
        return cartService.simpleList();
    }

    @Operation(summary = "查询 - 分页查询", description = "分页查询购物车记录")
    @GetMapping("page")
    public Page<Cart> page(@Validated CartPageDTO dto) {
        return cartService.page(dto);
    }

    @Operation(summary = "修改 - 单条修改", description = "按主键修改一条购物车记录")
    @PutMapping("update")
    public boolean update(@Validated @RequestBody CartUpdateDTO dto) {
        return cartService.update(dto);
    }

    @Operation(summary = "删除 - 单条删除", description = "按主键删除一条购物车记录")
    @DeleteMapping("delete/{id}")
    public boolean delete(@PathVariable("id") Long id) {
        return cartService.delete(id);
    }

    @Operation(summary = "删除 - 批量删除", description = "按主键批量删除购物车记录")
    @DeleteMapping("deleteBatch")
    public boolean deleteBatch(@RequestParam("ids") List<Long> ids) {
        return cartService.deleteBatch(ids);
    }
    @Operation(summary = "查询 - 总计金额", description = "按用户主键查询该用户全部购物车记录的总计金额")
    @GetMapping("totalAmountByUserId/{userId}")
    public double totalAmountByUserId(@PathVariable("userId") Long userId) {
        return cartService.totalAmountByUserId(userId);
    }
    @Operation(summary = "删除 - 清空记录", description = "按用户主键清空该用户的购物车记录")
    @DeleteMapping("clearByUserId/{userId}")
    public boolean clearByUserId(@PathVariable("userId") Long userId) {
        return cartService.clearByUserId(userId);
    }
    @Operation(summary = "删除 - 用户批删", description = "根据用户ID和课程IDS批量删除购物车记录")
    @DeleteMapping("deleteByUserIdAndCourseIds")
    public boolean deleteByUserIdAndCourseIds(@RequestParam("userId") Long userId,
                                              @RequestParam("ids") List<Long> courseIds) {
        return cartService.deleteByUserIdAndCourseIds(userId, courseIds);
    }
}
