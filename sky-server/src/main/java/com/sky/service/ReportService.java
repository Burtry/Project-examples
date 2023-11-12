package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 营业额统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return TurnoverReportVO对象
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    /**
     * 根据某一时间段来统计用户数量
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    /**
     * 根据某一时间段来统计用户数量
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 根据某一时间段来查询销量排名top10
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
