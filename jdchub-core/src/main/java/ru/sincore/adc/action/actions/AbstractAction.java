package ru.sincore.adc.action.actions;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;

/**
 * Base class for all ADC actions.
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 22.11.11
 *         Time: 14:19
 */
public abstract class AbstractAction
{
    private final static Logger log = LoggerFactory.getLogger(AbstractAction.class);

    // Action name, like MSG, INF
    protected String actionName = null;

    // RAW actionName cache
    protected String cache;

    // Action prefix
    protected MessageType  messageType         = MessageType.INVALID_MESSAGE_TYPE;

    protected String       sourceSID           = null;
    protected String       targetSID           = null;

    protected List<String> requiredFeatureList = new Vector<String>();
    protected List<String> excludedFeatureList = new Vector<String>();

    // Action arguments: should be processed in extension classes
    protected Queue<String> arguments           = new LinkedList<String>();
    // Storage for unparsed arguments
    protected List<String> unparsed            = new Vector<String>();

    protected boolean isParsed = false;


    public AbstractAction()
    {
        // For compose message from scratch
        cache = null;
        isParsed = true;
    }

    public AbstractAction(String rawCommand)
    {
        cache = rawCommand;
        isParsed = false;
    }


    protected String getRawCommand(Collection<String> ... argumentsList)
            throws CommandException
    {
        if (isParsed)
        {
            if (messageType == MessageType.INVALID_MESSAGE_TYPE)
            {
                throw new CommandException("Invalid message type");
            }

            StringBuilder sb = new StringBuilder(messageType.name()).append(actionName);

            switch (messageType)
            {
                case B:
                    sb.append(" ").append(sourceSID);
                    break;

                case D:
                case E:
                    sb.append(" ").append(sourceSID).append(" ").append(targetSID);
                    break;

                case F:
                    sb.append(" ").append(sourceSID).append(" ");

                    for (String feature : requiredFeatureList)
                    {
                        sb.append("+").append(feature);
                    }

                    for (String feature : excludedFeatureList)
                    {
                        sb.append("-").append(feature);
                    }

                    break;

                case H:
                case I:
                    // Ignore in this case
                    break;

                case U:
                case C:
                    // Ignore: this type of messages should not incoming/outgoing to/from HUB
                    break;
            }

            // Fill additional arguments
            for (Collection<String> arguments : argumentsList)
            {
                for (String argument : arguments)
                {
                    sb.append(" ").append(argument);
                }
            }

            for (String argument : unparsed)
            {
                sb.append(" ").append(argument);
            }

            sb.append("\n");

            return sb.toString();
        }
        else
        {
            return cache;
        }
    }


    public String getRawCommand()
            throws CommandException
    {
        return getRawCommand(arguments);
    }


    public void setRawCommand(String rawCommand)
    {
        sourceSID = null;
        targetSID = null;
        requiredFeatureList.clear();
        excludedFeatureList.clear();
        arguments.clear();
        unparsed.clear();
        isParsed = false;

        cache = rawCommand;
    }


    private String validateSid(String sid)
    {
        if (sid.length() != 4)
        {
            throw new RuntimeException("Incorrect SID: " + sid);
        }

        return sid;
    }


    /**
     * Preparse command and action name
     */
    protected void preParse()
            throws CommandException
    {
        messageType = MessageType.valueOf(cache.substring(0, 1));
        String command = cache.substring(1,4);

        if (!command.equals(this.actionName))
        {
            throw new CommandException("Incorrect raw actionName for parser. Wait: " + this.actionName + ", supplied: " + command);
        }
    }


    protected boolean parse()
            throws CommandException, STAException
    {
        if (isParsed)
        {
            return false;
        }

        preParse();

        StringTokenizer tokenizer = new StringTokenizer(cache, " ");
        tokenizer.nextToken(); // Skip message type and action name

        switch (messageType)
        {
            case B:
                log.debug("Parse source SID...");
                sourceSID = validateSid(tokenizer.nextToken());
                break;

            case D:
            case E:
                log.debug("Parse source SID...");
                sourceSID = validateSid(tokenizer.nextToken());
                log.debug("Parse target SID...");
                targetSID = validateSid(tokenizer.nextToken());
                break;

            case F:
                log.debug("Parse source SID...");
                sourceSID = validateSid(tokenizer.nextToken());

                log.debug("Parse feature list...");

                String features = tokenizer.nextToken();
                int    pos      = 0;

                log.debug("  Unparsed feature list: " + features);

                while (pos < features.length() && (features.charAt(pos) == '+' || features.charAt(pos) == '-'))
                {
                    List<String> featureList;
                    if (features.charAt(pos++) == '+')
                    {
                        featureList = requiredFeatureList;
                    }
                    else
                    {
                        featureList = excludedFeatureList;
                    }

                    String feature = features.substring(pos, pos + 4);
                    pos += 4;

                    featureList.add(feature);
                }

                log.debug("  Required feature list: " + requiredFeatureList);
                log.debug("  Excluded feature list: " + excludedFeatureList);

                break;

            case H:
            case I:
                // Ignore
                break;

            case U:
            case C:
                // Ignore it
                break;
        }

        // Unparsed action arguments
        while (tokenizer.hasMoreElements())
        {
            arguments.add(tokenizer.nextToken());
        }

        return true;
    }


    public void tryParse()
            throws CommandException, STAException
    {
        if (isParsed == false)
        {
            isParsed = parse();
        }
    }

    /**********************************************************************************************/
    /*  Getters and Setters                                                                       */
    /**********************************************************************************************/

    public MessageType getMessageType()
            throws CommandException, STAException
    {
        // Take message type from unparsed command witout full command parsing
        if (!isParsed)
        {
            preParse();
        }
        return messageType;
    }


    public void setMessageType(MessageType messageType)
            throws CommandException, STAException
    {
        tryParse();
        this.messageType = messageType;
    }


    public String getSourceSID()
            throws CommandException, STAException
    {
        tryParse();
        return sourceSID;
    }


    public void setSourceSID(String sourceSID)
            throws CommandException, STAException
    {
        tryParse();
        this.sourceSID = sourceSID;
    }


    public String getTargetSID()
            throws CommandException, STAException
    {
        tryParse();
        return targetSID;
    }


    public void setTargetSID(String targetSID)
            throws CommandException, STAException
    {
        tryParse();
        this.targetSID = targetSID;
    }


    public List<String> getRequiredFeatureList()
            throws CommandException, STAException
    {
        tryParse();
        return requiredFeatureList;
    }


    public void setRequiredFeatureList(List<String> requiredFeatureList)
            throws CommandException, STAException
    {
        tryParse();
        this.requiredFeatureList = requiredFeatureList;
    }


    public List<String> getExcludedFeatureList()
            throws CommandException, STAException
    {
        tryParse();
        return excludedFeatureList;
    }


    public void setExcludedFeatureList(List<String> excludedFeatureList)
            throws CommandException, STAException
    {
        tryParse();
        this.excludedFeatureList = excludedFeatureList;
    }
}
