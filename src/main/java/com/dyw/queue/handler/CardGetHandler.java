package com.dyw.queue.handler;

import com.dyw.queue.HCNetSDK;
import com.sun.jna.Pointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class CardGetHandler implements HCNetSDK.FRemoteConfigCallback {
    private Logger logger = LoggerFactory.getLogger(CardGetHandler.class);
    private String cardNumber = "none";

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {
        switch (dwType) {
            case 0: //NET_SDK_CALLBACK_TYPE_STATUS
                HCNetSDK.REMOTECONFIGSTATUS_CARD struCfgStatus = new HCNetSDK.REMOTECONFIGSTATUS_CARD();
                struCfgStatus.write();
                Pointer pCfgStatus = struCfgStatus.getPointer();
                pCfgStatus.write(0, lpBuffer.getByteArray(0, struCfgStatus.size()), 0, struCfgStatus.size());
                struCfgStatus.read();
                int iStatus = 0;
                for (int i = 0; i < 4; i++) {
                    int ioffset = i * 8;
                    int iByte = struCfgStatus.byStatus[i] & 0xff;
                    iStatus = iStatus + (iByte << ioffset);
                }
                switch (iStatus) {
                    case 1000:// NET_SDK_CALLBACK_STATUS_SUCCESS
                        logger.info("查询卡参数成功,dwStatus:" + iStatus);
                        break;
                    case 1001:
                        logger.info("正在查询卡参数中,dwStatus:" + iStatus);
                        break;
                    case 1002:
                        int iErrorCode = 0;
                        for (int i = 0; i < 4; i++) {
                            int ioffset = i * 8;
                            int iByte = struCfgStatus.byErrorCode[i] & 0xff;
                            iErrorCode = iErrorCode + (iByte << ioffset);
                        }
                        cardNumber = "none";
                        break;
                }
                break;
            case 2: //NET_SDK_CALLBACK_TYPE_DATA
                HCNetSDK.NET_DVR_CARD_CFG_V50 m_struCardInfo = new HCNetSDK.NET_DVR_CARD_CFG_V50();
                m_struCardInfo.write();
                Pointer pInfoV30 = m_struCardInfo.getPointer();
                pInfoV30.write(0, lpBuffer.getByteArray(0, m_struCardInfo.size()), 0, m_struCardInfo.size());
                m_struCardInfo.read();
                String str = new String(m_struCardInfo.byCardNo).trim();
                cardNumber = str;
                break;
            default:
                break;
        }
    }
}
