package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

/*    @Autowired
    private RedisTemplate redisTemplate;*/

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping()
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐");
        setmealService.save(setmealDTO);
/*        String key = "setmeal_" + setmealDTO.getCategoryId();
        clearCache(key);*/
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询:{}", setmealPageQueryDTO);
        PageResult pageResult  = setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除套餐
     * @param ids 套餐id集合
     * @return
     */
    @DeleteMapping()
    @ApiOperation("批量删除套餐")
    public Result delete(@RequestParam List<Long> ids) {
        log.info("批量删除套餐");
        setmealService.delete(ids);
        //clearCache("setmeal_*");
        return Result.success();
    }

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售、停售")
    public Result StartOrStop(@PathVariable Integer status , Long id) {
        log.info("套餐起售、停售");
        setmealService.startOrStop(status,id);
        //clearCache("setmeal_*");
        return Result.success();
    }

    /**
     * 根据套餐ID查询套餐(用于页面回显)
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据套餐ID查询套餐(用于页面回显)")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        log.info("根据套餐的id查询套餐");
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
     * 修改套餐(与增加套餐的页面一样)
     * @param setmealDTO
     * @return
     */
    @PutMapping()
    @ApiOperation("修改套餐")
    public Result modify(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐");
        setmealService.modify(setmealDTO);
        //clearCache("setmeal_*");
        return Result.success();
    }

    /**
     * 清理缓冲数据
     * @param pattern
    private void clearCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }*/
}
