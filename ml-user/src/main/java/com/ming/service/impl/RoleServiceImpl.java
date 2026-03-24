package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.dto.RoleInsertDTO;
import com.ming.dto.RolePageDTO;
import com.ming.dto.RoleUpdateDTO;
import com.ming.entity.UserRole;
import com.ming.exception.RepeatRecordException;
import com.ming.exception.ServerErrorException;
import com.ming.mapper.RoleMenuMapper;
import com.ming.mapper.UserRoleMapper;
import com.ming.vo.RoleSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Role;
import com.ming.mapper.RoleMapper;
import com.ming.service.RoleService;
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

import static com.ming.entity.table.RoleMenuTableDef.ROLE_MENU;
import static com.ming.entity.table.RoleTableDef.ROLE;
import static com.ming.entity.table.UserRoleTableDef.USER_ROLE;

/**
 * 角色表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "role")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>  implements RoleService{


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
    public boolean update(Role entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Role entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Role> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Role getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Role getOne(QueryWrapper query) {
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
    public List<Role> list(QueryWrapper query) {
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
    public List<Role> listByIds(Collection<? extends Serializable> ids) {
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
    @Resource
    private UserRoleMapper userRoleMapper;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(RoleInsertDTO dto) {
        // 标题查重
        // select count(*) from role where title = ?
        if (QueryChain.of(mapper)
                .where(ROLE.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Role role = BeanUtil.copyProperties(dto, Role.class);
        role.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        role.setCreated(LocalDateTime.now());
        role.setUpdated(LocalDateTime.now());
        // insert into role (title, info, idx, created, updated) values (?, ?, ?, ?, ?)
        return mapper.insert(role) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Role select(Long id) {
        // select * from role where id = ?
        Role role = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(role)) {
            throw new ServerErrorException("记录不存在");
        }
        return role;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<RoleSimpleListVO> simpleList() {
        // select * from role order by idx asc, id desc
        return QueryChain.of(mapper)
                .orderBy(ROLE.IDX.asc(), ROLE.ID.desc())
                .withRelations()
                .listAs(RoleSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", unless = "#result == null")
    @Override
    public Page<Role> page(RolePageDTO dto) {
        // select * from role order by idx asc, id desc
        QueryChain<Role> queryChain = QueryChain.of(mapper)
                .orderBy(ROLE.IDX.asc(), ROLE.ID.desc());
        // title条件
        String title = dto.getTitle();
        if (ObjectUtil.isNotNull(title)) {
            queryChain.where(ROLE.TITLE.like(title));
        }
        // DB分页
        Page<Role> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(RoleUpdateDTO dto) {
        // 标题查重
        // select count(*) from role where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(ROLE.TITLE.eq(dto.getTitle()))
                .and(ROLE.ID.ne(dto.getId()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Role role = BeanUtil.copyProperties(dto, Role.class);
        role.setUpdated(LocalDateTime.now());
        // update role set title = ?, info = ?, idx = ?, updated = ? where id = ?
        return UpdateChain.of(role)
                .where(ROLE.ID.eq(role.getId()))
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // 删除中间表
        // delete from role_menu where fk_role_id = ?
        UpdateChain.of(roleMenuMapper)
                .where(ROLE_MENU.FK_ROLE_ID.eq(id))
                .remove();
        // delete from user_role where fk_role_id = ?
        UpdateChain.of(userRoleMapper)
                .where(USER_ROLE.FK_ROLE_ID.eq(id))
                .remove();
        // 删除基础表
        // delete from role where id = ?
        return mapper.deleteById(id) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // 删除中间表
        // delete from role_menu where fk_role_id in (?)
        UpdateChain.of(roleMenuMapper)
                .where(ROLE_MENU.FK_ROLE_ID.in(ids))
                .remove();
        // delete from user_role where fk_role_id in (?)
        UpdateChain.of(userRoleMapper)
                .where(USER_ROLE.FK_ROLE_ID.in(ids))
                .remove();
        // 删除基础表
        // delete from role where id in (?)
        return mapper.deleteBatchByIds(ids) == ids.size();
    }
    @Cacheable(key = "#root.methodName + ':' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        // select fk_role_id from user_role where fk_user_id = ?
        return QueryChain.of(UserRole.class)
                .select(USER_ROLE.FK_ROLE_ID)
                .where(USER_ROLE.FK_USER_ID.eq(userId))
                .objListAs(Long.class);
    }
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateRolesByUserId(Long userId, List<Long> roleIds) {
        // 新角色列表为空，直接返回即可
        if (CollUtil.isEmpty(roleIds)) {
            return true;
        }
        // 清空中间表：删除中间表中该用户的全部角色记录
        // delete from user_role where fk_user_id = ?
        UpdateChain.of(userRoleMapper)
                .where(USER_ROLE.FK_USER_ID.eq(userId))
                .remove();
        // 添加中间表：在中间表中批量添加该用户的新角色记录
        List<UserRole> userRoles = new ArrayList<>();
        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setFkUserId(userId);
            userRole.setFkRoleId(roleId);
            userRole.setCreated(LocalDateTime.now());
            userRole.setUpdated(LocalDateTime.now());
            userRoles.add(userRole);
        }
        // insert into user_role (fk_user_id, fk_role_id, created, updated) values (?, ?, ?, ?)
        return userRoleMapper.insertBatch(userRoles) == roleIds.size();
    }

}
