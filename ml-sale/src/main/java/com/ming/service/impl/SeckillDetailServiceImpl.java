package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.dto.SeckillDetailInsertDTO;
import com.ming.dto.SeckillDetailPageDTO;
import com.ming.dto.SeckillDetailUpdateDTO;
import com.ming.entity.Course;
import com.ming.exception.IllegalParamException;
import com.ming.feign.CourseFeign;
import com.ming.vo.SeckillDetailSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.SeckillDetail;
import com.ming.mapper.SeckillDetailMapper;
import com.ming.service.SeckillDetailService;
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

/**
 * 秒杀明细表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "seckillDetail")
public class SeckillDetailServiceImpl extends ServiceImpl<SeckillDetailMapper, SeckillDetail>  implements SeckillDetailService{


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
    public boolean update(SeckillDetail entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(SeckillDetail entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<SeckillDetail> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public SeckillDetail getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public SeckillDetail getOne(QueryWrapper query) {
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
    public List<SeckillDetail> list(QueryWrapper query) {
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
    public List<SeckillDetail> listByIds(Collection<? extends Serializable> ids) {
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
    private CourseFeign courseFeign;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(SeckillDetailInsertDTO dto) {
        // 课程查重
        // select count(*) from seckill_detail where fk_seckill_id = ? and fk_course_id = ?
        Long fkCourseId = dto.getFkCourseId();
        if (QueryChain.of(mapper)
                .where(SECKILL_DETAIL.FK_SECKILL_ID.eq(dto.getFkSeckillId()))
                .and(SECKILL_DETAIL.FK_COURSE_ID.eq(fkCourseId))
                .exists()) {
            throw new IllegalParamException("秒杀活动明细记录已存在");
        }
        // 组装实体类
        SeckillDetail seckillDetail = BeanUtil.copyProperties(dto, SeckillDetail.class);
        Course course = courseFeign.select(fkCourseId).getData();
        if (ObjectUtil.isNull(course)) {
            throw new IllegalParamException("课程不存在");
        }
        seckillDetail.setCourseTitle(course.getTitle());
        seckillDetail.setCourseCover(course.getCover());
        seckillDetail.setCoursePrice(course.getPrice());
        seckillDetail.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        seckillDetail.setCreated(LocalDateTime.now());
        seckillDetail.setUpdated(LocalDateTime.now());
        // insert into seckill_detail (fk_seckill_id, fk_course_id, course_title, course_cover, course_price, sk_price, sk_count, info, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(seckillDetail) > 0;
    }

    @Cacheable(key = "'detail:' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public SeckillDetail select(Long id) {
        // select * from seckill_detail where id = ?
        SeckillDetail seckillDetail = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(seckillDetail)) {
            throw new IllegalParamException("秒杀活动明细不存在");
        }
        return seckillDetail;
    }

    @Cacheable(key = "'detail:' + #root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<SeckillDetail> page(SeckillDetailPageDTO dto) {
        QueryChain<SeckillDetail> queryChain = QueryChain.of(mapper);
        // seckillId条件
        Long seckillId = dto.getSeckillId();
        if (ObjectUtil.isNotNull(seckillId)) {
            queryChain.where(SECKILL_DETAIL.FK_SECKILL_ID.eq(seckillId));
        }
        // courseTitle条件
        String courseTitle = dto.getCourseTitle();
        if (ObjectUtil.isNotNull(courseTitle)) {
            queryChain.where(SECKILL_DETAIL.COURSE_TITLE.like(courseTitle));
        }
        // DB分页
        Page<SeckillDetail> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @Cacheable(key = "'detail:' + #root.methodName", unless = "#result == null")
    @Override
    public List<SeckillDetailSimpleListVO> simpleList() {
        // select * from seckill_detail
        return QueryChain.of(mapper)
                .withRelations()
                .listAs(SeckillDetailSimpleListVO.class);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(SeckillDetailUpdateDTO dto) {
        // 课程查重
        // select count(*) from seckill_detail where fk_course_id = ? and fk_seckill_id = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(SECKILL_DETAIL.FK_COURSE_ID.eq(dto.getFkCourseId()))
                .and(SECKILL_DETAIL.FK_SECKILL_ID.eq(dto.getFkSeckillId()))
                .and(SECKILL_DETAIL.ID.ne(dto.getId()))
                .exists()) {
            throw new IllegalParamException("秒杀活动明细记录已存在");
        }
        // 组装实体类
        SeckillDetail seckillDetail = BeanUtil.copyProperties(dto, SeckillDetail.class);
        Course course = courseFeign.select(dto.getFkCourseId()).getData();
        if (ObjectUtil.isNull(course)) {
            throw new IllegalParamException("课程不存在");
        }
        seckillDetail.setCourseTitle(course.getTitle());
        seckillDetail.setCourseCover(course.getCover());
        seckillDetail.setCoursePrice(course.getPrice());
        seckillDetail.setUpdated(LocalDateTime.now());
        // update seckill_detail set fk_seckill_id = ?, fk_course_id = ?, course_title = ?, course_cover = ?, course_price = ?, sk_price = ?, sk_count = ?, info = ?, updated = ? where id = ?
        return UpdateChain.of(seckillDetail)
                .where(SECKILL_DETAIL.ID.eq(dto.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from seckill_detail where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from seckill_detail where id in (?, ?, ?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
}
