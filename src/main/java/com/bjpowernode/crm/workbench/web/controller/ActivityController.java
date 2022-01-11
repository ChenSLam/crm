package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.settings.service.impl.UserServiceImpl;
import com.bjpowernode.crm.utils.*;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
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
import java.util.Timer;

public class ActivityController extends HttpServlet {
    //前端要什么,控制层就要管业务层要什么
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
        }else if ("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }else if ("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if ("/workbench/activity/update.do".equals(path)){
            update(request,response);
        }else if ("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }else if ("/workbench/activity/getRemarkListByAid.do".equals(path)){
            getRemarkListByAid(request,response);
        }else if ("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        }else if ("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }else if ("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }
    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("备注修改操作===========================");
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession().getAttribute("user")).getName();
        String editFlag = "1";
        ActivityRemark ar = new ActivityRemark();
        ar.setEditFlag(editFlag);
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateRemark(ar);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("ar",ar);
        map.put("success",flag);
        PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("添加备注操作");
        String noteContext = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        //创建时间:当前系统时间
        String createTime = DateTimeUtil.getSysTime();
        //创建人:当前登陆的用户
        String createBy = ((User)request.getSession().getAttribute("user")).getName();//session里取user,再从user里取name
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContext);
        ar.setActivityId(activityId);
        ar.setCreateBy(createBy);
        ar.setCreateTime(createTime);
        ar.setEditFlag(editFlag);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.saveRemark(ar);

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("ar",ar);
        map.put("success",flag);

        PrintJson.printJsonObj(response,map);

    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("删除备注操作");
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteRemark(id);
        PrintJson.printJsonFlag(response,flag);

    }

    private void getRemarkListByAid(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("根据市场活动的id,取得备注信息列表");

        String activityId = request.getParameter("activityId");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        List<ActivityRemark> arList = as.getRemarkListByAid(activityId);
        PrintJson.printJsonObj(response,arList);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("进入到跳转到详细信息页的操作");
        String id = request.getParameter("id");

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity a = as.detail(id);
        //重定向、转发，使用转发
        //a保存到request域，能用小的域，就不用大的域，作用域从小到大，能小就小
        request.setAttribute("a",a);
        //request重定向不好使，用转发比较合适
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);//抛异常
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动修改操作");
        String id  = request.getParameter("id");
        String owner = request.getParameter("owner");//此处的"owner"从前端拿，index.jsp中$.ajax  data{}里的数据 也就是"owner":$.trim($("#create-owner").val()),中的"owner"
        String name = request.getParameter("name");//从前端拿，index.jsp中$.ajax  data{}里的数据
        String startDate = request.getParameter("startDate");//从前端拿，index.jsp中$.ajax  data{}里的数据
        String endDate = request.getParameter("endDate");//从前端拿，index.jsp中$.ajax  data{}里的数据
        String cost = request.getParameter("cost");//从前端拿，index.jsp中$.ajax  data{}里的数据
        String description = request.getParameter("description");//从前端拿，index.jsp中$.ajax  data{}里的数据
        //修改时间:当前系统时间
        String editTime = DateTimeUtil.getSysTime();
        //修改人:当前登陆的用户
        String editBy = ((User)request.getSession().getAttribute("user")).getName();//session里取user,再从user里取name

        Activity a = new Activity();
        a.setId(id);
        a.setOwner(owner);
        a.setName(name);
        a.setStartDate(startDate);
        a.setEndDate(endDate);
        a.setCost(cost);
        a.setDescription(description);
        a.setEditBy(editBy);
        a.setEditTime(editTime);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());

        boolean flag = as.update(a);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("进入到查询用户信息列表和根据市场活动id查询单挑记录");
        String id = request.getParameter("id");
        //前端要什么,控制层就要管业务层要什么

        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        /*
             总结:
                controller调用service的方法,返回值应该是什么
                  想一想前端要什么,就要从service层取什么

              前端需要的,管业务层去要

         *   uList
         *   a
         *
                以上两项信息,复用率不高,选择使用map打包这两项信息即可
         *   map
         * */
        Map<String,Object> map = as.getUserListAndActivity(id);

        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("执行市场活动的删除操作");
        String ids[] = request.getParameterValues("id");
        //前端要什么,控制层就要管业务层要什么
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.delete(ids);
        PrintJson.printJsonFlag(response,flag);
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
        //前端要什么,控制层就要管业务层要什么
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
        System.out.println("0000000000000000000");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        System.out.println("11111111111111");

        boolean flag = as.save(a);
        System.out.println("22222222222222222");
        PrintJson.printJsonFlag(response,flag);

    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("取得用户信息列表");

        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

        List<User> uList = us.getUserList();

        PrintJson.printJsonObj(response,uList);
    }


}

