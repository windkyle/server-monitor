package com.dyw.queue;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.awt.*;
import java.util.*;
import java.util.List;

/*
 * Convert IntPtr to int,
 *         uint to int,
 *         ushort to short,
 *         bool to boolean,
 *         string to byte[]
 * */

/**
 * Created by yushun on 2017/7/12.
 */
public interface HCNetSDK extends StdCallLibrary {

    //Function Mapped via JNA
    HCNetSDK INSTANCE = (HCNetSDK) Native.loadLibrary(System.getProperty("user.dir") + "\\lib\\HCNetSDK", HCNetSDK.class);
//    HCNetSDK INSTANCE = (HCNetSDK) Native.loadLibrary("C:\\software\\server\\lib\\HCNetSDK", HCNetSDK.class);

    boolean NET_DVR_SetConnectTime(int dwWaitTime, int dwTryTime);

    boolean NET_DVR_SetReconnect(int dwInternal, boolean bEnableRecon);

    boolean NET_DVR_GetDeviceAbility(NativeLong lUserID, int dwAbilityType, String pInBuf, int dwInLength, String pOutBuf, int dwOutLength);

    //HCNetSDK.dll function definition

    public static class NET_DVR_FACE_PARAM_CTRL extends Structure {
        public int dwSize;
        public byte byMode;          //删除方式，0-按卡号方式删除，1-按读卡器删除
        public byte[] byRes1 = new byte[3];        //保留
        public NET_DVR_DEL_FACE_PARAM_MODE struProcessMode;  //处理方式
        public byte[] byRes = new byte[64];          //保留
    }

    public static class NET_DVR_DEL_FACE_PARAM_MODE extends Union {
        public byte[] uLen = new byte[588];   //联合体长度
        public NET_DVR_FACE_PARAM_BYCARD struByCard;     //按卡号的方式删除
        public NET_DVR_FACE_PARAM_BYREADER struByReader;   //按读卡器的方式删除
    }

    public static class NET_DVR_FACE_PARAM_BYREADER extends Structure {
        public int dwCardReaderNo;  //按值表示，人脸读卡器编号
        public byte byClearAllCard;  //是否删除所有卡的人脸信息，0-按卡号删除人脸信息，1-删除所有卡的人脸信息
        public byte[] byRes1 = new byte[3];       //保留
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN]; //人脸关联的卡号
        public byte[] byRes = new byte[548];          //保留
    }

    public static final int MAX_FACE_NUM = 2;    //最大人脸数


    public static class NET_DVR_FACE_PARAM_BYCARD extends Structure {
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN]; //人脸关联的卡号
        public byte[] byEnableCardReader = new byte[MAX_CARD_READER_NUM_512];  //人脸的读卡器信息，按数组表示
        public byte[] byFaceID = new byte[MAX_FACE_NUM];        //需要删除的人脸编号，按数组下标，值表示0-不删除，1-删除该人脸
        public byte[] byRes1 = new byte[42];          //保留
    }

    public static final int NET_DVR_DEL_FACE_PARAM_CFG = 2509;

    // function definition
    /* The SDK initialization function */
    public boolean NET_DVR_Init();

    /* Release the SDK resources, before the end of the procedure call*/
    public boolean NET_DVR_Cleanup();

    /* Enable to write the log file function
     * [in] nLogLevel(default 0) - log level, 0:close, 1:ERROR, 2:ERROR and DEBUG, 3-ALL
     * [in] strLogDir - file directory to save, default:"C:\\SdkLog\\"(win)and "/home/sdklog/"(linux)
     * [in] bAutoDel - whether to delete log file by auto, TRUE is default
     */
    public boolean NET_DVR_SetLogToFile(int bLogEnable, byte[] strLogDir, boolean bAutoDel);

    /* Returns the last error code of the operation */
    public int NET_DVR_GetLastError();

    /* Returns the last error code information of the operation */
    //public int NET_DVR_GetErrorMsg(ref int pErrorNo);
    public String NET_DVR_GetErrorMsg(Pointer pErrorNo);

    /* Alarm host device user configuration function(following two:get and set)
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] lUserIndex - index of user
     * [in] lpDeviceUser - lookup NET_DVR_ALARM_DEVICE_USER definition
     */
    //public boolean NET_DVR_SetAlarmDeviceUser(int lUserID, int lUserIndex, ref NET_DVR_ALARM_DEVICE_USER lpDeviceUser);
    public boolean NET_DVR_SetAlarmDeviceUser(NativeLong lUserID, int lUserIndex, Pointer lpDeviceUser);

    //public boolean NET_DVR_GetAlarmDeviceUser(int lUserID, int lUserIndex, ref NET_DVR_ALARM_DEVICE_USER lpDeviceUser);
    public boolean NET_DVR_GetAlarmDeviceUser(NativeLong lUserID, int lUserIndex, Pointer lpDeviceUser);

    /* Get device configuration information function
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] dwCommand - the configuration command(usually with NET_DVR_ prefix)
     * [in] lChannel - channel number with command related, 0xFFFFFFFF represent invalid
     * [out] lpOutBuffer - a pointer to a buffer to receive data
     * [in] dwOutBufferSize- the receive data buffer size, don't assign 0, unit:byte
     * [out] lpBytesReturned - pointer to the length of the data received, e.g. a int type pointer, can't be NULL
     */
    public boolean NET_DVR_GetDVRConfig(NativeLong lUserID, int dwCommand, NativeLong lChannel, Pointer lpOutBuffer, int dwOutBufferSize, IntByReference lpBytesReturned);

    boolean NET_DVR_GetDVRConfig(int lUserID, int dwCommand, int lChannel, Pointer lpOutBuffer, int dwOutBufferSize, IntByReference lpBytesReturned);

    /* Set device configuration information function
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] dwCommand - the configuration command(usually with NET_DVR_ prefix)
     * [in] lChannel - channel number with command related, 0xFFFFFFFF represent invalid
     * [in] lpInBuffer - a pointer to a buffer of send data
     * [in] dwInBufferSize- the send data buffer size, unit:byte
     */
    //public boolean NET_DVR_SetDVRConfig(int lUserID, int dwCommand, int lChannel, int lpInBuffer, int dwInBufferSize);
    public boolean NET_DVR_SetDVRConfig(NativeLong lUserID, int dwCommand, NativeLong lChannel, Pointer lpInBuffer, int dwInBufferSize);

    /* Long connection call back function
     * [out] dwType - refer enum NET_SDK_CALLBACK_TYPE
     * [out] lpBuffer - pointer to data buffer(user manual for more details)
     * [out] dwBufLen - the buffer size
     * [out] pUserData - pointer to user input data
     */
    // public delegate void RemoteConfigCallback(int dwType, int lpBuffer, int dwBufLen, int pUserData);

    // Long connection configuration function
    /* Start the remote configuration
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] dwCommand - the configuration command(usually with NET_DVR_ prefix)
     * [in] lpInBuffer - a pointer to a buffer of send data
     * [in] dwInBufferLen - the send data buffer size, unit:byte
     * [in] cbStateCallback - the callback function
     * [in] pUserData - pointer to user input data
     */
    //public int NET_DVR_StartRemoteConfig(int lUserID, int dwCommand, int lpInBuffer, Int32 dwInBufferLen, RemoteConfigCallback cbStateCallback, int pUserData);

    /* Send a long connection data
     * [in] lHandle - handle ,NET_DVR_StartRemoteConfig return value
     * [in] dwDataType - refer enum LONG_CFG_SEND_DATA_TYPE_ENUM, associated with NET_DVR_StartRemoteConfig command parameters
     *                   (user manual for more details)
     * [in] pSendBuf - a pointer to a buffer of send data, associated with dwDataType
     * [in] dwBufSize - the send data buffer size, unit:byte
     */
    //public boolean NET_DVR_SendRemoteConfig(int lHandle, int dwDataType, int pSendBuf, int dwBufSize);

    // stop a long connection
    // [in] lHandle - handle ,NET_DVR_StartRemoteConfig return value
    //public boolean NET_DVR_StopRemoteConfig(int lHandle);

    /* get long connection configuration status
     * [in] lHandle - handle ,NET_DVR_StartRemoteConfig return value
     * [out] pState - the return status pointer
     */
    //public boolean NET_DVR_GetRemoteConfigState(int lHandle, int pState);

    /* obtain the result of the information one by one
     * [in] lHandle - handle ,NET_DVR_StartRemoteConfig return value
     * [out] lpOutBuff - a pointer to a buffer to receive data(user manual for more details)
     * [in] dwOutBuffSize- the receive data buffer size, unit:byte
     */
    //public int NET_DVR_GetNextRemoteConfig(int lHandle, int lpOutBuff, int dwOutBuffSize);

    /* Batch for device configuration information (with sending data)
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] dwCommand - the configuration command(usually with NET_DVR_ prefix)
     * [in] dwCount - the number of configuration at a time, 0 and 1 represent one, in order to increase, maximum:64
     * [in] lpInBuffer - a pointer to conditions buffer(user manual for more details)
     * [in] dwInBufferSize- the conditions buffer size, unit:byte
     * [out] lpStatusList - a pointer to the error code list, One to one correspondence(user manual for more details)
     * [out] lpOutBuffer - a pointer to receive data buffer, One to one correspondence(user manual for more details)
     * [in] dwOutBufferSize- the receive data buffer size, unit:byte
     */
    //public boolean NET_DVR_GetDeviceConfig(int lUserID, int dwCommand, int dwCount, int lpInBuffer, int dwInBufferSize, int lpStatusList, int lpOutBuffer, int dwOutBufferSize);

    /* Batch for device configuration information (with sending data)
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] dwCommand - the configuration command(usually with NET_DVR_ prefix)
     * [in] dwCount - the number of configuration at a time, 0 and 1 represent one, in order to increase, maximum:64
     * [in] lpInBuffer - a pointer to conditions buffer(user manual for more details)
     * [in] dwInBufferSize- the conditions buffer size, unit:byte
     * [out] lpStatusList - a pointer to the error code list, One to one correspondence(user manual for more details)
     * [out] lpInParamBuffer - a pointer to set parameters for the device buffer, One to one correspondence(user manual for more details)
     * [in] dwInParamBufferSize- the correspond data buffer size, unit:byte
     */
    //public boolean NET_DVR_SetDeviceConfig(int lUserID, int dwCommand, int dwCount, int lpInBuffer, int dwInBufferSize, int lpStatusList, int lpInParamBuffer, int dwInParamBufferSize);


    /* The remote control function
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] dwCommand - the configuration command(usually with NET_DVR_ prefix)
     * [in] dwCount - the number of configuration at a time, 0 and 1 represent one, in order to increase, maximum:64
     * [in] lpInBuffer - a pointer to send data buffer(user manual for more details)
     * [in] dwInBufferSize- the correspond buffer size, unit:byte
     */
    public boolean NET_DVR_RemoteControl(NativeLong lUserID, int dwCommand, Pointer lpInBuffer, int dwInBufferSize);

    /* login
     * [in] pLoginInfo - login parameters
     * [in] lpDeviceInfo - device informations
     */
    public int NET_DVR_Login_V40(NET_DVR_USER_LOGIN_INFO pLoginInfo, NET_DVR_DEVICEINFO_V40 lpDeviceInfo);

    public boolean NET_DVR_Logout(NativeLong lUserID);

    //public boolean NET_DVR_Logout_V30(Int32 lUserID);

    //public delegate void RealDataCallBack(int lPlayHandle, int dwDataType, int pBuffer, int dwBufSize, int pUser);

    //public int NET_DVR_RealPlay_V40(int lUserID, ref NET_DVR_PREVIEWINFO lpPreviewInfo, RealDataCallBack fRealDataCallBack_V30, int pUser);

    // alarm

    /* Set up alarm upload channel, to obtain the information such as alarm*/
    //public int NET_DVR_SetupAlarmChan(int lUserID);
    //public int NET_DVR_SetupAlarmChan_V30(int lUserID);
    //public int NET_DVR_SetupAlarmChan_V41(int lUserID, ref NET_DVR_SETUPALARM_PARAM lpSetupParam);

    /* shut down alarm upload channel, to obtain the information such as alarm*/
    //public boolean NET_DVR_CloseAlarmChan(int lAlarmHandle);
    //public boolean NET_DVR_CloseAlarmChan_V30(int lAlarmHandle);


    /* Alarm information callback function
     * [out] lCommand - message type upload(user manual for more details) entrance guard device : COMM_ALARM_ACS
     * [out] pAlarmer -  information of alarm device
     * [out] pAlarmInfo - alarm information (NET_DVR_ACS_ALARM_INFO)
     * [out] dwBufLen - size of pAlarmInfo
     * [out] pUser - user data
     */
    //public delegate void MSGCallBack(int lCommand, ref NET_DVR_ALARMER pAlarmer, int pAlarmInfo, int dwBufLen, int pUser);
//    public delegate boolean MSGCallBack_V31(int lCommand, ref NET_DVR_ALARMER pAlarmer, int pAlarmInfo, int dwBufLen, int pUser);

    /* Alarm information registered callback function
     * [in] iIndex - iIndex, scope:[0,15]
     * [in] fMessageCallBack - callback function
     * [in] pUser - user data
     */
    //public boolean NET_DVR_SetDVRMessageCallBack_V50(int iIndex, MSGCallBack fMessageCallBack, int pUser);

