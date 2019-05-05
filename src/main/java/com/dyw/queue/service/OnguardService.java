package com.dyw.queue.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.dyw.queue.controller.Egci;
import com.dyw.queue.entity.StaffEntity;
import com.dyw.queue.entity.TemporaryStaffEntity;
import com.dyw.queue.tool.Tool;
import net.iharder.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OnguardService extends Thread {
    private static Logger logger = LoggerFactory.getLogger(OnguardService.class);

    @Override
    public void run() {
        try {
            Socket socket = new Socket(Egci.configEntity.getOnGuardIp(), Egci.configEntity.getOnGuardPort());
            //接口服务端信息
            logger.info("连接onGuard服务器成功，等待接收数据...");
            while (true) {
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String info = br.readLine();
                    TemporaryStaffEntity temporaryStaffEntity = JSON.parseObject(info, new TypeReference<TemporaryStaffEntity>() {
                    });
                    assert temporaryStaffEntity != null;
                    switch (temporaryStaffEntity.getType()) {
                        case 1:
                            insert(temporaryStaffEntity);
                            break;
                        case 2:
                            update(temporaryStaffEntity);
                            break;
                        case 3:
                            deleteStaff(temporaryStaffEntity);
                            deleteTemporary(temporaryStaffEntity);
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                    logger.error("onGuard数据处理出错", e);
                }
            }
        } catch (Exception e) {
            logger.error("接收onGuard数据出错：", e);
        }
    }

    /*
     * 新增数据
     * */
    private void insert(TemporaryStaffEntity temporaryStaffEntity) {
        try {
            Egci.session.insert("mapping.temporaryStaffMapper.insertTemporaryStaff", temporaryStaffEntity);
            Egci.session.commit();
        } catch (Exception e) {
            logger.error("onGuard数据新增失败:", e);
        }
    }

    /*
     * 更新数据
     * */
    private void update(TemporaryStaffEntity temporaryStaffEntity) {
        //更新人员表信息
        try {
            if (Egci.session.selectList("mapping.staffMapper.getSingleStaff", temporaryStaffEntity.getCardNumber()).size() > 0) {
                Egci.session.update("mapping.staffMapper.updateStaff", temporaryStaffEntity);
                Egci.session.commit();
            }
        } catch (Exception e) {
            logger.error("更新人员表信息出错", e);
        }
        //更新临时表信息
        try {
            if (Egci.session.selectList("mapping.temporaryStaffMapper.getTemporaryStaff", temporaryStaffEntity.getCardNumber()).size() > 0) {
                Egci.session.update("mapping.temporaryStaffMapper.updateTemporaryStaff", temporaryStaffEntity);
                Egci.session.commit();
            }
        } catch (Exception e) {
            logger.error("更新临时表信息出错", e);
        }
        //更新设备人员信息
        try {
            //读取数据库获取人员信息
            StaffEntity staff = Egci.session.selectOne("mapping.staffMapper.getSingleStaff", temporaryStaffEntity.getCardNumber());
            //重新组织人员信息:操作码+卡号+名称+图片
            String staffInfo = "1#" + staff.getCardNumber() + "#" + staff.getName() + "#" + Base64.encodeBytes(staff.getPhoto());
            //发送消息到队列中
            for (int i = 0; i < Egci.deviceIps0WithOctothorpe.size(); i++) {
                Egci.producerServiceList.get(i).sendToQueue(staffInfo.concat(Egci.deviceIps0WithOctothorpe.get(i)));
            }
        } catch (Exception e) {
            logger.error("更新设备人员信息出错", e);
        }
    }

    /*
     * 删除人员表数据
     * */
    private void deleteStaff(TemporaryStaffEntity temporaryStaffEntity) {
        //删除人员表数据
        try {
            if (Egci.session.selectList("mapping.staffMapper.getSingleStaff", temporaryStaffEntity.getCardNumber()).size() > 0) {
                Egci.session.update("mapping.staffMapper.deleteStaff", temporaryStaffEntity);
                Egci.session.commit();
            }
        } catch (Exception e) {
            logger.error("删除人员表数据出错", e);
        }
        //删除设备人员信息
        try {
            String staffInfo = "2#" + temporaryStaffEntity.getCardNumber() + "#test#none";
            for (int i = 0; i < Egci.deviceIps0WithOctothorpe.size(); i++) {
                Egci.producerServiceList.get(i).sendToQueue(staffInfo.concat(Egci.deviceIps0WithOctothorpe.get(i)));
            }
        } catch (Exception e) {
            logger.error("删除设备人员信息出错", e);
        }
    }

    /*
     * 删除临时表数据
     * */
    private void deleteTemporary(TemporaryStaffEntity temporaryStaffEntity) {
        try {
            //删除临时表数据
            Egci.session.delete("mapping.temporaryStaffMapper.deleteSingleStaff", temporaryStaffEntity);
            Egci.session.commit();
        } catch (Exception e) {
            logger.error("临时表数据删除失败");
        }
    }
}