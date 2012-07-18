package ru.sincore.adc.action.actions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.client.AbstractClient;

/**
 * Factory for creating instances to parsing given RAW ADC commands
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 29.11.11
 *         Time: 12:33
 */
public class ActionFactory
{
    private static final Logger log = LoggerFactory.getLogger(ActionFactory.class);
    private static Map<String, Class<? extends AbstractAction>> parsers = new ConcurrentHashMap<String, Class<? extends AbstractAction>> ();

    static
    {
        // TODO: can we do it in config file?
        parsers.put("CTM", CTM.class);
        parsers.put("GPA", GPA.class);
        parsers.put("INF", INF.class);
        parsers.put("MSG", MSG.class);
        parsers.put("PAS", PAS.class);
        parsers.put("RCM", RCM.class);
        parsers.put("SCH", SCH.class);
        parsers.put("SID", SID.class);
        parsers.put("STA", STA.class);
        parsers.put("SUP", SUP.class);
        parsers.put("RES", RES.class);
        parsers.put("NAT", NAT.class);
        parsers.put("RNT", RNT.class);
    }


    /**
     * Register new parser
     *
     * @param actionName    action name, like MSG, INF, SUP or so on
     * @param clazz         class that used to parse this commands
     */
    public static void registerParser(String actionName, Class<? extends AbstractAction> clazz)
    {
        parsers.put(actionName, clazz);
    }


    /**
     * Unregister parser
     *
     * @param actionName    action name, like MSG, INF, SUP or so on
     */
    public static void unregisterParser(String actionName)
    {
        parsers.remove(actionName);
    }


    /**
     * Automatic create parser for given RAW ADC action
     * @param rawCommand    command to parse
     *
     * @return instance of parser or null if parser does not found
     */
    public static AbstractAction createParser(String rawCommand)
    {
        String actionName = rawCommand.substring(1,4);

        AbstractAction action = null;

        if (parsers.containsKey(actionName))
        {
            Class<? extends AbstractAction> actionClass = parsers.get(actionName);
            try
            {
                Constructor constructor = actionClass.getConstructor(String.class);
                action = (AbstractAction) constructor.newInstance(rawCommand);
            }
            catch (NoSuchMethodException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }
            catch (InstantiationException e)
            {
                e.printStackTrace();
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }


        if (action == null)
        {
            log.error("Can't found parser for RAW action: " + rawCommand);
        }

        return action;
    }
}
