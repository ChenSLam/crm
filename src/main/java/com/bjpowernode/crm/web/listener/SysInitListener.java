package com.bjpowernode.crm.web.listener;

import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.service.DicService;
import com.bjpowernode.crm.settings.service.impl.DicServiceImpl;
import com.bjpowernode.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

//监听哪个域对象就实现哪个域接口
public class SysInitListener implements ServletContextListener {
    @Override/*
                该方法是用来监听上下文域对象的方法,当服务器启动,上下文域对象创建
                对象创建完毕后,马上执行该方法

                event:该参数能够取得监听的对象
                        监听的是什么对象,就可以通过该参数取得什么对象
                        例如现在监听的上下文域对象,通过该参数就可以取得上下文与对象
    */
    public void contextInitialized(ServletContextEvent event) {

        System.out.println("上下文域对象创建了");


        System.out.println("服务器缓存处理数据字典开始");

        //取数据字典
        ServletContext application = event.getServletContext();

        //调业务层
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        /*
        *   应该管业务层要
        *       7个list
        *
        * 可以打包成为一个map
        * 业务层应该是这样保存数据的:
        *   map.put("appellationList",dvList1);
        *   map.put("clueStateList",dvList2);
        *   map.put("stageList",dvList3);
        *
        * */
        Map<String, List<DicValue>> map =  ds.getAll();
        //将map解析为上下文与对象中保存的键值对
        Set<String> set = map.keySet();
        for (String key : set) {
            application.setAttribute(key+"List",map.get(key));//传给域对象,在index.jsp中 使用该字段
            System.out.println("set:"+set);
            System.out.println("key:"+key);
            System.out.println("map.get(key):   "+map.get(key));

        }
        System.out.println("服务器缓存处理数据字典结束");
    }

    //销毁
    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
