package com.dyw.queue.service;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.controller.Egci;
import com.sun.jna.NativeLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {
    private Logger logger = LoggerFactory.getLogger(LoginService.class);
    private NativeLong lUserID = new NativeLong(-1);

    public Boolean login(String ip, short port, String name, String pass) {
        Boolean status = false;
        try {
            //注册之前先注销已注册的用户
            if (lUserID.longValue() > -1) {
                //先注销
                HCNetSDK.INSTANCE.NET_DVR_Logout(lUserID);
                lUserID = new NativeLong(-1);
            }
            HCNetSDK.NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new HCNetSDK.NET_DVR_DEVICEINFO_V30();
            lUserID = HCNetSDK.INSTANCE.NET_DVR_Login_V30(ip, port, name, pass, m_strDeviceInfo);
            if (lUserID.longValue() < 0) {
                logger.info(ip + ":设备登陆失败，错误码：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
                status = false;
            } else {
                status = true;
            }
        } catch (Exception e) {
            logger.error("设备登陆出错", e);
            status = false;
        }
        return status;
    }

    public NativeLong getlUserID() {
        return lUserID;
    }

    public boolean logout() {
        // 注销和清空资源
        if (HCNetSDK.INSTANCE.NET_DVR_Logout(lUserID)) {
            return true;
        } else {
            logger.info("设备资源释放失败：" + Egci.hcNetSDK.NET_DVR_GetLastError());
            return false;
        }
    }
}