//    public boolean NET_DVR_SetDVRMessageCallBack_V31(MSGCallBack_V31 fMessageCallBack, int pUser);

    /* NET_DVR_GetDeviceAbility get device ability
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] dwAbilityType - the configuration command(ACS_ABILITY)
     * [in] pInBuf - a pointer to send data buffer(user manual for more details)
     * [in] dwInLength - the correspond buffer size, unit:byte
     * [out] pOutBuf- out buff(ACS_ABILITY is described with XML)
     * [in] dwOutLength - the correspond buffer size, unit:byte
     */
    //public boolean NET_DVR_GetDeviceAbility(int lUserID, int dwAbilityType, int pInBuf, int dwInLength, int pOutBuf, int dwOutLength);
    /* Get to the SDK version information*/
    //public int NET_DVR_GetSDKVersion(); //convert return type int to int
    /* Get version number of the SDK and build information*/
    //public int NET_DVR_GetSDKBuildVersion(); //convert return type int to int

    /**
     * remote control gateway
     * [in] lUserID - NET_DVR_Login_V40 return value
     * [in] lGatewayIndex - 1-begin 0xffffffff-all
     * [in] dwStaic - : 0-close，1-open，2-always open，3-always close
     */
    //public boolean NET_DVR_ControlGateway(int lUserID, int lGatewayIndex, int dwStaic);
    public boolean NET_DVR_STDXMLConfig(int lUserID, int lpInputParam, int lpOutputParam);

    //HCNetSDK.dll macro definition
    //macro definition
    //region common use

    /*******************Global Error Code**********************/
    public static final int NET_DVR_NOERROR = 0; //No Error
    public static final int NET_DVR_PASSWORD_ERROR = 1;//Username or Password error
    public static final int NET_DVR_NOENOUGHPRI = 2;//Don't have enough authority
    public static final int NET_DVR_NOINIT = 3;//have not Initialized
    public static final int NET_DVR_CHANNEL_ERROR = 4;//Channel number error
    public static final int NET_DVR_OVER_MAXLINK = 5;//Number of clients connecting to DVR beyonds the Maximum
    public static final int NET_DVR_VERSIONNOMATCH = 6;//Version is not matched
    public static final int NET_DVR_NETWORK_FAIL_CONNECT = 7;//Connect to server failed
    public static final int NET_DVR_NETWORK_SEND_ERROR = 8;//Send data to server failed
    public static final int NET_DVR_NETWORK_RECV_ERROR = 9;//Receive data from server failed
    public static final int NET_DVR_NETWORK_RECV_TIMEOUT = 10;//Receive data from server timeout
    public static final int NET_DVR_NETWORK_ERRORDATA = 11;//Transferred data has error
    public static final int NET_DVR_ORDER_ERROR = 12;//Wrong Sequence of invoking API
    public static final int NET_DVR_OPERNOPERMIT = 13;//No such authority.
    public static final int NET_DVR_COMMANDTIMEOUT = 14;//Execute command timeout
    public static final int NET_DVR_ERRORSERIALPORT = 15;//Serial port number error
    public static final int NET_DVR_ERRORALARMPORT = 16;//Alarm port error
    public static final int NET_DVR_PARAMETER_ERROR = 17;//Parameters error
    public static final int NET_DVR_CHAN_EXCEPTION = 18;//Server channel in error status
    public static final int NET_DVR_NODISK = 19;//No hard disk
    public static final int NET_DVR_ERRORDISKNUM = 20;//Hard disk number error
    public static final int NET_DVR_DISK_FULL = 21;//Server's hard disk is full
    public static final int NET_DVR_DISK_ERROR = 22;//Server's hard disk error
    public static final int NET_DVR_NOSUPPORT = 23;//Server doesn't support
    public static final int NET_DVR_BUSY = 24;//Server is busy
    public static final int NET_DVR_MODIFY_FAIL = 25;//Server modification failed
    public static final int NET_DVR_PASSWORD_FORMAT_ERROR = 26;///Input format of Password error
    public static final int NET_DVR_DISK_FORMATING = 27;//Hard disk is formating,  cannot execute.
    public static final int NET_DVR_DVRNORESOURCE = 28;//DVR don't have enough resource
    public static final int NET_DVR_DVROPRATEFAILED = 29;//DVR Operation failed
    public static final int NET_DVR_OPENHOSTSOUND_FAIL = 30;//Open PC audio failed
    public static final int NET_DVR_DVRVOICEOPENED = 31;///Server's talk channel is occupied
    public static final int NET_DVR_TIMEINPUTERROR = 32;//Time input is not correct
    public static final int NET_DVR_NOSPECFILE = 33;//Can't playback the file that does not exist in Server
    public static final int NET_DVR_CREATEFILE_ERROR = 34;//Create file error
    public static final int NET_DVR_FILEOPENFAIL = 35;///Open file error
    public static final int NET_DVR_OPERNOTFINISH = 36; //The previous operation is not finished yet
    public static final int NET_DVR_GETPLAYTIMEFAIL = 37;//Get current playing time error
    public static final int NET_DVR_PLAYFAIL = 38;//Playback error
    public static final int NET_DVR_FILEFORMAT_ERROR = 39;//Wrong file format
    public static final int NET_DVR_DIR_ERROR = 40;//Wrong directory
    public static final int NET_DVR_ALLOC_RESOURCE_ERROR = 41;//Assign resource error
    public static final int NET_DVR_AUDIO_MODE_ERROR = 42;//Audio card mode error
    public static final int NET_DVR_NOENOUGH_BUF = 43;///Buffer is too small
    public static final int NET_DVR_CREATESOCKET_ERROR = 44;//Create SOCKET error
    public static final int NET_DVR_SETSOCKET_ERROR = 45;//Setup SOCKET error
    public static final int NET_DVR_MAX_NUM = 46;//Reach the maximum number
    public static final int NET_DVR_USERNOTEXIST = 47;//User does not exist
    public static final int NET_DVR_WRITEFLASHERROR = 48;//Write to FLASH error
    public static final int NET_DVR_UPGRADEFAIL = 49;//DVR update failed
    public static final int NET_DVR_CARDHAVEINIT = 50;//Decoding Card has been initialized already
    public static final int NET_DVR_PLAYERFAILED = 51;//Invoke API of player library error
    public static final int NET_DVR_MAX_USERNUM = 52;//Reach the maximum number of Users
    public static final int NET_DVR_GETLOCALIPANDMACFAIL = 53;//Failed to get Client software's IP or MAC address
    public static final int NET_DVR_NOENCODEING = 54;//No encoding on this channel
    public static final int NET_DVR_IPMISMATCH = 55;//IP address is not matched
    public static final int NET_DVR_MACMISMATCH = 56;//MAC address is not matched

    public static final int NET_DVR_USER_LOCKED = 153;
    /*******************END**********************/

    public static final int NET_DVR_DEV_ADDRESS_MAX_LEN = 129; //device address max length
    public static final int NET_DVR_LOGIN_USERNAME_MAX_LEN = 64;   //login username max length
    public static final int NET_DVR_LOGIN_PASSWD_MAX_LEN = 64; //login password max length
    public static final int SERIALNO_LEN = 48; //serial number length
    public static final int STREAM_ID_LEN = 32;
    public static final int MAX_AUDIO_V40 = 8;
    public static final int LOG_INFO_LEN = 11840; // log append information

    public static final int MAX_NAMELEN = 16;        //DVR's local Username
    public static final int MAX_DOMAIN_NAME = 64;  // max domain name length
    public static final int MAX_ETHERNET = 2;      // device
    public static final int NAME_LEN = 32;// name length
    public static final int PASSWD_LEN = 16;//password length
    public static final int MAX_RIGHT = 32;        //Authority permitted by Device (1- 12 for local authority,  13- 32 for remote authority)
    public static final int MACADDR_LEN = 6;//mac adress length
    public static final int DEV_TYPE_NAME_LEN = 24;

    public static final int MAX_ANALOG_CHANNUM = 32;      //32 analog channels in total
    public static final int MAX_IP_CHANNEL = 32;      //9000 DVR can connect 32 IP channels
    public static final int MAX_CHANNUM_V30 = (MAX_ANALOG_CHANNUM + MAX_IP_CHANNEL);   //64
    public static final int MAX_CHANNUM_V40 = 512;
    public static final int MAX_IP_DEVICE_V40 = 64;      //Maximum number of IP devices that can be added, the value is 64, including IVMS-2000
    public static final int DEV_ID_LEN = 32;

    public static final int MAX_IP_DEVICE = 32;//9000 DVR can connect 32 IP devices
    public static final int MAX_IP_ALARMIN_V40 = 4096;//Maximum number of alarm input channels that can be added
    public static final int MAX_IP_ALARMOUT_V40 = 4096;//Maximum number of alarm output channels that can be added
    public static final int MAX_IP_ALARMIN = 128;//Maximum number of alarm input channels that can be added
    public static final int MAX_IP_ALARMOUT = 64;//Maximum number of alarm output channels that can be added
    public static final int URL_LEN = 240;   //URL length

    public static final int ACS_ABILITY = 0x801; //acs ability

    public static final int NET_DVR_CLEAR_ACS_PARAM = 2118;    //clear acs host parameters

    //region acs event upload

    public static final int COMM_ALARM_ACS = 0x5002; //access card alarm

    /* Alarm */
    // Main Type
    public static final int MAJOR_ALARM = 0x1;
    // Hypo- Type
    public static final int MINOR_ALARMIN_SHORT_CIRCUIT = 0x400; // region short circuit
    public static final int MINOR_ALARMIN_BROKEN_CIRCUIT = 0x401; // region broken circuit
    public static final int MINOR_ALARMIN_EXCEPTION = 0x402; // region exception
    public static final int MINOR_ALARMIN_RESUME = 0x403; // region resume
    public static final int MINOR_HOST_DESMANTLE_ALARM = 0x404; // host desmantle alarm
    public static final int MINOR_HOST_DESMANTLE_RESUME = 0x405; //  host desmantle resume
    public static final int MINOR_CARD_READER_DESMANTLE_ALARM = 0x406; // card reader desmantle alarm
    public static final int MINOR_CARD_READER_DESMANTLE_RESUME = 0x407; // card reader desmantle resume
    public static final int MINOR_CASE_SENSOR_ALARM = 0x408; // case sensor alarm
    public static final int MINOR_CASE_SENSOR_RESUME = 0x409; // case sensor resume
    public static final int MINOR_STRESS_ALARM = 0x40a; // stress alarm
    public static final int MINOR_OFFLINE_ECENT_NEARLY_FULL = 0x40b; // offline ecent nearly full
    public static final int MINOR_CARD_MAX_AUTHENTICATE_FAIL = 0x40c; // card max authenticate fall
    public static final int MINOR_SD_CARD_FULL = 0x40d; // SD card is full
    public static final int MINOR_LINKAGE_CAPTURE_PIC = 0x40e; // lingage capture picture
    public static final int MINOR_SECURITY_MODULE_DESMANTLE_ALARM = 0x40f;  //Door control security module desmantle alarm
    public static final int MINOR_SECURITY_MODULE_DESMANTLE_RESUME = 0x410;  //Door control security module desmantle resume
    public static final int MINOR_POS_START_ALARM = 0x411; // POS Start
    public static final int MINOR_POS_END_ALARM = 0x412; // POS end
    public static final int MINOR_FACE_IMAGE_QUALITY_LOW = 0x413; // face image quality low
    public static final int MINOR_FINGE_RPRINT_QUALITY_LOW = 0x414; // finger print quality low
    public static final int MINOR_FIRE_IMPORT_SHORT_CIRCUIT = 0x415; // Fire import short circuit
    public static final int MINOR_FIRE_IMPORT_BROKEN_CIRCUIT = 0x416; // Fire import broken circuit
    public static final int MINOR_FIRE_IMPORT_RESUME = 0x417; // Fire import resume
    public static final int MINOR_FIRE_BUTTON_TRIGGER = 0x418; // fire button trigger
    public static final int MINOR_FIRE_BUTTON_RESUME = 0x419; // fire button resume
    public static final int MINOR_MAINTENANCE_BUTTON_TRIGGER = 0x41a; // maintenance button trigger
    public static final int MINOR_MAINTENANCE_BUTTON_RESUME = 0x41b; // maintenance button resume
    public static final int MINOR_EMERGENCY_BUTTON_TRIGGER = 0x41c; // emergency button trigger
    public static final int MINOR_EMERGENCY_BUTTON_RESUME = 0x41d; // emergency button resume
    public static final int MINOR_DISTRACT_CONTROLLER_ALARM = 0x41e; // distract controller alarm
    public static final int MINOR_DISTRACT_CONTROLLER_RESUME = 0x41f; // distract controller resume

    /* Exception*/
    // Main Type
    public static final int MAJOR_EXCEPTION = 0x2;
    // Hypo- Type

    public static final int MINOR_NET_BROKEN = 0x27; // Network disconnected
    public static final int MINOR_RS485_DEVICE_ABNORMAL = 0x3a; // RS485 connect status exception
    public static final int MINOR_RS485_DEVICE_REVERT = 0x3b; // RS485 connect status exception recovery

    public static final int MINOR_DEV_POWER_ON = 0x400; // device power on
    public static final int MINOR_DEV_POWER_OFF = 0x401; // device power off
    public static final int MINOR_WATCH_DOG_RESET = 0x402; // watch dog reset
    public static final int MINOR_LOW_BATTERY = 0x403; // low battery
    public static final int MINOR_BATTERY_RESUME = 0x404; // battery resume
    public static final int MINOR_AC_OFF = 0x405; // AC off
    public static final int MINOR_AC_RESUME = 0x406; // AC resume
    public static final int MINOR_NET_RESUME = 0x407; // Net resume
    public static final int MINOR_FLASH_ABNORMAL = 0x408; // FLASH abnormal
    public static final int MINOR_CARD_READER_OFFLINE = 0x409; // card reader offline
    public static final int MINOR_CARD_READER_RESUME = 0x40a; // card reader resume
    public static final int MINOR_INDICATOR_LIGHT_OFF = 0x40b; // Indicator Light Off
    public static final int MINOR_INDICATOR_LIGHT_RESUME = 0x40c; // Indicator Light Resume
    public static final int MINOR_CHANNEL_CONTROLLER_OFF = 0x40d; // channel controller off
    public static final int MINOR_CHANNEL_CONTROLLER_RESUME = 0x40e; // channel controller resume
    public static final int MINOR_SECURITY_MODULE_OFF = 0x40f; // Door control security module off
    public static final int MINOR_SECURITY_MODULE_RESUME = 0x410; // Door control security module resume
    public static final int MINOR_BATTERY_ELECTRIC_LOW = 0x411; // battery electric low
    public static final int MINOR_BATTERY_ELECTRIC_RESUME = 0x412; // battery electric resume
    public static final int MINOR_LOCAL_CONTROL_NET_BROKEN = 0x413; // Local control net broken
    public static final int MINOR_LOCAL_CONTROL_NET_RSUME = 0x414; // Local control net resume
    public static final int MINOR_MASTER_RS485_LOOPNODE_BROKEN = 0x415; // Master RS485 loop node broken
    public static final int MINOR_MASTER_RS485_LOOPNODE_RESUME = 0x416; // Master RS485 loop node resume
    public static final int MINOR_LOCAL_CONTROL_OFFLINE = 0x417; // Local control offline
    public static final int MINOR_LOCAL_CONTROL_RESUME = 0x418; // Local control resume
    public static final int MINOR_LOCAL_DOWNSIDE_RS485_LOOPNODE_BROKEN = 0x419; // Local downside RS485 loop node broken
    public static final int MINOR_LOCAL_DOWNSIDE_RS485_LOOPNODE_RESUME = 0x41a; // Local downside RS485 loop node resume
    public static final int MINOR_DISTRACT_CONTROLLER_ONLINE = 0x41b; // distract controller online
    public static final int MINOR_DISTRACT_CONTROLLER_OFFLINE = 0x41c; // distract controller offline
    public static final int MINOR_ID_CARD_READER_NOT_CONNECT = 0x41d; // Id card reader not connected(intelligent dedicated)
    public static final int MINOR_ID_CARD_READER_RESUME = 0x41e; //Id card reader connection restored(intelligent dedicated)
    public static final int MINOR_FINGER_PRINT_MODULE_NOT_CONNECT = 0x41f; // fingerprint module is not connected(intelligent dedicated)
    public static final int MINOR_FINGER_PRINT_MODULE_RESUME = 0x420; // The fingerprint module connection restored(intelligent dedicated)
    public static final int MINOR_CAMERA_NOT_CONNECT = 0x421; // Camera not connected
    public static final int MINOR_CAMERA_RESUME = 0x422; // Camera connection restored
    public static final int MINOR_COM_NOT_CONNECT = 0x423; // COM not connected
    public static final int MINOR_COM_RESUME = 0x424;// COM connection restored
    public static final int MINOR_DEVICE_NOT_AUTHORIZE = 0x425; // device are not authorized
    public static final int MINOR_PEOPLE_AND_ID_CARD_DEVICE_ONLINE = 0x426; // people and ID card device online
    public static final int MINOR_PEOPLE_AND_ID_CARD_DEVICE_OFFLINE = 0x427;// people and ID card device offline
    public static final int MINOR_LOCAL_LOGIN_LOCK = 0x428; // local login lock
    public static final int MINOR_LOCAL_LOGIN_UNLOCK = 0x429; //local login unlock

    /* Operation  */
    // Main Type
    public static final int MAJOR_OPERATION = 0x3;

    // Hypo- Type
    public static final int MINOR_LOCAL_UPGRADE = 0x5a; // Upgrade  (local)
    public static final int MINOR_REMOTE_LOGIN = 0x70; // Login  (remote)
    public static final int MINOR_REMOTE_LOGOUT = 0x71; // Logout   (remote)
    public static final int MINOR_REMOTE_ARM = 0x79; // On guard   (remote)
    public static final int MINOR_REMOTE_DISARM = 0x7a; // Disarm   (remote)
    public static final int MINOR_REMOTE_REBOOT = 0x7b; // Reboot   (remote)
    public static final int MINOR_REMOTE_UPGRADE = 0x7e; // upgrade  (remote)
    public static final int MINOR_REMOTE_CFGFILE_OUTPUT = 0x86; // Export Configuration   (remote)
    public static final int MINOR_REMOTE_CFGFILE_INTPUT = 0x87; // Import Configuration  (remote)
    public static final int MINOR_REMOTE_ALARMOUT_OPEN_MAN = 0xd6; // remote mamual open alarmout
    public static final int MINOR_REMOTE_ALARMOUT_CLOSE_MAN = 0xd7; // remote mamual close alarmout

    public static final int MINOR_REMOTE_OPEN_DOOR = 0x400; // remote open door
    public static final int MINOR_REMOTE_CLOSE_DOOR = 0x401; // remote close door (controlled)
    public static final int MINOR_REMOTE_ALWAYS_OPEN = 0x402; // remote always open door (free)
    public static final int MINOR_REMOTE_ALWAYS_CLOSE = 0x403; // remote always close door (forbiden)
    public static final int MINOR_REMOTE_CHECK_TIME = 0x404; // remote check time
    public static final int MINOR_NTP_CHECK_TIME = 0x405; // ntp check time
    public static final int MINOR_REMOTE_CLEAR_CARD = 0x406; // remote clear card
    public static final int MINOR_REMOTE_RESTORE_CFG = 0x407; // remote restore configure
    public static final int MINOR_ALARMIN_ARM = 0x408; // alarm in arm
    public static final int MINOR_ALARMIN_DISARM = 0x409; // alarm in disarm
    public static final int MINOR_LOCAL_RESTORE_CFG = 0x40a; // local configure restore
    public static final int MINOR_REMOTE_CAPTURE_PIC = 0x40b; // remote capture picture
    public static final int MINOR_MOD_NET_REPORT_CFG = 0x40c; // modify net report cfg
    public static final int MINOR_MOD_GPRS_REPORT_PARAM = 0x40d; // modify GPRS report param
    public static final int MINOR_MOD_REPORT_GROUP_PARAM = 0x40e; // modify report group param
    public static final int MINOR_UNLOCK_PASSWORD_OPEN_DOOR = 0x40f; // unlock password open door
    public static final int MINOR_AUTO_RENUMBER = 0x410; // auto renumber
    public static final int MINOR_AUTO_COMPLEMENT_NUMBER = 0x411; // auto complement number
    public static final int MINOR_NORMAL_CFGFILE_INPUT = 0x412; // normal cfg file input
    public static final int MINOR_NORMAL_CFGFILE_OUTTPUT = 0x413; // normal cfg file output
    public static final int MINOR_CARD_RIGHT_INPUT = 0x414; // card right input
    public static final int MINOR_CARD_RIGHT_OUTTPUT = 0x415; // card right output
    public static final int MINOR_LOCAL_USB_UPGRADE = 0x416; // local USB upgrade
    public static final int MINOR_REMOTE_VISITOR_CALL_LADDER = 0x417; // visitor call ladder
    public static final int MINOR_REMOTE_HOUSEHOLD_CALL_LADDER = 0x418; // household call ladder

    /* Additional Log Info*/
    // Main Type
    public static final int MAJOR_EVENT = 0x5;/*event*/
    // Hypo- Type
    public static final int MINOR_LEGAL_CARD_PASS = 0x01; // legal card pass
    public static final int MINOR_CARD_AND_PSW_PASS = 0x02; // swipe and password pass
    public static final int MINOR_CARD_AND_PSW_FAIL = 0x03; // swipe and password fail
    public static final int MINOR_CARD_AND_PSW_TIMEOUT = 0x04; // swipe and password timeout
    public static final int MINOR_CARD_AND_PSW_OVER_TIME = 0x05; // swipe and password over time
    public static final int MINOR_CARD_NO_RIGHT = 0x06; // card no right
    public static final int MINOR_CARD_INVALID_PERIOD = 0x07; // invalid period
    public static final int MINOR_CARD_OUT_OF_DATE = 0x08; // card out of date
    public static final int MINOR_INVALID_CARD = 0x09; // invalid card
    public static final int MINOR_ANTI_SNEAK_FAIL = 0x0a; // anti sneak fail
    public static final int MINOR_INTERLOCK_DOOR_NOT_CLOSE = 0x0b; // interlock door doesn't close
    public static final int MINOR_NOT_BELONG_MULTI_GROUP = 0x0c; // card no belong multi group
    public static final int MINOR_INVALID_MULTI_VERIFY_PERIOD = 0x0d; // invalid multi verify period
    public static final int MINOR_MULTI_VERIFY_SUPER_RIGHT_FAIL = 0x0e; // have no super right in multi verify mode
    public static final int MINOR_MULTI_VERIFY_REMOTE_RIGHT_FAIL = 0x0f; // have no remote right in multi verify mode
    public static final int MINOR_MULTI_VERIFY_SUCCESS = 0x10; // success in multi verify mode
    public static final int MINOR_LEADER_CARD_OPEN_BEGIN = 0x11; // leader card begin to open
    public static final int MINOR_LEADER_CARD_OPEN_END = 0x12; // leader card end to open
    public static final int MINOR_ALWAYS_OPEN_BEGIN = 0x13; // always open begin
    public static final int MINOR_ALWAYS_OPEN_END = 0x14; // always open end
    public static final int MINOR_LOCK_OPEN = 0x15; // lock open
    public static final int MINOR_LOCK_CLOSE = 0x16; // lock close
    public static final int MINOR_DOOR_BUTTON_PRESS = 0x17; // press door open button
    public static final int MINOR_DOOR_BUTTON_RELEASE = 0x18; // release door open button
    public static final int MINOR_DOOR_OPEN_NORMAL = 0x19; // door open normal
    public static final int MINOR_DOOR_CLOSE_NORMAL = 0x1a; // door close normal
    public static final int MINOR_DOOR_OPEN_ABNORMAL = 0x1b; // open door abnormal
    public static final int MINOR_DOOR_OPEN_TIMEOUT = 0x1c; // open door timeout
    public static final int MINOR_ALARMOUT_ON = 0x1d; // alarm out turn on
    public static final int MINOR_ALARMOUT_OFF = 0x1e; // alarm out turn off
    public static final int MINOR_ALWAYS_CLOSE_BEGIN = 0x1f; // always close begin
    public static final int MINOR_ALWAYS_CLOSE_END = 0x20; // always close end
    public static final int MINOR_MULTI_VERIFY_NEED_REMOTE_OPEN = 0x21; // need remote open in multi verify mode
    public static final int MINOR_MULTI_VERIFY_SUPERPASSWD_VERIFY_SUCCESS = 0x22; // superpasswd verify success in multi verify mode
    public static final int MINOR_MULTI_VERIFY_REPEAT_VERIFY = 0x23; // repeat verify in multi verify mode
    public static final int MINOR_MULTI_VERIFY_TIMEOUT = 0x24; // timeout in multi verify mode
    public static final int MINOR_DOORBELL_RINGING = 0x25; // doorbell ringing
    public static final int MINOR_FINGERPRINT_COMPARE_PASS = 0x26; // fingerprint compare pass
    public static final int MINOR_FINGERPRINT_COMPARE_FAIL = 0x27; // fingerprint compare fail
    public static final int MINOR_CARD_FINGERPRINT_VERIFY_PASS = 0x28; // card and fingerprint verify pass
    public static final int MINOR_CARD_FINGERPRINT_VERIFY_FAIL = 0x29; // card and fingerprint verify fail
    public static final int MINOR_CARD_FINGERPRINT_VERIFY_TIMEOUT = 0x2a; // card and fingerprint verify timeout
    public static final int MINOR_CARD_FINGERPRINT_PASSWD_VERIFY_PASS = 0x2b; // card and fingerprint and passwd verify pass
    public static final int MINOR_CARD_FINGERPRINT_PASSWD_VERIFY_FAIL = 0x2c; // card and fingerprint and passwd verify fail
    public static final int MINOR_CARD_FINGERPRINT_PASSWD_VERIFY_TIMEOUT = 0x2d; // card and fingerprint and passwd verify timeout
    public static final int MINOR_FINGERPRINT_PASSWD_VERIFY_PASS = 0x2e; // fingerprint and passwd verify pass
    public static final int MINOR_FINGERPRINT_PASSWD_VERIFY_FAIL = 0x2f; // fingerprint and passwd verify fail
    public static final int MINOR_FINGERPRINT_PASSWD_VERIFY_TIMEOUT = 0x30; // fingerprint and passwd verify timeout
    public static final int MINOR_FINGERPRINT_INEXISTENCE = 0x31; // fingerprint inexistence
    public static final int MINOR_CARD_PLATFORM_VERIFY = 0x32; // card platform verify
    public static final int MINOR_CALL_CENTER = 0x33; // call center
    public static final int MINOR_FIRE_RELAY_TURN_ON_DOOR_ALWAYS_OPEN = 0x34; // fire relay turn on door always open
    public static final int MINOR_FIRE_RELAY_RECOVER_DOOR_RECOVER_NORMAL = 0x35; // fire relay recover door recover normal
    public static final int MINOR_FACE_AND_FP_VERIFY_PASS = 0x36; // face and finger print verify pass
    public static final int MINOR_FACE_AND_FP_VERIFY_FAIL = 0x37; // face and finger print verify fail
    public static final int MINOR_FACE_AND_FP_VERIFY_TIMEOUT = 0x38; // face and finger print verify timeout
    public static final int MINOR_FACE_AND_PW_VERIFY_PASS = 0x39; // face and password verify pass
    public static final int MINOR_FACE_AND_PW_VERIFY_FAIL = 0x3a; // face and password verify fail
    public static final int MINOR_FACE_AND_PW_VERIFY_TIMEOUT = 0x3b; // face and password verify timeout
    public static final int MINOR_FACE_AND_CARD_VERIFY_PASS = 0x3c; // face and card verify pass
    public static final int MINOR_FACE_AND_CARD_VERIFY_FAIL = 0x3d; // face and card verify fail
    public static final int MINOR_FACE_AND_CARD_VERIFY_TIMEOUT = 0x3e; // face and card verify timeout
    public static final int MINOR_FACE_AND_PW_AND_FP_VERIFY_PASS = 0x3f; // face and password and finger print verify pass
    public static final int MINOR_FACE_AND_PW_AND_FP_VERIFY_FAIL = 0x40; // face and password and finger print verify fail
    public static final int MINOR_FACE_AND_PW_AND_FP_VERIFY_TIMEOUT = 0x41; // face and password and finger print verify timeout
    public static final int MINOR_FACE_CARD_AND_FP_VERIFY_PASS = 0x42; // face and card and finger print verify pass
    public static final int MINOR_FACE_CARD_AND_FP_VERIFY_FAIL = 0x43; // face and card and finger print verify fail
    public static final int MINOR_FACE_CARD_AND_FP_VERIFY_TIMEOUT = 0x44; // face and card and finger print verify timeout
    public static final int MINOR_EMPLOYEENO_AND_FP_VERIFY_PASS = 0x45; // employee and finger print verify pass
    public static final int MINOR_EMPLOYEENO_AND_FP_VERIFY_FAIL = 0x46; // employee and finger print verify fail
    public static final int MINOR_EMPLOYEENO_AND_FP_VERIFY_TIMEOUT = 0x47; // employee and finger print verify timeout
    public static final int MINOR_EMPLOYEENO_AND_FP_AND_PW_VERIFY_PASS = 0x48; // employee and finger print and password verify pass
    public static final int MINOR_EMPLOYEENO_AND_FP_AND_PW_VERIFY_FAIL = 0x49; // employee and finger print and password verify fail
    public static final int MINOR_EMPLOYEENO_AND_FP_AND_PW_VERIFY_TIMEOUT = 0x4a; // employee and finger print and password verify timeout
    public static final int MINOR_FACE_VERIFY_PASS = 0x4b; // face verify pass
    public static final int MINOR_FACE_VERIFY_FAIL = 0x4c; // face verify fail
    public static final int MINOR_EMPLOYEENO_AND_FACE_VERIFY_PASS = 0x4d; // employee no and face verify pass
    public static final int MINOR_EMPLOYEENO_AND_FACE_VERIFY_FAIL = 0x4e; // employee no and face verify fail
    public static final int MINOR_EMPLOYEENO_AND_FACE_VERIFY_TIMEOUT = 0x4f; // employee no and face verify time out
    public static final int MINOR_FACE_RECOGNIZE_FAIL = 0x50; // face recognize fail
    public static final int MINOR_FIRSTCARD_AUTHORIZE_BEGIN = 0x51; // first card authorize begin
    public static final int MINOR_FIRSTCARD_AUTHORIZE_END = 0x52; // first card authorize end
    public static final int MINOR_DOORLOCK_INPUT_SHORT_CIRCUIT = 0x53; // door lock input short circuit
    public static final int MINOR_DOORLOCK_INPUT_BROKEN_CIRCUIT = 0x54; // door lock input broken circuit
    public static final int MINOR_DOORLOCK_INPUT_EXCEPTION = 0x55; // door lock input exception
    public static final int MINOR_DOORCONTACT_INPUT_SHORT_CIRCUIT = 0x56; // door contact input short circuit
    public static final int MINOR_DOORCONTACT_INPUT_BROKEN_CIRCUIT = 0x57; // door contact input broken circuit
    public static final int MINOR_DOORCONTACT_INPUT_EXCEPTION = 0x58; // door contact input exception
    public static final int MINOR_OPENBUTTON_INPUT_SHORT_CIRCUIT = 0x59; // open button input short circuit
    public static final int MINOR_OPENBUTTON_INPUT_BROKEN_CIRCUIT = 0x5a; // open button input broken circuit
    public static final int MINOR_OPENBUTTON_INPUT_EXCEPTION = 0x5b; // open button input exception
    public static final int MINOR_DOORLOCK_OPEN_EXCEPTION = 0x5c; // door lock open exception
    public static final int MINOR_DOORLOCK_OPEN_TIMEOUT = 0x5d; // door lock open timeout
    public static final int MINOR_FIRSTCARD_OPEN_WITHOUT_AUTHORIZE = 0x5e; // first card open without authorize
    public static final int MINOR_CALL_LADDER_RELAY_BREAK = 0x5f; // call ladder relay break
    public static final int MINOR_CALL_LADDER_RELAY_CLOSE = 0x60; // call ladder relay close
    public static final int MINOR_AUTO_KEY_RELAY_BREAK = 0x61; // auto key relay break
    public static final int MINOR_AUTO_KEY_RELAY_CLOSE = 0x62; // auto key relay close
    public static final int MINOR_KEY_CONTROL_RELAY_BREAK = 0x63; // key control relay break
    public static final int MINOR_KEY_CONTROL_RELAY_CLOSE = 0x64; // key control relay close
    public static final int MINOR_EMPLOYEENO_AND_PW_PASS = 0x65; // minor employee no and password pass
    public static final int MINOR_EMPLOYEENO_AND_PW_FAIL = 0x66; // minor employee no and password fail
    public static final int MINOR_EMPLOYEENO_AND_PW_TIMEOUT = 0x67; // minor employee no and password timeout
    public static final int MINOR_HUMAN_DETECT_FAIL = 0x68; // human detect fail
    public static final int MINOR_PEOPLE_AND_ID_CARD_COMPARE_PASS = 0x69; // the comparison with people and id card success
    public static final int MINOR_PEOPLE_AND_ID_CARD_COMPARE_FAIL = 0x70; // the comparison with people and id card failed
    public static final int MINOR_CERTIFICATE_BLACK_LIST = 0x71; // black list
    public static final int MINOR_LEGAL_MESSAGE = 0x72; // legal message
    public static final int MINOR_ILLEGAL_MESSAGE = 0x73; // illegal messag
    public static final int MINOR_MAC_DETECT = 0x74; // mac detect

    //region card parameters configuration
    public static final int CARD_PARAM_CARD_VALID = 0x00000001;  //card valid parameter
    public static final int CARD_PARAM_VALID = 0x00000002;  //valid period parameter
    public static final int CARD_PARAM_CARD_TYPE = 0x00000004;  //card type parameter
    public static final int CARD_PARAM_DOOR_RIGHT = 0x00000008;  //door right parameter
    public static final int CARD_PARAM_LEADER_CARD = 0x00000010;  //leader card parameter
    public static final int CARD_PARAM_SWIPE_NUM = 0x00000020;  //max swipe time parameter
    public static final int CARD_PARAM_GROUP = 0x00000040;  //belong group parameter
    public static final int CARD_PARAM_PASSWORD = 0x00000080;  //card password parameter
    public static final int CARD_PARAM_RIGHT_PLAN = 0x00000100;  //card right plan parameter
    public static final int CARD_PARAM_SWIPED_NUM = 0x00000200;  //has swiped card time parameter
    public static final int CARD_PARAM_EMPLOYEE_NO = 0x00000400;  //employee no

    public static final int ACS_CARD_NO_LEN = 32;  //access card No. len
    public static final int MAX_DOOR_NUM_256 = 256; //max door num
    public static final int MAX_GROUP_NUM_128 = 128; //The largest number of grou
    public static final int CARD_PASSWORD_LEN = 8;   // card password len
    public static final int MAX_CARD_RIGHT_PLAN_NUM = 4;   //max card right plan number
    public static final int MAX_DOOR_CODE_LEN = 8; //room code length
    public static final int MAX_LOCK_CODE_LEN = 8; //lock code length

    public static final int MAX_CASE_SENSOR_NUM = 8;   //max case sensor number
    public static final int MAX_CARD_READER_NUM_512 = 512; //max card reader num
    public static final int MAX_ALARMHOST_ALARMIN_NUM = 512; //Max number of alarm host alarm input ports
    public static final int MAX_ALARMHOST_ALARMOUT_NUM = 512; //Max number of alarm host alarm output ports

    public static final int NET_DVR_GET_ACS_WORK_STATUS_V50 = 2180;   //Access door host working condition (V50)
    public static final int NET_DVR_GET_CARD_CFG_V50 = 2178;    //Parameters to acquire new CARDS (V50)
    public static final int NET_DVR_SET_CARD_CFG_V50 = 2179;    //Setting up the new parameters (V50)

    //region door parameters configuration
    public static final int DOOR_NAME_LEN = 32;//door name len
    public static final int STRESS_PASSWORD_LEN = 8;//stress password len
    public static final int SUPER_PASSWORD_LEN = 8;//super password len
    public static final int UNLOCK_PASSWORD_LEN = 8;
    public static final int MAX_DOOR_NUM = 32;
    public static final int MAX_GROUP_NUM = 32;
    public static final int LOCAL_CONTROLLER_NAME_LEN = 32;

    public static final int NET_DVR_GET_DOOR_CFG = 2108; //get door parameter
    public static final int NET_DVR_SET_DOOR_CFG = 2109; //set door parameter

    //region group configuration

    public static final int GROUP_NAME_LEN = 32;

    public static final int NET_DVR_GET_GROUP_CFG = 2112;   //get group parameter
    public static final int NET_DVR_SET_GROUP_CFG = 2113;    //set group parameter

    //region user parameters configuration

    public static final int MAX_ALARMHOST_VIDEO_CHAN = 64;

    public static final int NET_DVR_GET_DEVICECFG_V40 = 1100;//Get extended device parameters
    public static final int NET_DVR_SET_DEVICECFG_V40 = 1101;//Set extended device parameters

    //region cardreader configuration

    public static final int CARD_READER_DESCRIPTION = 32;       //card reader description

    public static final int NET_DVR_GET_CARD_READER_CFG_V50 = 2505;      //get card reader configure v50
    public static final int NET_DVR_SET_CARD_READER_CFG_V50 = 2506;      //set card reader configure v50

    //region fingerprint configuration

    public static final int MAX_FINGER_PRINT_LEN = 768; //max finger print len
    public static final int MAX_FINGER_PRINT_NUM = 10; //max finger print num
    public static final int ERROR_MSG_LEN = 32;

    public static final int NET_DVR_GET_FINGERPRINT_CFG = 2150;    //get fingerprint parameter
    public static final int NET_DVR_SET_FINGERPRINT_CFG = 2151;    //set fingerprint parameter
    public static final int NET_DVR_DEL_FINGERPRINT_CFG = 2152;    //delete fingerprint parameter


    //endregion

    //region plan configuration

    public static final int MAX_DAYS = 7; //The number of days in a week
    public static final int MAX_TIMESEGMENT_V30 = 8; //Maximum number of time segments in 9000 DVR's guard schedule
    public static final int HOLIDAY_GROUP_NAME_LEN = 32;  //holiday group name len
    public static final int MAX_HOLIDAY_PLAN_NUM = 16;  //holiday max plan number
    public static final int TEMPLATE_NAME_LEN = 32; //plan template name len
    public static final int MAX_HOLIDAY_GROUP_NUM = 16;   //plan template max group number

    public static final int NET_DVR_GET_WEEK_PLAN_CFG = 2100; //get door status week plan config
    public static final int NET_DVR_SET_WEEK_PLAN_CFG = 2101; //set door status week plan config
    public static final int NET_DVR_GET_DOOR_STATUS_HOLIDAY_PLAN = 2102; //get door status holiday week plan config
    public static final int NET_DVR_SET_DOOR_STATUS_HOLIDAY_PLAN = 2103; //set door status holiday week plan config
    public static final int NET_DVR_GET_DOOR_STATUS_HOLIDAY_GROUP = 2104; //get door holiday group parameter
    public static final int NET_DVR_SET_DOOR_STATUS_HOLIDAY_GROUP = 2105; //set door holiday group parameter
    public static final int NET_DVR_GET_DOOR_STATUS_PLAN_TEMPLATE = 2106; //get door status plan template parameter
    public static final int NET_DVR_SET_DOOR_STATUS_PLAN_TEMPLATE = 2107; //set door status plan template parameter
    public static final int NET_DVR_GET_VERIFY_WEEK_PLAN = 2124; //get reader card verfy week plan
    public static final int NET_DVR_SET_VERIFY_WEEK_PLAN = 2125; //set reader card verfy week plan
    public static final int NET_DVR_GET_CARD_RIGHT_WEEK_PLAN = 2126;  //get card right week plan
    public static final int NET_DVR_SET_CARD_RIGHT_WEEK_PLAN = 2127; //set card right week plan
    public static final int NET_DVR_GET_VERIFY_HOLIDAY_PLAN = 2128; //get card reader verify holiday plan
    public static final int NET_DVR_SET_VERIFY_HOLIDAY_PLAN = 2129; //set card reader verify holiday plan
    public static final int NET_DVR_GET_CARD_RIGHT_HOLIDAY_PLAN = 2130; //get card right holiday plan
    public static final int NET_DVR_SET_CARD_RIGHT_HOLIDAY_PLAN = 2131; //set card right holiday plan
    public static final int NET_DVR_GET_VERIFY_HOLIDAY_GROUP = 2132; //get card reader verify holiday group
    public static final int NET_DVR_SET_VERIFY_HOLIDAY_GROUP = 2133; //set card reader verify holiday group
    public static final int NET_DVR_GET_CARD_RIGHT_HOLIDAY_GROUP = 2134; //get card right holiday group
    public static final int NET_DVR_SET_CARD_RIGHT_HOLIDAY_GROUP = 2135; //set card right holiday group
    public static final int NET_DVR_GET_VERIFY_PLAN_TEMPLATE = 2136; //get card reader verify plan template
    public static final int NET_DVR_SET_VERIFY_PLAN_TEMPLATE = 2137; //set card reader verify plan template
    public static final int NET_DVR_GET_CARD_RIGHT_PLAN_TEMPLATE = 2138; //get card right plan template
    public static final int NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE = 2139; //set card right plan template
    // V50
    public static final int NET_DVR_GET_CARD_RIGHT_WEEK_PLAN_V50 = 2304;  //Access card right V50 weeks plan parameters
    public static final int NET_DVR_SET_CARD_RIGHT_WEEK_PLAN_V50 = 2305;  //Set card right V50 weeks plan parameters
    public static final int NET_DVR_GET_CARD_RIGHT_HOLIDAY_PLAN_V50 = 2310;  //Access card right parameters V50 holiday plan
    public static final int NET_DVR_SET_CARD_RIGHT_HOLIDAY_PLAN_V50 = 2311;  //Set card right parameters V50 holiday plan
    public static final int NET_DVR_GET_CARD_RIGHT_HOLIDAY_GROUP_V50 = 2316; //Access card right parameters V50 holiday group
    public static final int NET_DVR_SET_CARD_RIGHT_HOLIDAY_GROUP_V50 = 2317; //Set card right parameters V50 holiday group
    public static final int NET_DVR_GET_CARD_RIGHT_PLAN_TEMPLATE_V50 = 2322; //Access card right parameters V50 plan template
    public static final int NET_DVR_SET_CARD_RIGHT_PLAN_TEMPLATE_V50 = 2323; //Set card right parameters V50 plan template

    //region card reader verification mode and door status planning parameters configuration

    public static final int NET_DVR_GET_DOOR_STATUS_PLAN = 2110; //get door status plan parameter
    public static final int NET_DVR_SET_DOOR_STATUS_PLAN = 2111; //set door status plan parameter
    public static final int NET_DVR_GET_CARD_READER_PLAN = 2142; //get card reader verify plan parameter
    public static final int NET_DVR_SET_CARD_READER_PLAN = 2143; //get card reader verify plan parameter

    //region card number associated with the user information parameter configuration

    public static final int NET_DVR_GET_CARD_USERINFO_CFG = 2163; //get card userinfo cfg
    public static final int NET_DVR_SET_CARD_USERINFO_CFG = 2164; //set card userinfo cfg

    //region event card linkage

    public static final int NET_DVR_GET_EVENT_CARD_LINKAGE_CFG_V50 = 2181; //get event card linkage cfg
    public static final int NET_DVR_SET_EVENT_CARD_LINKAGE_CFG_V50 = 2182; //set event card linkage cfg

    //region net configuration

    public static final int NET_DVR_GET_NETCFG_V30 = 1000;//Get network parameter configuration
    public static final int NET_DVR_SET_NETCFG_V30 = 1001;//Set network parameter configuration
    public static final int NET_DVR_GET_NETCFG_V50 = 1015;    //Get network parameter configuration (V50)
    public static final int NET_DVR_SET_NETCFG_V50 = 1016;    //Set network parameter configuration (V50)

    //HCNetSDK.dll macro definition

    //region HCNetSDK.dll structure definition

    //structure definition

    //region common use

    public static class NET_DVR_DATE extends Structure //convert type short to short in Structure
    {
        public short wYear;        //year
        public byte byMonth;        //month
        public byte byDay;        //day

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"wYear", "byMonth", "byDay"});
        }
    }

    public static class NET_DVR_SIMPLE_DAYTIME extends Structure {
        public byte byHour; //hour
        public byte byMinute; //minute
        public byte bySecond; //second
        public byte byRes;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byHour", "byMinute", "bySecond", "byRes"});
        }
    }

    // Time correction structure

    public static class NET_DVR_TIME extends Structure {
        public int dwYear;
        public int dwMonth;
        public int dwDay;
        public int dwHour;
        public int dwMinute;
        public int dwSecond;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwYear", "dwMonth", "dwDay", "dwHour", "dwMinute", "dwSecond"});
        }
    }

    public static class NET_DVR_TIME_EX extends Structure //convert type short to short in Structure
    {
        public short wYear;
        public byte byMonth;
        public byte byDay;
        public byte byHour;
        public byte byMinute;
        public byte bySecond;
        public byte byRes;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"wYear", "byMonth", "byDay", "byHour", "byMinute", "bySecond", "byRes"});
        }
    }

    // Long config callback type
    public enum NET_SDK_CALLBACK_TYPE {
        NET_SDK_CALLBACK_TYPE_STATUS(0),        // Status
        NET_SDK_CALLBACK_TYPE_PROGRESS(1),            // Progress
        NET_SDK_CALLBACK_TYPE_DATA(2);                // Data
        private final int value;

        private NET_SDK_CALLBACK_TYPE(int value) {
            this.value = value;
        }
    }

    // Long config status value
    public enum NET_SDK_CALLBACK_STATUS_NORMAL {
        NET_SDK_CALLBACK_STATUS_SUCCESS(1000),        // Success
        NET_SDK_CALLBACK_STATUS_PROCESSING(1001),            // Processing
        NET_SDK_CALLBACK_STATUS_FAILED(1002),                // Failed
        NET_SDK_CALLBACK_STATUS_EXCEPTION(1003),            // Exception
        NET_SDK_CALLBACK_STATUS_LANGUAGE_MISMATCH(1004),    // Language mismatch
        NET_SDK_CALLBACK_STATUS_DEV_TYPE_MISMATCH(1005),    // Device type mismatch
        NET_DVR_CALLBACK_STATUS_SEND_WAIT(1006);          // send wait
        private final int value;

        private NET_SDK_CALLBACK_STATUS_NORMAL(int value) {
            this.value = value;
        }
    }

    public enum LONG_CFG_SEND_DATA_TYPE_ENUM {
        ENUM_DVR_VEHICLE_CHECK(1), //vehicle Black list check
        ENUM_MSC_SEND_DATA(2),  //screen control data type
        ENUM_ACS_SEND_DATA(3), //access card data type
        ENUM_TME_CARD_SEND_DATA(4), //Parking Card data type
        ENUM_TME_VEHICLE_SEND_DATA(5), //TME Vehicle Info data type
        ENUM_DVR_DEBUG_CMD(6), //Debug Cmd
        ENUM_DVR_SCREEN_CTRL_CMD(7), //Screen interactive
        ENUM_CVR_PASSBACK_SEND_DATA(8), //CVR get passback task executable data type
        ENUM_ACS_INTELLIGENT_IDENTITY_DATA(9);  //intelligent identity data type
        private final int value;

        private LONG_CFG_SEND_DATA_TYPE_ENUM(int value) {
            this.value = value;
        }
    }

    public enum NET_SDK_GET_NEXT_STATUS {
        NET_SDK_GET_NEXT_STATUS_SUCCESS(1000),    // Get data successfully, Call API NET_DVR_RemoteConfigGetNext after processing this data.
        NET_SDK_GET_NETX_STATUS_NEED_WAIT(1001),        // Need wait, keep calling NET_DVR_RemoteConfigGetNext
        NET_SDK_GET_NEXT_STATUS_FINISH(1002),            // Get data finish, call API NET_DVR_StopRemoteConfig
        NET_SDK_GET_NEXT_STATUS_FAILED(1003);            // Get data failed, call API NET_DVR_StopRemoteConfig
        private final int value;

        private NET_SDK_GET_NEXT_STATUS(int value) {
            this.value = value;
        }
    }

    public enum LONG_CFG_RECV_DATA_TYPE_ENUM {
        ENUM_DVR_ERROR_CODE(1), //Error code
        ENUM_MSC_RECV_DATA(2), //screen control data type
        ENUM_ACS_RECV_DATA(3); //ACS control data type
        private final int value;

        private LONG_CFG_RECV_DATA_TYPE_ENUM(int value) {
            this.value = value;
        }
    }

    public enum ACS_DEV_SUBEVENT_ENUM {
        EVENT_ACS_HOST_ANTI_DISMANTLE(1),
        EVENT_ACS_OFFLINE_ECENT_NEARLY_FULL(2),
        EVENT_ACS_NET_BROKEN(3),
        EVENT_ACS_NET_RESUME(4),
        EVENT_ACS_LOW_BATTERY(5),
        EVENT_ACS_BATTERY_RESUME(6),
        EVENT_ACS_AC_OFF(7),
        EVENT_ACS_AC_RESUME(8),
        EVENT_ACS_SD_CARD_FULL(9),
        EVENT_ACS_LINKAGE_CAPTURE_PIC(10),
        EVENT_ACS_IMAGE_QUALITY_LOW(11),
        EVENT_ACS_FINGER_PRINT_QUALITY_LOW(12),
        EVENT_ACS_BATTERY_ELECTRIC_LOW(13),
        EVENT_ACS_BATTERY_ELECTRIC_RESUME(14),
        EVENT_ACS_FIRE_IMPORT_SHORT_CIRCUIT(15),
        EVENT_ACS_FIRE_IMPORT_BROKEN_CIRCUIT(16),
        EVENT_ACS_FIRE_IMPORT_RESUME(17),
        EVENT_ACS_MASTER_RS485_LOOPNODE_BROKEN(18),
        EVENT_ACS_MASTER_RS485_LOOPNODE_RESUME(19),
        EVENT_ACS_LOCAL_CONTROL_OFFLINE(20),
        EVENT_ACS_LOCAL_CONTROL_RESUME(21),
        EVENT_ACS_LOCAL_DOWNSIDE_RS485_LOOPNODE_BROKEN(22),
        EVENT_ACS_LOCAL_DOWNSIDE_RS485_LOOPNODE_RESUME(23),
        EVENT_ACS_DISTRACT_CONTROLLER_ONLINE(24),
        EVENT_ACS_DISTRACT_CONTROLLER_OFFLINE(25),
        EVENT_ACS_FIRE_BUTTON_TRIGGER(26),
        EVENT_ACS_FIRE_BUTTON_RESUME(27),
        EVENT_ACS_MAINTENANCE_BUTTON_TRIGGER(28),
        EVENT_ACS_MAINTENANCE_BUTTON_RESUME(29),
        EVENT_ACS_EMERGENCY_BUTTON_TRIGGER(30),
        EVENT_ACS_EMERGENCY_BUTTON_RESUME(31),
        EVENT_ACS_MAC_DETECT(32);
        private final int value;

        private ACS_DEV_SUBEVENT_ENUM(int value) {
            this.value = value;
        }
    }

    public enum ACS_ALARM_SUBEVENT_ENUM {
        EVENT_ACS_ALARMIN_SHORT_CIRCUIT(0),
        EVENT_ACS_ALARMIN_BROKEN_CIRCUIT(1),
        EVENT_ACS_ALARMIN_EXCEPTION(2),
        EVENT_ACS_ALARMIN_RESUME(3),
        EVENT_ACS_CASE_SENSOR_ALARM(4),
        EVENT_ACS_CASE_SENSOR_RESUME(5);
        private final int value;

        private ACS_ALARM_SUBEVENT_ENUM(int value) {
            this.value = value;
        }
    }

    public enum ACS_DOOR_SUBEVENT_ENUM {
        EVENT_ACS_LEADER_CARD_OPEN_BEGIN(0),
        EVENT_ACS_LEADER_CARD_OPEN_END(1),
        EVENT_ACS_ALWAYS_OPEN_BEGIN(2),
        EVENT_ACS_ALWAYS_OPEN_END(3),
        EVENT_ACS_ALWAYS_CLOSE_BEGIN(4),
        EVENT_ACS_ALWAYS_CLOSE_END(5),
        EVENT_ACS_LOCK_OPEN(6),
        EVENT_ACS_LOCK_CLOSE(7),
        EVENT_ACS_DOOR_BUTTON_PRESS(8),
        EVENT_ACS_DOOR_BUTTON_RELEASE(9),
        EVENT_ACS_DOOR_OPEN_NORMAL(10),
        EVENT_ACS_DOOR_CLOSE_NORMAL(11),
        EVENT_ACS_DOOR_OPEN_ABNORMAL(12),
        EVENT_ACS_DOOR_OPEN_TIMEOUT(13),
        EVENT_ACS_REMOTE_OPEN_DOOR(14),
        EVENT_ACS_REMOTE_CLOSE_DOOR(15),
        EVENT_ACS_REMOTE_ALWAYS_OPEN(16),
        EVENT_ACS_REMOTE_ALWAYS_CLOSE(17),
        EVENT_ACS_NOT_BELONG_MULTI_GROUP(18),
        EVENT_ACS_INVALID_MULTI_VERIFY_PERIOD(19),
        EVENT_ACS_MULTI_VERIFY_SUPER_RIGHT_FAIL(20),
        EVENT_ACS_MULTI_VERIFY_REMOTE_RIGHT_FAIL(21),
        EVENT_ACS_MULTI_VERIFY_SUCCESS(22),
        EVENT_ACS_MULTI_VERIFY_NEED_REMOTE_OPEN(23),
        EVENT_ACS_MULTI_VERIFY_SUPERPASSWD_VERIFY_SUCCESS(24),
        EVENT_ACS_MULTI_VERIFY_REPEAT_VERIFY_FAIL(25),
        EVENT_ACS_MULTI_VERIFY_TIMEOUT(26),
        EVENT_ACS_REMOTE_CAPTURE_PIC(27),
        EVENT_ACS_DOORBELL_RINGING(28),
        EVENT_ACS_SECURITY_MODULE_DESMANTLE_ALARM(29),
        EVENT_ACS_CALL_CENTER(30),
        EVENT_ACS_FIRSTCARD_AUTHORIZE_BEGIN(31),
        EVENT_ACS_FIRSTCARD_AUTHORIZE_END(32),
        EVENT_ACS_DOORLOCK_INPUT_SHORT_CIRCUIT(33),
        EVENT_ACS_DOORLOCK_INPUT_BROKEN_CIRCUIT(34),
        EVENT_ACS_DOORLOCK_INPUT_EXCEPTION(35),
        EVENT_ACS_DOORCONTACT_INPUT_SHORT_CIRCUIT(36),
        EVENT_ACS_DOORCONTACT_INPUT_BROKEN_CIRCUIT(37),
        EVENT_ACS_DOORCONTACT_INPUT_EXCEPTION(38),
        EVENT_ACS_OPENBUTTON_INPUT_SHORT_CIRCUIT(39),
        EVENT_ACS_OPENBUTTON_INPUT_BROKEN_CIRCUIT(40),
        EVENT_ACS_OPENBUTTON_INPUT_EXCEPTION(41),
        EVENT_ACS_DOORLOCK_OPEN_EXCEPTION(42),
        EVENT_ACS_DOORLOCK_OPEN_TIMEOUT(43),
        EVENT_ACS_FIRSTCARD_OPEN_WITHOUT_AUTHORIZE(44),
        EVENT_ACS_CALL_LADDER_RELAY_BREAK(45),
        EVENT_ACS_CALL_LADDER_RELAY_CLOSE(46),
        EVENT_ACS_AUTO_KEY_RELAY_BREAK(47),
        EVENT_ACS_AUTO_KEY_RELAY_CLOSE(48),
        EVENT_ACS_KEY_CONTROL_RELAY_BREAK(49),
        EVENT_ACS_KEY_CONTROL_RELAY_CLOSE(50),
        EVENT_ACS_REMOTE_VISITOR_CALL_LADDER(51),
        EVENT_ACS_REMOTE_HOUSEHOLD_CALL_LADDER(52),
        EVENT_ACS_LEGAL_MESSAGE(53),
        EVENT_ACS_ILLEGAL_MESSAGE(54);
        private final int value;

        private ACS_DOOR_SUBEVENT_ENUM(int value) {
            this.value = value;
        }
    }

    public enum ACS_CARD_READER_SUBEVENT_ENUM {
        EVENT_ACS_STRESS_ALARM(0),
        EVENT_ACS_CARD_READER_DESMANTLE_ALARM(1),
        EVENT_ACS_LEGAL_CARD_PASS(2),
        EVENT_ACS_CARD_AND_PSW_PASS(3),
        EVENT_ACS_CARD_AND_PSW_FAIL(4),
        EVENT_ACS_CARD_AND_PSW_TIMEOUT(5),
        EVENT_ACS_CARD_MAX_AUTHENTICATE_FAIL(6),
        EVENT_ACS_CARD_NO_RIGHT(7),
        EVENT_ACS_CARD_INVALID_PERIOD(8),
        EVENT_ACS_CARD_OUT_OF_DATE(9),
        EVENT_ACS_INVALID_CARD(10),
        EVENT_ACS_ANTI_SNEAK_FAIL(11),
        EVENT_ACS_INTERLOCK_DOOR_NOT_CLOSE(12),
        EVENT_ACS_FINGERPRINT_COMPARE_PASS(13),
        EVENT_ACS_FINGERPRINT_COMPARE_FAIL(14),
        EVENT_ACS_CARD_FINGERPRINT_VERIFY_PASS(15),
        EVENT_ACS_CARD_FINGERPRINT_VERIFY_FAIL(16),
        EVENT_ACS_CARD_FINGERPRINT_VERIFY_TIMEOUT(17),
        EVENT_ACS_CARD_FINGERPRINT_PASSWD_VERIFY_PASS(18),
        EVENT_ACS_CARD_FINGERPRINT_PASSWD_VERIFY_FAIL(19),
        EVENT_ACS_CARD_FINGERPRINT_PASSWD_VERIFY_TIMEOUT(20),
        EVENT_ACS_FINGERPRINT_PASSWD_VERIFY_PASS(21),
        EVENT_ACS_FINGERPRINT_PASSWD_VERIFY_FAIL(22),
        EVENT_ACS_FINGERPRINT_PASSWD_VERIFY_TIMEOUT(23),
        EVENT_ACS_FINGERPRINT_INEXISTENCE(24),
        EVENT_ACS_FACE_VERIFY_PASS(25),
        EVENT_ACS_FACE_VERIFY_FAIL(26),
        EVENT_ACS_FACE_AND_FP_VERIFY_PASS(27),
        EVENT_ACS_FACE_AND_FP_VERIFY_FAIL(28),
        EVENT_ACS_FACE_AND_FP_VERIFY_TIMEOUT(29),
        EVENT_ACS_FACE_AND_PW_VERIFY_PASS(30),
        EVENT_ACS_FACE_AND_PW_VERIFY_FAIL(31),
        EVENT_ACS_FACE_AND_PW_VERIFY_TIMEOUT(32),
        EVENT_ACS_FACE_AND_CARD_VERIFY_PASS(33),
        EVENT_ACS_FACE_AND_CARD_VERIFY_FAIL(34),
        EVENT_ACS_FACE_AND_CARD_VERIFY_TIMEOUT(35),
        EVENT_ACS_FACE_AND_PW_AND_FP_VERIFY_PASS(36),
        EVENT_ACS_FACE_AND_PW_AND_FP_VERIFY_FAIL(37),
        EVENT_ACS_FACE_AND_PW_AND_FP_VERIFY_TIMEOUT(38),
        EVENT_ACS_FACE_AND_CARD_AND_FP_VERIFY_PASS(39),
        EVENT_ACS_FACE_AND_CARD_AND_FP_VERIFY_FAIL(40),
        EVENT_ACS_FACE_AND_CARD_AND_FP_VERIFY_TIMEOUT(41),
        EVENT_ACS_EMPLOYEENO_AND_FP_VERIFY_PASS(42),
        EVENT_ACS_EMPLOYEENO_AND_FP_VERIFY_FAIL(43),
        EVENT_ACS_EMPLOYEENO_AND_FP_VERIFY_TIMEOUT(44),
        EVENT_ACS_EMPLOYEENO_AND_FP_AND_PW_VERIFY_PASS(45),
        EVENT_ACS_EMPLOYEENO_AND_FP_AND_PW_VERIFY_FAIL(46),
        EVENT_ACS_EMPLOYEENO_AND_FP_AND_PW_VERIFY_TIMEOUT(47),
        EVENT_ACS_EMPLOYEENO_AND_FACE_VERIFY_PASS(48),
        EVENT_ACS_EMPLOYEENO_AND_FACE_VERIFY_FAIL(49),
        EVENT_ACS_EMPLOYEENO_AND_FACE_VERIFY_TIMEOUT(50),
        EVENT_ACS_FACE_RECOGNIZE_FAIL(51),
        EVENT_ACS_EMPLOYEENO_AND_PW_PASS(52),
        EVENT_ACS_EMPLOYEENO_AND_PW_FAIL(53),
        EVENT_ACS_EMPLOYEENO_AND_PW_TIMEOUT(54),
        EVENT_ACS_HUMAN_DETECT_FAIL(55);
        private final int value;

        private ACS_CARD_READER_SUBEVENT_ENUM(int value) {
            this.value = value;
        }
    }


    public static class NET_DVR_XML_CONFIG_INPUT extends Structure//convert type int to int in Structure
    {
        public int dwSize;                    //size of NET_DVR_XML_CONFIG_INPUT
        public int lpRequestUrl;                //command string
        public int dwRequestUrlLen;            //command string length
        public int lpInBuffer;                //input buffer ，XML format
        public int dwInBufferSize;            //input buffer length
        public int dwRecvTimeOut;                //receive timeout，unit：ms，0 represent 5s
        public byte[] byRes = new byte[32];  //reserve

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "lpRequestUrl", "dwRequestUrlLen", "lpInBuffer", "dwInBufferSize", "dwRecvTimeOut", "byRes"});
        }
    }

    public static class NET_DVR_XML_CONFIG_OUTPUT extends Structure//convert type int to int in Structure
    {
        public int dwSize;                    //size of NET_DVR_XML_CONFIG_OUTPUT
        public int lpOutBuffer;                //output buffer，XMLformat
        public int dwOutBufferSize;            //input buffer length
        public int dwReturnedXMLSize;            //the real receive Xml size
        public int lpStatusBuffer;            //return status(XML format),no assignment with success, If you don't care about it ,just set it NULL
        public int dwStatusSize;                //status length(unit byte)
        public byte[] byRes = new byte[32];  //reserve

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "lpOutBuffer", "dwOutBufferSize", "dwReturnedXMLSize", "lpStatusBuffer", "dwStatusSize", "byRes"});
        }
    }

    //region acs event upload

    public static class NET_DVR_LOG_V30 extends Structure//convert type int to int, string to String in Structure
    {
        public NET_DVR_TIME strLogTime;
        public int dwMajorType;//Main type 1- alarm;  2- abnormal;  3- operation;  0xff- all
        public int dwMinorType; //Hypo- Type 0- all;
        public byte[] sPanelUser = new byte[MAX_NAMELEN];//user ID for local panel operation
        public byte[] sNetUser = new byte[MAX_NAMELEN];//user ID for network operation
        public NET_DVR_IPADDR struRemoteHostAddr;//remote host IP
        public int dwParaType;//parameter type,  for 9000 series MINOR_START_VT/MINOR_STOP_VT,  channel of the voice talking
        public int dwChannel;//channel number
        public int dwDiskNumber;//HD number
        public int dwAlarmInPort;//alarm input port
        public int dwAlarmOutPort;//alarm output port
        public int dwInfoLen;
        public byte[] sInfo = new byte[LOG_INFO_LEN];    //Change string to byte[]

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"strLogTime", "dwMajorType", "dwMinorType", "sPanelUser", "sNetUser",
                    "struRemoteHostAddr", "dwParaType", "dwChannel", "dwDiskNumber", "dwAlarmInPort", "dwAlarmOutPort", "dwInfoLen", "sInfo"});
        }
    }

    //  ACS event informations
    public static class NET_DVR_ACS_EVENT_INFO extends Structure//convert type int to int, short to short in Structure
    {
        public int dwSize;
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN]; // card No, 0 means invalid
        public byte byCardType; // card type,1-ordinary card,2-disable card,3-black list card, 4-patrol card,5-stress card,6-super card,7-client card, 0 means invalid
        public byte byWhiteListNo;  // white list No, 1-8, 0 means invalid
        public byte byReportChannel; // report channel, 1-alarmin updata, 2-center group 1, 3-center group 2, 0 means invalid
        public byte byCardReaderKind; // card reader type: 0-invalid, 1-IC card reader, 2-Id card reader, 3-Qr code reader, 4-Fingerprint head
        public int dwCardReaderNo;  // card reader No, 0 means invalid
        public int dwDoorNo;   // door No(floor No), 0 means invalid
        public int dwVerifyNo;  // mutilcard verify No. 0 means invalid
        public int dwAlarmInNo;  // alarm in No, 0 means invalid
        public int dwAlarmOutNo;  // alarm out No 0 means invalid
        public int dwCaseSensorNo;   // case sensor No 0 means invalid
        public int dwRs485No;  // RS485 channel,0 means invalid
        public int dwMultiCardGroupNo;  // multicard group No.
        public short wAccessChannel;      // Staff channel number
        public byte byDeviceNo;  // device No,0 means invalid
        public byte byDistractControlNo;  // distract control,0 means invalid
        public int dwEmployeeNo;   // employee No,0 means invalid
        public short wLocalControllerID; // On the controller number, 0 - access the host, 1-64 on behalf of the local controller
        public byte byInternetAccess;  // Internet access ID (1-uplink network port 1, 2-uplink network port 2,3- downstream network interface 1
        public byte byType; // protection zone type, 0-real time, 1-24 hours, 2-delay, 3-internal, 4-the key, 5-fire, 6-perimeter, 7-24 hours of silent
        // 8-24 hours auxiliary, 9-24 hours vibration, 10-door emergency open, 11-door emergency shutdown, 0xff-null
        public byte[] byMACAddr = new byte[MACADDR_LEN]; // mac addr 0 means invalid
        public byte bySwipeCardType;// swipe card type, 0-invalid,1-Qr code

        public byte[] byRes = new byte[13];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byCardNo", "byCardType", "byWhiteListNo", "byReportChannel", "byCardReaderKind", "dwCardReaderNo", "dwDoorNo", "dwVerifyNo",
                    "dwAlarmInNo", "dwAlarmOutNo", "dwCaseSensorNo", "dwRs485No", "dwMultiCardGroupNo", "wAccessChannel", "byDeviceNo",
                    "byDistractControlNo", "dwEmployeeNo", "wLocalControllerID", "byInternetAccess", "byType", "byMACAddr", "bySwipeCardType", "byRes"});
        }
    }

    // Entrance guard alarm information structure
    public static class NET_DVR_ACS_ALARM_INFO extends Structure//convert type int to int in Structure
    {
        public int dwSize;
        public int dwMajor;  // alarm major, reference to macro
        public int dwMinor;  // alarm minor, reference to macro
        public NET_DVR_TIME struTime;
        public byte[] sNetUser = new byte[MAX_NAMELEN];  // net operator user
        public NET_DVR_IPADDR struRemoteHostAddr; // remote host address
        public NET_DVR_ACS_EVENT_INFO struAcsEventInfo;
        public int dwPicDataLen; // picture length, when 0 ,means has no picture
        public Pointer pPicData;  // picture data
        public byte[] byRes = new byte[24];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwMajor", "dwMinor", "struTime", "sNetUser", "struRemoteHostAddr", "struAcsEventInfo", "dwPicDataLen", "pPicData", "byRes"});
        }
    }

    //Alarm Device Infor
    public static class NET_DVR_ALARMER extends Structure//convert type int to int, short to short, string to String in Structure
    {
        public byte byUserIDValid; /* Whether userID is valid,  0- invalid 1- valid. */
        public byte bySerialValid; /* Whether serial number is valid,  0- invalid 1- valid.  */
        public byte byVersionValid; /* Whether version number is valid,  0- invalid 1- valid. */
        public byte byDeviceNameValid; /* Whether device name is valid,  0- invalid 1- valid. */
        public byte byMacAddrValid; /* Whether MAC address is valid,  0- invalid 1- valid. */
        public byte byLinkPortValid; /* Whether login port number is valid,  0- invalid 1- valid. */
        public byte byDeviceIPValid; /* Whether device IP is valid,  0- invalid 1- valid.*/
        public byte bySocketIPValid; /* Whether socket IP is valid,  0- invalid 1- valid. */
        public int lUserID; /* NET_DVR_Login () effective when establishing alarm upload channel*/
        public byte[] sSerialNumber = new byte[SERIALNO_LEN]; /* Serial number. */
        public int dwDeviceVersion; /* Version number,  2 high byte means the major version,  2 low byte means the minor version*/
        public byte[] sDeviceName = new byte[NAME_LEN]; /* Device name. Change string to byte []*/
        public byte[] byMacAddr = new byte[MACADDR_LEN]; /* MAC address */
        public short wLinkPort; /* link port */
        public byte[] sDeviceIP = new byte[128]; /* IP address */
        public byte[] sSocketIP = new byte[128]; /* alarm push- mode socket IP address. */
        public byte byIpProtocol;  /* IP protocol:  0- IPV4;  1- IPV6. */
        public byte[] byRes2 = new byte[11];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byUserIDValid", "bySerialValid", "byVersionValid", "byDeviceNameValid", "byMacAddrValid", "byLinkPortValid", "byDeviceIPValid",
                    "bySocketIPValid", "lUserID", "sSerialNumber", "dwDeviceVersion", "sDeviceName", "byMacAddr", "wLinkPort", "sDeviceIP",
                    "sSocketIP", "byIpProtocol", "byRes2"});
        }
    }

    //Alarm protection structure parameters

    public static class NET_DVR_SETUPALARM_PARAM extends Structure//convert type int to int, short to short in Structure
    {
        public int dwSize;
        public byte byLevel;  //Arming priority: 0-high, 1-middle, 2-low
        public byte byAlarmInfoType;//Upload alarm information types（Intelligent traffic camera support）：0- old（NET_DVR_PLATE_RESULT），1- new(NET_ITS_PLATE_RESULT)
        public byte byRetAlarmTypeV40; //0- Ret NET_DVR_ALARMINFO_V30 or Older, 1- if Device Support NET_DVR_ALARMINFO_V40,  Ret NET_DVR_ALARMINFO_V40, else Ret NET_DVR_ALARMINFO_V30 Or NET_DVR_ALARMINFO
        public byte byRetDevInfoVersion; //CVR alarm 0-COMM_ALARM_DEVICE, 1-COMM_ALARM_DEVICE_V40
        public byte byRetVQDAlarmType; //Exptected VQD alarm type,0-upload NET_DVR_VQD_DIAGNOSE_INFO,1-upload NET_DVR_VQD_ALARM
        //1-(INTER_FACE_DETECTION),0-(INTER_FACESNAP_RESULT)
        public byte byFaceAlarmDetection;
        //Bit0 - indicates whether the secondary protection to upload pictures: 0 - upload, 1 - do not upload
        //Bit1 - said open data upload confirmation mechanism; 0 - don't open, 1 - to open
        public byte bySupport;
        //broken Net Http
        //bit0-Vehicle Detection(IPC) (0 - not continuingly, 1 - continuingly)
        //bit1-PDC(IPC)  (0 - not continuingly, 1 - continuingly)
        //bit2-HeatMap(IPC)  (0 - not continuingly, 1 - continuingly)
        public byte byBrokenNetHttp;
        public short wTaskNo;//Tasking number and the (field dwTaskNo corresponding data upload NET_DVR_VEHICLE_RECOG_RESULT the same time issued a task structure NET_DVR_VEHICLE_RECOG_COND corresponding fields in dwTaskNo
        public byte byDeployType;//deploy type:0-client deploy,1-real time deploy
        public byte[] byRes1 = new byte[3];
        public byte byAlarmTypeURL;//bit0-(NET_DVR_FACESNAP_RESULT),0-binary,1-URL
        public byte byCustomCtrl;//Bit0- Support the copilot face picture upload: 0-Upload,1-Do not upload

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byLevel", "byAlarmInfoType", "byRetAlarmTypeV40", "byRetDevInfoVersion", "byRetVQDAlarmType", "byFaceAlarmDetection", "bySupport", "byBrokenNetHttp", "wTaskNo", "byDeployType", "byRes1", "byAlarmTypeURL", "byCustomCtrl"});
        }
    }

    //region card parameters configuration
    public static class NET_DVR_VALID_PERIOD_CFG extends Structure {
        public byte byEnable; //whether to enable , 0-disable 1-enable
        public byte[] byRes1 = new byte[3];
        public NET_DVR_TIME_EX struBeginTime; //valid begin time
        public NET_DVR_TIME_EX struEndTime; //valid end time
        public byte[] byRes2 = new byte[32];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "byRes1", "struBeginTime", "struEndTime", "byRes2"});
        }
    }

    public static class CARDRIGHTPLAN_WORD extends Structure {
        public short[] wRightPlan = new short[MAX_CARD_RIGHT_PLAN_NUM];
    }

    public static class NET_DVR_CARD_CFG_V50 extends Structure {
        public int dwSize;
        public int dwModifyParamType;//需要修改的卡参数，设置卡参数时有效，按位表示，每位代表一种参数，1为需要修改，0为不修改
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN]; //卡号
        public byte byCardValid; //卡是否有效，0-无效，1-有效（用于删除卡，设置时置为0进行删除，获取时此字段始终为1）
        public byte byCardType; //卡类型，1-普通卡，2-残疾人卡，3-黑名单卡，4-巡更卡，5-胁迫卡，6-超级卡，7-来宾卡，8-解除卡，9-员工卡，10-应急卡，11-应急管理卡，默认普通卡
        public byte byLeaderCard; //是否为首卡，1-是，0-否
        public byte byRes1;
        public byte[] byDoorRight = new byte[MAX_DOOR_NUM_256]; //门权限(楼层权限)，按位表示，1为有权限，0为无权限，从低位到高位表示对门1-N是否有权限
        public NET_DVR_VALID_PERIOD_CFG struValid; //有效期参数
        public byte[] byBelongGroup = new byte[MAX_GROUP_NUM_128]; //所属群组，按字节表示，1-属于，0-不属于
        public byte[] byCardPassword = new byte[CARD_PASSWORD_LEN]; //卡密码
        public CARDRIGHTPLAN_WORD[] wCardRightPlan = new CARDRIGHTPLAN_WORD[MAX_DOOR_NUM_256]; //卡权限计划，取值为计划模板编号，同个门不同计划模板采用权限或的方式处理
        public int dwMaxSwipeTime; //最大刷卡次数，0为无次数限制（开锁次数）
        public int dwSwipeTime; //已刷卡次数
        public short wRoomNumber;  //房间号
        public short wFloorNumber;   //层号
        public int dwEmployeeNo;   //工号
        public byte[] byName = new byte[NAME_LEN];   //姓名
        public short wDepartmentNo;   //部门编号
        public short wSchedulePlanNo;   //排班计划编号
        public byte bySchedulePlanType;  //排班计划类型：0-无意义、1-个人、2-部门
        public byte byRightType;  //下发权限类型：0-普通发卡权限、1-二维码权限、2-蓝牙权限（可视对讲设备二维码权限配置项：房间号、卡号（虚拟卡号）、最大刷卡次数（开锁次数）、有效期参数；蓝牙权限：卡号（萤石APP账号）、其他参数配置与普通发卡权限一致）
        public byte[] byRes2 = new byte[2];
        public int dwLockID;  //锁ID
        public byte[] byLockCode = new byte[MAX_LOCK_CODE_LEN];    //锁代码
        public byte[] byRoomCode = new byte[MAX_DOOR_CODE_LEN];  //房间代码
        public int dwCardRight;      //卡权限
        public int dwPlanTemplate;   //计划模板(每天)各时间段是否启用，按位表示，0--不启用，1-启用
        public int dwCardUserId;    //持卡人ID
        public byte byCardModelType;  //0-空，1- MIFARE S50，2- MIFARE S70，3- FM1208 CPU卡，4- FM1216 CPU卡，5-国密CPU卡，6-身份证，7- NFC
        public byte[] byRes3 = new byte[83];
    }

    //卡参数配置条件
    public static class NET_DVR_CARD_CFG_COND extends Structure {
        public int dwSize;
        public int dwCardNum;
        public byte byCheckCardNo;
        public byte[] ibyRes = new byte[31];
    }

    public static class NET_DVR_CARD_CFG_SEND_DATA extends Structure {
        public int dwSize;
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN]; //card No
        public int dwCardUserId;
        public byte[] byRes = new byte[12];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byCardNo", "dwCardUserId", "byRes"});
        }
    }

    public static class NET_DVR_ACS_PARAM_TYPE extends Structure {
        public int dwSize;
        public int dwParamType;
        //parameter type,bitwise representation
        ////define ACS_PARAM_DOOR_STATUS_WEEK_PLAN        0x00000001 //door status week plan
        ////define ACS_PARAM_VERIFY_WEEK_PALN             0x00000002 //card reader week plan
        ////define ACS_PARAM_CARD_RIGHT_WEEK_PLAN         0x00000004 //card right week plan
        ////define ACS_PARAM_DOOR_STATUS_HOLIDAY_PLAN     0x00000008 //door status holiday plan
        ////define ACS_PARAM_VERIFY_HOLIDAY_PALN          0x00000010 //card reader holiday plan
        ////define ACS_PARAM_CARD_RIGHT_HOLIDAY_PLAN      0x00000020 //card right holiday plan
        ////define ACS_PARAM_DOOR_STATUS_HOLIDAY_GROUP    0x00000040 //door status holiday group plan
        ////define ACS_PARAM_VERIFY_HOLIDAY_GROUP         0x00000080 //card reader verify  holiday group plan
        ////define ACS_PARAM_CARD_RIGHT_HOLIDAY_GROUP     0x00000100 //card right holiday group plan
        ////define ACS_PARAM_DOOR_STATUS_PLAN_TEMPLATE    0x00000200 // door status plan template
        ////define ACS_PARAM_VERIFY_PALN_TEMPLATE         0x00000400 //card reader verify plan template
        ////define ACS_PARAM_CARD_RIGHT_PALN_TEMPLATE     0x00000800 //card right plan template
        ////define ACS_PARAM_CARD                         0x00001000 //card configure
        ////define ACS_PARAM_GROUP                        0x00002000 //group configure
        ////define ACS_PARAM_ANTI_SNEAK_CFG               0x00004000 //anti-sneak configure
        ////define ACS_PAPAM_EVENT_CARD_LINKAGE           0x00008000 //event linkage card
        ////define ACS_PAPAM_CARD_PASSWD_CFG              0x00010000 //open door by password
        public short wLocalControllerID; //On-site controller serial number[1,64],0 represent guard host
        public byte[] byRes = new byte[30];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwParamType", "wLocalControllerID", "byRes"});
        }
    }

    //region door parameters configuration

    public static class NET_DVR_DOOR_CFG extends Structure {
        public int dwSize;
        public byte[] byDoorName = new byte[DOOR_NAME_LEN];//door name
        public byte byMagneticType;//magnetic type, 0-always close 1-always open
        public byte byOpenButtonType;//open button type,  0-always close 1-always open
        public byte byOpenDuration;//open duration time, 1-255s(ladder control relay action time)
        public byte byDisabledOpenDuration;//disable open duration , 1-255s
        public byte byMagneticAlarmTimeout;//magnetic alarm time out , 0-255s,0 means not to alarm
        public byte byEnableDoorLock;//whether to enable door lock, 0-disable, 1-enable
        public byte byEnableLeaderCard;//whether to enable leader card , 0-disable, 1-enable
        public byte byLeaderCardMode;//First card mode, 0 - first card function is not enabled, and 1 - the first card normally open mode, 2 - the first card authorization mode (using this field, the byEnableLeaderCard is invalid )
        public int dwLeaderCardOpenDuration;//leader card open duration 1-1440mi
        public byte[] byStressPassword = new byte[STRESS_PASSWORD_LEN];//stress ppasswor
        public byte[] bySuperPassword = new byte[SUPER_PASSWORD_LEN]; //super passwor
        public byte[] byUnlockPassword = new byte[UNLOCK_PASSWORD_LEN];
        public byte byUseLocalController; //Read-only, whether the connection on the local controller, 0 - no, 1 - yes
        public byte byRes1;
        public short wLocalControllerID; //Read-only, on-site controller serial number, 1-64, 0 on behalf of unregistered
        public short wLocalControllerDoorNumber; //Read-only, on-site controller door number, 1-4, 0 represents the unregistered
        public short wLocalControllerStatus; //Read-only, on-site controller online status: 0 - offline, 1 - online, 2 - loop of RS485 serial port 1 on 1, 3 - loop of RS485 serial port 2 on 2, 4 - loop of RS485 serial port 1, 5 - loop of RS485 serial port 2, 6 - loop 3 of RS485 serial port 1, 7 - the loop on the RS485 serial port on the 3 4 2, 8 - loop on the RS485 serial port 1, 9 - loop 4 of RS485 serial port 2 (read-only)
        public byte byLockInputCheck; //Whether to enable the door input detection (1 public byte, 0 is not enabled, 1 is enabled, is not enabled by default)
        public byte byLockInputType; //Door lock input type
        public byte byDoorTerminalMode; //Gate terminal working mode
        public byte byOpenButton; //Whether to enable the open button
        public byte byLadderControlDelayTime; //ladder control delay time,1-255min
        public byte[] byRes2 = new byte[43];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byDoorName", "byMagneticType", "byOpenButtonType", "byOpenDuration", "byDisabledOpenDuration",
                    "byMagneticAlarmTimeout", "byEnableDoorLock", "byEnableLeaderCard", "byLeaderCardMode", "dwLeaderCardOpenDuration", "byStressPassword",
                    "bySuperPassword", "byUnlockPassword", "byUseLocalController", "byRes1", "wLocalControllerID", "wLocalControllerDoorNumber",
                    "wLocalControllerStatus", "byLockInputCheck", "byLockInputType", "byDoorTerminalMode", "byOpenButton", "byLadderControlDelayTime", "byRes2"});
        }
    }

    //region group parameters configuration

    public static class NET_DVR_GROUP_CFG extends Structure {
        public int dwSize;
        public byte byEnable;
        public byte[] byRes1 = new byte[3];
        public NET_DVR_VALID_PERIOD_CFG struValidPeriodCfg;
        public byte[] byGroupName = new byte[GROUP_NAME_LEN];
        public byte[] byRes2 = new byte[32];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byEnable", "byRes1", "struValidPeriodCfg", "byGroupName", "byRes2"});
        }
    }

    //region user parameters configuration

    public static class NET_DVR_ALARM_DEVICE_USER extends Structure {
        public int dwSize; //Structure size
        public byte[] sUserName = new byte[NAME_LEN];
        public byte[] sPassword = new byte[PASSWD_LEN];
        public NET_DVR_IPADDR struUserIP;//User IP (0 stands for no IP restriction)
        public byte[] byAMCAddr = new byte[MACADDR_LEN];
        public byte byUserType;    //0- general user, 1- administrator user
        public byte byAlarmOnRight;//Arming authority
        public byte byAlarmOffRight; //Disarming authority
        public byte byBypassRight; //Bypass authority
        public byte[] byOtherRight = new byte[MAX_RIGHT];//Other authority
        // 0 -- log
        // 1 -- reboot/shutdown
        // 2 -- set parameter
        // 3 -- get parameter
        // 4 -- resume
        // 5 -- siren
        // 6 -- PTZ
        // 7 -- remote upgrade
        // 8 -- preview
        // 9 -- manual record
        // 10 --remote playback
        public byte[] byNetPreviewRight = new byte[MAX_ALARMHOST_VIDEO_CHAN / 8];// preview channels,eg. bit0-channel 1,0-no permission 1-permission enable
        public byte[] byNetRecordRight = new byte[MAX_ALARMHOST_VIDEO_CHAN / 8]; // record channels,eg. bit0-channel 1,0-no permission 1-permission enable]
        public byte[] byNetPlaybackRight = new byte[MAX_ALARMHOST_VIDEO_CHAN / 8]; // playback channels,eg. bit0-channel 1,0-no permission 1-permission enable
        public byte[] byNetPTZRight = new byte[MAX_ALARMHOST_VIDEO_CHAN / 8]; // PTZ channels,eg. bit0-channel 1,0-no permission 1-permission enable
        public byte[] sOriginalPassword = new byte[PASSWD_LEN];        // Original password
        public byte[] byRes2 = new byte[152];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "sUserName", "sPassword", "struUserIP", "byAMCAddr", "byUserType", "byAlarmOnRight", "byAlarmOffRight", "byBypassRight",
                    "byOtherRight", "byNetPreviewRight", "byNetRecordRight", "byNetPlaybackRight", "byNetPTZRight", "sOriginalPassword", "byRes2"});
        }
    }

    public static class NET_DVR_FACE_PARAM_COND extends Structure {
        public int dwSize;
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN];    //人脸关联的卡号
        public byte[] byEnableCardReader = new byte[MAX_CARD_READER_NUM_512];  //人脸的读卡器是否有效，0-无效，1-有效
        public int dwFaceNum;    //设置或获取人脸数量，获取时置为0xffffffff表示获取所有人脸信息
        public byte byFaceID;     //人脸编号，有效值范围为1-2   0xff表示该卡所有人脸
        public byte[] byRes = new byte[126];   //保留
    }

    public static class NET_DVR_FACE_PARAM_CFG extends Structure {
        public int dwSize;
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN];    //人脸关联的卡号
        public int dwFaceLen;    //人脸数据长度<DES加密处理>，设备端返回的即加密后的数据
        public Pointer pFaceBuffer;  //人脸数据指针
        public byte[] byEnableCardReader = new byte[MAX_CARD_READER_NUM_512];  //需要下发人脸的读卡器，按数组表示，从低位到高位表示，0-不下发该读卡器，1-下发到该读卡器
        public byte byFaceID;     //人脸编号，有效值范围为1-2
        public byte byFaceDataType;   //人脸数据类型：0-模板（默认），1-图片
        public byte[] byRes = new byte[126];
    }

    public static interface FRemoteConfigCallback extends StdCallCallback {
        public void invoke(int dwType, Pointer lpBuffer, int dwBufLen, Pointer pUserData);
    }

    NativeLong NET_DVR_StartRemoteConfig(NativeLong lUserID, int dwCommand, Pointer lpInBuffer, int dwInBufferLen, FRemoteConfigCallback cbStateCallback, Pointer pUserData);

    //region cardreader configuration

    public static class NET_DVR_CARD_READER_CFG_V50 extends Structure {
        public int dwSize;
        public byte byEnable; //whether to enable, 1-enable, 0-disable
        public byte byCardReaderType; //card reader type,1-DS-K110XM/MK/C/CK,2-DS-K192AM/AMP,3-DS-K192BM/BMP,4-DS-K182AM/AMP,5-DS-K182BM/BMP,6-DS-K182AMF/ACF,7-wiegand or 485 not online,8- DS-K1101M/MK,9- DS-K1101C/CK,10- DS-K1102M/MK/M-A
        //11- DS-K1102C/CK,12- DS-K1103M/MK,13- DS-K1103C/CK,14- DS-K1104M/MK,15- DS-K1104C/CK,16- DS-K1102S/SK/S-A,17- DS-K1102G/GK,18- DS-K1100S-B,19- DS-K1102EM/EMK,20- DS-K1102E/EK,
        //21- DS-K1200EF,22- DS-K1200MF,23- DS-K1200CF,24- DS-K1300EF,25- DS-K1300MF,26- DS-K1300CF,27- DS-K1105E,28- DS-K1105M,29- DS-K1105C,30- DS-K182AMF,31- DS-K196AMF,32-DS-K194AMP
        //33-DS-K1T200EF/EF-C/MF/MF-C/CF/CF-C,34-DS-K1T300EF/EF-C/MF/MF-C/CF/CF-C,35-DS-K1T105E/E-C/M/M-C/C/C-C,36-DS-K1T803F/MF/SF/EF,37-DS-K1A801F/MF/SF/EF,38-DS-K1107M/MK,39-DS-K1107E/EK,
        //40-DS-K1107S/SK,41-DS-K1108M/MK,42-DS-K1108E/EK,43-DS-K1108S/SK,44-DS-K1200F,45-DS-K1S110-I,46-DS-K1T200M-PG/PGC,47-DS-K1T200M-PZ/PZC,48-DS-K1109H
        public byte byOkLedPolarity; //OK LED polarity,0-negative,1-positive
        public byte byErrorLedPolarity; //Error LED polarity,0-negative,1-positive
        public byte byBuzzerPolarity; //buzzer polarity,0-negative,1-positive
        public byte bySwipeInterval; //swipe interval, unit: second
        public byte byPressTimeout;  //press time out, unit:second
        public byte byEnableFailAlarm; //whether to enable fail alarm, 0-disable 1-enable
        public byte byMaxReadCardFailNum; //max reader card fail time
        public byte byEnableTamperCheck;  //whether to support tamper check, 0-disable ,1-enable
        public byte byOfflineCheckTime;  //offline check time, int second
        public byte byFingerPrintCheckLevel; //fingerprint check lever,1-1/10,2-1/100,3-1/1000,4-1/10000,5-1/100000,6-1/1000000,7-1/10000000,8-1/100000000,9-3/100,10-3/1000,11-3/10000,12-3/100000,13-3/1000000,14-3/10000000,15-3/100000000,16-Automatic Normal,17-Automatic Secure,18-Automatic More Secure
        public byte byUseLocalController; //read only,weather connect with local control:0-no,1-yes
        public byte byRes1;
        public short wLocalControllerID; //read only,local controller ID, byUseLocalController=1 effective,1-64,0 present not register
        public short wLocalControllerReaderID; //read only,local controller reader ID,byUseLocalController=1 effective,0 present not register
        public short wCardReaderChannel; //read only,card reader channel,byUseLocalController=1 effective,0-wiegand or offline,1-RS485A,2-RS485B
        public byte byFingerPrintImageQuality; //finger print image quality,0-no effective,1-weak qualification(V1),2-moderate qualification(V1),3-strong qualification(V1),4-strongest qualification(V1),5-weak qualification(V2),6-moderate qualification(V2),7-strong qualification(V2),8-strongest qualification(V2)
        public byte byFingerPrintContrastTimeOut; //finger print contrast time out,0-no effective,1-20 present:1s-20s,0xff-infinite
        public byte byFingerPrintRecognizeInterval; //finger print recognize interval,0-no effective,1-10 present:1s-10s,0xff-no delay
        public byte byFingerPrintMatchFastMode; //finger print match fast mode,0-no effective,1-5 present:fast mode 1-fast mode 5,0xff-auto
        public byte byFingerPrintModuleSensitive; //finger print module sensitive,0-no effective,1-8 present:sensitive level 1-sensitive level 8
        public byte byFingerPrintModuleLightCondition; //finger print module light condition,0-no effective,1-out door,2-in door
        public byte byFaceMatchThresholdN; //range 0-100
        public byte byFaceQuality; //face quality,range 0-100
        public byte byFaceRecognizeTimeOut; //face recognize time out,1-20 present:1s-20s,0xff-infinite
        public byte byFaceRecognizeInterval; //face recognize interval,0-no effective,1-10 present:1s-10s,0xff-no delay
        public short wCardReaderFunction; //read only,card reader function
        public byte[] byCardReaderDescription = new byte[CARD_READER_DESCRIPTION]; //read only,card reader description
        public short wFaceImageSensitometry; //face image sensitometry,range 0-65535
        public byte byLivingBodyDetect; //living body detect,0-no effective,1-disable,2-enable
        public byte byFaceMatchThreshold1; //range 0-100
        public short wBuzzerTime; //buzzer time,range 0-5999(s) 0 present yowl
        public byte[] byRes = new byte[254];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byEnable", "byCardReaderType", "byOkLedPolarity", "byErrorLedPolarity",
                    "byBuzzerPolarity", "bySwipeInterval", "byPressTimeout", "byEnableFailAlarm", "byMaxReadCardFailNum", "byEnableTamperCheck",
                    "byOfflineCheckTime", "byFingerPrintCheckLevel", "byUseLocalController", "byRes1", "wLocalControllerID", "wLocalControllerReaderID",
                    "wCardReaderChannel", "byFingerPrintImageQuality", "byFingerPrintContrastTimeOut", "byFingerPrintRecognizeInterval", "byFingerPrintMatchFastMode",
                    "byFingerPrintModuleSensitive", "byFingerPrintModuleLightCondition", "byFaceMatchThresholdN", "byFaceQuality", "byFaceRecognizeTimeOut",
                    "byFaceRecognizeInterval", "wCardReaderFunction", "byCardReaderDescription", "wFaceImageSensitometry", "byLivingBodyDetect", "byFaceMatchThreshold1", "wBuzzerTime", "byRes"});
        }
    }

    //region fingerprint configuration

    public static class NET_DVR_FINGER_PRINT_CFG extends Structure {
        public int dwSize;
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN]; //card NO
        public int dwFingerPrintLen;     //fingerprint len
        public byte[] byEnableCardReader = new byte[MAX_CARD_READER_NUM_512];  //the card reader which finger print send to,according to the values,0-not send,1-send
        public byte byFingerPrintID;     //finger print ID,[1,10]
        public byte byFingerType;       //finger type  0-normal,1-stress
        public byte[] byRes1 = new byte[30];
        public byte[] byFingerData = new byte[MAX_FINGER_PRINT_LEN];
        public byte[] byRes = new byte[64];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byCardNo", "dwFingerPrLen", "byEnableCardReader", "byFingerPrID",
                    "byFingerType", "byRes1", "byFingerData", "byRes"
            });
        }
    }

    public static class NET_DVR_FINGER_PRINT_STATUS extends Structure {
        public int dwSize;
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN];
        public byte[] byCardReaderRecvStatus = new byte[MAX_CARD_READER_NUM_512];  //Fingerprint reader state, press the public bytes, 0 - failure, 1 -, 2 - the fingerprint module is not online, 3 - try again or poor quality of fingerprint, 4 - memory is full, 5 - existing the fingerprints, 6 - existing the fingerprint ID, illegal fingerprint ID, 7-8 - don't need to configure the fingerprint module
        public byte byFingerPrintID;     //finger print ID,[1,10]
        public byte byFingerType;        //finger type  0-normal,1-stress
        public byte byTotalStatus;  //
        public byte byRes1;
        public byte[] byErrorMsg = new byte[ERROR_MSG_LEN]; //Issued false information, when the byCardReaderRecvStatus is 5, said existing fingerprint matching card number
        public int dwCardReaderNo;  //Grain number card reader, can be used to return issued by mistake
        public byte[] byRes = new byte[24];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byCardNo", "byCardReaderRecvStatus", "byFingerPrID", "byFingerType",
                    "byTotalStatus", "byRes1", "byErrorMsg", "dwCardReaderNo", "byRes"});
        }
    }

    public static class NET_DVR_FINGER_PRINT_INFO_COND extends Structure {
        public int dwSize;
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN];
        public byte[] byEnableCardReader = new byte[MAX_CARD_READER_NUM_512];  //which card reader to send,according to the values
        public int dwFingerPrintNum; //the number send or get. if get,0xffffffff means all
        public byte byFingerPrintID;     //finger print ID,[1,10],   0xff means all
        public byte byCallbackMode;     //
        public byte[] byRes1 = new byte[26];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byCardNo", "byEnableCardReader", "dwFingerPrintNum", "byFingerPrintID", "byCallbackMode", "byRes1"});
        }
    }

    public static class NET_DVR_FINGER_PRINT_BYCARD extends Structure {
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN];
        public byte[] byEnableCardReader = new byte[MAX_CARD_READER_NUM_512];  //be enable card reader,according to the values
        public byte[] byFingerPrintID = new byte[MAX_FINGER_PRINT_NUM];        //finger print ID,according to the values,0-not delete,1-delete
        public byte[] byRes1 = new byte[34];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byCardNo", "byEnableCardReader", "byFingerPrintID", "byRes1"});
        }
    }

    public static class NET_DVR_FINGER_PRINT_BYREADER extends Structure {
        public int dwCardReaderNo;
        public byte byClearAllCard;  //clear all card,0-delete by card,1-delete all card
        public byte[] byRes1 = new byte[3];
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN];
        public byte[] byRes = new byte[548];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwCardReaderNo", "byClearAllCard", "byRes1", "byCardNo", "byRes"});
        }
    }

    public static class NET_DVR_FINGER_PRINT_INFO_CTRL_BYCARD extends Structure {
        public int dwSize;
        public byte byMode;          //delete mode,0-delete by card,1-delete by reader
        public byte[] byRes1 = new byte[3];

        public NET_DVR_FINGER_PRINT_BYCARD struByCard;   //delete by card
        public byte[] byRes = new byte[64];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byMode", "byRes1", "struByCard", "byRes"});
        }

    }

    public static class NET_DVR_FINGER_PRINT_INFO_CTRL_BYREADER extends Structure {
        public int dwSize;
        public byte byMode;          //delete mode,0-delete by card,1-delete by reader
        public byte[] byRes1 = new byte[3];

        public NET_DVR_FINGER_PRINT_BYREADER struByReader;  //delete by reader
        public byte[] byRes = new byte[64];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byMode", "byRes1", "struByReader", "byRes"});
        }
    }

    //region plan configuration

    public static class NET_DVR_TIME_SEGMENT extends Structure {
        public NET_DVR_SIMPLE_DAYTIME struBeginTime;  //begin time
        public NET_DVR_SIMPLE_DAYTIME struEndTime;    //end time

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"struBeginTime", "struEndTime"});
        }
    }

    public static class NET_DVR_SINGLE_PLAN_SEGMENT extends Structure {
        public byte byEnable; //whether to enable, 1-enable, 0-disable
        public byte byDoorStatus; //door status(control ladder status),0-invaild, 1-always open(free), 2-always close(forbidden), 3-ordinary status(used by door plan)
        public byte byVerifyMode;  //verify method, 0-invaild, 1-swipe card, 2-swipe card +password(used by card verify ) 3-swipe card(used by card verify) 4-swipe card or password(used by card verify)
        //5-fingerprint, 6-fingerprint and passwd, 7-fingerprint or swipe card, 8-fingerprint and swipe card, 9-fingerprint and passwd and swipe card,
        //10-face or finger print or swipe card or password,11-face and finger print,12-face and password,13-face and swipe card,14-face,15-employee no and password,
        //16-finger print or password,17-employee no and finger print,18-employee no and finger print and password,
        //19-face and finger print and swipe card,20-face and password and finger print,21-employee no and face,22-face or face and swipe card
        public byte[] byRes = new byte[5];
        public NET_DVR_TIME_SEGMENT struTimeSegment;  //time segment parameter

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "byDoorStatus", "byVerifyMode", "byRes", "struTimeSegment"});
        }
    }

    //2-dimen array
    public static class arrayStruPlanCfg extends Structure {
        public NET_DVR_SINGLE_PLAN_SEGMENT[] struDaysPlanCfg = (NET_DVR_SINGLE_PLAN_SEGMENT[]) new NET_DVR_SINGLE_PLAN_SEGMENT().toArray(MAX_TIMESEGMENT_V30);

        @Override
        protected List<String> getFieldOrder() {
            // TODO Auto-generated method stub
            return Arrays.asList("struDaysPlanCfg");
        }
    }

    public static class NET_DVR_WEEK_PLAN_CFG extends Structure {
        public int dwSize;
        public byte byEnable;  //0-no,1-enabled
        public byte[] byRes1 = new byte[3];
        public arrayStruPlanCfg[] struPlanCfg = (arrayStruPlanCfg[]) new arrayStruPlanCfg().toArray(MAX_DAYS);
        public byte[] byRes2 = new byte[16];

        @Override
        protected List<String> getFieldOrder() {
            // TODO Auto-generated method stub
            return Arrays.asList("dwSize", "byEnable", "byRes1", "struPlanCfg",
                    "byRes2");
        }
    }

    public static class NET_DVR_HOLIDAY_PLAN_CFG extends Structure {
        public int dwSize;
        public byte byEnable;  //whether to enable, 1-enable, 0-disable
        public byte[] byRes1 = new byte[3];
        public NET_DVR_DATE struBeginDate;  //holiday begin date
        public NET_DVR_DATE struEndDate;  //holiday end date
        public NET_DVR_SINGLE_PLAN_SEGMENT[] struPlanCfg;  //time segment parameter
        public byte[] byRes2 = new byte[16];

        public void Init() {
            struPlanCfg = new NET_DVR_SINGLE_PLAN_SEGMENT[MAX_TIMESEGMENT_V30];
            /*foreach (NET_DVR_SINGLE_PLAN_SEGMENT singlStruPlanCfg : struPlanCfg)
            {
                singlStruPlanCfg.Init();
            }*/
        }

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byEnable", "byRes1", "struBeginDate", "struEndDate", "struPlanCfg", "byRes2"});
        }
    }

    public static class NET_DVR_HOLIDAY_GROUP_CFG extends Structure {
        public int dwSize;
        public byte byEnable; //whether to enable, 1-enable, 0-disable
        public byte[] byRes1 = new byte[3];
        public byte[] byGroupName = new byte[HOLIDAY_GROUP_NAME_LEN]; //holiday group name
        public int[] dwHolidayPlanNo = new int[MAX_HOLIDAY_PLAN_NUM]; //holiday plan No. fill in from the front side, invalid when meet zero.
        public byte[] byRes2 = new byte[32];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byEnable", "byRes1", "byGroupName", "dwHolidayPlanNo", "byRes2"});
        }
    }

    public static class NET_DVR_PLAN_TEMPLATE extends Structure {
        public int dwSize;
        public byte byEnable; //whether to enable, 1-enable, 0-disable
        public byte[] byRes1 = new byte[3];
        public byte[] byTemplateName = new byte[TEMPLATE_NAME_LEN]; //template name
        public int dwWeekPlanNo; //week plan no. 0 invalid
        public int[] dwHolidayGroupNo = new int[MAX_HOLIDAY_GROUP_NUM]; //holiday group. fill in from the front side, invalid when meet zero.
        public byte[] byRes2 = new byte[32];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byEnable", "byRes1", "byTemplateName", "dwWeekPlanNo", "dwHolidayGroupNo", "byRes2"});
        }
    }

    public static class NET_DVR_HOLIDAY_PLAN_COND extends Structure {
        public int dwSize;
        public int dwHolidayPlanNumber; //Holiday plan number
        public short wLocalControllerID; //On the controller serial number [1, 64]
        public byte[] byRes = new byte[106];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwHolidayPlanNumber", "wLocalControllerID", "byRes"});
        }
    }

    public static class NET_DVR_WEEK_PLAN_COND extends Structure {
        public int dwSize;
        public int dwWeekPlanNumber; //Week plan number
        public short wLocalControllerID; //On the controller serial number [1, 64]
        public byte[] byRes = new byte[106];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwWeekPlanNumber", "wLocalControllerID", "byRes"});
        }
    }

    public static class NET_DVR_HOLIDAY_GROUP_COND extends Structure {
        public int dwSize;
        public int dwHolidayGroupNumber; //Holiday group number
        public short wLocalControllerID; //On the controller serial number [1, 64]
        public byte[] byRes = new byte[106];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwHolidayGroupNumber", "wLocalControllerID", "byRes"});
        }
    }

    public static class NET_DVR_PLAN_TEMPLATE_COND extends Structure {
        public int dwSize;
        public int dwPlanTemplateNumber; //Plan template number, starting from 1, the maximum value from the entrance guard capability sets
        public short wLocalControllerID; //On the controller serial number[1,64], 0 is invalid
        public byte[] byRes = new byte[106];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwPlanTemplateNumber", "wLocalControllerID", "byRes"});
        }
    }

    //region card reader verification mode and door status planning parameters configuration

    public static class NET_DVR_DOOR_STATUS_PLAN extends Structure {
        public int dwSize;
        public int dwTemplateNo;  // plan template No. 0 means cancel relation,resolve default status(ordinary status)
        public byte[] byRes = new byte[64];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwTemplateNo", "byRes"});
        }
    }

    public static class NET_DVR_CARD_READER_PLAN extends Structure {
        public int dwSize;
        public int dwTemplateNo; // plan template No. 0 means cancel relation,resolve default status(swipe card)
        public byte[] byRes = new byte[64];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwTemplateNo", "byRes"});
        }
    }

    //region card number associated with the user information parameter configuration

    public static class NET_DVR_CARD_USER_INFO_CFG extends Structure {
        public int dwSize;
        public byte[] byUsername = new byte[NAME_LEN];// user name
        public byte[] byRes2 = new byte[256]; // byRes2[0]--user number for alarm host

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byUsername", "byRes2"});
        }
    }

    //region user login managed

    public static class NET_DVR_DEVICEINFO_V30 extends Structure {
        public byte[] sSerialNumber = new byte[SERIALNO_LEN];    //serial number
        public byte byAlarmInPortNum;   //Number of Alarm input
        public byte byAlarmOutPortNum;  //Number of Alarm Output
        public byte byDiskNum;  //Number of Hard Disk
        public byte byDVRType;  //DVR Type, 1: DVR 2: ATM DVR 3: DVS ......
        public byte byChanNum;  //Number of Analog Channel
        public byte byStartChan;    //The first Channel No. E.g. DVS- 1, DVR- 1
        public byte byAudioChanNum; //Number of Audio Channel
        public byte byIPChanNum;    //Maximum number of IP Channel  low
        public byte byZeroChanNum;  //Zero channel encoding number//2010- 01- 16
        public byte byMainProto;    //Main stream transmission protocol 0- private,  1- rtsp,2-both private and rtsp
        public byte bySubProto; //Sub stream transmission protocol 0- private,  1- rtsp,2-both private and rtsp
        public byte bySupport;  //Ability, the 'AND' result by bit: 0- not support;  1- support
        //bySupport & 0x1,  smart search
//bySupport & 0x2,  backup
//bySupport & 0x4,  get compression configuration ability
//bySupport & 0x8,  multi network adapter
//bySupport & 0x10, support remote SADP
//bySupport & 0x20  support Raid card
//bySupport & 0x40 support IPSAN directory search
        public byte bySupport1; //Ability expand, the 'AND' result by bit: 0- not support;  1- support
        //bySupport1 & 0x1, support snmp v30
//bySupport1& 0x2,support distinguish download and playback
//bySupport1 & 0x4, support deployment level
//bySupport1 & 0x8, support vca alarm time extension
//bySupport1 & 0x10, support muti disks(more than 33)
//bySupport1 & 0x20, support rtsp over http
//bySupport1 & 0x40, support delay preview
//bySuppory1 & 0x80 support NET_DVR_IPPARACFG_V40, in addition  support  License plate of the new alarm information
        public byte bySupport2; //Ability expand, the 'AND' result by bit: 0- not support;  1- support
        //bySupport & 0x1, decoder support get stream by URL
//bySupport2 & 0x2,  support FTPV40
//bySupport2 & 0x4,  support ANR
//bySupport2 & 0x20, support get single item of device status
//bySupport2 & 0x40,  support stream encryt
        public short wDevType; //device type
        public byte bySupport3; //Support  epresent by bit, 0 - not support 1 - support
        //bySupport3 & 0x1-muti stream support
//bySupport3 & 0x8  support use delay preview parameter when delay preview
//bySupport3 & 0x10 support the interface of getting alarmhost main status V40
        public byte byMultiStreamProto; //support multi stream, represent by bit, 0-not support ;1- support; bit1-stream 3 ;bit2-stream 4, bit7-main stream, bit8-sub stream
        public byte byStartDChan;   //Start digital channel
        public byte byStartDTalkChan;   //Start digital talk channel
        public byte byHighDChanNum; //Digital channel number high
        public byte bySupport4; //Support  epresent by bit, 0 - not support 1 - support
        //bySupport4 & 0x4 whether support video wall unified interface
// bySupport4 & 0x80 Support device upload center alarm enable
        public byte byLanguageType; //support language type by bit,0-support,1-not support
        //byLanguageType 0 -old device
//byLanguageType & 0x1 support chinese
//byLanguageType & 0x2 support english
        public byte byVoiceInChanNum;   //voice in chan num
        public byte byStartVoiceInChanNo;   //start voice in chan num
        public byte bySupport5;  //0-no support,1-support,bit0-muti stream
        //bySupport5 &0x01support wEventTypeEx
//bySupport5 &0x04support sence expend
        public byte bySupport6;
        public byte byMirrorChanNum;    //mirror channel num,<it represents direct channel in the recording host
        public short wStartMirrorChanNo;   //start mirror chan
        public byte bySupport7;        //Support  epresent by bit, 0 - not support 1 - support
        //bySupport7 & 0x1- supports INTER_VCA_RULECFG_V42 extension
        // bySupport7 & 0x2  Supports HVT IPC mode expansion
        // bySupport7 & 0x04  Back lock time
        // bySupport7 & 0x08  Set the pan PTZ position, whether to support the band channel
        // bySupport7 & 0x10  Support for dual system upgrade backup
        // bySupport7 & 0x20  Support OSD character overlay V50
        // bySupport7 & 0x40  Support master slave tracking (slave camera)
        // bySupport7 & 0x80  Support message encryption
        public byte byRes2;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"sSerialNumber", "byAlarmInPortNum", "byAlarmOutPortNum", "byDiskNum", "byDVRType", "byChanNum",
                    "byStartChan", "byAudioChanNum", "byIPChanNum", "byZeroChanNum", "byMainProto", "bySubProto", "bySupport", "bySupport1",
                    "bySupport2", "wDevType", "bySupport3", "byMultiStreamProto", "byStartDChan", "byStartDTalkChan", "byHighDChanNum",
                    "bySupport4", "byLanguageType", "byVoiceInChanNum", "byStartVoiceInChanNo", "bySupport5", "bySupport6", "byMirrorChanNum",
                    "wStartMirrorChanNo", "bySupport7", "byRes2"});
        }

        public static class ByReference extends NET_DVR_DEVICEINFO_V30 implements Structure.ByReference {
        }

        public static class ByValue extends NET_DVR_DEVICEINFO_V30 implements Structure.ByValue {
        }
    }

    public static class NET_DVR_DEVICEINFO_V40 extends Structure {
        public NET_DVR_DEVICEINFO_V30 struDeviceV30;
        public byte bySupportLock;        //the device support lock function,this byte assigned by SDK.when bySupportLock is 1,dwSurplusLockTime and byRetryLoginTime is valid
        public byte byRetryLoginTime;        //retry login times
        public byte byPasswordLevel;      //PasswordLevel,0-invalid,1-default password,2-valid password,3-risk password
        public byte byProxyType;  //Proxy Type,0-not use proxy, 1-use socks5 proxy, 2-use EHome proxy
        public int dwSurplusLockTime;    //surplus locked time
        public byte byCharEncodeType;     //character encode type
        public byte bySupportDev5;//Support v50 version of the device parameters, device name and device type name length is extended to 64 bytes
        public byte[] byRes2 = new byte[254];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"struDeviceV30", "bySupportLock", "byRetryLoginTime", "byPasswordLevel", "byProxyType",
                    "dwSurplusLockTime", "byCharEncodeType", "bySupportDev5", "byRes2"});
        }

        public static class ByReference extends NET_DVR_DEVICEINFO_V40 implements Structure.ByReference {
        }

        public static class ByValue extends NET_DVR_DEVICEINFO_V40 implements Structure.ByValue {
        }
    }

    //DVR device parameters
    public static class NET_DVR_DEVICECFG_V40 extends Structure {
        public int dwSize;
        public byte[] sDVRName = new byte[NAME_LEN];//DVR name
        public int dwDVRID;    //DVR ID //V1.4 (0- 99) ,  V1.5 (0- 255)
        public int dwRecycleRecord;  //cycle record, 0-disable, 1-enable
        //the following to the end is Read-only
        public byte[] sSerialNumber = new byte[SERIALNO_LEN];//SN
        public int dwSoftwareVersion;    //Software version,Major version:16 MSB,minor version:16 LSB
        public int dwSoftwareBuildDate;    //Build, 0xYYYYMMDD
        public int dwDSPSoftwareVersion;      //DSP Version: 16 high bit is the major version, and 16 low bit is the minor version
        public int dwDSPSoftwareBuildDate;  // DSP Build, 0xYYYYMMDD
        public int dwPanelVersion;        // Front panel version,Major version:16 MSB,minor version:16 LSB
        public int dwHardwareVersion;    // Hardware version,Major version:16 MSB,minor version:16 LSB
        public byte byAlarmInPortNum;  //DVR Alarm input
        public byte byAlarmOutPortNum;  //DVR Alarm output
        public byte byRS232Num;    //DVR 232 port number
        public byte byRS485Num;    //DVR 485 port number
        public byte byNetworkPortNum;  //Network port number
        public byte byDiskCtrlNum;    //DVR HDD number
        public byte byDiskNum;    //DVR disk number
        public byte byDVRType;    //DVRtype, 1:DVR 2:ATM DVR 3:DVS ......
        public byte byChanNum;    //DVR channel number
        public byte byStartChan;    //start,e.g.1: DVR 2: ATM DVR 3: DVS ......- -
        public byte byDecordChans;    //DVR decoding channels
        public byte byVGANum;    //VGA interface number
        public byte byUSBNum;    //USB interface number
        public byte byAuxoutNum;    //Aux output number
        public byte byAudioNum;    //voice interface number
        public byte byIPChanNum;    //Max. IP channel number  8 LSB ，8 MSB with byHighIPChanNum
        public byte byZeroChanNum;    //Zero channel number
        public byte bySupport;        //Ability set，0 represent not support ，1 represent support,
        //bySupport & 0x1, smart search
        //bySupport & 0x2, backup
        //bySupport & 0x4, compression ability set
        //bySupport & 0x8, multiple network adapter
        //bySupport & 0x10, remote SADP
        //bySupport & 0x20, support Raid
        //bySupport & 0x40, support IPSAN
        //bySupport & 0x80, support RTP over RTSP
        public byte byEsataUseage;  //Default E-SATA: 0- backup, 1- record
        public byte byIPCPlug;    //0- disable plug-and-play, 1- enable plug-and-play
        public byte byStorageMode;  //Hard Disk Mode:0-group,1-quota,2-draw frame,3-Auto
        public byte bySupport1;  //Ability set，0 represent not support ，1 represent support,
        //bySupport1 & 0x1, support snmp v30
        //bySupport1 & 0x2, support distinguish download and playback
        //bySupport1 & 0x4, support deployment level
        //bySupport1 & 0x8, support vca alarm time extension
        //bySupport1 & 0x10, support muti disks(more than 33)
        //bySupport1 & 0x20, support rtsp over http
        public short wDevType;//Device type
        public byte[] byDevTypeName = new byte[DEV_TYPE_NAME_LEN];//Device model name
        public byte bySupport2;  //The ability to set extension, bit 0 indicates does not support one expressed support for
        //bySupport2 & 0x1, Whether to support extended the OSD character overlay (terminal and capture machine expansion distinguish)
        public byte byAnalogAlarmInPortNum; //Analog alarm in number
        public byte byStartAlarmInNo;    //Analog alarm in Start No.
        public byte byStartAlarmOutNo;  //Analog alarm Out Start No.
        public byte byStartIPAlarmInNo;   //IP alarm in Start No.  0-Invalid
        public byte byStartIPAlarmOutNo; //IP Alarm Out Start No.  0-Invalid
        public byte byHighIPChanNum;     //Ip Chan Num High 8 Bit
        public byte byEnableRemotePowerOn;//enable the equipment in a dormant state remote boot function, 0- is not enabled, the 1- enabled
        public short wDevClass; //device class
        public byte[] byRes2 = new byte[6];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "sDVRName", "dwDVRID", "dwRecycleRecord", "sSerialNumber", "dwSoftwareVersion", "dwSoftwareBuildDate",
                    "dwDSPSoftwareVersion", "dwDSPSoftwareBuildDate", "dwPanelVersion", "dwHardwareVersion", "byAlarmInPortNum", "byAlarmOutPortNum",
                    "byRS232Num", "byRS485Num", "byNetworkPortNum", "byDiskCtrlNum", "byDiskNum", "byDVRType", "byChanNum", "byStartChan", "byDecordChans",
                    "byVGANum", "byUSBNum", "byAuxoutNum", "byAudioNum", "byIPChanNum", "byZeroChanNum", "bySupport", "byEsataUseage", "byIPCPlug",
                    "byStorageMode", "bySupport1", "wDevType", "byDevTypeName", "bySupport2", "byAnalogAlarmInPortNum", "byStartAlarmInNo", "byStartAlarmOutNo",
                    "byStartIPAlarmInNo", "byStartIPAlarmOutNo", "byHighIPChanNum", "byEnableRemotePowerOn", "wDevClass", "byRes2"});
        }
    }

    /* Asynchronous login callback function
     * [out] lUserID - NET_DVR_Login_V40 return value
     * [out] dwResult - asynchronous login status, 0:failed,1:success
     * [out] NET_DVR_DEVICEINFO_V30 - device informations
     * [out] pUser - user input data
     */
    //public delegate void LoginResultCallBack(int lUserID, int dwResult, ref NET_DVR_DEVICEINFO_V30 lpDeviceInfo, int pUser);

    public static interface FLoginResultCallBack extends StdCallCallback {
        public int invoke(NativeLong lUserID, int dwResult, NET_DVR_DEVICEINFO_V30 lpDeviceinfo, Pointer pUser);
    }

    public static class NET_DVR_USER_LOGIN_INFO extends Structure /*Change string to byte []*/ {
        public byte[] sDeviceAddress = new byte[NET_DVR_DEV_ADDRESS_MAX_LEN];
        public byte byUseTransport;
        //public byte byRes1;
        public short wPort;
        public byte[] sUserName = new byte[NET_DVR_LOGIN_USERNAME_MAX_LEN];
        public byte[] sPassword = new byte[NET_DVR_LOGIN_PASSWD_MAX_LEN];
        public FLoginResultCallBack cbLoginResult;
        public Pointer pUser;
        public boolean bUseAsynLogin;
        public byte byProxyType;
        public byte byUseUTCTime;
        public byte[] byRes2 = new byte[2];
        public int iProxyID;
        public byte[] byRes3 = new byte[120];

        //public byte[] byRes2 = new byte[128];
        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"sDeviceAddress", "byUseTransport", "wPort", "sUserName", "sPassword", "cbLoginResult", "pUser",
                    "bUseAsynLogin", "byProxyType", "byUseUTCTime", "byRes2", "iProxyID", "byRes3"});
            /*return Arrays.asList(new String[]{ "sDeviceAddress","byRes1","wPort","sUserName","sPassword","cbLoginResult","pUser",
                    "bUseAsynLogin","byRes2"});*/
        }

        public void Init() {
            sDeviceAddress = new byte[NET_DVR_DEV_ADDRESS_MAX_LEN];
        }

        public static class ByReference extends NET_DVR_USER_LOGIN_INFO implements Structure.ByReference {
        }

        public static class ByValue extends NET_DVR_USER_LOGIN_INFO implements Structure.ByValue {
        }
    }

    public static class NET_DVR_PREVIEWINFO extends Structure {
        public int lChannel;    //Channel no.
        public int dwStreamType;   //Stream type 0-main stream,1-sub stream,2-third stream,3-forth stream, and so on
        public int dwLinkMode; //Protocol type: 0-TCP, 1-UDP, 2-Muticast, 3-RTP,4-RTP/RTSP, 5-RSTP/HTTP
        public int hPlayWnd; //Play window's handle;  set NULL to disable preview
        public int bBlocked;   //If data stream requesting process is blocked or not: 0-no, 1-yes
        //if true, the SDK Connect failure return until 5s timeout  , not suitable for polling to preview.
        public int bPassbackRecord;    //0- not enable  ,1 enable
        public byte byPreviewMode;  //Preview mode 0-normal preview,2-delay preview
        public byte[] byStreamID = new byte[STREAM_ID_LEN];   //Stream ID
        public byte byProtoType;    //0-private,1-RTSP
        public byte byRes1;
        public byte byVideoCodingType;
        public int dwDisplayBufNum;    //soft player display buffer size(number of frames), range:1-50, default:1
        public byte[] byRes = new byte[216];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"lChannel", "dwStreamType", "dwLinkMode", "hPlayWnd", "bBlocked", "bPassbackRecord",
                    "byPreviewMode", "byStreamID", "byProtoType", "byRes1", "byVideoCodingType", "dwDisplayBufNum", "byRes"});
        }
    }

    //region network configuration
    /*IP address*/
    public static class NET_DVR_IPADDR extends Structure {

        /// char[16]
        public byte[] sIpV4 = new byte[16]; //Change string to byte[]
        /// BYTE[128]
        public byte[] byIPv6 = new byte[128];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"sIpV4", "byIPv6"});
        }
    }

    /* Network structure(sub struct)(9000 extension)*/

    public static class NET_DVR_ETHERNET_V30 extends Structure {
        public NET_DVR_IPADDR struDVRIP;//DVR IP address
        public NET_DVR_IPADDR struDVRIPMask;//DVR IP address mask
        public int dwNetInterface;//net card: 1-10MBase-T 2-10MBase-T Full duplex 3-100MBase-TX 4-100M Full duplex 5-10M/100M adaptive
        public short wDVRPort;//port
        public short wMTU;//MTU default:1500。
        public byte[] byMACAddr = new byte[MACADDR_LEN];// mac address
        public byte[] byRes = new byte[2];// reserve

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"struDVRIP", "struDVRIPMask", "dwNetInterface", "wDVRPort", "wMTU", "byMACAddr", "byRes"});
        }
    }

    public static class NET_DVR_PPPOECFG extends Structure {
        public int dwPPPOE;//0-disable,1-enable
        public byte[] sPPPoEUser = new byte[NAME_LEN];//PPPoE user name
        public byte[] sPPPoEPassword = new byte[PASSWD_LEN];// PPPoE password
        public NET_DVR_IPADDR struPPPoEIP;//PPPoE IP address

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwPPPOE", "sPPPoEUser", "sPPPoEPassword", "struPPPoEIP"});
        }
    }

    //network configuration struct(9000 extension)

    public static class NET_DVR_NETCFG_V30 extends Structure {
        public int dwSize;
        public NET_DVR_ETHERNET_V30[] struEtherNet = new NET_DVR_ETHERNET_V30[MAX_ETHERNET];//Ethernet
        public NET_DVR_IPADDR[] struRes1;//reserve
        public NET_DVR_IPADDR struAlarmHostIpAddr = new NET_DVR_IPADDR();// alarm host IP address
        public short[] wRes2;
        public short wAlarmHostIpPort;
        public byte byUseDhcp;
        public byte byIPv6Mode;//IPv6 distribute methods，0-Routing announcement，1-manually，2-Enable the DHCP allocation
        public NET_DVR_IPADDR struDnsServer1IpAddr = new NET_DVR_IPADDR(); // primary dns server
        public NET_DVR_IPADDR struDnsServer2IpAddr = new NET_DVR_IPADDR(); // secondary dns server
        public byte[] byIpResolver = new byte[MAX_DOMAIN_NAME];
        public short wIpResolverPort;
        public short wHttpPortNo;
        public NET_DVR_IPADDR struMulticastIpAddr = new NET_DVR_IPADDR(); // Multicast group address
        public NET_DVR_IPADDR struGatewayIpAddr = new NET_DVR_IPADDR(); // The gateway address
        public NET_DVR_PPPOECFG struPPPoE = new NET_DVR_PPPOECFG();
        public byte byEnablePrivateMulticastDiscovery;  //Private multicast search，0~default，1~enable ，2-disable
        public byte byEnableOnvifMulticastDiscovery;  //Onvif multicast search，0~default，1~enable，2-disable
        public byte byEnableDNS; //DNS Atuo enable, 0-Res,1-open, 2-close
        public byte[] byRes = new byte[61];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "struEtherNet", "struRes1", "struAlarmHostIpAddr", "wRes2", "wAlarmHostIpPort",
                    "byUseDhcp", "byIPv6Mode", "struDnsServer1IpAddr", "struDnsServer2IpAddr", "byIpResolver", "wIpResolverPort",
                    "wHttpPortNo", "struMulticastIpAddr", "struGatewayIpAddr", "struPPPoE", "byEnablePrivateMulticastDiscovery",
                    "byEnableOnvifMulticastDiscovery", "byEnableDNS", "byRes"});
        }
    }

    //Network Configure Structure(V50)

    public static class NET_DVR_NETCFG_V50 extends Structure {
        public int dwSize;
        public NET_DVR_ETHERNET_V30[] struEtherNet = new NET_DVR_ETHERNET_V30[MAX_ETHERNET];        //Network Port
        public NET_DVR_IPADDR[] struRes1 = new NET_DVR_IPADDR[2];                           /*reserve*/
        public NET_DVR_IPADDR struAlarmHostIpAddr = new NET_DVR_IPADDR();                    /* IP address of remote management host */
        public byte[] byRes2;                                        /* reserve */
        public short wAlarmHostIpPort;                                /* Port of remote management Host */
        public byte byUseDhcp;                                      /* Whether to enable the DHCP 0xff- invalid 0- enabled 1- not enabled */
        public byte byIPv6Mode;                                        //IPv6 allocation, 0- routing announcement, 1- manually, 2- enable DHCP allocation
        public NET_DVR_IPADDR struDnsServer1IpAddr = new NET_DVR_IPADDR();                   /* IP address of the domain name server 1  */
        public NET_DVR_IPADDR struDnsServer2IpAddr = new NET_DVR_IPADDR();                    /* IP address of the domain name server 2  */
        public byte[] byIpResolver = new byte[MAX_DOMAIN_NAME];                    /* IP parse server domain name or IP address */
        public short wIpResolverPort;                                /* Parsing IP server port number */
        public short wHttpPortNo;                                    /* HTTP port number  */
        public NET_DVR_IPADDR struMulticastIpAddr = new NET_DVR_IPADDR();                    /* Multicast group address */
        public NET_DVR_IPADDR struGatewayIpAddr = new NET_DVR_IPADDR();                        /* Gateway address  */
        public NET_DVR_PPPOECFG struPPPoE = new NET_DVR_PPPOECFG();
        public byte byEnablePrivateMulticastDiscovery;                //Private multicast search, 0- default, 1- enabled, 2 - disabled
        public byte byEnableOnvifMulticastDiscovery;                //Onvif multicast search, 0- default, 1- enabled, 2 - disabled
        public short wAlarmHost2IpPort;                                /* Alarm host 2 port */
        public NET_DVR_IPADDR struAlarmHost2IpAddr = new NET_DVR_IPADDR();                    /* Alarm host 2 IP addresses */
        public byte byEnableDNS; //DNS Enabled, 0-close,1-open
        public byte[] byRes = new byte[599];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "struEtherNet", "struRes1", "struAlarmHostIpAddr", "byRes2",
                    "wAlarmHostIpPort", "byUseDhcp", "byIPv6Mode", "struDnsServer1IpAddr", "struDnsServer2IpAddr", "byIpResolver",
                    "wIpResolverPort", "wHttpPortNo", "struMulticastIpAddr", "struGatewayIpAddr", "struPPPoE", "byEnablePrivateMulticastDiscovery",
                    "byEnableOnvifMulticastDiscovery", "wAlarmHost2IpPort", "struAlarmHost2IpAddr", "byEnableDNS", "byRes"});
        }
    }

    public static class NET_DVR_IPDEVINFO_V31 extends Structure {
        public byte byEnable;//Valid status for IP device
        public byte byProType;                 //Protocol type,  0- private (default) ,  1-  Panasonic,  2-  SONY
        public byte byEnableQuickAdd;          //0-  does not support quick adding of IP device;  1-   enable quick adding of IP device
        //Quick add of device IP and protocol,  fill in the other parameters as system default
        public byte byRes1;                     //reserved as 0

        public byte[] sUserName = new byte[NAME_LEN];//user name
        public byte[] sPassword = new byte[PASSWD_LEN];//Password
        public byte[] byDomain = new byte[MAX_DOMAIN_NAME];//Domain name of the device
        public NET_DVR_IPADDR struIP;//IP
        public short wDVRPort;// Port number
        public byte[] szDeviceID = new byte[DEV_ID_LEN];  //Device ID
        public byte[] byRes2 = new byte[2];                 //Reserved as 0

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "byProType", "byEnableQuickAdd", "byRes1", "sUserName", "sPassword", "byDomain",
                    "struIP", "wDVRPort", "szDeviceID", "byRes2"});
        }
    }

    //region event card linkage
    public static class NET_DVR_EVENT_CARD_LINKAGE_COND extends Structure {
        public int dwSize;
        public int dwEventID; //Event ID
        public short wLocalControllerID; //On the controller serial number [1, 64]
        public byte[] byRes = new byte[106];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwEventID", "wLocalControllerID", "byRes"});
        }
    }

    public static class NET_DVR_EVENT_LINKAGE_INFO extends Structure {
        public short wMainEventType;                     //main event type,0-device,1-alarmin,2-door,3-card reader
        public short wSubEventType;                      //sub event type
        public byte[] byRes = new byte[28];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"wMainEventType", "wSubEventType", "byRes"});
        }
    }

    public static class NET_DVR_EVETN_CARD_LINKAGE_UNION extends Structure {
        public byte[] byCardNo = new byte[ACS_CARD_NO_LEN];
        public NET_DVR_EVENT_LINKAGE_INFO struEventLinkage;
        public byte[] byMACAddr = new byte[MACADDR_LEN];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byCardNo", "struEventLinkage", "byMACAddr"});
        }
    }

    public static class NET_DVR_EVENT_CARD_LINKAGE_CFG_V50 extends Structure {
        public int dwSize;
        public byte byProMode;                          //linkage type,0-by event,1-by card, 2-by mac
        public byte[] byRes1 = new byte[3];
        public int dwEventSourceID;                    //event source ID,when the main event is device ,it not use; when the main event is door ,it is the door No; when the main event is card reader ,it is the card reader No; when the main event is alarmin,it is the alarmin ID; 0xffffffff means all
        public NET_DVR_EVETN_CARD_LINKAGE_UNION uLinkageInfo;  //Linkage mode parameters
        public byte[] byAlarmout = new byte[MAX_ALARMHOST_ALARMOUT_NUM];            //linkage alarmout NO,according to the values,0-not linkage,1-linkage
        public byte[] byRes2 = new byte[32];
        public byte[] byOpenDoor = new byte[MAX_DOOR_NUM_256];     //whether linkage open door,according to the values,0-not linkage,1-linkage
        public byte[] byCloseDoor = new byte[MAX_DOOR_NUM_256];    //whether linkage close door,according to the values,0-not linkage,1-linkage
        public byte[] byNormalOpen = new byte[MAX_DOOR_NUM_256];   //whether linkage normal open door,according to the values,0-not linkage,1-linkage
        public byte[] byNormalClose = new byte[MAX_DOOR_NUM_256];  //whether linkage normal close door,according to the values,0-not linkage,1-linkage
        public byte byMainDevBuzzer;                    //whether linkage main device buzzer, 0-not linkage,1-linkage
        public byte byCapturePic;                    //whether linkage capture picture, 0-no, 1-yes
        public byte byRecordVideo;                   //whether linkage record video, 0-no, 1-yes
        public byte[] byRes3 = new byte[29];
        public byte[] byReaderBuzzer = new byte[MAX_CARD_READER_NUM_512]; //linkage reader buzzer,according to the values,0-not linkage,1-linkage
        public byte[] byAlarmOutClose = new byte[MAX_ALARMHOST_ALARMOUT_NUM];            //Associated alarm output shut down, in bytes, 0-not linkage,1-linkage
        public byte[] byAlarmInSetup = new byte[MAX_ALARMHOST_ALARMIN_NUM];  //Associated slip protection, in bytes, 0-not linkage,1-linkage
        public byte[] byAlarmInClose = new byte[MAX_ALARMHOST_ALARMIN_NUM];  //Removal associated protection zones, in bytes, 0-not linkage,1-linkage
        public byte[] byRes = new byte[500];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "byProMode", "byRes1", "dwEventSourceID", "uLinkageInfo", "byAlarmout",
                    "byRes2", "byOpenDoor", "byCloseDoor", "byNormalOpen", "byNormalClose", "byMainDevBuzzer", "byCapturePic", "byRecordVideo",
                    "byRes3", "byReaderBuzzer", "byAlarmOutClose", "byAlarmInSetup", "byAlarmInClose", "byRes"});
        }
    }

    //region DVR IP channel configuration
    /* Alarm output parameters */
    /* Alarm output channel */

    public static class NET_DVR_IPALARMOUTINFO extends Structure {
        public byte byIPID;                     /* ID of IP device,  the range:  1 to MAX_IP_DEVICE */
        public byte byAlarmOut;                 /* Alarm output NO. */
        public byte[] byRes = new byte[18];                 /* Reserved */

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byIPID", "byAlarmOut", "byRes"});
        }
    }

    /* IP Alarm output configuration */

    public static class NET_DVR_IPALARMOUTCFG extends Structure {
        public int dwSize;                                                 /*struct size */
        public NET_DVR_IPALARMOUTINFO[] struIPAlarmOutInfo = new NET_DVR_IPALARMOUTINFO[MAX_IP_ALARMOUT]; /* IP alarm output */

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "struIPAlarmOutInfo"});
        }
    }

    public static class NET_DVR_IPALARMOUTINFO_V40 extends Structure {
        public int dwIPID;                    /* ID of IP device,  the range:  1 to MAX_IP_DEVICE*/
        public int dwAlarmOut;                /* Alarm Out NO. */
        public byte[] byRes = new byte[32];                 /* Reserved */

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwIPID", "dwAlarmOut", "byRes"});
        }
    }

    public static class NET_DVR_IPALARMOUTCFG_V40 extends Structure {
        public int dwSize;
        public int dwCurIPAlarmOutNum;
        public NET_DVR_IPALARMOUTINFO_V40[] struIPAlarmOutInfo = new NET_DVR_IPALARMOUTINFO_V40[MAX_IP_ALARMIN_V40];
        public byte[] byRes = new byte[256];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwCurIPAlarmOutNum", "struIPAlarmOutInfo", "byRes"});
        }
    }

    /* IP Alarm input configuration */

    public static class NET_DVR_IPALARMININFO extends Structure {
        public byte byIPID;                     /* ID of IP device,  the range:  1 to MAX_IP_DEVICE */
        public byte byAlarmIn;                 /* Alarm input NO. */
        public byte[] byRes = new byte[18];                 /* Reserved */

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byIPID", "byAlarmIn", "byRes"});
        }
    }

    public static class NET_DVR_IPALARMINCFG extends Structure {
        public int dwSize;                                              /*struct size */
        public NET_DVR_IPALARMININFO[] struIPAlarmInInfo = new NET_DVR_IPALARMININFO[MAX_IP_ALARMIN];  /* IP alarm input */

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "struIPAlarmInInfo"});
        }
    }

    public static class NET_DVR_IPALARMININFO_V40 extends Structure {
        public int dwIPID;                    /* ID of IP device,  the range:  1 to MAX_IP_DEVICE */
        public int dwAlarmIn;                /* Alarm input NO. */
        public byte[] byRes = new byte[32];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwIPID", "dwAlarmIn", "byRes"});
        }
    }

    public static class NET_DVR_IPALARMINCFG_V40 extends Structure {
        public int dwSize;
        public NET_DVR_IPALARMININFO_V40[] struIPAlarmInInfo = new NET_DVR_IPALARMININFO_V40[MAX_IP_ALARMIN_V40];/* IP alarmin */
        public byte[] byRes = new byte[256];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "struIPAlarmInInfo", "byRes"});
        }
    }

    /* IP Channel parameters */
    public static class NET_DVR_IPCHANINFO extends Structure {
        public byte byEnable;                     //0- Failed to connect IP device; 1- Successfully;
        public byte byIPID;                     //ID of IP device,  low 8 bit
        public byte byChannel;                 //Channel No.
        public byte byIPIDHigh;                //ID of IP device,  high 8 bit
        public byte byTransProtocol;            //Trans Protocol Type 0-TCP/auto (Determined by the device),1-UDP 2-Multicast 3-only TCP 4-auto
        public byte[] byres = new byte[31];                    /* Reserved */

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "byIPID", "byChannel", "byIPIDHigh", "byTransProtocol", "byres"});
        }
    }

    public static class NET_DVR_IPSERVER_STREAM extends Structure {
        public byte byEnable;   //Is enable
        public byte[] byRes = new byte[3];
        public NET_DVR_IPADDR struIPServer;   //IPServer Address
        public short wPort;                   //IPServer port
        public short wDvrNameLen;             //DVR Name Length
        public byte[] byDVRName = new byte[NAME_LEN];    //DVR Name
        public short wDVRSerialLen;           //Serial Length
        public short[] byRes1 = new short[2];              //reserved
        public byte[] byDVRSerialNumber = new byte[SERIALNO_LEN];    //DVR Serial
        public byte[] byUserName = new byte[NAME_LEN];              //DVR User name
        public byte[] byPassWord = new byte[PASSWD_LEN];             //DVR User password
        public byte byChannel;                         //DVR channel
        public byte[] byRes2 = new byte[11];              //Reserved

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "byRes", "struIPServer", "wPort", "wDvrNameLen", "byDVRName", "wDVRSerialLen", "byRes1",
                    "byDVRSerialNumber", "byUserName", "byPassWord", "byChannel", "byRes2"});
        }
    }

    /*the configuration of stream server*/
    public static class NET_DVR_STREAM_MEDIA_SERVER_CFG extends Structure {
        public byte byValid;            //Is enable
        public byte[] byRes1 = new byte[3];
        public NET_DVR_IPADDR struDevIP;  //stream server IP
        public short wDevPort;            //stream server Port
        public byte byTransmitType;        //Protocol: 0-TCP, 1-UDP
        public byte[] byRes2 = new byte[69];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byValid", "byRes1", "struDevIP", "wDevPort", "byTransmitType", "byRes2"});
        }
    }

    //device information
    public static class NET_DVR_DEV_CHAN_INFO extends Structure {
        public NET_DVR_IPADDR struIP;            //DVR IP address
        public short wDVRPort;                 //DVR PORT
        public byte byChannel;                //Channel
        public byte byTransProtocol;        //Transmit protocol:0-TCP,1-UDP
        public byte byTransMode;            //Stream mode: 0－mian stream 1－sub stream
        public byte byFactoryType;            /*IPC factory type*/
        public byte byDeviceType; //Device type(Used by videoplatfom VCA card),1-decoder(use decode channel No. or display channel depends on byVcaSupportChanMode in videoplatform ability struct),2-coder
        public byte byDispChan;//Display channel No. used by VCA configuration
        public byte bySubDispChan;//Display sub channel No. used by VCA configuration
        public byte byResolution;    //Resolution: 1-CIF 2-4CIF 3-720P 4-1080P 5-500w used by big screen controler
        public byte[] byRes = new byte[2];
        ;
        public byte[] byDomain = new byte[MAX_DOMAIN_NAME];    //Device domain name
        public byte[] sUserName = new byte[NAME_LEN];    //Remote device user name
        public byte[] sPassword = new byte[PASSWD_LEN];    //Remote device password

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"struIP", "wDVRPort", "byChannel", "byTransProtocol", "byTransMode", "byFactoryType", "byDeviceType",
                    "byDispChan", "bySubDispChan", "byResolution", "byRes", "byDomain", "sUserName", "sPassword"});
        }
    }

    public static class NET_DVR_PU_STREAM_CFG extends Structure {
        public int dwSize;
        public NET_DVR_STREAM_MEDIA_SERVER_CFG struStreamMediaSvrCfg;
        public NET_DVR_DEV_CHAN_INFO struDevChanInfo;

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "struStreamMediaSvrCfg", "struDevChanInfo"});
        }
    }

    public static class NET_DVR_DDNS_STREAM_CFG extends Structure {
        public byte byEnable;   //Is Enable.
        public byte[] byRes1 = new byte[3];
        NET_DVR_IPADDR struStreamServer;   //Stream server IP
        public short wStreamServerPort;           //Stream server Port
        public byte byStreamServerTransmitType;  //Stream protocol
        public byte byRes2;
        NET_DVR_IPADDR struIPServer;      //IPserver IP
        public short wIPServerPort;               //IPserver Port
        public byte[] byRes3 = new byte[2];
        public byte[] sDVRName;     //DVR Name
        public short wDVRNameLen;            //DVR Name Len
        public short wDVRSerialLen;          //Serial Len
        public byte[] sDVRSerialNumber = new byte[SERIALNO_LEN];   //Serial number
        public byte[] sUserName = new byte[NAME_LEN];   //the user name which is used to login DVR.
        public byte[] sPassWord = new byte[PASSWD_LEN]; //the password which is used to login DVR.
        public short wDVRPort;        //DVR port
        public byte[] byRes4 = new byte[2];
        public byte byChannel;       //channel
        public byte byTransProtocol; //protocol
        public byte byTransMode;     //transform mode
        public byte byFactoryType;   //The type of factory who product the device.

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "byRes1", "NET_DVR_IPADDRstruStreamServer", "wStreamServerPort",
                    "byStreamServerTransmitType", "byRes2", "NET_DVR_IPADDRstruIPServer", "wIPServerPort", "byRes3", "sDVRName",
                    "wDVRNameLen", "wDVRSerialLen", "sDVRSerialNumber", "sUserName", "sPassWord", "wDVRPort", "byRes4", "byChannel",
                    "byTransProtocol", "byTransMode", "byFactoryType"});
        }
    }

    public static class NET_DVR_PU_STREAM_URL extends Structure {
        public byte byEnable;
        public byte[] strURL = new byte[URL_LEN];
        public byte byTransPortocol; // transport protocol type  0-tcp  1-UDP
        public short wIPID;  //Device ID,wIPID = iDevInfoIndex + iGroupNO*64 +1
        public byte byChannel;  //channel NO.
        public byte[] byRes = new byte[7];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "strURL", "byTransPortocol", "wIPID", "byChannel", "byRes"});
        }
    }

    public static class NET_DVR_HKDDNS_STREAM extends Structure {
        public byte byEnable;                                       //Is enable
        public byte[] byRes = new byte[3];
        ;
        public byte[] byDDNSDomain = new byte[64];                  // hiDDNS domain
        public short wPort;                                         //IPServer port
        public short wAliasLen;                                     //Alias Length
        public byte[] byAlias = new byte[NAME_LEN];                 //Alias
        public short wDVRSerialLen;                                 //Serial Length
        public byte[] byRes1 = new byte[2];                         //reserved
        public byte[] byDVRSerialNumber = new byte[SERIALNO_LEN];   //DVR Serial
        public byte[] byUserName = new byte[NAME_LEN];              //DVR User name
        public byte[] byPassWord = new byte[PASSWD_LEN];            //DVR User passward
        public byte byChannel;                                      //DVR channel
        public byte[] byRes2 = new byte[11];                        //Reserved

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "byRes", "byDDNSDomain", "wPort", "wAliasLen", "byAlias", "wDVRSerialLen",
                    "byRes1", "byDVRSerialNumber", "byUserName", "byPassword", "byChannel", "byRes2"});
        }
    }

    public static final int NET_DVR_GET_IPPARACFG_V40 = 1062;
    public static final int NET_DVR_SET_IPPARACFG_V40 = 1063;

    public static class NET_DVR_IPCHANINFO_V40 extends Structure {
        public byte byEnable;                /* Enable */
        public byte byRes1;
        public short wIPID;                  //IP ID
        public int dwChannel;                //channel
        public byte byTransProtocol;        //Trans protocol,0-TCP,1-UDP
        public byte byTransMode;            //Trans mode 0－main, 1－sub
        public byte byFactoryType;            /*Factory type*/
        public byte[] byRes = new byte[241];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byEnable", "byRes1", "wIPID", "dwChannel", "byTransProtocol", "byTransMode", "byFactoryType", "byRes"});
        }
    }

    public static class NET_DVR_GET_STREAM_UNION extends Structure {
        public NET_DVR_IPCHANINFO struChanInfo;    //Get stream from Device.
        public NET_DVR_IPSERVER_STREAM struIPServerStream;  // //Get stream from Device which register the IPServer
        public NET_DVR_PU_STREAM_CFG struPUStream;     //Get stream from stream server.
        public NET_DVR_DDNS_STREAM_CFG struDDNSStream;     //Get stream by IPserver and stream server.
        public NET_DVR_PU_STREAM_URL struStreamUrl;        //get stream through stream server by url.
        public NET_DVR_HKDDNS_STREAM struHkDDNSStream;   //get stream through hiDDNS
        public NET_DVR_IPCHANINFO_V40 struIPChan; //Get stream from device(Extend)

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"struChanInfo", "struIPServerStream", "struPUStream", "struDDNSStream", "struStreamUrl", "struHkDDNSStream", "struIPChan"});
        }
    }

    public static class NET_DVR_STREAM_MODE extends Structure {
        public byte byGetStreamType; //the type of gettin stream:0-Get stream from Device, 1-Get stream fram stream server,
        //2-Get stream from Device which register the IPServer, 3.Get stream by IPserver and stream server
        //4-get stream by url,5-hkDDNS,6-Get stream from Device,NET_DVR_IPCHANINFO_V40,7- Get Stream by Rtsp Protocal
        public byte[] byRes = new byte[3];
        public NET_DVR_GET_STREAM_UNION uGetStream;    //the union of different getting stream type.

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"byGetStreamType", "byRes", "uGetStream"});
        }
    }

    public static class NET_DVR_IPPARACFG_V40 extends Structure {
        public int dwSize;
        public int dwGroupNum;                    //The number of group
        public int dwAChanNum;                    //The number of simulate channel
        public int dwDChanNum;                  //the number of IP channel
        public int dwStartDChan;                //the begin NO. of IP channel
        public byte[] byAnalogChanEnable = new byte[MAX_CHANNUM_V30];       //Is simulate channel enable? represent by bit
        public NET_DVR_IPDEVINFO_V31[] struIPDevInfo = new NET_DVR_IPDEVINFO_V31[MAX_IP_DEVICE_V40];
        public NET_DVR_STREAM_MODE[] struStreamMode = new NET_DVR_STREAM_MODE[MAX_CHANNUM_V30];
        public byte[] byRes2 = new byte[20];

        @Override
        protected List getFieldOrder() {
            return Arrays.asList(new String[]{"dwSize", "dwGroupNum", "dwAChanNum", "dwDChanNum", "dwStgartDChan", "byAnalogChanEnable",
                    "struIPDevInfo", "struStreamMode", "byRes2"});
        }
    }

    //升级
    NativeLong NET_DVR_Upgrade(NativeLong lUserID, String sFileName);

    int NET_DVR_GetUpgradeProgress(NativeLong lUpgradeHandle);

    NativeLong NET_DVR_Upgrade_V40(NativeLong lUserID, ENUM_UPGRADE_TYPE dwUpgradeType, String sFileName, Pointer lpInBufer, int dwBufferSize);

    //升级类型
    enum ENUM_UPGRADE_TYPE {
        ENUM_UPGRADE_DVR, // 普通设备升级
        ENUM_UPGRADE_ADAPTER, // DVR适配器升级
        ENUM_UPGRADE_VCALIB, // 智能库升级
        ENUM_UPGRADE_OPTICAL, // 光端机升级
        ENUM_UPGRADE_ACS, // 门禁系统升级
        ENUM_UPGRADE_AUXILIARY_DEV, // 辅助设备升级
        ENUM_UPGRADE_LED //LED发送卡和接收卡升级
    }

    ;

    public static class NET_DVR_ACS_WORK_STATUS_V50 extends Structure {
        public int dwSize;
        public byte[] byDoorLockStatus = new byte[MAX_DOOR_NUM_256];//door lock status(relay status), 0 normally closed,1 normally open, 2 damage short - circuit alarm, 3 damage breaking alarm, 4 abnormal alarm
        public byte[] byDoorStatus = new byte[MAX_DOOR_NUM_256];
        public byte[] byMagneticStatus = new byte[MAX_DOOR_NUM_256];
        public byte[] byCaseStatus = new byte[MAX_CASE_SENSOR_NUM];
        public short wBatteryVoltage; //vattery voltage , multiply 10, unit: V
        public byte byBatteryLowVoltage; //Is battery in low voltage, 0-no 1-yes
        public byte byPowerSupplyStatus; //power supply status, 1-alternating current supply, 2-battery supply
        public byte byMultiDoorInterlockStatus;//multi door interlock status, 0-close 1-open
        public byte byAntiSneakStatus; //anti sneak status, 0-close 1-open
        public byte byHostAntiDismantleStatus; //host anti dismantle status, 0-close, 1-open
        public byte byIndicatorLightStatus; //Indicator Light Status 0-offLine,1-Online
        public byte[] byCardReaderOnlineStatus = new byte[MAX_CARD_READER_NUM_512];
        public byte[] byCardReaderAntiDismantleStatus = new byte[MAX_CARD_READER_NUM_512]; //card reader anti dismantle status, 0-close 1-open
        public byte[] byCardReaderVerifyMode = new byte[MAX_CARD_READER_NUM_512]; //card reader verify mode, 1-swipe 2-swipe+password 3-swipe card 4-swipe card or password
        public byte[] bySetupAlarmStatus = new byte[MAX_ALARMHOST_ALARMIN_NUM];//alarm in setup alarm status,0- alarm in disarm status, 1 - alarm in arm status
        public byte[] byAlarmInStatus = new byte[MAX_ALARMHOST_ALARMIN_NUM]; //alarm in status, 0-alarm in no alarm, 1-alarm in has alarm
        public byte[] byAlarmOutStatus = new byte[MAX_ALARMHOST_ALARMOUT_NUM]; //alarm out status, 0-alarm out no alarm, 1-alarm out has alarm
        public int dwCardNum; //add card number
        public byte byFireAlarmStatus; //Fire alarm status is displayed: 0 - normal, short-circuit alarm 1 -, 2 - disconnect the alarm
        public byte[] byRes2 = new byte[123];
    }

    public static class NET_DVR_ACS_WORK_STATUS extends Structure {
        public int dwSize;
        public byte[] byDoorLockStatus = new byte[MAX_DOOR_NUM_256];//door lock status(relay status), 0 normally closed,1 normally open, 2 damage short - circuit alarm, 3 damage breaking alarm, 4 abnormal alarm
        public byte[] byDoorStatus = new byte[MAX_DOOR_NUM_256];
        public byte[] byMagneticStatus = new byte[MAX_DOOR_NUM_256];
        public byte[] byCaseStatus = new byte[MAX_CASE_SENSOR_NUM];
        public short wBatteryVoltage; //vattery voltage , multiply 10, unit: V
        public byte byBatteryLowVoltage; //Is battery in low voltage, 0-no 1-yes
        public byte byPowerSupplyStatus; //power supply status, 1-alternating current supply, 2-battery supply
        public byte byMultiDoorInterlockStatus;//multi door interlock status, 0-close 1-open
        public byte byAntiSneakStatus; //anti sneak status, 0-close 1-open
        public byte byHostAntiDismantleStatus; //host anti dismantle status, 0-close, 1-open
        public byte byIndicatorLightStatus; //Indicator Light Status 0-offLine,1-Online
        public byte[] byCardReaderOnlineStatus = new byte[MAX_CARD_READER_NUM_512];
        public byte[] byCardReaderAntiDismantleStatus = new byte[MAX_CARD_READER_NUM_512]; //card reader anti dismantle status, 0-close 1-open
        public byte[] byCardReaderVerifyMode = new byte[MAX_CARD_READER_NUM_512]; //card reader verify mode, 1-swipe 2-swipe+password 3-swipe card 4-swipe card or password
        public byte[] bySetupAlarmStatus = new byte[MAX_ALARMHOST_ALARMIN_NUM];//alarm in setup alarm status,0- alarm in disarm status, 1 - alarm in arm status
        public byte[] byAlarmInStatus = new byte[MAX_ALARMHOST_ALARMIN_NUM]; //alarm in status, 0-alarm in no alarm, 1-alarm in has alarm
        public byte[] byAlarmOutStatus = new byte[MAX_ALARMHOST_ALARMOUT_NUM]; //alarm out status, 0-alarm out no alarm, 1-alarm out has alarm
        public int dwCardNum; //add card number
        public byte[] byRes2 = new byte[32];
    }

    public static class REMOTECONFIGSTATUS_CARD extends Structure {
        public byte[] byStatus = new byte[4];
        public byte[] byErrorCode = new byte[4];
        public byte[] byCardNum = new byte[32];
    }

    public static final int NET_DVR_SET_FACE_PARAM_CFG = 2508;    //设置人脸参数

    boolean NET_DVR_SendRemoteConfig(NativeLong lHandle, int dwDataType, Pointer pSendBuf, int dwBufSize);

    public static int ENUM_ACS_INTELLIGENT_IDENTITY_DATA = 0x9;

    boolean NET_DVR_StopRemoteConfig(NativeLong lHandle);

    NativeLong NET_DVR_Login_V30(String sDVRIP, short wDVRPort, String sUserName, String sPassword, NET_DVR_DEVICEINFO_V30 lpDeviceInfo);

    public static interface FMSGCallBack_V31 extends StdCallCallback {
        public boolean invoke(NativeLong lCommand, NET_DVR_ALARMER pAlarmer, Pointer pAlarmInfo, int dwBufLen, Pointer pUser);
    }

    boolean NET_DVR_SetDVRMessageCallBack_V31(FMSGCallBack_V31 fMessageCallBack, Pointer pUser);

    NativeLong NET_DVR_SetupAlarmChan_V41(NativeLong lUserID, NET_DVR_SETUPALARM_PARAM lpSetupParam);

    boolean NET_DVR_CloseAlarmChan_V30(NativeLong lAlarmHandle);

    public static final int COMM_ID_INFO_ALARM = 0x5200; //门禁身份证刷卡信息

    public static final int COMM_ALARM_V30 = 0x4000;//9000报警信息主动上传

    //身份证信息报警
    public static class NET_DVR_ID_CARD_INFO_ALARM extends Structure {
        public int dwSize;        //结构长度
        public NET_DVR_ID_CARD_INFO struIDCardCfg = new NET_DVR_ID_CARD_INFO();//身份证信息
        public int dwMajor; //报警主类型，参考宏定义
        public int dwMinor; //报警次类型，参考宏定义
        public NET_DVR_TIME_V30 struSwipeTime; //时间
        public byte[] byNetUser = new byte[MAX_NAMELEN];//网络操作的用户名
        public NET_DVR_IPADDR struRemoteHostAddr;//远程主机地址
        public int dwCardReaderNo; //读卡器编号，为0无效
        public int dwDoorNo; //门编号，为0无效
        public int dwPicDataLen;   //图片数据大小，不为0是表示后面带数据
        public Pointer pPicData;
        public byte byCardType; //卡类型，1-普通卡，2-残疾人卡，3-黑名单卡，4-巡更卡，5-胁迫卡，6-超级卡，7-来宾卡，8-解除卡，为0无效
        public byte byDeviceNo;                             // 设备编号，为0时无效（有效范围1-255）
        public byte[] byRes2 = new byte[2];
        public int dwFingerPrintDataLen;                  // 指纹数据大小，不为0是表示后面带数据
        public Pointer pFingerPrintData;
        public int dwCapturePicDataLen;                   // 抓拍图片数据大小，不为0是表示后面带数据
        public Pointer pCapturePicData;
        public byte[] byRes = new byte[188];
    }

    public static final int MAX_ID_NUM_LEN = 32;  //最大身份证号长度
    public static final int MAX_ID_NAME_LEN = 128;  //最大姓名长度
    public static final int MAX_ID_ADDR_LEN = 280;  //最大住址长度
    public static final int MAX_ID_ISSUING_AUTHORITY_LEN = 128;  //最大签发机关长度

    //身份证信息
    public static class NET_DVR_ID_CARD_INFO extends Structure {
        public int dwSize;        //结构长度
        public byte[] byName = new byte[MAX_ID_NAME_LEN];   //姓名
        public NET_DVR_DATE struBirth; //出生日期
        public byte[] byAddr = new byte[MAX_ID_ADDR_LEN];  //住址
        public byte[] byIDNum = new byte[MAX_ID_NUM_LEN];   //身份证号码
        public byte[] byIssuingAuthority = new byte[MAX_ID_ISSUING_AUTHORITY_LEN];  //签发机关
        public NET_DVR_DATE struStartDate;  //有效开始日期
        public NET_DVR_DATE struEndDate;  //有效截止日期
        public byte byTermOfValidity;  //是否长期有效， 0-否，1-是（有效截止日期无效）
        public byte bySex;  //性别，1-男，2-女
        public byte byNation;    //民族，1-"汉"，2-"蒙古"，3-"回",4-"藏",5-"维吾尔",6-"苗",7-"彝",8-"壮",9-"布依",10-"朝鲜",
        //11-"满",12-"侗",13-"瑶",14-"白",15-"土家",16-"哈尼",17-"哈萨克",18-"傣",19-"黎",20-"傈僳",
        //21-"佤",22-"畲",23-"高山",24-"拉祜",25-"水",26-"东乡",27-"纳西",28-"景颇",29-"柯尔克孜",30-"土",
        //31-"达斡尔",32-"仫佬",33-"羌",34-"布朗",35-"撒拉",36-"毛南",37-"仡佬",38-"锡伯",39-"阿昌",40-"普米",
        //41-"塔吉克",42-"怒",43-"乌孜别克",44-"俄罗斯",45-"鄂温克",46-"德昂",47-"保安",48-"裕固",49-"京",50-"塔塔尔",
        //51-"独龙",52-"鄂伦春",53-"赫哲",54-"门巴",55-"珞巴",56-"基诺"
        public byte[] byRes = new byte[101];
    }

    public static class NET_DVR_TIME_V30 extends Structure {
        public short wYear;
        public byte byMonth;
        public byte byDay;
        public byte byHour;
        public byte byMinute;
        public byte bySecond;
        public byte byRes;
        public short wMilliSec;
        public byte[] byRes1 = new byte[2];
    }
    //HCNetSDK.dll structure definition
}