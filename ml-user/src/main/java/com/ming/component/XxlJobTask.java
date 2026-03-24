package com.ming.component;

import com.ming.service.UserService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/** @author Ming */
@Slf4j
@Component
public class XxlJobTask {

    @Resource
    private UserService userService;

    @XxlJob(value = "statistics")
    public void statistics() throws Exception {
        Map<String, Object> result = userService.statistics();
        // todo: 将统计结果存入DB表，给管理员发送邮件等
        log.info(result.toString());
        XxlJobHelper.handleSuccess("今日数据统计完毕！");
    }
}