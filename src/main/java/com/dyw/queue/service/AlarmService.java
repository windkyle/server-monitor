package com.dyw.queue.service;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.controller.Egci;
import com.dyw.queue.handler.AlarmHandler;
import com.sun.jna.NativeLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AlarmService {
    //布防标识符
    private NativeLong lAlarmHandleFlag = new NativeLong(-1);
    private Logger logger = LoggerFactory.getLogger(AlarmService.class);

    /**
     * 布防
     *
     * @param lUserID 海康注册成功后返回的userId
     * @return
     */
    public Boolean setupAlarmChan(NativeLong lUserID) {
        Boolean status = false;
        try {
            if (lUserID.intValue() == -1) {
                logger.info("请先注册！");
            }
            if (lAlarmHandleFlag.intValue() >= 0) {
                logger.info("已经布防过了！");
            }
            HCNetSDK.NET_DVR_SETUPALARM_PARAM strAlarmInfo = new HCNetSDK.NET_DVR_SETUPALARM_PARAM();
            strAlarmInfo.dwSize = strAlarmInfo.size();
            strAlarmInfo.byLevel = 1;
            strAlarmInfo.byAlarmInfoType = 1;
            strAlarmInfo.byDeployType = 0;
            strAlarmInfo.write();
            lAlarmHandleFlag = Egci.hcNetSDK.NET_DVR_SetupAlarmChan_V41(lUserID, strAlarmInfo);
            if (lAlarmHandleFlag.intValue() == -1) {
                if (Egci.hcNetSDK.NET_DVR_GetLastError() == 52) {
                    status = true;
                } else {
                    logger.info("布防失败，错误码：" + Egci.hcNetSDK.NET_DVR_GetLastError());
                    status = false;
                }
            } else {
                logger.info("布防成功!");
                status = true;
            }
        } catch (Exception e) {
            logger.error("error", e);
            status = false;
        }
        return status;
    }

    public void closeAlarmChan() {
        if (lAlarmHandleFlag.intValue() > -1) {
            if (!Egci.hcNetSDK.NET_DVR_CloseAlarmChan_V30(lAlarmHandleFlag)) {
                logger.info("撤防失败!");
            } else {
                lAlarmHandleFlag = new NativeLong(-1);
            }
        }
        logger.info("撤防成功！");
    }
}
