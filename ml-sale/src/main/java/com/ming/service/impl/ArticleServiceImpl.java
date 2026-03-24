package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ming.dto.ArticleInsertDTO;
import com.ming.dto.ArticlePageDTO;
import com.ming.dto.ArticleUpdateDTO;
import com.ming.exception.IllegalParamException;
import com.ming.exception.ServerErrorException;
import com.ming.vo.ArticleSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Article;
import com.ming.mapper.ArticleMapper;
import com.ming.service.ArticleService;
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

import static com.ming.entity.table.ArticleTableDef.ARTICLE;

/**
 * 新闻表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "article")
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>  implements ArticleService{


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
    public boolean update(Article entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Article entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Article> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Article getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Article getOne(QueryWrapper query) {
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
    public List<Article> list(QueryWrapper query) {
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
    public List<Article> listByIds(Collection<? extends Serializable> ids) {
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
    public boolean insert(ArticleInsertDTO dto) {
        // 标题查重
        // select count(*) from article where title = ?
        if (QueryChain.of(mapper)
                .where(ARTICLE.TITLE.eq(dto.getTitle()))
                .exists()) {
            throw new IllegalParamException("标题已存在");
        }
        // 组装实体类
        Article article = BeanUtil.copyProperties(dto, Article.class);
        article.setCreated(LocalDateTime.now());
        article.setUpdated(LocalDateTime.now());
        // insert into article (idx, title, content, created, updated) values (?, ?, ?, ?, ?)
        return mapper.insert(article) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Article select(Long id) {
        // select * from article where id = ?
        Article article = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(article)) {
            throw new ServerErrorException("新闻不存在");
        }
        return article;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<ArticleSimpleListVO> simpleList() {
        // select * from article order by idx asc, id desc
        return QueryChain.of(mapper)
                .orderBy(ARTICLE.IDX.asc(), ARTICLE.ID.desc())
                .listAs(ArticleSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Article> page(ArticlePageDTO dto) {
        QueryChain<Article> queryChain = QueryChain.of(mapper)
                .orderBy(ARTICLE.IDX.asc(), ARTICLE.ID.desc());
        // title 条件
        String title = dto.getTitle();
        if (ObjectUtil.isNotNull(title)) {
            queryChain.where(ARTICLE.TITLE.like(title));
        }
        // DB分页
        Page<Article> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(ArticleUpdateDTO dto) {
        // 标题查重
        // select count(*) from article where title = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(ARTICLE.TITLE.eq(dto.getTitle()))
                .and(ARTICLE.ID.ne(dto.getId()))
                .exists()) {
            throw new IllegalParamException("标题已存在");
        }
        // 组装实体类
        Article article = BeanUtil.copyProperties(dto, Article.class);
        article.setUpdated(LocalDateTime.now());
        // update article set title = ?, content = ?, updated = ? where id = ?
        return UpdateChain.of(article)
                .where(ARTICLE.ID.eq(article.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from article where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from article where id in (?, ?, ?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Cacheable(key = "#root.methodName + ':' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public List<Article> top(Long n) {
        // select * from article order by idx asc, id desc limit ?
        return QueryChain.of(mapper)
                .orderBy(ARTICLE.IDX.asc(), ARTICLE.ID.desc())
                .limit(n)
                .list();
    }
}
