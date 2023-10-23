package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
 public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param setmealDTO
     */
    @Override
    @Transactional //添加事物注解
    public void save(SetmealDTO setmealDTO) {
        //将SetmealDTO转成Setmeal
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //新增套餐
        setmealMapper.insert(setmeal);
        //获取对应套餐id
        Long setmealId = setmeal.getId();

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if(setmealDishes != null && !setmealDishes.isEmpty()) {
            //添加对应菜品
            setmealDishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });
            //批量添加菜品
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        long total = page.getTotal();
        List<Setmeal> result = page.getResult();
        return new PageResult(total,result);

    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Override
    @Transactional //添加事物
    public void delete(List<Long> ids) {
        //1.起售状态下的套餐不可删
        //获得起售状态下的套餐列表,如果此列表没有起售状态的套餐，则可以删除
        List<Setmeal> setmealList =setmealMapper.getByIdOfStatus(ids);
        if(setmealList != null && !setmealList.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
        }
        //2.批量删除套餐
        setmealMapper.deleteByIds(ids);
        //删除套餐菜品关系表相应的数据
        setmealDishMapper.deleteBySetmealId(ids);

    }
}