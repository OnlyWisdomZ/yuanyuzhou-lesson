package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.constant.ML;
import com.ming.dto.OrderInsertDTO;
import com.ming.dto.OrderPageDTO;
import com.ming.dto.OrderUpdateDTO;
import com.ming.dto.PrePayDTO;
import com.ming.entity.Course;
import com.ming.entity.OrderDetail;
import com.ming.entity.User;
import com.ming.exception.IllegalParamException;
import com.ming.exception.ServerErrorException;
import com.ming.feign.CourseFeign;
import com.ming.feign.UserFeign;
import com.ming.mapper.OrderDetailMapper;
import com.ming.service.CartService;
import com.ming.vo.OrderSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.query.QueryMethods;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.Order;
import com.ming.mapper.OrderMapper;
import com.ming.service.OrderService;
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
import java.util.*;

import static com.ming.entity.table.OrderDetailTableDef.ORDER_DETAIL;
import static com.ming.entity.table.OrderTableDef.ORDER;
import static com.mybatisflex.core.query.QueryMethods.*;

/**
 * 订单表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "order")
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>  implements OrderService{


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
    public boolean update(Order entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(Order entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<Order> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public Order getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public Order getOne(QueryWrapper query) {
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
    public List<Order> list(QueryWrapper query) {
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
    public List<Order> listByIds(Collection<? extends Serializable> ids) {
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
    @Resource
    private OrderDetailMapper orderDetailMapper;
    @Resource
    private CartService cartService;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(OrderInsertDTO dto) {
        // 组装实体类
        Order order = BeanUtil.copyProperties(dto, Order.class);
        order.setSn(RandomUtil.randomNumbers(19));
        User user = userFeign.select(dto.getFkUserId()).getData();
        if (ObjectUtil.isNull(user)) {
            throw new IllegalParamException("用户不存在");
        }
        order.setUsername(user.getUsername());
        order.setInfo(StrUtil.isEmpty(dto.getInfo()) ? "暂无描述。" : dto.getInfo());
        order.setCreated(LocalDateTime.now());
        order.setUpdated(LocalDateTime.now());
        // insert into order (sn, total_amount, pay_amount, pay_type, info, status, fk_user_id, username, created, updated)
        // values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(order) > 0;
    }

    @Cacheable(key = "#p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Order select(Long id) {
        // select * from order where id = ?
        Order order = mapper.selectOneWithRelationsById(id);
        if (ObjectUtil.isNull(order)) {
            throw new IllegalParamException("订单不存在");
        }
        return order;
    }

    @Override
    @Cacheable(key = "#root.methodName", unless = "#result == null")
    public List<OrderSimpleListVO> simpleList() {
        // select * from order order by updated desc
        return QueryChain.of(mapper)
                .orderBy(ORDER.UPDATED.desc())
                .withRelations()
                .listAs(OrderSimpleListVO.class);
    }

    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<Order> page(OrderPageDTO dto) {
        QueryChain<Order> queryChain = QueryChain.of(mapper).orderBy(ORDER.UPDATED.desc());
        // sn条件
        String sn = dto.getSn();
        if (ObjectUtil.isNotEmpty(sn)) {
            queryChain.where(ORDER.SN.like(sn));
        }
        // status条件
        Integer status = dto.getStatus();
        if (ObjectUtil.isNotNull(status)) {
            queryChain.where(ORDER.STATUS.eq(status));
        }
        // username条件
        String username = dto.getUsername();
        if (ObjectUtil.isNotNull(username)) {
            queryChain.where(ORDER.USERNAME.like(username));
        }
        // DB分页
        Page<Order> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(OrderUpdateDTO dto) {
        // 组装实体类
        Order order = BeanUtil.copyProperties(dto, Order.class);
        order.setUpdated(LocalDateTime.now());
        // update order set sn = ?, total_amount = ?, pay_amount = ?, pay_type = ?, info = ?, status = ?, fk_user_id = ?, username = ?, updated = ? where id = ?
        return UpdateChain.of(order)
                .where(ORDER.ID.eq(order.getId()))
                .update();
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // 按订单主键删除订单明细记录
        // delete from order_detail where fk_order_id = ?
        UpdateChain.of(orderDetailMapper)
                .where(ORDER_DETAIL.FK_ORDER_ID.eq(id))
                .remove();
        // 按订单主键删除一条订单记录
        // delete from `order` where id = ?
        return mapper.deleteById(id) > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // 按订单主键批量删除订单明细记录
        // delete from order_detail where fk_order_id in (?, ?, ?)
        UpdateChain.of(orderDetailMapper)
                .where(ORDER_DETAIL.FK_ORDER_ID.in(ids))
                .remove();
        // 按订单主键批量删除订单记录
        // delete from `order` where id in (?, ?, ?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Cacheable(key = "#root.methodName")
    @Override
    public Map<String, Object> statistics() {
        Map<String, Object> result = new HashMap<>();
        // 统计订单支付方式比例
        // select pay_type as name, count(*) as value from `order` group by pay_type
        result.put("payTypeCount", mapper.selectListByQueryAs(QueryWrapper.create()
                .select(ORDER.PAY_TYPE.as("name"), QueryMethods.count().as("value"))
                .groupBy(ORDER.PAY_TYPE)
                .orderBy(ORDER.PAY_TYPE.asc()), Map.class));
        // 统计今日订单数
        // select count(*) from `order` where datediff(curdate(), date_format(created, '%Y-%m-%d')) = 0
        double todayCount = QueryChain.of(mapper)
                .where(dateDiff(currentDate(), dateFormat(ORDER.CREATED, "%Y-%m-%d")).eq(0))
                .count();
        // 统计昨日订单数
        // select count(*) from `order` where datediff(curdate(), date_format(created, '%Y-%m-%d')) = 1
        double yesterdayCount = QueryChain.of(mapper)
                .where(dateDiff(currentDate(), dateFormat(ORDER.CREATED, "%Y-%m-%d")).eq(1))
                .count();
        // 统计今年订单数
        // select count(*) from `order` where year(created) = year(current_date);
        double thisYearCount = QueryChain.of(mapper)
                .where(year(ORDER.CREATED).eq(year(currentDate())))
                .count();
        // 统计去年订单总数
        // select count(*) from `order` where year(created) - year(current_date) = -1;
        double lastYearCount = QueryChain.of(mapper)
                .where(year(ORDER.CREATED).subtract(year(currentDate())).eq(-1))
                .count();
        result.put("todayCount", todayCount);
        result.put("yesterdayCount", yesterdayCount);
        result.put("dayIncrease", increase(todayCount, yesterdayCount));
        result.put("thisYearCount", thisYearCount);
        result.put("lastYearCount", lastYearCount);
        result.put("yearIncrease", increase(thisYearCount, lastYearCount));
        return result;
    }

    /**
     * 计算a到b的增长率
     *
     * @param a 第一个操作数
     * @param b 第二个操作数
     * @return 保留两位小数的增长率
     */
    private static String increase(double a, double b) {
        if (b == 0) {
            return a > b ? "-100.00" : a < b ? "100.00" : "0";
        }
        return String.format("%.2f", (a - b) / b);
    }


    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(allEntries = true)
    public String prePay(PrePayDTO dto) {
        // 取出用户ID
        Long userId = dto.getFkUserId();
        // 获取该用户的全部订单ID集合
        // select id from order where fk_user_id = ?
        List<Long> orderIds = QueryChain.of(mapper)
                .select(ORDER.ID)
                .where(ORDER.FK_USER_ID.eq(userId))
                .listAs(Long.class);
        // 如果该用户存在订单记录，需要判断是否存在重复购买课程现象
        if (CollUtil.isNotEmpty(orderIds)) {
            // 获取该用户的全部已购买的课程ID集合
            // select fk_course_id from order_detail where fk_order_id in ?
            List<Long> courseIds = QueryChain.of(orderDetailMapper)
                    .select(ORDER_DETAIL.FK_COURSE_ID)
                    .where(ORDER_DETAIL.FK_ORDER_ID.in(orderIds))
                    .listAs(Long.class);
            // 判断是否存在重复购买课程现象：将courseIds变更为两个List的交集结果，有交集说明重复购买
            courseIds.retainAll(dto.getCourseIds());
            if (CollUtil.isNotEmpty(courseIds)) {
                throw new IllegalParamException("至少一门课程已购买");
            }
        }
        // 组装 entity 实体类
        Order order = BeanUtil.copyProperties(dto, Order.class);
        String sn = RandomUtil.randomNumbers(19);
        order.setSn(sn);
        order.setPayType(ML.Order.NO_PAY);
        order.setStatus(ML.Order.UNPAID);
        order.setInfo("暂无描述。");
        User user = userFeign.select(userId).getData();
        if (ObjectUtil.isNull(user)) {
            throw new IllegalParamException("用户不存在");
        }
        order.setUsername(user.getUsername());
        order.setCreated(LocalDateTime.now());
        order.setUpdated(LocalDateTime.now());
        // DB添加订单表记录
        if (mapper.insert(order) <= 0) {
            throw new ServerErrorException("DB订单添加异常");
        }
        // 批量添加订单明细表记录
        Long orderId = order.getId();
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (Long courseId : dto.getCourseIds()) {
            Course course = courseFeign.select(courseId).getData();
            if (ObjectUtil.isNull(course)) {
                throw new ServerErrorException("课程不存在");
            }
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setFkCourseId(courseId);
            orderDetail.setFkOrderId(orderId);
            orderDetail.setCourseTitle(course.getTitle());
            orderDetail.setCourseCover(course.getCover());
            orderDetail.setCoursePrice(course.getPrice());
            orderDetail.setCreated(LocalDateTime.now());
            orderDetail.setUpdated(LocalDateTime.now());
            orderDetails.add(orderDetail);
        }
        if (orderDetailMapper.insertBatch(orderDetails) <= 0) {
            throw new ServerErrorException("DB订单明细表添加异常");
        }
        // 删除购物车表记录
        cartService.deleteByUserIdAndCourseIds(userId, dto.getCourseIds());
        // todo 将订单发送到MQ，延迟15分钟后取出，若仍然是未支付状态
        return sn;
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean updateStatusBySn(String sn, Integer status) {
        // 根据订单编号更新订单状态
        return UpdateChain.of(mapper)
                .set(ORDER.STATUS, status)
                .set(ORDER.UPDATED, LocalDateTime.now())
                .where(ORDER.SN.eq(sn))
                .update();
    }
    @Override
    public boolean checkStatusBySn(String sn) {
        // 根据订单编号查询订单记录（仅查询支付成功的订单）
        Order order = QueryChain.of(mapper)
                .where(ORDER.SN.eq(sn))
                .and(ORDER.STATUS.eq(ML.Order.PAID))
                .one();
        return ObjectUtil.isNotNull(order);
    }
}
