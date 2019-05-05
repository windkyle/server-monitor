package com.dyw.queue.service;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.controller.Egci;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModeService {
    private Logger logger = LoggerFactory.getLogger(ModeService.class);

    public Boolean changeMode(NativeLong iUserID, byte mode) {
        HCNetSDK.NET_DVR_WEEK_PLAN_CFG struWeekPlan = new HCNetSDK.NET_DVR_WEEK_PLAN_CFG();
        struWeekPlan.dwSize = struWeekPlan.size();
        IntByReference pInt = new IntByReference(0);
        struWeekPlan.write();

        if (!Egci.hcNetSDK.NET_DVR_GetDVRConfig(iUserID, HCNetSDK.NET_DVR_GET_VERIFY_WEEK_PLAN, new NativeLong(1), struWeekPlan.getPointer(), struWeekPlan.size(), pInt)) {
            logger.info("获取通行模式信息失败，错误码：" + Egci.hcNetSDK.NET_DVR_GetLastError());
        } else {
            logger.info("获取通行模式信息成功");
        }
        struWeekPlan.byEnable = 1;
        for (int i = 0; i < 7; i++) {
            struWeekPlan.struPlanCfg[i].struDaysPlanCfg[0].byEnable = 1;
            struWeekPlan.struPlanCfg[i].struDaysPlanCfg[0].byVerifyMode = mode;//14是人脸；13是卡加人脸
            struWeekPlan.struPlanCfg[i].struDaysPlanCfg[0].struTimeSegment.struBeginTime.byHour = 0; //时
            struWeekPlan.struPlanCfg[i].struDaysPlanCfg[0].struTimeSegment.struBeginTime.byMinute = 0; //分
            struWeekPlan.struPlanCfg[i].struDaysPlanCfg[0].struTimeSegment.struBeginTime.bySecond = 0; //秒
            struWeekPlan.struPlanCfg[i].struDaysPlanCfg[0].struTimeSegment.struEndTime.byHour = 23;   //时
            struWeekPlan.struPlanCfg[i].struDaysPlanCfg[0].struTimeSegment.struEndTime.byMinute = 59;   //分
            struWeekPlan.struPlanCfg[i].struDaysPlanCfg[0].struTimeSegment.struEndTime.bySecond = 59;   //秒
        }
        struWeekPlan.write();
        if (!Egci.hcNetSDK.NET_DVR_SetDVRConfig(iUserID, HCNetSDK.NET_DVR_SET_VERIFY_WEEK_PLAN, new NativeLong(1), struWeekPlan.getPointer(), struWeekPlan.size())) {
            logger.info("通行方式更改失败，错误码：" + Egci.hcNetSDK.NET_DVR_GetLastError());
            return true;
        } else {
            logger.info("通行方式更改成功");
            return false;
        }
    }
}
