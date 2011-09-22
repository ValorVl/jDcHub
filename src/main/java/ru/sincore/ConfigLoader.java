package ru.sincore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.TigerImpl.CIDGenerator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author Valor
 * @since 10.09.2011
 */
public class ConfigLoader
{

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

	private static final String HUB_CONFIG;
	private static CIDGenerator cid = new CIDGenerator();

	public static String	OP_CHAT_CID;
	public static String 	SECURITY_CID;
	static
	{
		HUB_CONFIG 		= "./etc/hub.properties";
		OP_CHAT_CID  	= cid.generate();
		SECURITY_CID 	= cid.generate();
	}

    /**
     * Hub properties
     */

	// General
	// Big static text file storage, DB or FILE, if selected FILE, needed to specify file path
	public static int		BIG_FILE_STORAGE; 				// Available DB and FILE; default FILE;
	public static String	FILE_STORAGE_LOCATION;
	public static String	ABOUT_FILE;
	public static String	LICENSE_FILE;
	public static String	MOTD_FILE;
	public static String	RULES_FILE;

	public static String	HUB_DEFAULT_LOCALE;			// Default en_US if client not present self locale, all messages must be show default locale
    public static String 	HUB_LISTEN;
    public static Integer 	HUB_PORT;
	public static Integer 	MAX_USERS;
	public static Integer   MAX_HUBS_USERS;
	public static Integer	MAX_HUBS_REGISTERED;		// ?
	public static Long 		MAX_SHARE_SIZE;
	public static Long 		MIN_SHARE_SIZE;
	public static Integer 	MAX_NICK_SIZE;
	public static Integer 	MIN_NICK_SIZE;
	public static Integer 	MIN_SLOT_COUNT;
	public static Integer 	MAX_SLOT_COUNT;
	public static Integer 	MAX_EMAIL_CHAR_COUNT;
	public static Integer 	MAX_DESCRIPTION_CHAR_COUNT; 	// Maximum description char count
	public static Integer   MAX_OP_IN_HUB;					// Maximum OP in chat (needed ?)
	public static Integer   MAX_CHAT_MESSAGE_SIZE;			// Maximum available char count in chat messages received.
    public static String 	HUB_NAME;
    public static String 	HUB_DESCRIPTION;
    public static String 	HUB_OWNER;
	public static String 	HUB_VERSION;
    public static String    HUB_GREETING;
	public static String 	NICK_CHAR;  			// Available nickname char
	public static String 	REDIRECT_URL;           // The main redirect URL to send faulty users ( or default redirects )
    public static String    HUB_SID;
	// Command engine
	public static String    OP_COMMAND_PREFIX;
	public static String    USER_COMMAND_PREFIX;


	// Search settings
	public static Integer   MIN_CHARS_SEARCH_REQUEST;
	public static Integer   MAX_CHARS_SEARCH_REQUEST;
	public static Boolean   SAVE_SEARCH_LOG;			//Save log in DB
	public static Integer	AUTOMATIC_SEARCH_INTERVAL;  //Interval between automagic searches for each user, seconds, (?)
	public static Integer   SEARCH_STEPS;				//Maximum nr of search steps allowed until reset needed       (?)
	public static Integer	SEARCH_BASE_INTERVAL;		//Logarithmic base for user searches interval, millis         (?)
	public static Integer   SEARCH_SPAM_RESET;			//Interval until search_steps is being reset, seconds		  (?)
	// Timeouts
	public static Integer 	HUB_LOGIN_TIMEOUT;
	public static Integer 	KICK_DURATION; 			// Time in millisecond to kick duration
	// Localisations
	public static String 	HUB_MESSAGES_FILE_DIR;
	public static String 	HUB_MESSAGES_LANG;
	// Chat settings
	public static int 		CHAT_REFRESH;
	public static int 		LAST_MESSAGES_COUNT; 	// History messages line count
	public static String 	OP_CHAT_NAME;
	public static String 	OP_CHAT_DESCRIPTION;
	public static String 	BOT_CHAT_NAME;
	public static String 	BOT_CHAT_DESCRIPTION;
    public static String    BOT_CHAT_SID;
	public static String 	VIP_CHAT_NAME;
	public static String 	VIP_CHAT_DESCRIPTION;
	public static String 	REG_CHAT_NAME;
	public static String 	REG_CHAT_DESCRIPTION;
    public static Boolean 	COMMAND_PM_RETURN;      // If true, return command result in private chat
	public static Boolean   MARK_REGISTRATION_ONLY;	// Mark chat or command for registration users only
	// AdcUtils Extensions
	public static String 	ADC_EXTENSION_LIST;		// space separated adc extension list, send in first message to connected client.
	public static Boolean	ENABLE_ADCS;			// Enable or disable adcs extensions support;
	public static Boolean	CERT_LOGIN;				// Mmm.. maybe it certificate authorisation ? - no forgot add in properties

