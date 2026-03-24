package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ming.dto.CartInsertDTO;
import com.ming.dto.CartPageDTO;
import com.ming.dto.CartUpdateDTO;
import com.ming.entity.Course;
import com.ming.entity.User;
import com.ming.exception.IllegalParamException;
import com.ming.exception.ServerErrorException;
import com.ming.feign.CourseFeign;
import com.ming.feign.UserFeign;
import com.ming.vo.CartSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Cart;
import com.ming.mapper.CartMapper;
import com.ming.service.CartService;
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

import static com.ming.entity.table.CartTableDef.CART;
import static com.mybatisflex.core.query.QueryMethods.sum;

/**
 * 购物车表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "cart")
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart>  implements CartService{


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
    public boolean update(Cart entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Cart entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Cart> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Cart getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Cart getOne(QueryWrapper query) {
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
    public List<Cart> list(QueryWrapper query) {
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
    public List<Cart> listByIds(Collection<? extends Serializable> ids) {
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
    @Resource
    private CourseFeign courseFeign;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(CartInsertDTO dto) {

        // 购物车记录查重
        // select count(*) from cart where fk_user_id = ? and fk_course_id = ?
        if (QueryChain.of(mapper)
                .where(CART.FK_USER_ID.eq(dto.getFkUserId()))
                .and(CART.FK_COURSE_ID.eq(dto.getFkCourseId()))
                .exists()) {
            throw new IllegalParamException("购物车记录已存在");
        }

        // 组装实体类
        Cart cart = BeanUtil.copyProperties(dto, Cart.class);
        User user = userFeign.select(cart.getFkUserId()).getData();
        if (ObjectUtil.isNull(user)) {
            throw new IllegalParamException("用户不存在");
        }
        cart.setUsername(user.getUsername());
        Course course = courseFeign.select(cart.getFkCourseId()).getData();
        if (ObjectUtil.isNull(course)) {
            throw new IllegalParamException("课程不存在");
        }
        cart.setCourseTitle(course.getTitle());
        cart.setCourseCover(course.getCover());
        cart.setCoursePrice(course.getPrice());
        cart.setCreated(LocalDateTime.now());
        cart.setUpdated(LocalDateTime.now());
        // insert into cart (fk_user_id, username, fk_course_id, course_title, course_cover, course_price, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(cart) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Cart select(Long id) {
        // select * from cart where id = ?
        Cart cart = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(cart)) {
            throw new ServerErrorException("购物车记录不存在");
        }
        return cart;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<CartSimpleListVO> simpleList() {
        // select * from cart
        return QueryChain.of(mapper)
                .withRelations()
                .listAs(CartSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Cart> page(CartPageDTO dto) {
        QueryChain<Cart> queryChain = QueryChain.of(mapper);
        // username条件
        String username = dto.getUsername();
        if (ObjectUtil.isNotNull(username)) {
            queryChain.where(CART.USERNAME.like(username));
        }
        // courseTitle条件
        String courseTitle = dto.getCourseTitle();
        if (ObjectUtil.isNotNull(courseTitle)) {
            queryChain.where(CART.COURSE_TITLE.like(courseTitle));
        }
        // DB分页
        Page<Cart> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(CartUpdateDTO dto) {
        // 购物车记录查重
        // select count(*) from cart where fk_user_id = ? and fk_course_id = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(CART.FK_USER_ID.eq(dto.getFkUserId()))
                .and(CART.FK_COURSE_ID.eq(dto.getFkCourseId()))
                .and(CART.ID.ne(dto.getId()))
                .exists()) {
            throw new IllegalParamException("购物车记录已存在");
        }
        // 组装实体类
        Cart cart = BeanUtil.copyProperties(dto, Cart.class);
        User user = userFeign.select(cart.getFkUserId()).getData();
        if (ObjectUtil.isNull(user)) {
            throw new IllegalParamException("用户不存在");
        }
        cart.setUsername(user.getUsername());
        Course course = courseFeign.select(cart.getFkCourseId()).getData();
        if (ObjectUtil.isNull(course)) {
            throw new IllegalParamException("课程不存在");
        }
        cart.setCourseTitle(course.getTitle());
        cart.setCourseCover(course.getCover());
        cart.setCoursePrice(course.getPrice());
        cart.setUpdated(LocalDateTime.now());
        // update cart set fk_user_id = ?, username = ?, fk_course_id = ?, course_title = ?, course_cover = ?, course_price = ?, updated = ? where id = ?
        return UpdateChain.of(cart)
                .where(CART.ID.eq(cart.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from cart where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from cart where id in (?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Cacheable(key = "#root.methodName + ':' + #p0", condition = "#p0!= null", unless = "#result == null")
    @Override
    public double totalAmountByUserId(Long userId) {
        // 计算购物车记录总金额
        // select sum(course_price) from cart where fk_user_id = ?
        Double result = QueryChain.of(mapper)
                .select(sum(CART.COURSE_PRICE))
                .where(CART.FK_USER_ID.eq(userId))
                .objAs(Double.class);
        // 处理空指针异常
        return ObjectUtil.isNotNull(result) ? result : 0.0;
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean clearByUserId(Long userId) {
        // delete from cart where fk_user_id = ?
        return UpdateChain.of(mapper)
                .where(CART.FK_USER_ID.eq(userId))
                .remove();
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteByUserIdAndCourseIds(Long userId, List<Long> courseIds) {
        // 根据用户主键和课程主键列表删除购物车记录
        // delete from cart where fk_user_id = ? and fk_course_id in (?)
        return UpdateChain.of(mapper)
                .where(CART.FK_USER_ID.eq(userId))
                .and(CART.FK_COURSE_ID.in(courseIds))
                .remove();
    }
}
