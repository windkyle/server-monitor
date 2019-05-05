package com.dyw.queue.service;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.controller.Egci;
import com.dyw.queue.entity.FaceInfoEntity;
import com.dyw.queue.handler.FaceSendHandler;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaceService {
    private Logger logger = LoggerFactory.getLogger(FaceService.class);
    private FaceSendHandler faceSendHandler = new FaceSendHandler();

    /*
     * 人脸下发
     * */
    public Boolean setFaceInfo(String cardNo, byte[] byteFace, NativeLong lUserID) {
        try {
            HCNetSDK.NET_DVR_FACE_PARAM_COND lpInBuffer = new HCNetSDK.NET_DVR_FACE_PARAM_COND();
            for (int i = 0; i < cardNo.length(); i++) {
                lpInBuffer.byCardNo[i] = (byte) cardNo.charAt(i);
            }
            lpInBuffer.dwFaceNum = 1;
            lpInBuffer.byFaceID = (byte) 1;
            lpInBuffer.byEnableCardReader = new byte[HCNetSDK.MAX_CARD_READER_NUM_512];
            lpInBuffer.byEnableCardReader[0] = 1;
            lpInBuffer.dwSize = lpInBuffer.size();
            lpInBuffer.write();
            // 启动远程配置。
            NativeLong lHandle = Egci.hcNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_FACE_PARAM_CFG,
                    lpInBuffer.getPointer(), lpInBuffer.size(), faceSendHandler, null);
            if (lHandle.longValue() < 0) {
                logger.info("人脸下发连接开启失败，错误码：" + Egci.hcNetSDK.NET_DVR_GetLastError());
                return false;
            }
            lpInBuffer.read();
            // 发送长连接数据
            HCNetSDK.NET_DVR_FACE_PARAM_CFG pSendBuf = new HCNetSDK.NET_DVR_FACE_PARAM_CFG();
            for (int i = 0; i < cardNo.length(); i++) {
                pSendBuf.byCardNo[i] = (byte) cardNo.charAt(i);
            }
            FaceInfoEntity faceInfo = new FaceInfoEntity();
            faceInfo.byFaceInfo = byteFace;
            faceInfo.write();
            pSendBuf.pFaceBuffer = faceInfo.getPointer();
            pSendBuf.dwFaceLen = byteFace.length;
            pSendBuf.byEnableCardReader = new byte[HCNetSDK.MAX_CARD_READER_NUM_512];
            pSendBuf.byEnableCardReader[0] = 1;
            pSendBuf.byFaceID = (byte) 1;
            pSendBuf.byFaceDataType = (byte) 1;
            pSendBuf.dwSize = pSendBuf.size();
            pSendBuf.write();
            boolean result = Egci.hcNetSDK.NET_DVR_SendRemoteConfig(lHandle, HCNetSDK.ENUM_ACS_INTELLIGENT_IDENTITY_DATA,
                    pSendBuf.getPointer(), pSendBuf.size());
            Thread.sleep(500);
            if (!result) {
                logger.error("人脸下发失败，错误码：" + Egci.hcNetSDK.NET_DVR_GetLastError());
                stopRemoteConfig(lHandle);
                Thread.sleep(500);
                return false;
            } else {
                stopRemoteConfig(lHandle);
                Thread.sleep(500);
                return true;
            }
        } catch (Exception e) {
            logger.error("人脸下发出错", e);
            return false;
        }
    }

    /*
     * 删除人脸
     * */
    public Boolean delFace(String cardNo, NativeLong lUserID) {
        try {
            //删除人脸数据
            HCNetSDK.NET_DVR_FACE_PARAM_CTRL m_struFaceDel = new HCNetSDK.NET_DVR_FACE_PARAM_CTRL();
            m_struFaceDel.dwSize = m_struFaceDel.size();
            m_struFaceDel.byMode = 0; //删除方式：0- 按卡号方式删除，1- 按读卡器删除
            m_struFaceDel.struProcessMode.setType(HCNetSDK.NET_DVR_FACE_PARAM_BYCARD.class);
            m_struFaceDel.struProcessMode.struByCard.byCardNo = cardNo.getBytes();//需要删除人脸关联的卡号
            m_struFaceDel.struProcessMode.struByCard.byEnableCardReader[0] = 1; //读卡器
            m_struFaceDel.struProcessMode.struByCard.byFaceID[0] = 1; //人脸ID
            m_struFaceDel.write();
            Pointer lpInBuffer = m_struFaceDel.getPointer();
            boolean lRemoteCtrl = Egci.hcNetSDK.NET_DVR_RemoteControl(lUserID, HCNetSDK.NET_DVR_DEL_FACE_PARAM_CFG, lpInBuffer, m_struFaceDel.size());
            Thread.sleep(500);
            if (!lRemoteCtrl) {
                logger.info("删除人脸图片失败，错误号：" + Egci.hcNetSDK.NET_DVR_GetLastError());
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            logger.error("删除人脸出错", e);
            return false;
        }
    }

    /*
     *
     * 断开长连接
     * */
    public Boolean stopRemoteConfig(NativeLong conFlag) {
        if (!Egci.hcNetSDK.NET_DVR_StopRemoteConfig(conFlag)) {
            logger.error("人脸图片断开长连接失败，错误号：" + Egci.hcNetSDK.NET_DVR_GetLastError());
            return false;
        } else {
            return true;
        }
    }
}
