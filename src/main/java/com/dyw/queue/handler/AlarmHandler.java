package com.dyw.queue.handler;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.controller.Egci;
import com.dyw.queue.service.CallBack4AlarmService;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;

public class AlarmHandler implements HCNetSDK.FMSGCallBack_V31 {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private CallBack4AlarmService callBack4AlarmService;
    private SqlSession session;

    public AlarmHandler() {
        try {
            callBack4AlarmService = new CallBack4AlarmService();
            //mybatis的配置文件
            String resource = "config/conf.xml";
            //使用类加载器加载mybatis的配置文件（它也加载关联的映射文件），这个在jar中不好使啊，坑
//            InputStream is = AlarmHandler.class.getClassLoader().getResourceAsStream(resource);
//            FileInputStream fiStream = new FileInputStream("C:/software/server/config/conf.xml");
            //构建sqlSession的工厂
//            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(fiStream);
            //使用MyBatis提供的Resources类加载mybatis的配置文件（它也加载关联的映射文件）
            Reader reader = Resources.getResourceAsReader(resource);
            //构建sqlSession的工厂
            SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
            //创建能执行映射文件中sql的sqlSession
            session = sessionFactory.openSession();
            Egci.session = session;//全局共享
            if (session == null) {
                logger.error("session对象创建失败");
            } else {
                logger.info("session对象创建成功");
            }
        } catch (Exception e) {
            logger.error("报警回调函数出错", e);
        }
    }

    @Override
    public boolean invoke(NativeLong lCommand,
                          HCNetSDK.NET_DVR_ALARMER pAlarmer,
                          Pointer pAlarmInfo,
                          int dwBufLen,
                          Pointer pUser) {
        try {
            callBack4AlarmService.alarmNotice(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser, session);
            Thread.sleep(300);//延迟
            return true;
        } catch (Exception e) {
            logger.error("获取一体机通行信息出错", e);
            return true;
        }
    }
}
