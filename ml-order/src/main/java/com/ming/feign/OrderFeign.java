package com.ming.feign;
import com.ming.entity.Order;
import com.ming.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** @author Ming */
@FeignClient("ml-order")
public interface OrderFeign {

    @GetMapping("/api/v1/order/select/{id}")
    Result<Order> select(@PathVariable("id") Long id);
}