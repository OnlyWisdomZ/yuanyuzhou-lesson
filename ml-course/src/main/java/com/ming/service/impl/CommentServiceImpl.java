package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ming.dto.CommentInsertDTO;
import com.ming.dto.CommentPageDTO;
import com.ming.dto.CommentUpdateDTO;
import com.ming.entity.User;
import com.ming.exception.ServerErrorException;
import com.ming.feign.UserFeign;
import com.ming.result.Result;
import com.ming.vo.CommentSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.relation.RelationManager;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Comment;
import com.ming.mapper.CommentMapper;
import com.ming.service.CommentService;
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

import static com.ming.entity.table.CommentTableDef.COMMENT;

/**
 * 评论表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "comment")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>  implements CommentService{


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
    public boolean update(Comment entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Comment entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Comment> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Comment getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Comment getOne(QueryWrapper query) {
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
    public List<Comment> list(QueryWrapper query) {
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
    public List<Comment> listByIds(Collection<? extends Serializable> ids) {
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
    public boolean insert(CommentInsertDTO dto) {
        // 组装实体类
        Comment comment = BeanUtil.copyProperties(dto, Comment.class);
        User user = userFeign.select(dto.getFkUserId()).getData();
        if (ObjectUtil.isNull(user)) {
            throw new ServerErrorException("用户不存在");
        }
        comment.setNickname(user.getNickname());
        comment.setAvatar(user.getAvatar());
        comment.setProvince(user.getProvince());
        comment.setCreated(LocalDateTime.now());
        comment.setUpdated(LocalDateTime.now());
        // insert into comment (fk_episode_id, fk_user_id, nickname, avatar, province, pid, content, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(comment) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Comment select(Long id) {
        // 指定联查字段
        RelationManager.addQueryRelations("episode");
        // select * from comment where id = ?
        Comment comment = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(comment)) {
            throw new ServerErrorException("记录不存在");
        }
        return comment;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<CommentSimpleListVO> simpleList() {
        // select * from comment
        return QueryChain.of(mapper).listAs(CommentSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Comment> page(CommentPageDTO dto) {
        // 指定联查字段
        RelationManager.addQueryRelations("episode");
        QueryChain<Comment> queryChain = QueryChain.of(mapper);
        // pid条件
        Long pid = dto.getPid();
        if (ObjectUtil.isNotNull(pid)) {
            queryChain.where(COMMENT.PID.eq(pid));
        }
        // episodeId条件
        Long episodeId = dto.getFkEpisodeId();
        if (ObjectUtil.isNotNull(episodeId)) {
            queryChain.where(COMMENT.FK_EPISODE_ID.eq(episodeId));
        }
        // nickname条件
        String nickname = dto.getNickname();
        if (ObjectUtil.isNotNull(nickname)) {
            queryChain.where(COMMENT.NICKNAME.like(nickname));
        }
        // DB分页
        Page<Comment> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(CommentUpdateDTO dto) {
        // 组装实体类
        Comment comment = BeanUtil.copyProperties(dto, Comment.class);
        Result<User> user = userFeign.select(dto.getFkUserId());
        if (ObjectUtil.isNull(user)) {
            throw new ServerErrorException("用户不存在");
        }
        comment.setNickname(user.getData().getNickname());
        comment.setAvatar(user.getData().getAvatar());
        comment.setProvince(user.getData().getProvince());
        comment.setUpdated(LocalDateTime.now());
        // update comment set fk_episode_id = ?, fk_user_id = ?, pid = ?, content = ?, nickname = ?, avatar = ?, province = ?, updated = ? where id = ?
        return UpdateChain.of(comment)
                .where(COMMENT.ID.eq(comment.getId()))
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // 删除全部子评论
        // delete from comment where pid = id
        UpdateChain.of(mapper)
                .where(COMMENT.PID.eq(id))
                .remove();
        // 删除父评论
        // delete from comment where id = id
        return mapper.deleteById(id) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // 删除全部子评论
        // delete from comment where pid in (ids)
        UpdateChain.of(mapper)
                .where(COMMENT.PID.in(ids))
                .remove();
        // 删除父评论
        // delete from comment where id in (ids)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteByUserId(Long userId) {
        // 查询该用户的所有评论ID列表
        // select id from comment where fk_user_id = userId
        List<Long> commentIds = QueryChain.of(mapper)
                .select(COMMENT.ID)
                .where(COMMENT.FK_USER_ID.eq(userId))
                .listAs(Long.class);
        // 无评论记录
        if (CollUtil.isEmpty(commentIds)) return true;
        // 查询子评论ID列表
        // select id from comment where pid in (commentIds)
        List<Long> subCommentIds = QueryChain.of(mapper)
                .select(COMMENT.ID)
                .where(COMMENT.PID.in(commentIds))
                .listAs(Long.class);
        // 组合父评论和子评论的ID列表
        if (CollUtil.isNotEmpty(subCommentIds)) {
            commentIds.addAll(subCommentIds);
        }
        // 删除组合后的评论
        // delete from comment where id in (commentIds)
        return mapper.deleteBatchByIds(commentIds) > 0;
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteByUserIds(List<Long> userIds) {
        // 查询该用户的所有评论ID列表
        // select id from comment where fk_user_id in (userIds)
        List<Long> commentIds = QueryChain.of(mapper)
                .select(COMMENT.ID)
                .where(COMMENT.FK_USER_ID.in(userIds))
                .listAs(Long.class);
        // 无评论记录
        if (CollUtil.isEmpty(commentIds)) return true;
        // 查询子评论ID列表
        // select id from comment where pid in (commentIds)
        List<Long> subCommentIds = QueryChain.of(mapper)
                .select(COMMENT.ID)
                .where(COMMENT.PID.in(commentIds))
                .listAs(Long.class);
        // 组合父评论和子评论的ID列表
        if (CollUtil.isNotEmpty(subCommentIds)) {
            commentIds.addAll(subCommentIds);
        }
        // 删除组合后的评论
        // delete from comment where id in (commentIds)
        return mapper.deleteBatchByIds(commentIds) > 0;
    }
}
