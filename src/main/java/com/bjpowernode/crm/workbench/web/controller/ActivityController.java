package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import com.bjpowernode.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到市场活动控制器");
        String path = request.getServletPath();//path字符串内容是:/settings/user/login.do
        System.out.println(path);
        if ("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if ("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }else if ("/workbench/activity/pageList.do".equals(path)){
            pageList(request,response);
        }
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询市场活动信息列表的操作");

        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        int pageNo = Integer.valueOf(pageNoStr);
        //每页展现的记录数
        String pageSizeStr = request.getParameter("pageSize");
        int pageSize = Integer.valueOf(pageSizeStr);
        //计算出略过的记录数
        int skipCount = (pageNo-1) * pageSize;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
        * 前端要:市场活动信息列表
        *       查询的总条数
        *
        *       业务层拿到了以上两项信息之后,如何做返回
        *       map 服务率低用map
        *           map.put("dataList":dataList)
        *           map.put("total":total)
        *           PrintJSON map --> json
        *           {"total":100,"dataList":[{市场活动1},{2},{3}...]}
        *       vo 服务率高用vo
        *
        *       paginationVO<T>
        *           private int total;
        *           private List<T> dataList;
        *
        *       PaginationVO<Activity> vo = new PaginationVO<>;
        *       vo.setTotal(total);
        *       vo.setDataList(dataList);
        *       PrintJSON vo --> json
        *       {"total":100,"dataList":[{市场活动1},{2},{3}...]}
        *       将来分页查询，每个模块都有，所以我们选择使用一个通用vo，操作起来比较方便
        * */
       PaginationVO<Activity> vo = as.pageList(map);
       PrintJson.printJsonObj(response,vo);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {

        System.out.println("执行市场活动添加操作");
        String id  = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");//此处的"owner"从前端拿，index.jsp中$.ajax  data{}里的数据 也就是"owner":$.trim($("#create-owner").val()),中的"owner"
        String name = request.getParameter("name");//从前端拿，index.jsp中$.ajax  data{}里的数据
        String startDate = request.getParameter("startDate");//从前端拿，index.jsp中$.ajax  data{}里的数据
        String endDate = request.getParameter("endDate");//从前端拿，index.jsp中$.ajax  data{}里的数据
        String cost = request.getParameter("cost");//从前端拿，index.jsp中$.ajax  data{}里的数据
        String description = request.getParameter("description");//从前端拿，index.jsp中$.ajax  data{}里的数据
        //创建时间:当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人:当前登陆的用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();//session里取user,再从user里取name

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setCreateTime(createTime);
        a.setCreateBy(createBy);

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.save(a);

        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得用户信息列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        PrintJson.printJsonObj(response,uList);
    }


}

