package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Transactional  //事物注解
    public void saveWithFlavor(DishDTO dishDTO) {
        //涉及到两张表的操作，保证数据的一致性，需要添加事物注解 @Transactional
        //向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        //获取insert语句生成的主键值
        Long dishId = dish.getId();
        //向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()) {
            //遍历flavors集合，将dishId赋值进去   //通过逻辑外键将菜品表和菜品口味表关联了起来
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            } );

            //批量插入口味
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        //获取需要返回页面的数据，所以用DishVo,里面包含了categoryName(分类名称)
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 批量删除菜品
     * @param ids
     */
    @Override
    @Transactional //事物注解
    public void delete(List<Long> ids) {
        //1.起售状态下的菜品不能删
        //减少访问数据库的操作,一次性请求
        //查询起售状态的菜品
        List<Dish> Dishs = dishMapper.getByIds(ids);
        for (Dish dish : Dishs) {
            if(dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //2.关联套餐的菜品不能删
            List<Long> setmealIds = setmealDishMapper.getSetmealIdByDishIds(ids);
        if(setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        //3.删除菜品
        //可以在SQL进行循环删除
        /*for (Long id : ids) {
            dishMapper.deleteById(id);
            //4.删除菜品对应的口味
            dishFlavorMapper.deleteByDishId(id);
        }*/
        //根据菜品id集合批量删除菜品和菜品口味数据
        dishMapper.deleteByIds(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    /**
     * 起售、禁售菜品
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.update(dish);
    }

    /**
     * 修改菜品
     * @param dishDTO
     */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        //修改菜品表基本信息
        dishMapper.update(dish);
        //删除原有口味数据，再重新插入口味数据
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            //遍历flavors集合，将dishId赋值进去   //通过逻辑外键将菜品表和菜品口味表关联了起来
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //批量插入口味
            dishFlavorMapper.insertBatch(flavors);


        }
    }
    /**
     * 根据分类id 查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> getByCategoryId(Long categoryId) {
        List<Dish> dishes = dishMapper.getByCategoryId(categoryId);
        return dishes;
    }

    /**
     * 根据id 查询菜品
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //根据id查询口味数据
        List<DishFlavor> dishFlavors = dishMapper.getByDishId(id);

        //进行封装VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }

}
