package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     *
     * @param openid
     * @return
     */
    @Select("select * from sky_take_out.user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 创建新用户
     * @param user
     */
    void insert(User user);

    /**
     * 通过userId获取当前用户
     * @param userId
     * @return
     */
    @Select("select * from sky_take_out.user where user.id = #{userId}")
    User getById(Long userId);

    /**
     * 通过map获取用户数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);

}
