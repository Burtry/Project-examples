package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j

public class ShopController {
    public static final String Key = "SET_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 店铺状态设置
     */
    @PutMapping("{status}")
    public Result setStatus(@PathVariable Integer status) {
      log.info("店铺状态设置：{}",status ==1? "营业中" :"打样中");
      redisTemplate.opsForValue().set(Key, status);
      return Result.success();
    }

    /**
     * 获取营业状态
     * @return
     */
    @GetMapping("/status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(Key);
        log.info("店铺营业状态：{}",status == 1? "营业中" : "打样中");
        return Result.success(status);
    }
}
