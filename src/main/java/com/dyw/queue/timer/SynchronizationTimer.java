package com.dyw.queue.timer;

import com.dyw.queue.controller.Egci;
import com.dyw.queue.task.SynchronizationTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class SynchronizationTimer {
    private static Logger logger = LoggerFactory.getLogger(SynchronizationTimer.class);
    //时间间隔(一天)
    private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;

    public static void open() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Egci.configEntity.getSynchronizationHour());
        calendar.set(Calendar.MINUTE, Egci.configEntity.getSynchronizationMinute());
        calendar.set(Calendar.SECOND, Egci.configEntity.getSynchronizationSecond());
        Date date = calendar.getTime(); //第一次执行定时任务的时间
        //如果第一次执行定时任务的时间 小于当前的时间
        //此时要在 第一次执行定时任务的时间加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
        if (date.before(new Date())) {
            date = addDay(date, 1);
        }
        Timer timer = new Timer();
        SynchronizationTaskService synchronizationTaskService = new SynchronizationTaskService();
        logger.info("用来测试同步的ip是：" + Egci.configEntity.getTestIp());
        logger.info("同步时刻是：" + calendar.getTime());
        //安排指定的任务在指定的时间开始进行重复的固定延迟执行。
        timer.schedule(synchronizationTaskService, date, PERIOD_DAY);
    }

    // 增加或减少天数
    private static Date addDay(Date date, int num) {
        Calendar startDT = Calendar.getInstance();
        startDT.setTime(date);
        startDT.add(Calendar.DAY_OF_MONTH, num);
        return startDT.getTime();
    }
}
