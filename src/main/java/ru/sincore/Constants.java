package ru.sincore;

/**
 * <b>STA code description</b> <br>
 * Contexts: F, T, C, U<br>
 * States: All<br>
 * Status code in the form "xyy" where x specifies severity and yy the specific
 * error code. The severity and error code are treated separately, the same
 * error could occur at different severity levels.
 * 
 * @author valor
 */
public class Constants
{

    public static final int STA_GENERIC_HUB_ERROR = 10;
    public static final int STA_HUB_FULL = 10;
    public static final int STA_HUB_DISABLED = 11;
    public static final int STA_GENERIC_LOGIN_ERROR = 20;
    public static final int STA_NICK_INVALID = 21;
    public static final int STA_NICK_TAKEN = 22;
    public static final int STA_INVALID_PASSWORD = 23;
    public static final int STA_CID_TAKEN = 24;
    public static final int STA_ACCESS_DENIED = 25;
    public static final int STA_REG_ONLY = 26;
    public static final int STA_INVALID_PID = 27;
    public static final int STA_GENERIC_KICK_DISCONNECT_BAN = 30;
    public static final int STA_PERMANENTLY_BANNED = 31;
    public static final int STA_TEMP_BANNED = 32;
    public static final int STA_GENERIC_PROTOCOL_ERROR = 40;
    public static final int STA_TRANSFER_PROTOCOL_ERROR = 41;
    public static final int STA_DIRECT_CONNECT_FAILED = 42;
    public static final int STA_REQUIRED_INF_FIELD_BAD_MISSING = 43;
    public static final int STA_INVALID_STATE = 44;
    public static final int STA_REQUIRED_FEATURE_MISSING = 45;
    public static final int STA_INVALID_IP = 46;
    public static final int STA_NO_HASH_OVERLAP = 47;

}
