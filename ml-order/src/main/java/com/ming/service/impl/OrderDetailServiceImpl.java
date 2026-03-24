package com.ming.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.ming.constant.ML;
import com.ming.dto.OrderDetailExcelDTO;
import com.ming.dto.OrderDetailInsertDTO;
import com.ming.dto.OrderDetailPageDTO;
import com.ming.dto.OrderDetailUpdateDTO;
import com.ming.entity.Course;
import com.ming.entity.Order;
import com.ming.exception.IllegalParamException;
import com.ming.feign.CourseFeign;
import com.ming.feign.OrderFeign;
import com.ming.vo.OrderDetailSimpleListVO;
import com.mybatisflex.core.query.QueryChain;
import com.mybatisflex.core.update.UpdateChain;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.ming.entity.OrderDetail;
import com.ming.mapper.OrderDetailMapper;
import com.ming.service.OrderDetailService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.ming.entity.table.OrderDetailTableDef.ORDER_DETAIL;

/**
 * 订单明细表 服务层实现。
 *
 * @author Ming
 * @since v1.0.0
 */
@Service
@CacheConfig(cacheNames = "orderDetail")
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>  implements OrderDetailService{


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
    public boolean update(OrderDetail entity, QueryWrapper query) {
        return super.update(entity, query);
    }

    @Override
    @CacheEvict(key = "#entity.id")
    public boolean updateById(OrderDetail entity, boolean ignoreNulls) {
        return super.updateById(entity, ignoreNulls);
    }

    @Override
    @CacheEvict(allEntries = true)
    public boolean updateBatch(Collection<OrderDetail> entities, int batchSize) {
        return super.updateBatch(entities, batchSize);
    }

    @Override
    @Cacheable(key = "#id")
    public OrderDetail getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    @Cacheable(key = "#root.methodName + ':' + #query.toSQL()")
    public OrderDetail getOne(QueryWrapper query) {
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
    public List<OrderDetail> list(QueryWrapper query) {
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
    public List<OrderDetail> listByIds(Collection<? extends Serializable> ids) {
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
    @Resource
    private OrderFeign orderFeign;

    @CacheEvict(allEntries = true)
    @Override
    public boolean insert(OrderDetailInsertDTO dto) {
        // 订单明细记录查重
        // select count(*) from order_detail where fk_order_id = ? and fk_course_id = ?
        if (QueryChain.of(mapper)
                .where(ORDER_DETAIL.FK_ORDER_ID.eq(dto.getFkOrderId()))
                .and(ORDER_DETAIL.FK_COURSE_ID.eq(dto.getFkCourseId()))
                .exists()) {
            throw new IllegalParamException("订单明细记录已存在");
        }
        // 组装实体类
        OrderDetail orderDetail = BeanUtil.copyProperties(dto, OrderDetail.class);
        Course course = courseFeign.select(dto.getFkCourseId()).getData();
        if (course == null) {
            throw new IllegalParamException("课程不存在");
        }
        orderDetail.setCourseTitle(course.getTitle());
        orderDetail.setCoursePrice(course.getPrice());
        orderDetail.setCourseCover(course.getCover());
        Order order = orderFeign.select(dto.getFkOrderId()).getData();
        if (order == null) {
            throw new IllegalParamException("订单不存在");
        }

        orderDetail.setCreated(LocalDateTime.now());
        orderDetail.setUpdated(LocalDateTime.now());
        // insert into order_detail (fk_order_id, fk_course_id, sn, course_title, course_cover, course_price, created, updated) values (?, ?, ?, ?, ?, ?, ?, ?)
        return mapper.insert(orderDetail) > 0;
    }

    @Cacheable(key = "'detail:' + #p0", condition = "#p0 != null", unless = "#result == null")
    @Override
    public OrderDetail select(Long id) {
        // select * from order_detail where id = ?
        OrderDetail orderDetail = mapper.selectOneWithRelationsById(id);
        if (orderDetail == null) {
            throw new IllegalParamException("订单明细不存在");
        }
        return orderDetail;
    }

    @Override
    @Cacheable(key = "'detail:' + #root.methodName", unless = "#result == null")
    public List<OrderDetailSimpleListVO> simpleList() {
        // select * from order_detail order by updated desc
        return QueryChain.of(mapper)
                .orderBy(ORDER_DETAIL.UPDATED.desc())
                .withRelations()
                .listAs(OrderDetailSimpleListVO.class);
    }

    @Cacheable(key = "'detail:' + #root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public Page<OrderDetail> page(OrderDetailPageDTO dto) {
        QueryChain<OrderDetail> queryChain = QueryChain.of(mapper).orderBy(ORDER_DETAIL.UPDATED.desc());
        // courseTitle条件
        String courseTitle = dto.getCourseTitle();
        if (ObjectUtil.isNotNull(courseTitle)) {
            queryChain.where(ORDER_DETAIL.COURSE_TITLE.like(courseTitle));
        }
        // DB分页
        Page<OrderDetail> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        return queryChain.withRelations().page(page);
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(OrderDetailUpdateDTO dto) {
        // 订单明细记录查重
        // select count(*) from order_detail where fk_order_id = ? and fk_course_id = ? and id <> ?
        if (QueryChain.of(mapper)
                .where(ORDER_DETAIL.FK_ORDER_ID.eq(dto.getFkOrderId()))
                .and(ORDER_DETAIL.FK_COURSE_ID.eq(dto.getFkCourseId()))
                .and(ORDER_DETAIL.ID.ne(dto.getId()))
                .exists()) {
            throw new IllegalParamException("订单明细记录已存在");
        }
        // 组装实体类
        OrderDetail orderDetail = BeanUtil.copyProperties(dto, OrderDetail.class);
        Course course = courseFeign.select(dto.getFkCourseId()).getData();
        if (ObjectUtil.isNull(course)) {
            throw new IllegalParamException("课程不存在");
        }
        orderDetail.setCourseTitle(course.getTitle());
        orderDetail.setCoursePrice(course.getPrice());
        orderDetail.setCourseCover(course.getCover());
        orderDetail.setUpdated(LocalDateTime.now());
        // update order_detail set fk_order_id = ?, fk_course_id = ?, sn = ?, course_title = ?, course_cover = ?, course_price = ?, updated = ? where id = ?
        return UpdateChain.of(orderDetail)
                .where(ORDER_DETAIL.ID.eq(orderDetail.getId()))
                .update();
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Long id) {
        // delete from order_detail where id = ?
        return mapper.deleteById(id) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean deleteBatch(List<Long> ids) {
        // delete from order_detail where id in (?, ?, ?)
        return mapper.deleteBatchByIds(ids) > 0;
    }
    @Override
    public List<OrderDetailExcelDTO> getExcelData() {
        // 查询全部订单明细记录
        // select * from order_detail
        List<OrderDetail> orderDetails = mapper.selectAllWithRelations();
        // 类型转换：List<OrderDetail> -> List<OrderDetailExcelDTO>
        List<OrderDetailExcelDTO> result = new ArrayList<>();
        orderDetails.forEach(orderDetail -> {
            OrderDetailExcelDTO excel = BeanUtil.copyProperties(orderDetail, OrderDetailExcelDTO.class);
            Order order = orderDetail.getOrder();
            excel.setSn(order.getSn());
            excel.setTotalAmount(order.getTotalAmount());
            excel.setPayAmount(order.getPayAmount());
            excel.setPayType(ML.Order.payTypeFormat(order.getPayType()));
            excel.setInfo(order.getInfo());
            excel.setStatus(ML.Order.statusFormat(order.getStatus()));
            excel.setUsername(order.getUsername());
            excel.setCreated(order.getCreated());
            excel.setUpdated(order.getUpdated());
            result.add(excel);
        });
        return result;
    }
}
