package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.dto.CouponsInsertDTO;
import com.ming.dto.CouponsPageDTO;
import com.ming.dto.CouponsUpdateDTO;
import com.ming.exception.IllegalParamException;
import com.ming.exception.ServerErrorException;
import com.ming.vo.CouponsSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Coupons;
import com.ming.mapper.CouponsMapper;
import com.ming.service.CouponsService;
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

import static com.ming.entity.table.CouponsTableDef.COUPONS;

/**
 * 优惠卷表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "coupons")
public class CouponsServiceImpl extends ServiceImpl<CouponsMapper, Coupons>  implements CouponsService{


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
    public boolean update(Coupons entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Coupons entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Coupons> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Coupons getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Coupons getOne(QueryWrapper query) {
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
    public List<Coupons> list(QueryWrapper query) {
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
    public List<Coupons> listByIds(Collection<? extends Serializable> ids) {
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
    public boolean insert(CouponsInsertDTO dto) {
        // 判断生效时间和失效时间是否合理
        if (dto.getStartTime().isAfter(dto.getEndTime())) {
            throw new IllegalParamException("失效时间过早");
        }
        // 标题查重
        // select count(*) from coupons where title = ?
        if (QueryChain.of(mapper)
                .where(COUPONS.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new IllegalParamException("标题已存在");
        }
        // 口令查重
        // select count(*) from coupons where code = ?
        if (QueryChain.of(mapper)
                .where(COUPONS.CODE.eq(dto.getCode()))
                .exists()) {
            throw new IllegalParamException("口令已存在");
        }
        // 组装实体类
        Coupons coupons = BeanUtil.copyProperties(dto, Coupons.class);
        coupons.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        coupons.setCreated(LocalDateTime.now());
        coupons.setUpdated(LocalDateTime.now());
        // insert into coupons (code, title, cp_price, info, start_time, end_time, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(coupons) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Coupons select(Long id) {
        // select * from coupons where id = ?
        Coupons coupons = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(coupons)) {
            throw new ServerErrorException("优惠卷不存在");
        }
        return coupons;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<CouponsSimpleListVO> simpleList() {
        // select * from coupons
        return QueryChain.of(mapper).listAs(CouponsSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Coupons> page(CouponsPageDTO dto) {
        QueryChain<Coupons> queryChain = QueryChain.of(mapper);
        // title 条件
        String title = dto.getTitle();
        if (ObjectUtil.isNotNull(title)) {
            queryChain.where(COUPONS.TITLE.like(title));
        }
        // code 条件
        String code = dto.getCode();
        if (ObjectUtil.isNotNull(code)) {
            queryChain.where(COUPONS.CODE.like(code));
        }
        // DB分页
        Page<Coupons> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(CouponsUpdateDTO dto) {
        LocalDateTime startTime = dto.getStartTime();
        LocalDateTime endTime = dto.getEndTime();
        // 判断生效时间和失效时间是否合理
        if (ObjectUtil.isNotNull(startTime) && ObjectUtil.isNotNull(endTime) && startTime.isAfter(endTime)) {
            throw new IllegalParamException("失效时间过早");
        }
        // 标题查重
        // select count(*) from coupons where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(COUPONS.TITLE.eq(dto.getTitle()))
                .and(COUPONS.ID.ne(dto.getId()))
                .exists()) {
            throw new IllegalParamException("标题已存在");
        }
        // 口令查重
        // select count(*) from coupons where code = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(COUPONS.CODE.eq(dto.getCode()))
                .and(COUPONS.ID.ne(dto.getId()))
                .exists()) {
            throw new IllegalParamException("口令已存在");
        }
        // 组装实体类
        Coupons coupons = BeanUtil.copyProperties(dto, Coupons.class);
        coupons.setUpdated(LocalDateTime.now());
        // update coupons set code = ?, title = ?, cp_price = ?, info = ?, start_time = ?, end_time = ?, updated = ? where id = ?
        return UpdateChain.of(coupons)
                .where(COUPONS.ID.eq(coupons.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from coupons where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from coupons where id in (?, ?, ?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Cacheable(key = "#root.methodName + ':' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Coupons selectByCode(String code) {
        // select * from coupons where code = ?
        return QueryChain.of(mapper)
                .where(COUPONS.CODE.eq(code))
                .one();
    }

}
