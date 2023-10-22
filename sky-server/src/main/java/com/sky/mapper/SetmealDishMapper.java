package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 查询与套餐关联的菜品id
     * @param ids
     * @return
     */

    List<Long> getSetmealIdByDishIds(List<Long> ids);
}
