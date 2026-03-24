package com.ming.service;

import com.ming.dto.CategoryInsertDTO;
import com.ming.dto.CategoryPageDTO;
import com.ming.dto.CategoryUpdateDTO;
import com.ming.vo.CategorySimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Category;

import java.util.List;

/**
 * 课程类别表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface CategoryService extends IService<Category> {
    boolean insert(CategoryInsertDTO dto);
    Category select(Long id);
    List<CategorySimpleListVO> simpleList();
    Page<Category> page(CategoryPageDTO dto);
    boolean update(CategoryUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
}
