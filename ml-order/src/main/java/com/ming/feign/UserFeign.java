package com.ming.feign;
import com.ming.entity.User;
import com.ming.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** @author Ming */
@FeignClient("ml-user")
public interface UserFeign {

    @GetMapping("/api/v1/user/select/{id}")
    Result<User> select(@PathVariable("id") Long id);
}