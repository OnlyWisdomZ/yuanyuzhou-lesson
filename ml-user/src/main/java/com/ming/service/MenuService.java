package com.ming.service;

import com.ming.dto.MenuInsertDTO;
import com.ming.dto.MenuPageDTO;
import com.ming.dto.MenuUpdateDTO;
import com.ming.vo.MenuSimpleListVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.ming.entity.Menu;

import java.util.List;

/**
 * 菜单表 服务层。
 *
 * @author Ming
 * @since v1.0.0
 */
public interface MenuService extends IService<Menu> {
    boolean insert(MenuInsertDTO dto);
    Menu select(Long id);
    List<MenuSimpleListVO> simpleList();
    Page<Menu> page(MenuPageDTO dto);
    boolean update(MenuUpdateDTO dto);
    boolean delete(Long id);
    boolean deleteBatch(List<Long> ids);
    /**
     * 按角色主键查询该角色的全部菜单ID列表
     *
     * @param roleId 角色主键
     * @return 该角色的全部菜单ID列表
     */
    List<Long> listMenuIdsByRoleId(Long roleId);
    /**
     * 按角色主键修改该角色的菜单列表
     *
     * @param roleId  角色主键
     * @param menuIds 菜单主键列表
     */
    boolean updateMenusByRoleId(Long roleId, List<Long> menuIds);
}
