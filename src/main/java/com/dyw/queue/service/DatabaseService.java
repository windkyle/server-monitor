package com.dyw.queue.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseService {
    private Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    //连接数据库
    private String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //    String dbURL = "jdbc:sqlserver://localhost:1433;DatabaseName=EntranceGuard";
    //    String userName = "dyw";
    //    String userPwd = "hik12345";
    private String dbURL;
    private String dataBaseName;
    private String dataBasePass;
    Connection dbConn = null;

    public DatabaseService(String dataBaseIp, short dataBasePort, String dataBaseName, String dataBasePass, String dataBaseLib) {
        dbURL = "jdbc:sqlserver://" + dataBaseIp + ":" + dataBasePort + ";DatabaseName=" + dataBaseLib;
        this.dataBaseName = dataBaseName;
        this.dataBasePass = dataBasePass;
    }

    public Connection connection() {
        try {
            Class.forName(driverName);
            dbConn = DriverManager.getConnection(dbURL, dataBaseName, dataBasePass);
            logger.info("连接数据库成功");
            return dbConn;
        } catch (Exception e) {
            logger.error("连接数据库失败：" + e);
            return dbConn;
        }
    }
}