	/**
	 * AdcUtils Context configuration
	 */

	public static Integer 	ADC_BMSG;
	public static Integer	ADC_DMSG;
	public static Integer	ADC_EMSG;
	public static Integer	ADC_FMSG;
	public static Integer	ADC_HMSG;

	public static Integer	ADC_BSTA;
	public static Integer	ADC_DSTA;
	public static Integer	ADC_ESTA;
	public static Integer	ADC_FSTA;
	public static Integer	ADC_HSTA;

	public static Integer	ADC_BCTM;
	public static Integer	ADC_DCTM;
	public static Integer	ADC_ECTM;
	public static Integer	ADC_FCTM;
	public static Integer	ADC_HCTM;

	public static Integer	ADC_BRCM;
	public static Integer	ADC_DRCM;
	public static Integer	ADC_ERCM;
	public static Integer	ADC_FRCM;
	public static Integer	ADC_HRCM;

	public static Integer	ADC_BINF;
	public static Integer	ADC_DINF;
	public static Integer	ADC_EINF;
	public static Integer	ADC_FINF;
	public static Integer	ADC_HINF;

	public static Integer	ADC_BSCH;
	public static Integer	ADC_DSCH;
	public static Integer	ADC_ESCH;
	public static Integer	ADC_FSCH;
	public static Integer	ADC_HSCH;

	public static Integer	ADC_BRES;
	public static Integer	ADC_DRES;
	public static Integer	ADC_ERES;
	public static Integer	ADC_FRES;
	public static Integer	ADC_HRES;

	public static Integer	ADC_BPAS;
	public static Integer	ADC_DPAS;
	public static Integer	ADC_EPAS;
	public static Integer	ADC_FPAS;
	public static Integer	ADC_HPAS;

	public static Integer	ADC_BSUP;
	public static Integer	ADC_DSUP;
	public static Integer	ADC_ESUP;
	public static Integer	ADC_FSUP;
	public static Integer	ADC_HSUP;


    public static void init()
    {
        initHub();
    }

