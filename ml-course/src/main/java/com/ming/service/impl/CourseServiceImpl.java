package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.constant.ML;
import com.ming.dto.CourseInsertDTO;
import com.ming.dto.CoursePageDTO;
import com.ming.dto.CourseUpdateDTO;
import com.ming.exception.RepeatRecordException;
import com.ming.exception.ServerErrorException;
import com.ming.mapper.EpisodeMapper;
import com.ming.mapper.SeasonMapper;
import com.ming.util.MinioUtil;
import com.ming.vo.CourseSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Course;
import com.ming.mapper.CourseMapper;
import com.ming.service.CourseService;
import jakarta.annotation.Resource;
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
import java.util.Collection;
import java.util.List;

import static com.ming.entity.table.CourseTableDef.COURSE;
import static com.ming.entity.table.EpisodeTableDef.EPISODE;
import static com.ming.entity.table.SeasonTableDef.SEASON;

/**
 * 课程表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "course")
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>  implements CourseService{


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
    public boolean update(Course entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Course entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Course> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Course getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Course getOne(QueryWrapper query) {
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
    public List<Course> list(QueryWrapper query) {
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
    public List<Course> listByIds(Collection<? extends Serializable> ids) {
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
    private SeasonMapper seasonMapper;
    @Resource
    private EpisodeMapper episodeMapper;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(CourseInsertDTO dto) {
        // 标题查重
        // select count(*) from course where title = ?
        if (QueryChain.of(mapper)
                .where(COURSE.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Course course = BeanUtil.copyProperties(dto, Course.class);
        course.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        course.setSummary(ML.Course.DEFAULT_SUMMARY);
        course.setCover(ML.Course.DEFAULT_COVER);
        course.setCreated(LocalDateTime.now());
        course.setUpdated(LocalDateTime.now());
        // insert into course (title, author, fk_category_id, info, summary, cover, price, idx, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(course) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Course select(Long id) {
        // 指定联查字段
        RelationManager.addQueryRelations("category", "seasons", "episodes");
        // select * from course where id = ?
        Course course = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(course)) {
            throw new ServerErrorException("记录不存在");
        }
        return course;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<CourseSimpleListVO> simpleList() {
        // select id, title, price from course order by idx asc, id desc
        return QueryChain.of(mapper)
                .orderBy(COURSE.IDX.asc(), COURSE.ID.desc())
                .withRelations()
                .listAs(CourseSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Course> page(CoursePageDTO dto) {
        // 指定联查字段
        RelationManager.addQueryRelations("category", "seasons", "episodes");
        // select * from course order by fk_category_id asc, idx asc, id desc
        QueryChain<Course> queryChain = QueryChain.of(mapper)
                .orderBy(COURSE.FK_CATEGORY_ID.asc(), COURSE.IDX.asc(), COURSE.ID.desc());
        // title条件
        String title = dto.getTitle();
        if (ObjectUtil.isNotNull(title)) {
            queryChain.where(COURSE.TITLE.like(title));
        }
        // categoryId条件
        Long categoryId = dto.getFkCategoryId();
        if (ObjectUtil.isNotNull(categoryId)) {
            queryChain.where(COURSE.FK_CATEGORY_ID.eq(categoryId));
        }
        // DB分页
        Page<Course> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(CourseUpdateDTO dto) {
        // 标题查重
        // select count(1) from course where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(COURSE.TITLE.eq(dto.getTitle()))
                .and(COURSE.ID.ne(dto.getId()))
                .exists()) {
            throw new RepeatRecordException("标题已存在");
        }
        // 组装实体类
        Course course = BeanUtil.copyProperties(dto, Course.class);
        course.setUpdated(LocalDateTime.now());
        // update course set title = ?, author = ?, fk_category_id = ?, info = ?, summary = ?, cover = ?, price = ?, idx = ?, updated = ? where id = ?
        return UpdateChain.of(course)
                .where(COURSE.ID.eq(course.getId()))
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // 通过课程主键查询全部季次的ID列表
        // select id from season where fk_course_id = ?
        List<Long> seasonIds = QueryChain.of(seasonMapper)
                .select(SEASON.ID)
                .where(SEASON.FK_COURSE_ID.eq(id))
                .objListAs(Long.class);
        // 存在季记录时，批量删除季
        this.clearSeasonAndEpisode(seasonIds);
        // 删除课程
        // delete from course where id = ?
        return mapper.deleteById(id) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // 通过课程主键列表批量查询全部季次的ID列表
        // select id from season where fk_course_id in (?)
        List<Long> seasonIds = QueryChain.of(seasonMapper)
                .select(SEASON.ID)
                .where(SEASON.FK_COURSE_ID.in(ids))
                .objListAs(Long.class);
        // 存在季记录时，批量删除季
        this.clearSeasonAndEpisode(seasonIds);
        // 批量删除课程
        // delete from course where id in (?)
        return mapper.deleteBatchByIds(ids) > 0;
    }

    /**
     * 根据季次ID列表，清空全部季次记录及每个季次中的集次记录
     *
     * @param seasonIds 季次主键列表
     */
    private void clearSeasonAndEpisode(List<Long> seasonIds) {
        // 存在季次时，批量删除季次
        if (ObjectUtil.isNotEmpty(seasonIds)) {
            // 通过季次主键列表查询全部集次的ID列表
            // select id from episode where fk_season_id in (?)
            List<Long> episodeIds = QueryChain.of(episodeMapper).select(EPISODE.ID).where(EPISODE.FK_SEASON_ID.in(seasonIds)).objListAs(Long.class);
            // 存在集次时，批量删除集次
            if (ObjectUtil.isNotEmpty(episodeIds)) {
                if (episodeMapper.deleteBatchByIds(episodeIds) <= 0) {
                    throw new ServerErrorException("集次删除失败");
                }
            }
            // 批量删除季次
            if (seasonMapper.deleteBatchByIds(seasonIds) <= 0) {
                throw new ServerErrorException("季次删除失败");
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(allEntries = true)
    public String uploadCover(MultipartFile newFile, Long id) {
        // 按主键查询记录
        // select * from course where id = ?
        Course course = mapper.selectOneById(id);
        if (ObjectUtil.isNull(course)) {
            throw new ServerErrorException("记录不存在");
        }
        // 备份旧文件名
        String oldFileName = course.getCover();
        // 生成新文件名
        String newFileName = MinioUtil.randomFilename(newFile);
        // DB更新文件名
        course.setCover(newFileName);
        if (mapper.update(course) <= 0) {
            throw new ServerErrorException("DB更新失败");
        }
        try {
            // MinIO删除旧文件（默认文件不删除）
            if (!ML.Course.DEFAULT_COVER.equals(oldFileName)) {
                MinioUtil.delete(oldFileName, ML.MinIO.COURSE_COVER_DIR, ML.MinIO.BUCKET_NAME);
            }
            // MinIO上传新文件
            MinioUtil.upload(newFile, newFileName, ML.MinIO.COURSE_COVER_DIR, ML.MinIO.BUCKET_NAME);
        } catch (Exception e) {
            throw new ServerErrorException("MinIO操作失败" + e.getMessage());
        }
        // 返回新文件名
        return newFileName;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(allEntries = true)
    public String uploadSummary(MultipartFile newFile, Long id) {
        // 按主键查询记录
        // select * from course where id = ?
        Course course = mapper.selectOneById(id);
        if (ObjectUtil.isNull(course)) {
            throw new ServerErrorException("记录不存在");
        }
        // 备份旧文件名
        String oldFileName = course.getSummary();
        // 生成新文件名
        String newFileName = MinioUtil.randomFilename(newFile);
        // DB更新文件名
        course.setSummary(newFileName);
        if (mapper.update(course) <= 0) {
            throw new ServerErrorException("DB更新失败");
        }
        try {
            // MinIO删除旧文件（默认文件不删除）
            if (!ML.Course.DEFAULT_SUMMARY.equals(oldFileName)) {
                MinioUtil.delete(oldFileName, ML.MinIO.COURSE_SUMMARY_DIR, ML.MinIO.BUCKET_NAME);
            }
            // MinIO上传新文件
            MinioUtil.upload(newFile, newFileName, ML.MinIO.COURSE_SUMMARY_DIR, ML.MinIO.BUCKET_NAME);
        } catch (Exception e) {
            throw new ServerErrorException("MinIO操作失败" + e.getMessage());
        }
        // 返回文件名
        return newFileName;
    }

}
