package ru.sincore.i18n;

import org.apache.log4j.Logger;
import ru.sincore.ConfigurationManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class load all localized string messages
 *
 * @author Valor
 */
public class Messages
{
    private static final Logger log = Logger.getLogger(Messages.class);
    private static final ConfigurationManager configurationManager = ConfigurationManager.instance();
    private static final String defaultLocale = configurationManager.getString(ConfigurationManager.HUB_DEFAULT_LOCALE);

    // define server message var
    private static final String SERVER_MESSAGE_RESOURCE  = "servermessages";
    private static final String SERVER_MESSAGE_DIRECTORY = "./etc/messages/";

    // Server messages
    public static final String SERVER_MESSAGE_STUB    = "core.server.message.stub";
    public static final String RESTART_HUB            = "core.server.message.restart_hub";
    public static final String CLOSE_HUB              = "core.server.message.close_hub";
    public static final String ACCOUNT_REGISTER       = "core.server.message.account_register";
    public static final String REGISTER_CID           = "core.server.message.register_cid";
    public static final String SERVER_STARTUP         = "core.server.message.startup";
    public static final String SERVER_STARTUP_DONE    = "core.server.message.startup_done";
    public static final String SEARCH_IN_PROGRESS     = "core.server.message.search_in_progress";
    public static final String CID_UNBANNED           = "core.server.message.cid_unbanned";
    public static final String CID_NOT_BANNED         = "core.server.message.cid_not_banned";
    public static final String USER_REGISTERED        = "core.server.message.user_registered";
    public static final String DONE                   = "core.server.message.done";


    // Client messages
    public static final String REG_MESSAGE            = "core.reg_message";
	public static final String REG_FAIL_MESSAGE       = "core.reg_fail_message";
    public static final String BAN_MESSAGE            = "core.ban_message";
    public static final String HUB_FULL_MESSAGE       = "core.hub_is_full";
    public static final String SEARCH_SPAM_MESSAGE    = "core.search_spam_detected";
    public static final String LOGIN_ERROR_MESSAGE    = "core.login_error_message";
    public static final String TIGER_ERROR            = "tiger.error";


    private static Map<String, ResourceBundle> resourcesMap =
                                                    new ConcurrentHashMap<String, ResourceBundle>();

    // Empty constructor
    private Messages()
    {
    }


    /**
     * Return string message by key in default server locale
     * @param key       string key
     * @return          string message by key or key value if message does not found
     */
    public static String get(String key)
    {
        return get(key, defaultLocale);
    }


    /**
     * Return string message by key in given localeString
     * @param key       string key
     * @param localeString    locate in forms like ru_RU, en_US or so on
     * @return          string message by key or key value if message does not found
     */
    public static String get(String key, String localeString)
    {
        String result;

        if (localeString == null || localeString.isEmpty())
        {
            localeString = defaultLocale;
        }

        if (!resourcesMap.containsKey(localeString))
        {
            try
            {
                loadResources(localeString);
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
                return key;
            }
        }

        ResourceBundle resourceBundle = resourcesMap.get(localeString);

        try
        {
            result = resourceBundle.getString(key);
        }
        catch (MissingResourceException e)
        {
            e.printStackTrace();
            result = key;
        }

        return result;
    }


    /**
     * Return string message with given replacements in default locale
     * @param key       message key
     * @param values    values for replasement of placeholders like {0}, {1}...{n} {@link MessageFormat}
     * @return message in given locale with resolved placeholders
     */
    public static String get(String key, Object values)
    {
        return get(key, values, defaultLocale);
    }


    /**
     * Return string message with given replacements in given locale
     * @param key           message key
     * @param values        values for replasement of placeholders like {0}, {1}...{n} {@link MessageFormat}
     * @param localeString  locale string, like 'ru_RU' or 'en_US' or 'ru' or 'en'
     * @return message in given locale with resolved placeholders
     */
    public static String get(String key, Object values, String localeString)
    {
        if (localeString == null || localeString.isEmpty())
        {
            localeString = defaultLocale;
        }

        String[] localeElements = localeString.split("_");
        Locale   locale;
        if (localeElements.length == 1)
        {
            locale = new Locale(localeElements[0]);
        }
        else
        {
            locale = new Locale(localeElements[0], localeElements[1]);
        }

        String pattern = get(key, localeString);
        MessageFormat messageFormat = new MessageFormat(pattern, locale);

        return messageFormat.format(values);
    }


    /**
     * Load resources for given locale
     * @param localeString           locale string in forms like 'ru_RU', 'en_US', 'ru', 'en'
     * @throws MalformedURLException if resources directories is incorrect
     */
    private static void loadResources(String localeString)
            throws MalformedURLException
    {
        String[] localeElements = localeString.split("_");
        Locale   loc;
        if (localeElements.length == 1)
        {
            loc = new Locale(localeElements[0]);
        }
        else
        {
            loc = new Locale(localeElements[0], localeElements[1]);
        }

        File           serverMessagesDirectory = new File(SERVER_MESSAGE_DIRECTORY);
        File           clientMessagesDirectory = new File(ConfigurationManager.instance()
                                                          .getString(ConfigurationManager.HUB_MESSAGES_FILE_DIR));

        URLClassLoader classLoader;
        ClassLoader    parentClassLoader = Messages.class.getClassLoader();

        classLoader = new URLClassLoader(new URL[]{
                                                    serverMessagesDirectory.toURI().toURL(),
                                                    clientMessagesDirectory.toURI().toURL()
                                                  },
                                         parentClassLoader);

        ResourceBundle serverMessages = ResourceBundle.getBundle(SERVER_MESSAGE_RESOURCE, loc, classLoader);
        ResourceBundle clientMessages = ResourceBundle.getBundle("messages", loc, classLoader);

        ResourceBundle resourceBundle = new MergedResourceBundle(new ResourceBundle[] {serverMessages, clientMessages});
        resourcesMap.put(localeString, resourceBundle);
    }
}
