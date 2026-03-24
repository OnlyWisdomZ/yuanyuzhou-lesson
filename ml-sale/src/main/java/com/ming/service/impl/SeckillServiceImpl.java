package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.dto.SeckillInsertDTO;
import com.ming.dto.SeckillPageDTO;
import com.ming.dto.SeckillUpdateDTO;
import com.ming.exception.IllegalParamException;
import com.ming.mapper.SeckillDetailMapper;
import com.ming.vo.SeckillSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Seckill;
import com.ming.mapper.SeckillMapper;
import com.ming.service.SeckillService;
import jakarta.annotation.Resource;
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

import static com.ming.entity.table.SeckillDetailTableDef.SECKILL_DETAIL;
import static com.ming.entity.table.SeckillTableDef.SECKILL;
import static com.mybatisflex.core.query.QueryMethods.curDate;
import static com.mybatisflex.core.query.QueryMethods.date;

/**
 * 秒杀表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "seckill")
public class SeckillServiceImpl extends ServiceImpl<SeckillMapper, Seckill>  implements SeckillService{


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
    public boolean update(Seckill entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Seckill entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Seckill> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Seckill getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Seckill getOne(QueryWrapper query) {
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
    public List<Seckill> list(QueryWrapper query) {
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
    public List<Seckill> listByIds(Collection<? extends Serializable> ids) {
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
    private SeckillDetailMapper seckillDetailMapper;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(SeckillInsertDTO dto) {
        // 标题查重
        // select count(*) from seckill where title = ?
        if (QueryChain.of(mapper)
                .where(SECKILL.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new IllegalParamException("标题已存在");
        }
        // 组装实体类
        Seckill seckill = BeanUtil.copyProperties(dto, Seckill.class);
        seckill.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        seckill.setCreated(LocalDateTime.now());
        seckill.setUpdated(LocalDateTime.now());
        // insert into seckill (title, info, start_time, end_time, status, created, updated) values (?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(seckill) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Seckill select(Long id) {
        // select * from seckill where id = ?
        Seckill seckill = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(seckill)) {
            throw new IllegalParamException("秒杀活动不存在");
        }
        return seckill;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<SeckillSimpleListVO> simpleList() {
        // select * from seckill
        return QueryChain.of(mapper)
                .withRelations()
                .listAs(SeckillSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Seckill> page(SeckillPageDTO dto) {
        QueryChain<Seckill> queryChain = QueryChain.of(mapper);
        // title条件
        String title = dto.getTitle();
        if (ObjectUtil.isNotNull(title)) {
            queryChain.where(SECKILL.TITLE.like(title));
        }
        // DB分页
        Page<Seckill> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(SeckillUpdateDTO dto) {
        // 标题查重
        // select count(*) from seckill where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(SECKILL.TITLE.eq(dto.getTitle()))
                .and(SECKILL.ID.ne(dto.getId()))
                .exists()) {
            throw new IllegalParamException("标题已存在");
        }
        // 组装实体类
        Seckill seckill = BeanUtil.copyProperties(dto, Seckill.class);
        // update seckill set title = ?, info = ?, start_time = ?, end_time = ?, updated = ? where id = ?
        return UpdateChain.of(seckill)
                .where(SECKILL.ID.eq(seckill.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // 按秒杀主键主键删除秒杀明细记录
        // delete from seckill_detail where fk_seckill_id = id
        UpdateChain.of(seckillDetailMapper)
                .where(SECKILL_DETAIL.FK_SECKILL_ID.eq(id))
                .remove();
        // 按秒杀主键删除一条秒杀记录
        // delete from seckill where id = id
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // 按秒杀主键批量删除秒杀明细记录
        // delete from seckill_detail where fk_seckill_id in (ids)
        UpdateChain.of(seckillDetailMapper)
                .where(SECKILL_DETAIL.FK_SECKILL_ID.in(ids))
                .remove();
        // 按秒杀主键批量删除秒杀记录
        // delete from seckill where id in (ids)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Cacheable(key = "#root.methodName + ':' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public List<Seckill> near(Long n) {
        // 查询距离当前时间最近的前N条秒杀活动记录，根据开始时间升序
        // select * from seckill where date(start_time) = curdate() order by start_time asc limit n
        return QueryChain.of(mapper)
                .where(date(SECKILL.START_TIME).eq(curDate()))
                .orderBy(SECKILL.START_TIME.asc())
                .limit(n)
                .withRelations()
                .list();
    }
}
