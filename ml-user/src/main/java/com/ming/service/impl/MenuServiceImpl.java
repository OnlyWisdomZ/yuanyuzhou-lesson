package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.dto.MenuInsertDTO;
import com.ming.dto.MenuPageDTO;
import com.ming.dto.MenuUpdateDTO;
import com.ming.entity.RoleMenu;
import com.ming.exception.RepeatRecordException;
import com.ming.exception.ServerErrorException;
import com.ming.mapper.RoleMenuMapper;
import com.ming.vo.MenuSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Menu;
import com.ming.mapper.MenuMapper;
import com.ming.service.MenuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ming.entity.table.MenuTableDef.MENU;
import static com.ming.entity.table.RoleMenuTableDef.ROLE_MENU;

/**
 * 菜单表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "menu")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>  implements MenuService{


    @Override
    @CacheEvict(allEntries = true)
    public boolean remove(QueryWrapper query) {
        return super.remove(query);
    }

    @Override
    @CacheEvict(key = "#id")
    public boolean removeById(Serializable id) {
        return super.removeById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean removeByIds(Collection<? extends Serializable> ids) {
        return super.removeByIds(ids);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean update(Menu entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Menu entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Menu> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Menu getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Menu getOne(QueryWrapper query) {
        return super.getOne(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getOneAs(QueryWrapper query, Class<R> asType) {
        return super.getOneAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Object getObj(QueryWrapper query) {
        return super.getObj(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> R getObjAs(QueryWrapper query, Class<R> asType) {
        return super.getObjAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<Object> objList(QueryWrapper query) {
        return super.objList(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> objListAs(QueryWrapper query, Class<R> asType) {
        return super.objListAs(query, asType);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public List<Menu> list(QueryWrapper query) {
        return super.list(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public <R> List<R> listAs(QueryWrapper query, Class<R> asType) {
        return super.listAs(query, asType);
    }

    /**
     * @deprecated 无法通过注解进行缓存操作。
     */
    @Override
    @Deprecated
    public List<Menu> listByIds(Collection<? extends Serializable> ids) {
        return super.listByIds(ids);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public long count(QueryWrapper query) {
        return super.count(query);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #page.getPageSize() + ':' + #page.getPageNumber() + ':' + #query.toSQL()")
    public <R> Page<R> pageAs(Page<R> page, QueryWrapper query, Class<R> asType) {
        return super.pageAs(page, query, asType);
    }
    @Resource
    private RoleMenuMapper roleMenuMapper;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(MenuInsertDTO dto) {
        // 标题查重
        // select count(*) from menu where title = ?
        if (QueryChain.of(mapper)
                .where(MENU.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Menu menu = BeanUtil.copyProperties(dto, Menu.class);
        menu.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        menu.setCreated(LocalDateTime.now());
        menu.setUpdated(LocalDateTime.now());
        // insert into menu (title, info, url, icon, pid, idx, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(menu) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Menu select(Long id) {
        // select * from menu where id = ?
        Menu menu = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(menu)) {
            throw new ServerErrorException("记录不存在");
        }
        return menu;
    }

    @Cacheable(key = "#root.methodName", unless = "#result == null")
    @Override
    public List<MenuSimpleListVO> simpleList() {
        // 查询全部菜单记录（联查父菜单记录）
        // select * from menu order by pid asc, idx asc, id desc
        List<Menu> menus = QueryChain.of(mapper)
                .orderBy(MENU.PID.asc(), MENU.IDX.asc(), MENU.ID.desc())
                .withRelations()
                .list();
        // 组装 VO 实体类
        List<MenuSimpleListVO> result = new ArrayList<>();
        menus.forEach(menu -> {
            MenuSimpleListVO menuSimpleListVO = BeanUtil.copyProperties(menu, MenuSimpleListVO.class);
            if (menu.getParentMenu() != null) {
                menuSimpleListVO.setParentTitle(menu.getParentMenu().getTitle());
            }
            result.add(menuSimpleListVO);
        });
        return result;
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", unless = "#result == null")
    @Override
    public Page<Menu> page(MenuPageDTO dto) {
        QueryChain<Menu> queryChain = QueryChain.of(mapper)
                .orderBy(MENU.PID.asc(), MENU.IDX.asc(), MENU.ID.desc());
        // pid条件
        if (ObjectUtil.isNotNull(dto.getPid())) {
            queryChain.where(MENU.PID.eq(dto.getPid()));
        }
        // title条件
        if (ObjectUtil.isNotNull(dto.getTitle())) {
            queryChain.where(MENU.TITLE.like(dto.getTitle()));
        }
        // DB分页
        Page<Menu> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(MenuUpdateDTO dto) {
        // 标题查重
        // select count(*) from menu where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(MENU.TITLE.eq(dto.getTitle()))
                .and(MENU.ID.ne(dto.getId()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Menu menu = BeanUtil.copyProperties(dto, Menu.class);
        menu.setUpdated(LocalDateTime.now());
        // update menu set title = ?, info = ?, url = ?, icon = ?, pid = ?, idx = ?, updated = ? where id = ?
        return UpdateChain.of(menu)
                .where(MENU.ID.eq(menu.getId()))
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // 查询父菜单ID和全部子菜单ID
        // select id from menu where pid = 1 or id = 1
        List<Long> deleteIds = QueryChain.of(mapper)
                .select(MENU.ID)
                .where(MENU.PID.eq(id))
                .or(MENU.ID.eq(id))
                .objListAs(Long.class);
        // 删除中间表
        // delete from role_menu where fk_menu_id in(?)
        UpdateChain.of(roleMenuMapper)
                .where(ROLE_MENU.FK_MENU_ID.in(deleteIds))
                .remove();
        // 删除 MENU 表中的菜单
        // delete from menu where id in(?)
        return UpdateChain.of(mapper)
                .where(MENU.ID.in(deleteIds))
                .remove();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // 查询父菜单ID和全部子菜单ID
        // select id from menu where pid in (?) or id in (?)
        List<Long> deleteIds = QueryChain.of(mapper)
                .select(MENU.ID)
                .where(MENU.PID.in(ids))
                .or(MENU.ID.in(ids))
                .objListAs(Long.class);
        // 删除中间表
        // delete from role_menu where fk_menu_id in(?)
        UpdateChain.of(roleMenuMapper)
                .where(ROLE_MENU.FK_MENU_ID.in(deleteIds))
                .remove();
        // 删除 MENU 表中的菜单
        // delete from menu where id in(?)
        return UpdateChain.of(mapper)
                .where(MENU.ID.in(deleteIds))
                .remove();
    }
    @Cacheable(key = "#root.methodName + ':' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        // select fk_menu_id from role_menu where fk_role_id = xx
        return QueryChain.of(roleMenuMapper)
                .select(ROLE_MENU.FK_MENU_ID)
                .where(ROLE_MENU.FK_ROLE_ID.eq(roleId))
                .objListAs(Long.class);
    }
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMenusByRoleId(Long roleId, List<Long> menuIds) {
        // 新菜单列表为空，直接返回即可
        if (CollUtil.isEmpty(menuIds)) {
            return true;
        }
        // 清空中间表：删除中间表中该角色的全部菜单记录
        UpdateChain.of(roleMenuMapper)
                .where(ROLE_MENU.FK_ROLE_ID.eq(roleId))
                .remove();
        // 添加中间表：在中间表中批量添加该角色的新菜单记录
        List<RoleMenu> roleMenus = new ArrayList<>();
        for (Long menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setFkRoleId(roleId);
            roleMenu.setFkMenuId(menuId);
            roleMenu.setCreated(LocalDateTime.now());
            roleMenu.setUpdated(LocalDateTime.now());
            roleMenus.add(roleMenu);
        }
        // insert into role_menu(fk_role_id, fk_menu_id, created, updated) values(?, ?, ?, ?)
        return roleMenuMapper.insertBatch(roleMenus) == menuIds.size();
    }
}
