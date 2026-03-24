package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.constant.ML;
import com.ming.dto.EpisodeExcelDTO;
import com.ming.dto.EpisodeInsertDTO;
import com.ming.dto.EpisodePageDTO;
import com.ming.dto.EpisodeUpdateDTO;
import com.ming.entity.Category;
import com.ming.entity.Course;
import com.ming.entity.Season;
import com.ming.exception.RepeatRecordException;
import com.ming.exception.ServerErrorException;
import com.ming.util.MinioUtil;
import com.ming.vo.EpisodeSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Episode;
import com.ming.mapper.EpisodeMapper;
import com.ming.service.EpisodeService;
import org.springframework.stereotype.Service;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ming.entity.table.EpisodeTableDef.EPISODE;

/**
 * 集次表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "episode")
public class EpisodeServiceImpl extends ServiceImpl<EpisodeMapper, Episode>  implements EpisodeService{


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
    public boolean update(Episode entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Episode entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Episode> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Episode getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Episode getOne(QueryWrapper query) {
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
    public List<Episode> list(QueryWrapper query) {
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
    public List<Episode> listByIds(Collection<? extends Serializable> ids) {
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
    public boolean insert(EpisodeInsertDTO dto) {
        // 标题查重
        // select count(1) from episode where title = ?
        if (QueryChain.of(mapper)
                .where(EPISODE.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Episode episode = BeanUtil.copyProperties(dto, Episode.class);
        episode.setVideo(ML.Episode.DEFAULT_VIDEO);
        episode.setCover(ML.Episode.DEFAULT_VIDEO_COVER);
        episode.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        episode.setCreated(LocalDateTime.now());
        episode.setUpdated(LocalDateTime.now());
        // insert into episode (title, info, video, cover, fk_season_id, idx, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(episode) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Episode select(Long id) {
        // 指定联查字段
        RelationManager.addQueryRelations("season");
        // select * from episode where id = ?
        Episode episode = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(episode)) {
            throw new ServerErrorException("记录不存在");
        }
        return episode;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<EpisodeSimpleListVO> simpleList() {
        // select id, title from episode order by idx asc, id desc
        return QueryChain.of(mapper)
                .orderBy(EPISODE.IDX.asc(), EPISODE.ID.desc())
                .withRelations()
                .listAs(EpisodeSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Episode> page(EpisodePageDTO dto) {
        // 指定联查字段
        RelationManager.addQueryRelations("season");
        // select * from episode order by idx asc, id desc
        QueryChain<Episode> queryChain = QueryChain.of(mapper)
                .orderBy(EPISODE.IDX.asc(), EPISODE.ID.desc());
        // title条件
        if (ObjectUtil.isNotEmpty(dto.getTitle())) {
            queryChain.where(EPISODE.TITLE.like(dto.getTitle()));
        }
        // fkSeasonId条件
        if (ObjectUtil.isNotEmpty(dto.getFkSeasonId())) {
            queryChain.where(EPISODE.FK_SEASON_ID.eq(dto.getFkSeasonId()));
        }
        // DB分页
        Page<Episode> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(EpisodeUpdateDTO dto) {
        // 标题查重
        // select count(1) from episode where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(EPISODE.TITLE.eq(dto.getTitle()))
                .and(EPISODE.ID.ne(dto.getId()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Episode episode = BeanUtil.copyProperties(dto, Episode.class);
        episode.setUpdated(LocalDateTime.now());
        // update episode set title = ?, info = ?, idx = ?, fk_season_id = ?, updated = ? where id = ?
        return UpdateChain.of(episode)
                .where(EPISODE.ID.eq(episode.getId()))
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from episode where id =?
        return mapper.deleteById(id) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from episode where id in (ids)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(allEntries = true)
    public String uploadVideoCover(MultipartFile newFile, Long id) {
        // 按主键查询记录
        // select * from episode where id = ?
        Episode episode = mapper.selectOneById(id);
        if (ObjectUtil.isNull(episode)) {
            throw new ServerErrorException("记录不存在");
        }
        // 备份旧文件名
        String oldFileName = episode.getCover();
        // 生成新文件名
        String newFileName = MinioUtil.randomFilename(newFile);
        // DB更新文件名
        episode.setCover(newFileName);
        if (mapper.update(episode) <= 0) {
            throw new ServerErrorException("DB更新失败");
        }
        try {
            // MinIO中删除旧文件：默认文件不删除
            if (!ML.Episode.DEFAULT_VIDEO_COVER.equals(oldFileName)) {
                MinioUtil.delete(oldFileName, ML.MinIO.EPISODE_VIDEO_COVER_DIR, ML.MinIO.BUCKET_NAME);
            }
            // MinIO上传新文件
            MinioUtil.upload(newFile, newFileName, ML.MinIO.EPISODE_VIDEO_COVER_DIR, ML.MinIO.BUCKET_NAME);
        } catch (Exception e) {
            throw new ServerErrorException("MinIO操作失败：" + e.getMessage());
        }
        // 返回新文件名
        return newFileName;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(allEntries = true)
    public String uploadVideo(MultipartFile newFile, Long id) {
        // 按主键查询记录
        // select * from episode where id = ?
        Episode episode = mapper.selectOneById(id);
        if (ObjectUtil.isNull(episode)) {
            throw new ServerErrorException("记录不存在");
        }
        // 备份旧文件名
        String oldVideoName = episode.getVideo();
        // 生成新文件名
        String newVideoName = MinioUtil.randomFilename(newFile);
        // DB更新文件名
        episode.setVideo(newVideoName);
        if (mapper.update(episode) <= 0) {
            throw new ServerErrorException("DB更新失败");
        }
        try {
            // MinIO中删除旧文件：默认文件不删除
            if (!ML.Episode.DEFAULT_VIDEO.equals(oldVideoName)) {
                MinioUtil.delete(oldVideoName, ML.MinIO.EPISODE_VIDEO_DIR, ML.MinIO.BUCKET_NAME);
            }
            // MinIO上传新文件
            MinioUtil.upload(newFile, newVideoName, ML.MinIO.EPISODE_VIDEO_DIR, ML.MinIO.BUCKET_NAME);
        } catch (Exception e) {
            throw new ServerErrorException("MinIO操作失败：" + e.getMessage());
        }
        // 返回新文件名
        return newVideoName;
    }
    @Override
    public List<EpisodeExcelDTO> getExcelData() {
        // 加1层递归深度: episode -> season -> course -> category
        RelationManager.setMaxDepth(4);
        // 查询全部集次记录
        // select * from episode
        List<Episode> episodes = mapper.selectAllWithRelations();
        // 类型转换：List<Episode> -> List<EpisodeExcelDTO>
        List<EpisodeExcelDTO> result = new ArrayList<>();
        episodes.forEach(episode -> {
            EpisodeExcelDTO episodeExcelDTO = BeanUtil.copyProperties(episode, EpisodeExcelDTO.class);
            Season season = episode.getSeason();
            episodeExcelDTO.setSeasonTitle(season.getTitle());
            episodeExcelDTO.setSeasonInfo(season.getInfo());
            Course course = season.getCourse();
            episodeExcelDTO.setCourseTitle(course.getTitle());
            episodeExcelDTO.setCourseInfo(course.getInfo());
            episodeExcelDTO.setPrice(course.getPrice());
            episodeExcelDTO.setAuthor(course.getAuthor());
            Category category = course.getCategory();
            episodeExcelDTO.setCategoryTitle(category.getTitle());
            result.add(episodeExcelDTO);
        });
        return result;
    }
}
