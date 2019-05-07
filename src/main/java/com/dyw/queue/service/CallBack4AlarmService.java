package com.dyw.queue.service;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.controller.Egci;
import com.dyw.queue.entity.AlarmEntity;
import com.dyw.queue.entity.StaffEntity;
import com.dyw.queue.tool.Tool;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Timestamp;


public class CallBack4AlarmService {
    private Logger logger = LoggerFactory.getLogger(CallBack4AlarmService.class);
    private StaffEntity staffEntity = new StaffEntity();
    private int alarmType;

    //一体机设备相关参数
    private HCNetSDK.NET_DVR_ACS_ALARM_INFO strACSInfo;
    private Pointer pACSInfo;
    private ByteBuffer bufferSnap;
    private byte[] byteSnap;
    private AlarmEntity alarmEntity;

    /*
     * 构造函数
     * */
    public CallBack4AlarmService() {
        //一体机设备相关参数
        strACSInfo = new HCNetSDK.NET_DVR_ACS_ALARM_INFO();
        strACSInfo.write();
        pACSInfo = strACSInfo.getPointer();
        alarmEntity = new AlarmEntity();
    }

    public void alarmNotice(NativeLong lCommand,
                            HCNetSDK.NET_DVR_ALARMER pAlarmer,
                            Pointer pAlarmInfo,
                            int dwBufLen,
                            Pointer pUser,
                            SqlSession session) {
        try {
            alarmType = lCommand.intValue();
            switch (alarmType) {
                case HCNetSDK.COMM_ALARM_ACS: //门禁主机报警信息
                    COMM_ALARM_ACS_info(pAlarmer, pAlarmInfo, session);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logger.error("接收消息出错", e);
        }
    }

    private void COMM_ALARM_ACS_info(HCNetSDK.NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, SqlSession session) {
        pACSInfo.write(0, pAlarmInfo.getByteArray(0, strACSInfo.size()), 0, strACSInfo.size());
        strACSInfo.read();
        alarmEntity.clear();
        alarmEntity.setCardNumber(new String(strACSInfo.struAcsEventInfo.byCardNo).trim());
        alarmEntity.setIP(new String(pAlarmer.sDeviceIP).trim());
        try {
            bufferSnap = strACSInfo.pPicData.getByteBuffer(0, strACSInfo.dwPicDataLen);
            byteSnap = new byte[strACSInfo.dwPicDataLen];
            bufferSnap.get(byteSnap);
            alarmEntity.setCapturePhoto(byteSnap);
        } catch (Exception e) {
            alarmEntity.setCapturePhoto(null);
            return;
        }
        alarmEntity.setEventTypeId(strACSInfo.dwMinor);
        alarmEntity.setDate(Timestamp.valueOf(strACSInfo.struTime.dwYear + "-" + strACSInfo.struTime.dwMonth + "-" + strACSInfo.struTime.dwDay + " " + strACSInfo.struTime.dwHour + ":" + strACSInfo.struTime.dwMinute + ":" + strACSInfo.struTime.dwSecond));
        alarmEntity.setEquipmentName(Egci.deviceIps0Map.get(alarmEntity.getIP()));//设备名称
        //依据事件类型生成不同的事件对象
        logger.info("事件类型：" + strACSInfo.dwMinor);
        switch (strACSInfo.dwMinor) {
            case 105:
                alarmEntity.setPass(true);
                alarmEntity.setSimilarity(Tool.getRandom(89, 76, 13));
                break;
            case 112:
                alarmEntity.setPass(false);
                alarmEntity.setSimilarity(Tool.getRandom(40, 15, 25));
                break;
            case 8:
                alarmEntity.setPass(false);
                alarmEntity.setSimilarity(0);
                break;
            default:
                alarmEntity.setPass(false);
                alarmEntity.setSimilarity(0);
                break;
        }
        //读取人员姓名
        try {
            staffEntity = session.selectOne("mapping.staffMapper.getSingleStaff", alarmEntity);
            alarmEntity.setStaffName(staffEntity.getName());
        } catch (Exception e) {
            logger.error("读取人员姓名出错", e);
            alarmEntity.setStaffName(null);
        }
        //提交数据
        try {

            alarmEntity.setCardNumber("858585");/////////////////////


            session.insert("mapping.alarmMapper.insertAlarm", alarmEntity);
            session.commit();
        } catch (Exception e) {
            logger.error("提交数据出错", e);
            return;
        }
        //推送通信到消费者
        if (Egci.deviceIps1.contains(alarmEntity.getIP())) {
            for (ProducerService producerService : Egci.producerMonitorOneServices) {
                try {
                    producerService.sendToQueue(alarmEntity.getId() + "");
                } catch (Exception e) {
                    logger.error("推送通信到消费者失败", e);
                }
            }
        } else if (Egci.deviceIps2.contains(alarmEntity.getIP())) {
            for (ProducerService producerService : Egci.producerMonitorTwoServices) {
                try {
                    producerService.sendToQueue(alarmEntity.getId() + "");
                } catch (Exception e) {
                    logger.error("推送通信到消费者失败", e);
                }
            }
        } else if (Egci.deviceIps3.contains(alarmEntity.getIP())) {
            for (ProducerService producerService : Egci.producerMonitorThreeServices) {
                try {
                    producerService.sendToQueue(alarmEntity.getId() + "");
                } catch (Exception e) {
                    logger.error("推送通信到消费者失败", e);
                }
            }
        }
        //判断布防是否是在线断开后自动重连了
        if (Egci.deviceIpsAlarmFail.contains(alarmEntity.getIP())) {
            Egci.deviceIpsAlarmFail.remove(alarmEntity.getIP());
        }
    }
}
