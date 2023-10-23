package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 查询与套餐关联的菜品id
     * @param ids
     * @return
     */

    List<Long> getSetmealIdByDishIds(List<Long> ids);

    /**
     * 保存套餐和菜品的关联关系,批量添加菜品
     * @param setmealDishes
     */
    @Autowired
    void insertBatch(List<SetmealDish> setmealDishes);
}
