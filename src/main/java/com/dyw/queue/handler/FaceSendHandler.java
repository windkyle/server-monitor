package com.dyw.queue.handler;

import com.dyw.queue.HCNetSDK;
import com.sun.jna.Pointer;

public class FaceSendHandler implements HCNetSDK.FRemoteConfigCallback {

    @Override
    public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData) {

    }
}
