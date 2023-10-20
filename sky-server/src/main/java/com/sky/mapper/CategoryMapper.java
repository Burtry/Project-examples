package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {

    /**
     * 新增分类
     * @param category
     */
    void add(Category category);

    /**
     * 启用禁用分类
     * @param category
     */
    void update(Category category);

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.category where id = #{id}")
    CategoryDTO getById(Integer id);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from sky_take_out.category where id = #{id}")
    void delete(Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);
}
