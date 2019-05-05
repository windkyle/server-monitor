package com.dyw.queue.service;

import com.dyw.queue.HCNetSDK;
import com.dyw.queue.controller.Egci;
import com.dyw.queue.entity.ConfigEntity;
import com.dyw.queue.entity.FaceInfoEntity;
import com.dyw.queue.entity.StaffEntity;
import com.dyw.queue.handler.CardSendHandler;
import com.dyw.queue.handler.FaceSendHandler;
import com.dyw.queue.handler.SynchronizationHandler;
import com.dyw.queue.tool.Tool;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class SynchronizationService implements Runnable {
    private Logger logger = LoggerFactory.getLogger(SynchronizationService.class);
    private String threadName;
    private HCNetSDK hcNetSDK = HCNetSDK.INSTANCE;
    private SynchronizationHandler synchronizationHandler = new SynchronizationHandler();
    private CardSendHandler cardSendHandler = new CardSendHandler();
    private FaceSendHandler faceSendHandler = new FaceSendHandler();
    private Thread t;
    private LoginService loginService;
    private ConfigEntity configEntity;
    private List<String> dataBaseCards;

    public SynchronizationService(String threadName, LoginService loginService, ConfigEntity configEntity, List<String> dataBaseCards) {
        this.threadName = threadName;
        this.loginService = loginService;
        this.configEntity = configEntity;
        this.dataBaseCards = dataBaseCards;
    }

    /**
     * 创建卡号查询的长连接
     */
    private NativeLong buildGetCardTcpCon(HCNetSDK hcNetSDK, NativeLong lUserID) {
        try {
            HCNetSDK.NET_DVR_CARD_CFG_COND m_struCardInputParam = new HCNetSDK.NET_DVR_CARD_CFG_COND();
            m_struCardInputParam.dwSize = m_struCardInputParam.size();
            m_struCardInputParam.dwCardNum = 0xffffffff; //查找全部
            m_struCardInputParam.byCheckCardNo = 1;
            Pointer lpInBuffer = m_struCardInputParam.getPointer();
            m_struCardInputParam.write();
            Pointer pUserData = null;
            Thread.sleep(500);
            NativeLong nativeLong = hcNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_GET_CARD_CFG_V50, lpInBuffer, m_struCardInputParam.size(), synchronizationHandler, pUserData);
            Thread.sleep(300);
            return nativeLong;
        } catch (Exception e) {
            logger.error("创建同步连接失败", e);
            return new NativeLong(-1);
        }
    }

    /**
     * 卡号信息获取
     */
    private Boolean getCardInfo(String cardNo, NativeLong lUserID) {
        NativeLong cardGetFtpFlag = buildGetCardTcpCon(HCNetSDK.INSTANCE, lUserID);
        if (cardGetFtpFlag.intValue() < 0) {
            logger.info("建立获取卡号数据长连接失败，错误号：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
            return false;
        }
        try {
            //查找指定卡号
            HCNetSDK.NET_DVR_CARD_CFG_SEND_DATA m_struCardSendInputParam = new HCNetSDK.NET_DVR_CARD_CFG_SEND_DATA();
            m_struCardSendInputParam.read();
            m_struCardSendInputParam.dwSize = m_struCardSendInputParam.size();
            m_struCardSendInputParam.byCardNo = cardNo.getBytes();
            m_struCardSendInputParam.byRes = "0".getBytes();
            Pointer pSendBuf = m_struCardSendInputParam.getPointer();
            m_struCardSendInputParam.write();
            Thread.sleep(configEntity.getSynchronizationTime());//查找全部卡号时，线程暂停时间
            if (!HCNetSDK.INSTANCE.NET_DVR_SendRemoteConfig(cardGetFtpFlag, 0x3, pSendBuf, m_struCardSendInputParam.size())) {
                logger.info("查询卡号失败，错误号：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
                stopRemoteConfig(cardGetFtpFlag);
                return false;
            } else {
                Thread.sleep(1000);
                stopRemoteConfig(cardGetFtpFlag);
                return true;
            }
        } catch (Exception e) {
            logger.error("查询卡号出错", e);
            return false;
        }
    }

    /*
     *
     * 断开长连接
     * */
    private void stopRemoteConfig(NativeLong conFlag) {
        if (!HCNetSDK.INSTANCE.NET_DVR_StopRemoteConfig(conFlag)) {
            logger.info("断开卡号操作的长连接失败，错误号：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
        }
    }

    /*
     * 卡号下发
     * */
    private Boolean setCardInfo(NativeLong lUserID, String cardNo, String cardName, String password) {
        NativeLong cardSendFtpFlag = buildSendCardTcpCon(HCNetSDK.INSTANCE, lUserID);
        if (cardSendFtpFlag.intValue() < 0) {
            logger.info("建立设置卡号数据长连接失败，错误号：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
            return false;
        }
        try {
            // 设置卡参数
            HCNetSDK.NET_DVR_CARD_CFG_V50 struCardInfo = new HCNetSDK.NET_DVR_CARD_CFG_V50();
            struCardInfo.read();
            struCardInfo.dwSize = struCardInfo.size();
            struCardInfo.dwModifyParamType = 0x00000001 + 0x00000002 + 0x00000004 + 0x00000008 +
                    0x00000010 + 0x00000020 + 0x00000080 + 0x00000100 + 0x00000200 + 0x00000400 + 0x00000800;
            struCardInfo.byCardValid = 1;
            struCardInfo.wRoomNumber = 302;
            struCardInfo.byCardType = 1;
            struCardInfo.byLeaderCard = 0;
            struCardInfo.byDoorRight[0] = 1; //门1有权限
            //卡有效期
            struCardInfo.struValid.byEnable = 0;
            struCardInfo.dwMaxSwipeTime = 0; //无次数限制
            struCardInfo.dwSwipeTime = 0; //已刷卡次数
            struCardInfo.byCardPassword = password.getBytes();//密码；固定
            //设置卡号
            for (int i = 0; i < HCNetSDK.ACS_CARD_NO_LEN; i++) {
                struCardInfo.byCardNo[i] = 0;
            }
            byte[] cardNoBytes = cardNo.trim().getBytes(); //卡号
            System.arraycopy(cardNoBytes, 0, struCardInfo.byCardNo, 0, cardNo.length());
            // 设置卡片名称
            for (int i = 0; i < HCNetSDK.NAME_LEN; i++) {
                struCardInfo.byName[i] = 0;
            }
            byte[] nameBytes = cardName.trim().getBytes("GBK");
            System.arraycopy(nameBytes, 0, struCardInfo.byName, 0, nameBytes.length);
            struCardInfo.write();
            Pointer pSendBufSet = struCardInfo.getPointer();
            // 发送卡信息
            if (!hcNetSDK.NET_DVR_SendRemoteConfig(cardSendFtpFlag, 0x3, pSendBufSet, struCardInfo.size())) {
                logger.info("卡号下发失败，错误码：" + hcNetSDK.NET_DVR_GetLastError());
                stopRemoteConfig(cardSendFtpFlag);
                Thread.sleep(500);
                return false;
            } else {
                stopRemoteConfig(cardSendFtpFlag);
                Thread.sleep(500);
                return true;
            }
        } catch (Exception e) {
            logger.error("卡号下发出错", e);
            return false;
        }
    }

    /*
     * 卡号下发的长连接
     * */
    private NativeLong buildSendCardTcpCon(HCNetSDK hcNetSDK, NativeLong lUserID) {
        try {
            HCNetSDK.NET_DVR_CARD_CFG_COND m_struCardInputParamSet = new HCNetSDK.NET_DVR_CARD_CFG_COND();
            m_struCardInputParamSet.read();
            m_struCardInputParamSet.dwSize = m_struCardInputParamSet.size();
            m_struCardInputParamSet.dwCardNum = 1;
            m_struCardInputParamSet.byCheckCardNo = 1;
            Pointer lpInBuffer = m_struCardInputParamSet.getPointer();
            m_struCardInputParamSet.write();
            Pointer pUserData = null;
            return hcNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_CARD_CFG_V50, lpInBuffer, m_struCardInputParamSet.size(), cardSendHandler, pUserData);
        } catch (Exception e) {
            logger.error("卡号下发的长连接出错", e);
            return new NativeLong(-1);
        }
    }

    /*
     * 删除卡号
     * */
    private Boolean delCardInfo(String cardNo, NativeLong lUserID) {
        NativeLong cardSendFtpFlag = buildSendCardTcpCon(HCNetSDK.INSTANCE, lUserID);
        if (cardSendFtpFlag.intValue() < 0) {
            logger.error("建立删除卡号的长连接失败，错误号：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
        }
        try {
            // 设置卡参数
            HCNetSDK.NET_DVR_CARD_CFG_V50 struCardInfo = new HCNetSDK.NET_DVR_CARD_CFG_V50(); //卡参数
            struCardInfo.read();
            struCardInfo.dwSize = struCardInfo.size();
            struCardInfo.dwModifyParamType = 0x00000001;
            System.arraycopy(cardNo.getBytes(), 0, struCardInfo.byCardNo, 0, cardNo.length());
            struCardInfo.byCardValid = 0;
            struCardInfo.write();
            Pointer pSendBufSet = struCardInfo.getPointer();
            // 发送卡信息
            if (!HCNetSDK.INSTANCE.NET_DVR_SendRemoteConfig(cardSendFtpFlag, 0x3, pSendBufSet, struCardInfo.size())) {
                logger.error("号信息删除请求下发失败，错误号：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
                Thread.sleep(500);
                stopRemoteConfig(cardSendFtpFlag);
                return false;
            } else {
                Thread.sleep(500);
                stopRemoteConfig(cardSendFtpFlag);
                return true;
            }
        } catch (Exception e) {
            logger.error("删除卡号出错", e);
            return false;
        }
    }

    /*
     * 人脸下发
     * */
    private void setFaceInfo(String cardNo, byte[] byteFace, NativeLong lUserID) {
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
            NativeLong lHandle = hcNetSDK.NET_DVR_StartRemoteConfig(lUserID, HCNetSDK.NET_DVR_SET_FACE_PARAM_CFG,
                    lpInBuffer.getPointer(), lpInBuffer.size(), faceSendHandler, null);
            if (lHandle.longValue() < 0) {
                logger.info("人脸下发连接开启失败，错误码：" + hcNetSDK.NET_DVR_GetLastError());
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
            boolean result = hcNetSDK.NET_DVR_SendRemoteConfig(lHandle, HCNetSDK.ENUM_ACS_INTELLIGENT_IDENTITY_DATA,
                    pSendBuf.getPointer(), pSendBuf.size());
            Thread.sleep(500);
            if (!result) {
                logger.info("人脸下发失败，错误码：" + hcNetSDK.NET_DVR_GetLastError());
                stopRemoteConfig(lHandle);
                Thread.sleep(500);
            } else {
                stopRemoteConfig(lHandle);
                Thread.sleep(500);
            }
        } catch (Exception e) {
            logger.error("人脸下发出错", e);
        }
    }

    /*
     * 删除人脸
     * */
    private void delFace(String cardNo, NativeLong lUserID) {
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
            boolean lRemoteCtrl = HCNetSDK.INSTANCE.NET_DVR_RemoteControl(lUserID, HCNetSDK.NET_DVR_DEL_FACE_PARAM_CFG, lpInBuffer, m_struFaceDel.size());
            Thread.sleep(500);
            if (!lRemoteCtrl) {
                logger.error("删除人脸图片失败，错误号：" + HCNetSDK.INSTANCE.NET_DVR_GetLastError());
            }
        } catch (Exception e) {
            logger.error("删除人脸出错", e);
        }
    }

    /*
     * run
     * */
    @Override
    public void run() {
        try {
            //获取一体机全部卡信息
            if (!getCardInfo("0", loginService.getlUserID())) {
                logger.info("获取卡号失败");
                return;
            }
            String[] deviceNumbers = synchronizationHandler.getCards().toArray(new String[synchronizationHandler.getCards().size()]);//一体机人员信息
            String[] dataBaseNumbers = dataBaseCards.toArray(new String[dataBaseCards.size()]);//数据库人员信息
            //获取一体机人脸和数据库人脸差集
            String[] result_minus = Tool.minus(deviceNumbers, dataBaseNumbers);
            List<String> deletes = Arrays.asList(Tool.intersect(result_minus, deviceNumbers));//要删除的卡号
            List<String> adds = Arrays.asList(Tool.intersect(result_minus, dataBaseNumbers));//要下发的卡号
            //删除一体机指定卡号信息
            for (String delCard : deletes) {
                if (delCardInfo(delCard, loginService.getlUserID())) {
                    delFace(delCard, loginService.getlUserID());
                }
            }
            //添加一体机指定卡号信息
            if (adds.size() > 1000) {
                List<List<String>> result = Tool.fixedGrouping(adds, 500);
                for (List<String> list : result) {
                    List<StaffEntity> staffEntities = Egci.session.selectList("mapping.staffMapper.getStaffByCard", list);
                    for (StaffEntity staffEntity : staffEntities) {
                        //下发卡号
                        if (setCardInfo(loginService.getlUserID(), staffEntity.getCardNumber().trim(), staffEntity.getName().trim(), "666666")) {
                            //下发人脸
                            setFaceInfo(staffEntity.getCardNumber().trim(), staffEntity.getPhoto(), loginService.getlUserID());
                        }
                    }
                }
            } else {
                List<StaffEntity> staffEntities = Egci.session.selectList("mapping.staffMapper.getStaffByCard", adds);
                for (StaffEntity staffEntity : staffEntities) {
                    //下发卡号
                    if (setCardInfo(loginService.getlUserID(), staffEntity.getCardNumber().trim(), staffEntity.getName().trim(), "666666")) {
                        //下发人脸
                        setFaceInfo(staffEntity.getCardNumber().trim(), staffEntity.getPhoto(), loginService.getlUserID());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("同步出现错误", e);
        } finally {
            loginService.logout();
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }
}
