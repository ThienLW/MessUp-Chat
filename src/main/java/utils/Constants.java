package utils;

public class Constants {
    public final static boolean SINGLE = true;
    public final static boolean GROUP = false;

    public final static int SUCCESS = 0x01;
    public final static int FAILED = 0x02;

    public static Integer  COMMAND = 0x10;
    public static Integer  TIME = 0x11;
    public static Integer  USERNAME = 0x12;
    public static Integer  PASSWORD = 0x13;
    public static Integer  SPEAKER = 0x14;
    public static Integer  RECEIVER = 0x15;
    public static Integer  CONTENT= 0x16;
//    Here
    public static Integer FIRSTNAME = 0x17;
    public static Integer SURNAME = 0x18;
    public static Integer BIRTHDAY = 0x19;
    public static Integer GENDER = 0x1A;
    public static Integer SECURITYQUESTION = 0x1B;
    public static Integer SECURITYANSWER = 0x1C;
    public static Integer STATUS = 0x1D;
//    Here

    public final static int COM_LOGIN = 0x20;
    public final static int COM_SIGNUP = 0x21;
    public final static int COM_RESULT = 0x22;
    public final static int COM_DESCRIPTION = 0x23;
    public final static int COM_LOGOUT =0x24;
    public final static int COM_CHATWITH = 0x25;
    public final static int COM_GROUP = 0x26;
    public final static int COM_CHATALL = 0x27;
    public final static int COM_KEEP = 0x28;
    public final static int COM_MESSAGEALL = 0X29;
    public final static int COM_CHANGESTATUS = 0X30;
    public final static int COM_CHECK_SECURITY_INFO = 0X31;
    public final static int COM_CHANGE_PASSWD = 0X32;
}
