package com.dyw.queue.service;

import com.dyw.queue.HCNetSDK;

public class BaseService {
    public BaseService() {
        if (!HCNetSDK.INSTANCE.NET_DVR_Init()) {
            return;
        }
    }
}
