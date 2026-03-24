package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ming.dto.NoticeInsertDTO;
import com.ming.dto.NoticePageDTO;
import com.ming.dto.NoticeUpdateDTO;
import com.ming.exception.ServerErrorException;
import com.ming.vo.NoticeSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Notice;
import com.ming.mapper.NoticeMapper;
import com.ming.service.NoticeService;
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

import static com.ming.entity.table.NoticeTableDef.NOTICE;

/**
 * 通知表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "notice")
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice>  implements NoticeService{


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
    public boolean update(Notice entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Notice entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Notice> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Notice getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Notice getOne(QueryWrapper query) {
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
    public List<Notice> list(QueryWrapper query) {
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
    public List<Notice> listByIds(Collection<? extends Serializable> ids) {
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
    public boolean insert(NoticeInsertDTO dto) {
        // 组装实体类
        Notice notice = BeanUtil.copyProperties(dto, Notice.class);
        notice.setCreated(LocalDateTime.now());
        notice.setUpdated(LocalDateTime.now());
        // insert into notice (content, idx, created, updated) values (?, ?, ?, ?)
        return mapper.insert(notice) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Notice select(Long id) {
        // select * from notice where id = ?
        Notice notice = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(notice)) {
            throw new ServerErrorException("记录不存在");
        }
        return notice;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<NoticeSimpleListVO> simpleList() {
        // select id, content from notice order by idx asc, id desc
        return QueryChain.of(mapper)
                .orderBy(NOTICE.IDX.asc(), NOTICE.ID.desc())
                .listAs(NoticeSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Notice> page(NoticePageDTO dto) {
        QueryChain<Notice> queryChain = QueryChain.of(mapper)
                .orderBy(NOTICE.IDX.asc(), NOTICE.ID.desc());
        // DB分页
        Page<Notice> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(NoticeUpdateDTO dto) {
        // 组装实体类
        Notice notice = BeanUtil.copyProperties(dto, Notice.class);
        notice.setUpdated(LocalDateTime.now());
        // update notice set content = ?, idx = ?, updated = ? where id = ?
        return UpdateChain.of(notice)
                .where(NOTICE.ID.eq(notice.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from notice where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from notice where id in (?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Cacheable(key = "#root.methodName + ':' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public List<Notice> top(Long n) {
        // select * from notice order by idx asc, id desc limit ?
        return QueryChain.of(mapper)
                .orderBy(NOTICE.IDX.asc(), NOTICE.ID.desc())
                .limit(n)
                .list();
    }
}
