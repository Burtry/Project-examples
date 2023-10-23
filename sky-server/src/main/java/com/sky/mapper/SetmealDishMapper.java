package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除关联此套餐的菜品
     * @param setmealId
     */
    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询对应菜品列表
     * @param setmealID 套餐id
     * @return
     */
    @Select("select * from sky_take_out.setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getDishBySetmealId(Long setmealID);

    /**
     * 批量删除关联此套餐的菜品
     * @param setmealIds
     */
    void deleteBySetmealIds(List<Long> setmealIds);
}
