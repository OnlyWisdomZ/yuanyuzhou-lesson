package com.ming.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ming.dao.CourseRepository;
import com.ming.dto.CoursePageDTO;
import com.ming.es.CourseDoc;
import com.ming.service.CourseService;
import com.ming.vo.CoursePageVO;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author Ming
 */
@CacheConfig(cacheNames = "course")
@Service
public class CourseServiceImpl implements CourseService {

    @Resource
    private CourseRepository courseRepository;
    @Cacheable(key = "#root.methodName + ':' + #p0.toString()", condition = "#p0 != null", unless = "#result == null")
    @Override
    public CoursePageVO<CourseDoc> search(CoursePageDTO dto) {
        int pageNumber = dto.getPageNum();
        int pageSize = dto.getPageSize();
        String keyword = dto.getKeyword();
        org.springframework.data.domain.Page<CourseDoc> esPage;
        // 默认值
        pageNumber = ObjectUtil.isNotNull(pageNumber) ? pageNumber : 0;
        pageSize = ObjectUtil.isNotNull(pageSize) ? pageSize : 10;
        // 参数处理: 对pageNum进行最小边界保护，规避ES报错，注意ES的分页是从0开始的
        pageNumber = pageNumber - 1;
        if (pageNumber < 0) pageNumber = 0;
        if (pageSize < 0) pageSize = 0;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        // 关键字为空时分页全查
        if (StrUtil.isEmpty(keyword)) {
            esPage = courseRepository.findAll(pageable);
        }
        // 关键字不为空时候按关键字搜索
        else {
            esPage = courseRepository.searchByTitleOrAuthorOrderByIdx(keyword, keyword, pageable);
        }
        // 组装VO并返回
        CoursePageVO<CourseDoc> result = new CoursePageVO<>();
        result.setPageNum(pageNumber + 1);
        result.setPageSize(pageSize);
        result.setTotalRow(esPage.getTotalElements());
        result.setTotalPage(esPage.getTotalPages());
        result.setRecords(esPage.getContent());
        return result;
    }
}
