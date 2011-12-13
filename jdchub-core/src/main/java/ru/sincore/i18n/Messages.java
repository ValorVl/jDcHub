package ru.sincore.i18n;

import org.apache.log4j.Logger;
import ru.sincore.ConfigurationManager;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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
    private static final String SERVER_MESSAGE_DIRECTORY = "messages/";

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
    public static final String GENERIC_KICK_DISCONNECT_BAN = "core.generic_kick_discronnect_ban_message";
    public static final String BAN_MESSAGE            = "core.ban_message";
    public static final String PERM_BAN_MESSAGE       = "core.perm_ban_message";

    public static final String HUB_FULL_MESSAGE       = "core.hub_is_full";
    public static final String SEARCH_SPAM_MESSAGE    = "core.search_spam_detected";
    public static final String LOGIN_ERROR_MESSAGE    = "core.login_error_message";
    public static final String TIGER_ERROR            = "tiger.error";
    public static final String INCORRECT_COMMAND      = "core.protocol.incorrect_command";
    public static final String INCORRECT_MESSAGE_TYPE = "core.protocol.incorrect_message_type";
    public static final String MESSAGE_TOO_LONG       = "core.protocol.message_too_long";
    public static final String INVALID_CONTEXT        = "core.protocol.invalid_context";
    public static final String WRONG_SID              = "core.protocol.wrong_sid";
    public static final String TCP_DISABLED           = "core.protocol.tcp_disabled";
    public static final String VERY_OLD_ADC           = "core.protocol.very_old_adc";
    public static final String NICK_TAKEN             = "core.protocol.nick_taken";
    public static final String CID_TAKEN              = "core.protocol.cid_taken";
    public static final String WEIRD_INFO             = "core.protocol.weird_info";
    public static final String CT_FIELD_DISALLOWED    = "core.protocol.ct_field_disallowed";
    public static final String CANT_CHANGE_PID        = "core.protocol.cant_change_pid";
    public static final String CANT_CHANGE_CID        = "core.protocol.cant_change_cid";
    public static final String REGISTERED_ONLY        = "core.protocol.registered_only";
    public static final String INVALID_PID            = "core.protocol.invalid_pid";
    public static final String INVALID_CID            = "core.protocol.invalid_cid";
    public static final String USER_NOT_FOUND         = "core.protocol.user_not_found";
    public static final String WRONG_TARGET_SID       = "core.protocol.wrong_target_sid";
    public static final String WRONG_SENDER           = "core.protocol.wrong_sender";
    public static final String WRONG_MY_SID           = "core.protocol.wrong_my_sid";
    public static final String MESSAGE_EXCEED_MAX_LENGTH = "core.protocol.message_exceed_max_length";
    public static final String INVALID_FLAG           = "core.protocol.invalid_flag";
    public static final String INVALID_FLAG_VALUE     = "core.protocol.invalid_flag_value";
    public static final String PM_RETURN_TO_SELF      = "core.protocol.pm_return_to_self";
    public static final String PM_TO_SELF             = "core.protocol.pm_to_self";
    public static final String NO_SID                 = "core.protocol.no_sid";
    public static final String NO_TARGET_SID          = "core.protocol.no_target_sid";
    public static final String HASH_FUNCTION_NOT_SELECTED = "core.protocol.hash_function_not_selected";
    public static final String BASE_FEATURE_NOT_SUPPORTED = "core.protocol.base_feature_not_supported";
    public static final String UNKNOWN_SUP_TOKEN      = "core.protocol.unknown_sup_token";
    public static final String EMPTY_PASSWORD         = "core.protocol.empty_password";
    public static final String PASSWORD_REQUIRED      = "core.protocol.password_required";
    public static final String AUTHENTICATED          = "core.protocol.authenticated";
    public static final String NICK_TOO_SMALL         = "core.protocol.nick_too_small";
    public static final String NICK_TOO_LARGE         = "core.protocol.nick_too_large";
    public static final String MISSING_FIELD          = "core.protocol.missing_field";
    public static final String WRONG_IP_ADDRESS       = "core.protocol.wrong_ip_address";
    public static final String REGISTERED_ON_MANY_HUBS = "core.protocol.registered_on_many_hubs";
    public static final String OPERATOR_ON_MANY_HUBS   = "core.protocol.operator_on_many_hubs";
    public static final String TOO_MANY_HUBS_OPEN     = "core.protocol.too_many_hubs_open";
    public static final String TOO_MANY_SLOTS         = "core.protocol.too_many_slots";
    public static final String TOO_FEW_SLOTS          = "core.protocol.too_few_slots";
    public static final String TOO_SMALL_SHARE        = "core.protocol.too_small_share";
    public static final String TOO_LARGE_SHARE        = "core.protocol.too_large_share";
    public static final String TOO_LARGE_MAIL         = "core.protocol.too_large_mail";
    public static final String TOO_LARGE_DESCRIPTION  = "core.protocol.too_large_description";

    public static final String COMMAND_REGISTERED     = "core.commands.command_registered";
    public static final String ARGUMENT_REQUIRED      = "core.commands.argument_required";
    public static final String LOW_WEIGHT             = "core.commands.low_weight";
    public static final String NICK_NOT_EXISTS        = "core.commands.nick_not_exists";
    public static final String INVALID_WEIGHT         = "core.commands.invalid_weight";
    public static final String WEIGHT_REQUIRED        = "core.commands.weight_required";
    public static final String NICK_REQUIRED          = "core.commands.nick_required";
    public static final String PASSWORDS_NOT_EQUAL    = "core.commands.passwords_not_equal";
    public static final String REGISTER_BEFOR_CHANGE_PASSWORD = "core.commands.register_befor_change_password";
    public static final String PASSWORD_CHANGED       = "core.commands.passrowd_changed";

    public static final String TIME_FORMAT            = "core.time_format";
    public static final String TIME_PERIOD_FORMAT     = "core.time_period_format";

    public static final String FORBIDDEN_WORD_USAGE   = "core.wordfilter.forbidden_word_usage";
    public static final String FORBIDDEN_WORD_USAGE_KICK_REASON = "core.wordfilter.forbidden_word_usage_kick_reason";


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

        Object[] newValues;
        if (values instanceof Object[])
        {
            newValues = (Object[])values;
        }
        else
        {
            newValues = new Object[] {values};
        }

        return messageFormat.format(newValues);
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

        File           serverMessagesDirectory = new File(ConfigurationManager.instance()
                                                          .getHubConfigDir() + "/" + SERVER_MESSAGE_DIRECTORY);
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
