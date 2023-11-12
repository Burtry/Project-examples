package com.sky.service;


import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface UserService {

    /**
     * 微信登录功能
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 根据某一时间段来统计用户数量
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);
}
