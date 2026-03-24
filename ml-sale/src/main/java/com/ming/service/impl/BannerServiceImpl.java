package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.constant.ML;
import com.ming.dto.BannerInsertDTO;
import com.ming.dto.BannerPageDTO;
import com.ming.dto.BannerUpdateDTO;
import com.ming.exception.ServerErrorException;
import com.ming.util.MinioUtil;
import com.ming.vo.BannerSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Banner;
import com.ming.mapper.BannerMapper;
import com.ming.service.BannerService;
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

import static com.ming.entity.table.BannerTableDef.BANNER;

/**
 * 横幅表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "banner")
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner>  implements BannerService{


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
    public boolean update(Banner entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Banner entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Banner> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Banner getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Banner getOne(QueryWrapper query) {
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
    public List<Banner> list(QueryWrapper query) {
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
    public List<Banner> listByIds(Collection<? extends Serializable> ids) {
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
    public boolean insert(BannerInsertDTO dto) {
        // 组装实体类
        Banner banner = BeanUtil.copyProperties(dto, Banner.class);
        banner.setUrl(ML.Banner.DEFAULT_BANNER);
        banner.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        banner.setCreated(LocalDateTime.now());
        banner.setUpdated(LocalDateTime.now());
        // insert into banner (url, info, idx, created, updated) values (?, ?, ?, ?, ?)
        return mapper.insert(banner) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Banner select(Long id) {
        // select * from banner where id = ?
        Banner banner = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(banner)) {
            throw new ServerErrorException("横幅不存在");
        }
        return banner;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<BannerSimpleListVO> simpleList() {
        // select id, url from banner order by idx asc, id desc
        return QueryChain.of(mapper)
                .orderBy(BANNER.IDX.asc(), BANNER.ID.desc())
                .listAs(BannerSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Banner> page(BannerPageDTO dto) {
        QueryChain<Banner> queryChain = QueryChain.of(mapper)
                .orderBy(BANNER.IDX.asc(), BANNER.ID.desc());
        // DB分页
        Page<Banner> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(BannerUpdateDTO dto) {
        // 组装实体类
        Banner banner = BeanUtil.copyProperties(dto, Banner.class);
        banner.setUpdated(LocalDateTime.now());
        // update banner set url = ?, info = ?, idx = ?, updated = ? where id = ?
        return UpdateChain.of(banner)
                .where(BANNER.ID.eq(banner.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from banner where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from banner where id in (?, ?, ?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Cacheable(key = "#root.methodName + ':' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public List<Banner> top(Long n) {
        // select * from banner order by idx asc, id desc limit ?
        return QueryChain.of(mapper)
                .orderBy(BANNER.IDX.asc(), BANNER.ID.desc())
                .limit(n)
                .list();
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    @CacheEvict(allEntries = true)
    public String uploadBanner(MultipartFile newFile, Long id) {
        // 按主键查询记录
        // select * from banner where id = ?
        Banner banner = mapper.selectOneById(id);
        if (ObjectUtil.isNull(banner)) {
            throw new ServerErrorException("记录不存在");
        }
        // 备份旧文件名
        String oldFileName = banner.getUrl();
        // 生成新文件名
        String newFileName = MinioUtil.randomFilename(newFile);
        // DB更新文件名
        banner.setUrl(newFileName);
        if (mapper.update(banner) <= 0) {
            throw new ServerErrorException("DB更新失败");
        }
        try {
            // MinIO删除旧文件（默认文件不删除）
            if (!ML.Banner.DEFAULT_BANNER.equals(oldFileName)) {
                MinioUtil.delete(oldFileName, ML.MinIO.BANNER_DIR, ML.MinIO.BUCKET_NAME);
            }
            // MinIO上传新文件
            MinioUtil.upload(newFile, newFileName, ML.MinIO.BANNER_DIR, ML.MinIO.BUCKET_NAME);
        } catch (Exception e) {
            throw new ServerErrorException("MinIO操作失败：" + e.getMessage());
        }
        // 返回新文件名
        return newFileName;
    }

}
