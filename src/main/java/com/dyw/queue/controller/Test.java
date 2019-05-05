package com.dyw.queue.controller;

import com.dyw.queue.entity.StaffEntity;
import com.dyw.queue.service.SessionService;
import org.apache.ibatis.session.SqlSession;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.wrap("one".getBytes());
        System.out.println(byteBuffer.limit());
        byteBuffer = ByteBuffer.wrap("egci".getBytes());
        System.out.println(byteBuffer.limit());
        byteBuffer = ByteBuffer.wrap("o".getBytes());
        System.out.println(byteBuffer.limit());

    }
}