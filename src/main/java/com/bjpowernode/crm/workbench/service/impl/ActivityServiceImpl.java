package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.settings.dao.UserDao;
import com.bjpowernode.crm.settings.domain.User;
import com.bjpowernode.crm.utils.SqlSessionUtil;
import com.bjpowernode.crm.vo.PaginationVO;
import com.bjpowernode.crm.workbench.dao.ActivityDao;
import com.bjpowernode.crm.workbench.dao.ActivityRemarkDao;
import com.bjpowernode.crm.workbench.domain.Activity;
import com.bjpowernode.crm.workbench.domain.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityServiceImpl implements ActivityService {
    //用到的数据库表用下面的代码查询,用到什么表,就用哪个Dao
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao  = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    @Override/*方法里的activityDao.save(a) 指的是 调用接口里写的方法,然后通过xml文件里的查询数据库语句查询到数据,返回int值给count*/
    public boolean save(Activity a) {
        boolean flag = true;
        int count = activityDao.save(a); //count受到影响的条数
        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override/*方法都是用来从数据库取数据*/
    public PaginationVO<Activity> pageList(Map<String, Object> map) {

        //取得total
        int total = activityDao.getToTalByCondition(map);
        //取得dataList
        List<Activity> dataList = activityDao.getActivityListByCondition(map);
        //创建一个vo对象  将total和dataList封装到vo中
        PaginationVO<Activity> vo = new PaginationVO<>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        //将vo返回
        return vo;
    }

    @Override/*方法都是用来从数据库取数据  activityDao.delete(ids)  activityRemarkDao.getCountByAids(ids) activityRemarkDao.deleteByAids(ids)*/
    public boolean delete(String[] ids) {
        boolean flag = true;
        //查询出需要删除的备注的数量
        int count1 = activityRemarkDao.getCountByAids(ids);
        //删除备注，返回受到影响的条数（实际删除的数量）
        int count2 = activityRemarkDao.deleteByAids(ids);

        if (count1!=count2){
            flag = false;
        }

        //删除市场活动
        int count3 = activityDao.delete(ids);
        if (count3 != ids.length){
            flag=false;
        }
        return flag;
    }

    @Override/*方法都是用来从数据库取数据 userDao.getUserList()  activityDao.getById()*/
    public Map<String, Object> getUserListAndActivity(String id) {

        //取uList
        List<User> uList = userDao.getUserList();
        //取a
        Activity a = activityDao.getById(id);
        //将uList和a打包到map中
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("uList",uList);//"uList"是前端index.jsp data里接收的uList
        map.put("a",a);
        //返回map就可以了

        return map;
    }

    @Override
    public boolean update(Activity a) {
        boolean flag = true;
        int count = activityDao.update(a); //count受到影响的条数
        if (count!=1){
            flag = false;
        }
        return flag;
    }

    @Override
    public Activity detail(String id) {
        //根据id查单条
        //activityDao.getById(id);//这个方法不能复用，表里面的id还是32位的编码，需要转为中文
        Activity a = activityDao.detail(id);
        return a;
    }

    @Override
    public List<ActivityRemark> getRemarkListByAid(String activityId) {

        List<ActivityRemark> arList = activityRemarkDao.getRemarkListByAid(activityId);

        return arList;
    }

    @Override
    public boolean deleteRemark(String id) {
        boolean flag = true;
        int count = activityRemarkDao.deleteById(id);
        if (count != 1){
            flag = false;
        }
        return flag;
    }

    @Override
    public boolean saveRemark(ActivityRemark ar) {
        boolean flag = true;
        int count = activityRemarkDao.saveRemark(ar);
        if (count !=1){
            flag = false;
        }
        return flag;
    }
}
