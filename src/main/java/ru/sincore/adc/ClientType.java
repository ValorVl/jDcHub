package ru.sincore.adc;

/**
 * Client types that transmited with flag CT in INF Action
 * </p>
 * CT type is bit mask, so values can be combined with bitwise OR:<br/>
 *   CT = BOT | OPERATOR
 *
 * @author hatred
 *         <p/>
 *         Date: 13.10.11
 *         Time: 17:46
 */
public class ClientType
{
    public static int BOT             = 1;
    public static int REGISTERED_USER = 2;
    public static int OPERATOR        = 4;
    public static int SUPER_USER      = 8;
    public static int HUB_OWNER       = 16;
    public static int HUB             = 32;
}
