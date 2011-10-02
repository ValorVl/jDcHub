package ru.sincore.i18n;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import ru.sincore.ConfigurationManager;

import java.io.*;
import java.util.Map;
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
    private static final String SERVER_MESSAGE_FILE = "./etc/messages/servermessages.properties";

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
    public static final String TIGER_ERROR            = "tiger.error";


    private static Map<String, PropertiesConfiguration> messagesMap =
                                                    new ConcurrentHashMap<String, PropertiesConfiguration>();

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
     * Return string message by key in given locale
     * @param key       string key
     * @param locale    locate in forms like ru_RU, en_US or so on
     * @return          string message by key or key value if message does not found
     */
    public static String get(String key, String locale)
    {
        String result = key;

        // TODO: optimize
        if (!messagesMap.containsKey(defaultLocale))
        {
            loadServerMessages();
            loadClientMessages(locale);
        }

        if (!messagesMap.containsKey(locale))
        {
            loadClientMessages(locale);
        }

        if (messagesMap.containsKey(locale) && messagesMap.get(locale).containsKey(key))
        {
            result = messagesMap.get(locale).getString(key);
        }
        else if (messagesMap.containsKey(defaultLocale) && messagesMap.get(defaultLocale).containsKey(key))
        {
            result = messagesMap.get(defaultLocale).getString(key);
        }
        else
        {
            log.error("Can't found message for string '" + key + "' in user locale '" + locale + "' and default locale '" + defaultLocale + "'");
        }


        return result;
    }


    /**
     * Get localized client messages file
     *
     * @return return localization filename
     */
    private static String localizedClienMessageFile(String locale)
            throws FileNotFoundException
    {
        File dir = new File(ConfigurationManager.instance()
                                                .getString(ConfigurationManager.HUB_MESSAGES_FILE_DIR));
        File messageFile;

        if (dir.isDirectory() && dir.exists())
        {
            messageFile = new File(dir + "/messages." + locale);
            if (!messageFile.exists() || !messageFile.isFile())
            {
                FileNotFoundException exception =
                        new FileNotFoundException("Client messages file does not found for locale: " + locale);
                log.error(exception.getMessage());
                throw exception;
            }
        }
        else
        {
            FileNotFoundException exception =
                    new FileNotFoundException("Localized file directory is not found, create this and try again");
            log.error(exception.getMessage());
            throw exception;
        }

        log.info("Client message file: " + messageFile);

        return messageFile.toString();
    }


    /**
     * Common method for loading client and server messages
     * @param fileName      properties file with messages
     * @param locale        locale
     */
    private static void loadMessages(String fileName, String locale)
    {
        File messagesFile;
        try
        {
            messagesFile = new File(fileName);

            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(messagesFile);
            propertiesConfiguration.load();

            if (!messagesMap.containsKey(locale))
            {
                messagesMap.put(locale, new PropertiesConfiguration());
            }

            synchronized (messagesMap.get(locale))
            {
                messagesMap.get(locale).append(propertiesConfiguration);
            }
        }
        catch (Exception ex)
        {
            log.error("Can not load messages file", ex);
        }
    }


    /**
     * Load client messages in given locale
     * @param locale locate in forms like ru_RU, en_US or so on
     */
    private static void loadClientMessages(String locale)
    {
        try
        {
            loadMessages(localizedClienMessageFile(locale), locale);
        }
        catch (FileNotFoundException e)
        {
            log.error("Can not load localized client messages file", e);
        }
    }


    /**
     * Load server messages
     */
    private static void loadServerMessages()
    {
        loadMessages(SERVER_MESSAGE_FILE, defaultLocale);
    }
}
