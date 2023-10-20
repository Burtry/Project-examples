package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 分类管理
 */
@Slf4j
@RestController
@RequestMapping("/admin/category")
@Api("分类管理")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增分类")
    public Result addcategory(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类:{}",categoryDTO);
        categoryService.addcategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分页查询分类
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询分类")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询分类{}" , categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用、禁用分类")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用、禁用分类:{}、{}",status,id);
        categoryService.startOrStop(status,id);
        return Result.success();
    }

    //TODO 不知道需不需要，先保留
    /**
     * 根据id查询分类
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询分类")
    public Result<CategoryDTO> getById(@PathVariable Integer id) {
        CategoryDTO categoryDTO = categoryService.getById(id);
        return Result.success(categoryDTO);
    }


    /**
     * 修改分类信息
     * @param categoryDTO
     * @return
     */
    @PutMapping()
    public Result modify(@RequestBody CategoryDTO categoryDTO) {
        categoryService.modify(categoryDTO);
        return Result.success();
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping()
    @ApiOperation("根据id删除分类")
    public Result delete(Long  id) {
        categoryService.delete(id);
        return Result.success();
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> getByType(Integer type) {
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
