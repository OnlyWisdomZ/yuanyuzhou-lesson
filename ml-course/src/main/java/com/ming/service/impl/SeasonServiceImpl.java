package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.dto.SeasonInsertDTO;
import com.ming.dto.SeasonPageDTO;
import com.ming.dto.SeasonUpdateDTO;
import com.ming.exception.RepeatRecordException;
import com.ming.exception.ServerErrorException;
import com.ming.mapper.EpisodeMapper;
import com.ming.vo.SeasonSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Season;
import com.ming.mapper.SeasonMapper;
import com.ming.service.SeasonService;
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
import java.util.Collection;
import java.util.List;

import static com.ming.entity.table.EpisodeTableDef.EPISODE;
import static com.ming.entity.table.SeasonTableDef.SEASON;

/**
 * 季次表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "season")
public class SeasonServiceImpl extends ServiceImpl<SeasonMapper, Season>  implements SeasonService{


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
    public boolean update(Season entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Season entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Season> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Season getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Season getOne(QueryWrapper query) {
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
    public List<Season> list(QueryWrapper query) {
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
    public List<Season> listByIds(Collection<? extends Serializable> ids) {
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
    private EpisodeMapper episodeMapper;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(SeasonInsertDTO dto) {
        // 标题查重
        // select count(*) from season where title = ?
        if (QueryChain.of(mapper)
                .where(SEASON.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new ServerErrorException("标题已存在");
        }
        // 组装实体类
        Season season = BeanUtil.copyProperties(dto, Season.class);
        season.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        season.setCreated(LocalDateTime.now());
        season.setUpdated(LocalDateTime.now());
        // insert into season (title, info, idx, fk_course_id, created, updated) values (?, ?, ?, ?, ?, ?)
        return mapper.insert(season) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Season select(Long id) {
        // 指定联查字段
        RelationManager.addQueryRelations("course", "category", "episodes");
        // select * from season where id = ?
        Season season = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(season)) {
            throw new ServerErrorException("记录不存在");
        }
        return season;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<SeasonSimpleListVO> simpleList() {
        // select * from season order by idx asc, id desc
        return QueryChain.of(mapper)
                .orderBy(SEASON.IDX.asc(), SEASON.ID.desc())
                .withRelations()
                .listAs(SeasonSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Season> page(SeasonPageDTO dto) {
        QueryChain<Season> queryChain = QueryChain.of(mapper)
                .orderBy(SEASON.IDX.asc(), SEASON.ID.desc());
        // title条件
        if (ObjectUtil.isNotEmpty(dto.getTitle())) {
            queryChain.where(SEASON.TITLE.like(dto.getTitle()));
        }
        // fkCourseId条件
        if (ObjectUtil.isNotEmpty(dto.getFkCourseId())) {
            queryChain.where(SEASON.FK_COURSE_ID.eq(dto.getFkCourseId()));
        }
        // DB分页
        Page<Season> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(SeasonUpdateDTO dto) {
        // 标题查重
        // select count(*) from season where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(SEASON.TITLE.eq(dto.getTitle()))
                .and(SEASON.ID.ne(dto.getId()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Season season = BeanUtil.copyProperties(dto, Season.class);
        season.setUpdated(LocalDateTime.now());
        // update season set title = ?, info = ?, idx = ?, fk_course_id = ?, updated = ? where id = ?
        return UpdateChain.of(season)
                .where(SEASON.ID.eq(season.getId()))
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // 通过季次主键查询全部集次的ID列表
        // select id from episode where fk_season_id = #{id}
        List<Long> episodeIds = QueryChain.of(episodeMapper)
                .select(EPISODE.ID)
                .where(EPISODE.FK_SEASON_ID.eq(id))
                .objListAs(Long.class);
        // 存在集次时，批量删除集次
        this.clearEpisode(episodeIds);
        // 删除季次
        // delete from season where id = ?
        return mapper.deleteById(id) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // 通过季次主键列表查询全部集次的ID列表
        // select id from episode where fk_season_id in (>)
        List<Long> episodeIds = QueryChain.of(episodeMapper)
                .select(EPISODE.ID)
                .where(EPISODE.FK_SEASON_ID.in(ids))
                .objListAs(Long.class);
        // 存在集次时，批量删除集次
        this.clearEpisode(episodeIds);
        // 批量删除季次
        // delete from season where id in (>)
        return mapper.deleteBatchByIds(ids) > 0;
    }

    /**
     * 根据集次ID列表，清空全部集次记录
     *
     * @param episodeIds 集次主键列表
     */
    private void clearEpisode(List<Long> episodeIds) {
        // 存在集记录时，批量删除集
        if (ObjectUtil.isNotEmpty(episodeIds)) {
            if (episodeMapper.deleteBatchByIds(episodeIds) <= 0) {
                throw new ServerErrorException("集次删除失败");
            }
        }
    }
}
