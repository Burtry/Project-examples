package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {

    /**
     * 添加订单
     * @param orders 订单
     */
    void insert(Orders orders);
}
