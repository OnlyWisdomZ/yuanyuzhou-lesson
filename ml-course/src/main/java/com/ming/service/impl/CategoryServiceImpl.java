package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.dto.CategoryInsertDTO;
import com.ming.dto.CategoryPageDTO;
import com.ming.dto.CategoryUpdateDTO;
import com.ming.exception.RepeatRecordException;
import com.ming.exception.ServerErrorException;
import com.ming.vo.CategorySimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Category;
import com.ming.mapper.CategoryMapper;
import com.ming.service.CategoryService;
import org.springframework.stereotype.Service;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static com.ming.entity.table.CategoryTableDef.CATEGORY;

/**
 * 课程类别表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "category")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>  implements CategoryService{


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
    public boolean update(Category entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Category entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Category> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Category getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Category getOne(QueryWrapper query) {
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
    public List<Category> list(QueryWrapper query) {
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
    public List<Category> listByIds(Collection<? extends Serializable> ids) {
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
    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(CategoryInsertDTO dto) {
        // 标题查重
        // select count(*) from category where title = ?
        if (QueryChain.of(mapper)
                .where(CATEGORY.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Category category = BeanUtil.copyProperties(dto, Category.class);
        category.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        category.setCreated(LocalDateTime.now());
        category.setUpdated(LocalDateTime.now());
        // insert into category (title, info, idx, created, updated) values (?, ?, ?, ?, ?)
        return mapper.insert(category) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Category select(Long id) {
        // select * from category where id = ?
        Category category = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(category)) {
            throw new ServerErrorException("记录不存在");
        }
        return category;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<CategorySimpleListVO> simpleList() {
        // select * from category order by idx asc, id desc
        return QueryChain.of(mapper)
                .orderBy(CATEGORY.IDX.asc(), CATEGORY.ID.desc())
                .withRelations()
                .listAs(CategorySimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Category> page(CategoryPageDTO dto) {
        QueryChain<Category> queryChain = QueryChain.of(mapper)
                .orderBy(CATEGORY.IDX.asc(), CATEGORY.ID.desc());
        // title条件
        String title = dto.getTitle();
        if (ObjectUtil.isNotNull(title)) {
            queryChain.where(CATEGORY.TITLE.like(title));
        }
        // DB分页
        Page<Category> page  = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(CategoryUpdateDTO dto) {
        // 标题查重
        // select count(1) from category where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(CATEGORY.TITLE.eq(dto.getTitle()))
                .and(CATEGORY.ID.ne(dto.getId()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Category category = BeanUtil.copyProperties(dto, Category.class);
        category.setUpdated(LocalDateTime.now());
        // update category set title = ?, info = ?, idx = ?, updated = ? where id = ?
        return UpdateChain.of(category)
                .where(CATEGORY.ID.eq(category.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from category where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from category where id in (?, ?, ?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
}
