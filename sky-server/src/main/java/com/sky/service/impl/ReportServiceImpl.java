package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Resource
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return TurnoverReportVO对象
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //构建dateList字符串对象
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String stringDateList = StringUtils.join(dateList, ",");

        //构建turnoverList字符串对象
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            //处理为0的情况
            turnover =turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        String stringTurnoverList = StringUtils.join(turnoverList, ",");

        //封装并返回
        return TurnoverReportVO.builder()
                .dateList(stringDateList)
                .turnoverList(stringTurnoverList)
                .build();
    }

    /**
     * 根据某一时间段来统计用户数量
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //构建dateList字符串对象
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String stringDateList = StringUtils.join(dateList, ",");
        //构建用户总量
        List<Integer> userList = new ArrayList<>();
        //构建新增用户总量
        List<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end",endTime);
            //用户总量
            Integer totalUserCount = userMapper.countByMap(map);
            //新增总量
            map.put("begin",beginTime);
            Integer newUserCount = userMapper.countByMap(map);

            userList.add(totalUserCount);
            newUserList.add(newUserCount);
        }

        //封装VO对象并返回
        return UserReportVO.builder()
                .dateList(stringDateList)
                .totalUserList(StringUtils.join(userList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 根据某一时间段来统计订单数量
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //构建dateList字符串对象
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        String stringDateList = StringUtils.join(dateList, ",");


        //每日订单数
        List<Integer> orderCountList = new ArrayList<>();

        //每日有效订单数
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList) {
            //构建每日订单数
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            //构建每日有效订单数
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //构建一定时间区间内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        //构建一定时间区间内的有效订单总数
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();
        //订单完成率

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0) {
            orderCompletionRate =validOrderCount.doubleValue() / totalOrderCount;
        }

        return OrderReportVO.builder()
                .dateList(stringDateList)
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
    /**
     * 根据条件统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status) {

        Map map = new HashMap();
        map.put("end", end);
        map.put("begin", begin);
        map.put("status", status);

        return orderMapper.getByMap(map);
    }

    /**
     * 根据某一时间段来查询销量排名top10
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, endTime);
        //处理数据
        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String stringNameList = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String stringNumberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder()
                .nameList(stringNameList)
                .numberList(stringNumberList)
                .build();
    }

    /**
     * 导出运营数据报表
     * @param httpServletResponse
     */
    @Override
    public void export(HttpServletResponse httpServletResponse) {

        //1.查询近三十天的运营数据
        LocalDate beginTime = LocalDate.now().plusDays(-30);
        LocalDate endTime = LocalDate.now().plusDays(-1);

        //查询概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(beginTime, LocalTime.MIN), LocalDateTime.of(endTime, LocalTime.MIN));

        //*2.通过POI将数据写入到Excel文件中
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template/ReportTemplate.xlsx");

        try {
            XSSFWorkbook excel = new XSSFWorkbook(inputStream);

            XSSFSheet sheet1 = excel.getSheet("Sheet1");
            //填充时间
            sheet1.getRow(1).getCell(1).setCellValue("时间:" + beginTime + "至" + endTime);
            //填充概览数据
            sheet1.getRow(3).getCell(2).setCellValue(businessData.getTurnover());
            sheet1.getRow(3).getCell(4).setCellValue(businessData.getOrderCompletionRate());
            sheet1.getRow(3).getCell(6).setCellValue(businessData.getNewUsers());
            sheet1.getRow(4).getCell(2).setCellValue(businessData.getValidOrderCount());
            sheet1.getRow(4).getCell(4).setCellValue(businessData.getUnitPrice());
            //填充明细数据

            for (int i = 0; i <30; i++){
                LocalDate date = beginTime.plusDays(i);
                BusinessDataVO businessDataI = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //获得某一行
                XSSFRow row = sheet1.getRow(7 + i);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessDataI.getTurnover());
                row.getCell(3).setCellValue(businessDataI.getValidOrderCount());
                row.getCell(4).setCellValue(businessDataI.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessDataI.getUnitPrice());
                row.getCell(6).setCellValue(businessDataI.getNewUsers());

            }

            //3.通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            excel.write(outputStream);


            //关闭资源
            outputStream.close();
            excel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
