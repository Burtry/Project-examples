package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 新增分类
     * @param categoryDTO
     */
    void addcategory(CategoryDTO categoryDTO);

    /**
     * 启用禁用分类
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    CategoryDTO getById(Integer id);

    /**
     * 修改分类信息
     * @param categoryDTO
     */
    void modify(CategoryDTO categoryDTO);

    /**
     * 根据id删除分类
     * @param id
     */
    void delete(Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */

    List<Category> list(Integer type);
}
