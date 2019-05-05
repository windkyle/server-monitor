package com.dyw.queue.tool;

import com.dyw.queue.entity.ConfigEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;

public class Tool {
    /*
     * 读取本地配置文件
     * */
    public static ConfigEntity getConfig(String path) {
        ConfigEntity configEntity = new ConfigEntity();
        //创建一个DocumentBuilderFactory的对象
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //创建一个DocumentBuilder的对象
        try {
            //创建DocumentBuilder对象
            DocumentBuilder db = dbf.newDocumentBuilder();
            //通过DocumentBuilder对象的parser方法加载books.xml文件到当前项目下
            Document document = db.parse("file:///" + path);
            //获取所有book节点的集合
            NodeList bookList = document.getElementsByTagName("config");
            //通过nodelist的getLength()方法可以获取bookList的长度
            //遍历每一个book节点
            //通过 item(i)方法 获取一个book节点，nodelist的索引值从0开始
            for (int i = 0; i < bookList.getLength(); i++) {
                Node book = bookList.item(i);
                NodeList childNodes = book.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    if (childNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                        //获取了属性名
                        String attrName = childNodes.item(j).getNodeName();
                        if (attrName.equals("devicePort")) {
                            configEntity.setDevicePort(Short.parseShort(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("deviceName")) {
                            configEntity.setDeviceName(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("devicePass")) {
                            configEntity.setDevicePass(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("dataBaseIp")) {
                            configEntity.setDataBaseIp(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("dataBasePort")) {
                            configEntity.setDataBasePort(Short.parseShort(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("dataBaseName")) {
                            configEntity.setDataBaseName(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("dataBasePass")) {
                            configEntity.setDataBasePass(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("dataBaseLib")) {
                            configEntity.setDataBaseLib(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("dataBaseTime")) {
                            configEntity.setDataBaseTime(Long.parseLong(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("queueIp")) {
                            configEntity.setQueueIp(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("socketPort")) {
                            configEntity.setSocketPort(Short.parseShort(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("socketRegisterPort")) {
                            configEntity.setSocketRegisterPort(Short.parseShort(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("socketMonitorPort")) {
                            configEntity.setSocketMonitorPort(Short.parseShort(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("testIp")) {
                            configEntity.setTestIp(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("synchronization")) {
                            configEntity.setSynchronization(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("synchronizationHour")) {
                            configEntity.setSynchronizationHour(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("synchronizationMinute")) {
                            configEntity.setSynchronizationMinute(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("synchronizationSecond")) {
                            configEntity.setSynchronizationSecond(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("synchronizationTime")) {
                            configEntity.setSynchronizationTime(Long.parseLong(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("onGuardIp")) {
                            configEntity.setOnGuardIp(childNodes.item(j).getFirstChild().getNodeValue());
                        }
                        if (attrName.equals("onGuardPort")) {
                            configEntity.setOnGuardPort(Short.parseShort(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("alarmTime")) {
                            configEntity.setAlarmTime(Long.parseLong(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("pushTime")) {
                            configEntity.setPushTime(Long.parseLong(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("callBackTime")) {
                            configEntity.setCallBackTime(Long.parseLong(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("exitTimeStatus1")) {
                            configEntity.setExitTimeStatus1(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("exitTimeStatus2")) {
                            configEntity.setExitTimeStatus2(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("exitTimeHour1")) {
                            configEntity.setExitTimeHour1(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("exitTimeHour2")) {
                            configEntity.setExitTimeHour2(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("exitTimeMinute1")) {
                            configEntity.setExitTimeMinute1(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("exitTimeMinute2")) {
                            configEntity.setExitTimeMinute2(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("exitTimeSecond1")) {
                            configEntity.setExitTimeSecond1(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                        if (attrName.equals("exitTimeSecond2")) {
                            configEntity.setExitTimeSecond2(Integer.parseInt(childNodes.item(j).getFirstChild().getNodeValue()));
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return configEntity;
    }

    //求两个字符串数组的并集，利用set的元素唯一性
    public static String[] union(String[] arr1, String[] arr2) {
        Set<String> set = new HashSet<String>();
        for (String str : arr1) {
            set.add(str);
        }
        for (String str : arr2) {
            set.add(str);
        }
        String[] result = {};
        return set.toArray(result);
    }

    //求两个数组的交集
    public static String[] intersect(String[] arr1, String[] arr2) {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        LinkedList<String> list = new LinkedList<String>();
        for (String str : arr1) {
            if (!map.containsKey(str)) {
                map.put(str, Boolean.FALSE);
            }
        }
        for (String str : arr2) {
            if (map.containsKey(str)) {
                map.put(str, Boolean.TRUE);
            }
        }

        for (Map.Entry<String, Boolean> e : map.entrySet()) {
            if (e.getValue().equals(Boolean.TRUE)) {
                list.add(e.getKey());
            }
        }

        String[] result = {};
        return list.toArray(result);
    }

    //求两个数组的差集
    public static String[] minus(String[] arr1, String[] arr2) {
        LinkedList<String> list = new LinkedList<String>();
        LinkedList<String> history = new LinkedList<String>();
        String[] longerArr = arr1;
        String[] shorterArr = arr2;
        //找出较长的数组来减较短的数组
        if (arr1.length > arr2.length) {
            longerArr = arr2;
            shorterArr = arr1;
        }
        for (String str : longerArr) {
            if (!list.contains(str)) {
                list.add(str);
            }
        }
        for (String str : shorterArr) {
            if (list.contains(str)) {
                history.add(str);
                list.remove(str);
            } else {
                if (!history.contains(str)) {
                    list.add(str);
                }
            }
        }
        String[] result = {};
        return list.toArray(result);
    }

    /*
     * 二进制转十六进制
     * */
    public static String printHex(byte[] byteArray) {
        StringBuffer sb = new StringBuffer();
        for (byte b : byteArray) {
            sb.append(Integer.toHexString((b >> 4) & 0xF));
            sb.append(Integer.toHexString(b & 0xF));
            sb.append(" ");
        }
        return sb.toString();
    }

    /*
     * 获取本地图片
     * */
    public static byte[] readPic7() {
        try {
            FileInputStream inputStream = new FileInputStream("C:/qb.jpg");
            int i = inputStream.available();
            // byte数组用于存放图片字节数据
            byte[] buff = new byte[i];
            inputStream.read(buff);
            // 关闭输入流
            inputStream.close();
            return buff;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 给字符串增加单引号
     * */
    public static String addQuote(String info) {
        if (info == null) {
            return "''";
        } else {
            return "'" + info + "'";
        }
    }

    /*
     * 生产随机数
     * */
    public static int getRandom(int max, int min, int difference) {
        Random rand = new Random();
        return rand.nextInt(max) % (difference) + min;
    }

    /*
     * 测试设备设备在线
     * */
    public static boolean ping(String ipAddress) {
        int timeOut = 3000;  //超时应该在3钞以上
        boolean status;
        try {
            status = InetAddress.getByName(ipAddress).isReachable(timeOut);
        } catch (IOException e) {
            status = false;
        }
        // 当返回值是true时，说明host是可用的，false则不可。
        return status;
    }

    /*
     * 获取当前进程pid
     * */
    public static int getProcessID() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        return Integer.valueOf(runtimeMXBean.getName().split("@")[0]).intValue();
    }

    /*
     * 分割查询参数，防止参数过多导致出错
     * */
    public static <T> List<List<T>> fixedGrouping(List<T> source, int n) {
        if (null == source || source.size() == 0 || n <= 0)
            return null;
        List<List<T>> result = new ArrayList<List<T>>();
        int sourceSize = source.size();
        int size = (source.size() / n) + 1;
        for (int i = 0; i < size; i++) {
            List<T> subset = new ArrayList<T>();
            for (int j = i * n; j < (i + 1) * n; j++) {
                if (j < sourceSize) {
                    subset.add(source.get(j));
                }
            }
            result.add(subset);
        }
        return result;
    }
}