    private static void initHub()
    {
		File hubPropertiesFile;
		FileInputStream fileInput;
		BufferedInputStream buffInput;
		Properties prop;

        try
        {
            hubPropertiesFile 		= new File(HUB_CONFIG);
            fileInput 				= new FileInputStream(hubPropertiesFile);
            buffInput 				= new BufferedInputStream(fileInput);
            prop 					= new Properties();

            prop.load(buffInput);

			HUB_PORT											= Integer.parseInt(prop.getProperty("core.listen.port","411"));
			HUB_LISTEN											= prop.getProperty("core.listen.network","0.0.0.0");
			MAX_USERS											= Integer.parseInt(prop.getProperty("core.hub.max_users","1000"));
			MAX_SHARE_SIZE										= Long.parseLong(prop.getProperty("core.hub.max_share_size","1000000"));
			MIN_SHARE_SIZE										= Long.parseLong(prop.getProperty("core.hub.min_share_size","0"));
			MAX_NICK_SIZE										= Integer.parseInt(prop.getProperty("core.hub.max_nick_size","100"));
			MIN_NICK_SIZE										= Integer.parseInt(prop.getProperty("core.hub.min_nick_size","5"));
			MIN_SLOT_COUNT										= Integer.parseInt(prop.getProperty("core.hub.min_slot_count","0"));
			MAX_SLOT_COUNT										= Integer.parseInt(prop.getProperty("core.hub.max_slot_count","1000"));
			MAX_EMAIL_CHAR_COUNT								= Integer.parseInt(prop.getProperty("core.hub.max_email_char_count","250"));
			MAX_DESCRIPTION_CHAR_COUNT							= Integer.parseInt(prop.getProperty("core.hub.max_description_char_count","1000"));
			MAX_OP_IN_HUB										= Integer.parseInt(prop.getProperty("core.hub.max_op_in_chat","10"));
			MAX_CHAT_MESSAGE_SIZE								= Integer.parseInt(prop.getProperty("core.hub.max_chat_message_size","100000"));
			HUB_NAME											= prop.getProperty("core.hub.name","");
			HUB_DESCRIPTION										= prop.getProperty("core.hub.description","");
			HUB_OWNER											= prop.getProperty("core.hub.owner","");
			HUB_VERSION											= prop.getProperty("core.hub.version","");
            HUB_GREETING                                        = prop.getProperty("core.hub.greeting", "");
			REDIRECT_URL										= prop.getProperty("core.hub.redirect_url","");
            HUB_SID                                             = prop.getProperty("core.hub.sid", "ABCD");
            if (HUB_SID.length() != 4)
            {
                log.error("core.hub.sid doesn\'t contain 4 symbols! Property will be set to default value = \'ABCD\'.");
                HUB_SID = "ABCD";
            }
			MIN_CHARS_SEARCH_REQUEST							= Integer.parseInt(prop.getProperty("core.hub.min_char_search_request","5"));
			MAX_CHARS_SEARCH_REQUEST							= Integer.parseInt(prop.getProperty("core.hub.max_char_search_request","200"));
			SAVE_SEARCH_LOG										= Boolean.parseBoolean(prop.getProperty("core.hub.save_search_log","true"));
			AUTOMATIC_SEARCH_INTERVAL							= Integer.parseInt(prop.getProperty("core.hub.automatic_search_interval","36"));
			SEARCH_STEPS										= Integer.parseInt(prop.getProperty("core.hub.search_step","6"));
			SEARCH_BASE_INTERVAL								= Integer.parseInt(prop.getProperty("core.hub.search_base_interval","2000"));
			SEARCH_SPAM_RESET									= Integer.parseInt(prop.getProperty("core.hub.search_spam_reset","300"));
			HUB_LOGIN_TIMEOUT									= Integer.parseInt(prop.getProperty("core.hub.login.timeout","60"));
			KICK_DURATION										= Integer.parseInt(prop.getProperty("core.hub.kick_duration","300"));
			LAST_MESSAGES_COUNT									= Integer.parseInt(prop.getProperty("last.messages.count","10"));
			CHAT_REFRESH										= Integer.parseInt(prop.getProperty("core.hub.chat_refresh","10"));
			HUB_MESSAGES_FILE_DIR								= prop.getProperty("core.client.messages.dir","./etc/clientmessages");
			HUB_MESSAGES_LANG									= prop.getProperty("core.client.messages.lang","EN");
			OP_CHAT_NAME										= prop.getProperty("core.hub.op_chat_name","OpChat");
			OP_CHAT_DESCRIPTION									= prop.getProperty("core.hub.op_chat_description","");
			BOT_CHAT_NAME										= prop.getProperty("core.hub.bot_chat_name","BotChat");
			BOT_CHAT_DESCRIPTION								= prop.getProperty("core.hub.bot_chat_description","");
            BOT_CHAT_SID                                        = prop.getProperty("core.hub.bot_chat_sid", "DCBA");
            if (BOT_CHAT_SID.length() != 4)
            {
                log.error("core.hub.bot_chat_sid doesn\'t contain 4 symbols! Property will be set to default value = \'DCBA\'.");
                BOT_CHAT_SID = "DCBA";
            }
			VIP_CHAT_NAME										= prop.getProperty("core.hub.vip_chat_name","VipChat");
			VIP_CHAT_DESCRIPTION								= prop.getProperty("core.hub.vip_chat_description","");
			REG_CHAT_NAME										= prop.getProperty("core.hub.reg_chat_name","RegChat");
			REG_CHAT_DESCRIPTION								= prop.getProperty("core.hub.reg_chat_description","");
			COMMAND_PM_RETURN									= Boolean.parseBoolean(prop.getProperty("core.hub.command_pm_return","true"));
			MARK_REGISTRATION_ONLY								= Boolean.parseBoolean(prop.getProperty("core.hub.mark_registration_only","true"));
			ENABLE_ADCS											= Boolean.parseBoolean(prop.getProperty("core.hub.extension.adcs.adcs_enable","false"));
			NICK_CHAR											= prop.getProperty("core.hub.nick_chars","([\\\\w\\\\W]*)");
			MAX_HUBS_USERS										= Integer.parseInt(prop.getProperty("core.hub.max_hubs_users","10"));
			MAX_HUBS_REGISTERED									= Integer.parseInt(prop.getProperty("core.hub.max_hubs_registered","10"));
			OP_COMMAND_PREFIX									= prop.getProperty("core.hub.command.engine.op_prefix","!");
			USER_COMMAND_PREFIX									= prop.getProperty("core.hub.command.engine.user_prefix","+");
			BIG_FILE_STORAGE									= Integer.parseInt(prop.getProperty("core.hub.big_file_storage","0"));
			ABOUT_FILE											= prop.getProperty("core.hub.about_file","about.%s.txt");
			LICENSE_FILE										= prop.getProperty("core.hub.license_file","locense.%s.txt");
			MOTD_FILE											= prop.getProperty("core.hub.motd_file","motd.%s.txt");
			RULES_FILE											= prop.getProperty("core.hub.rules_file","rules.%s.txt");
			FILE_STORAGE_LOCATION								= prop.getProperty("core.hub.file_storage_location","etc");
			HUB_DEFAULT_LOCALE									= prop.getProperty("core.hub.default_locale","en_US");
			ADC_EXTENSION_LIST									= prop.getProperty("core.hub.adc_extension_list","");



			// B 	 Broadcast 	 			Hub must send message to all connected clients, including the sender of the message.
			// C 	 Client message 	 	Clients must use this message type when communicating directly over TCP.
			// D 	 Direct message 	 	The hub must send the message to the target_sid user.
			// E 	 Echo message 	 		The hub must send the message to the target_sid user and the my_sid user.
			// F 	 Feature broadcast 	 	The hub must send message to all clients that support both all required (+) and no excluded (-) features named. The feature name is matched against the corresponding SU field in INF sent by each client.
			// H 	 Hub message 	 		Clients must use this message type when a message is intended for the hub only.
			// I 	 Info message 	 		Hubs must use this message type when sending a message to a client that didn't come from another client.
			// U 	 UDP message 	 		Clients must use this message type when communicating directly over UDP.


			ADC_BMSG                                            = Integer.parseInt(prop.getProperty("ADC.BMSG","1"));
			ADC_DMSG											= Integer.parseInt(prop.getProperty("ADC.DMSG","1"));
			ADC_EMSG											= Integer.parseInt(prop.getProperty("ADC.EMSG","1"));
			ADC_FMSG											= Integer.parseInt(prop.getProperty("ADC.FMSG","1"));
			ADC_HMSG											= Integer.parseInt(prop.getProperty("ADC.HMSG","1"));

			ADC_BSTA											= Integer.parseInt(prop.getProperty("ADC.BSTA","0"));
			ADC_DSTA											= Integer.parseInt(prop.getProperty("ADC.DSTA","1"));
			ADC_ESTA											= Integer.parseInt(prop.getProperty("ADC.ESTA","1"));
			ADC_FSTA											= Integer.parseInt(prop.getProperty("ADC.FSTA","0"));
			ADC_HSTA											= Integer.parseInt(prop.getProperty("ADC.HSTA","1"));

			ADC_BCTM											= Integer.parseInt(prop.getProperty("ADC.BCTM","0"));
			ADC_DCTM											= Integer.parseInt(prop.getProperty("ADC.DCTM","1"));
			ADC_ECTM											= Integer.parseInt(prop.getProperty("ADC.ECTM","1"));
			ADC_FCTM											= Integer.parseInt(prop.getProperty("ADC.FCTM","0"));
			ADC_HCTM											= Integer.parseInt(prop.getProperty("ADC.HCTM","0"));

			ADC_BRCM											= Integer.parseInt(prop.getProperty("ADC.BRCM","0"));
			ADC_DRCM											= Integer.parseInt(prop.getProperty("ADC.DRCM","1"));
			ADC_ERCM											= Integer.parseInt(prop.getProperty("ADC.ERCM","1"));
			ADC_FRCM											= Integer.parseInt(prop.getProperty("ADC.FRCM","0"));
			ADC_HRCM											= Integer.parseInt(prop.getProperty("ADC.HRCM","0"));

			ADC_BINF											= Integer.parseInt(prop.getProperty("ADC.BINF","1"));
			ADC_DINF											= Integer.parseInt(prop.getProperty("ADC.DINF","0"));
			ADC_EINF											= Integer.parseInt(prop.getProperty("ADC.EINF","0"));
			ADC_FINF											= Integer.parseInt(prop.getProperty("ADC.FINF","0"));
			ADC_HINF											= Integer.parseInt(prop.getProperty("ADC.HINF","0"));

			ADC_BSCH											= Integer.parseInt(prop.getProperty("ADC.BSCH","1"));
			ADC_DSCH											= Integer.parseInt(prop.getProperty("ADC.DSCH","1"));
			ADC_ESCH											= Integer.parseInt(prop.getProperty("ADC.ESCH","1"));
			ADC_FSCH											= Integer.parseInt(prop.getProperty("ADC.FSCH","1"));
			ADC_HSCH											= Integer.parseInt(prop.getProperty("ADC.HSCH","0"));

			ADC_BRES											= Integer.parseInt(prop.getProperty("ADC.BRES","0"));
			ADC_DRES											= Integer.parseInt(prop.getProperty("ADC.DRES","1"));
			ADC_ERES											= Integer.parseInt(prop.getProperty("ADC.ERES","1"));
			ADC_FRES											= Integer.parseInt(prop.getProperty("ADC.FRES","0"));
			ADC_HRES											= Integer.parseInt(prop.getProperty("ADC.HRES","0"));

			ADC_BPAS											= Integer.parseInt(prop.getProperty("ADC.BPAS","0"));
			ADC_DPAS											= Integer.parseInt(prop.getProperty("ADC.DPAS","0"));
			ADC_EPAS											= Integer.parseInt(prop.getProperty("ADC.EPAS","0"));
			ADC_FPAS											= Integer.parseInt(prop.getProperty("ADC.FPAS","0"));
			ADC_HPAS											= Integer.parseInt(prop.getProperty("ADC.HPAS","1"));

			ADC_BSUP											= Integer.parseInt(prop.getProperty("ADC.BSUP","0"));
			ADC_DSUP											= Integer.parseInt(prop.getProperty("ADC.DSUP","0"));
			ADC_ESUP											= Integer.parseInt(prop.getProperty("ADC.ESUP","0"));
			ADC_FSUP											= Integer.parseInt(prop.getProperty("ADC.FSUP","0"));
			ADC_HSUP											= Integer.parseInt(prop.getProperty("ADC.HSUP","1"));

		 	buffInput.close();
        }
        catch (Exception e)
        {
            log.error("Fatal error>>>", e);
			System.exit(0);
        }
    }

}
