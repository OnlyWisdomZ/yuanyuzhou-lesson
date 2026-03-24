package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ming.dto.ReportInsertDTO;
import com.ming.dto.ReportPageDTO;
import com.ming.dto.ReportUpdateDTO;
import com.ming.entity.User;
import com.ming.exception.ServerErrorException;
import com.ming.feign.UserFeign;
import com.ming.result.Result;
import com.ming.vo.ReportSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Report;
import com.ming.mapper.ReportMapper;
import com.ming.service.ReportService;
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

import static com.ming.entity.table.ReportTableDef.REPORT;

/**
 * 举报表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "report")
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report>  implements ReportService{


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
    public boolean update(Report entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Report entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Report> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Report getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Report getOne(QueryWrapper query) {
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
    public List<Report> list(QueryWrapper query) {
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
    public List<Report> listByIds(Collection<? extends Serializable> ids) {
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
    private UserFeign userFeign;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(ReportInsertDTO dto) {
        // 组装实体类
        Report report = BeanUtil.copyProperties(dto, Report.class);
        User user = userFeign.select(dto.getFkUserId()).getData();
        if (ObjectUtil.isNull(user)) {
            throw new ServerErrorException("用户不存在");
        }
        report.setNickname(user.getNickname());
        report.setCreated(LocalDateTime.now());
        report.setUpdated(LocalDateTime.now());
        // insert into report (fk_episode_id, fk_user_id, nickname, content, created, updated) values (?, ?, ?, ?, ?, ?)
        return mapper.insert(report) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Report select(Long id) {
        // 指定联查字段
        RelationManager.addQueryRelations("episode");
        // select * from report where id = ?
        return mapper.selectOneWithRelationsById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<ReportSimpleListVO> simpleList() {
        // select * from report
        return QueryChain.of(mapper).listAs(ReportSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Report> page(ReportPageDTO dto) {
        // 指定联查字段
        RelationManager.addQueryRelations("episode");
        QueryChain<Report> queryChain = QueryChain.of(mapper);
        // episodeId条件
        if (ObjectUtil.isNotNull(dto.getFkEpisodeId())) {
            queryChain.where(REPORT.FK_EPISODE_ID.eq(dto.getFkEpisodeId()));
        }
        // nickname条件
        if (ObjectUtil.isNotNull(dto.getNickname())) {
            queryChain.where(REPORT.NICKNAME.like(dto.getNickname()));
        }
        // DB分页
        Page<Report> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(ReportUpdateDTO dto) {
        // 组装实体类
        Report report = BeanUtil.copyProperties(dto, Report.class);
        Result<User> user = userFeign.select(dto.getFkUserId());
        if (ObjectUtil.isNull(user)) {
            throw new ServerErrorException("用户不存在");
        }
        report.setNickname(user.getData().getNickname());
        report.setUpdated(LocalDateTime.now());
        // update report set fk_episode_id = ?, fk_user_id = ?, nickname = ?, content = ?, updated = ? where id = ?
        return UpdateChain.of(report)
                .where(REPORT.ID.eq(report.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from report where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from report where id in (ids)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteByUserId(Long userId) {
        // delete from report where fk_user_id = userId
        return UpdateChain.of(mapper)
                .where(REPORT.FK_USER_ID.eq(userId))
                .remove();
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteByUserIds(List<Long> userIds) {
        // delete from report where fk_user_id in (userIds)
        return UpdateChain.of(mapper)
                .where(REPORT.FK_USER_ID.in(userIds))
                .remove();
    }

}
