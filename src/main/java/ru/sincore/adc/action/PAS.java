package ru.sincore.adc.action;

import java.util.Arrays;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Broadcast;
import ru.sincore.Client;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.TigerImpl.Tiger;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * Class/file description
 *
 * @author hatred
 *         <p/>
 *         Date: 30.09.11
 *         Time: 15:28
 */
public class PAS extends Action
{
    private static final Logger log = LoggerFactory.getLogger(PAS.class);

    ConfigurationManager configurationManager = ConfigurationManager.instance();

    public PAS(MessageType messageType, int context, Client fromClient, Client toClient)
    {
        super(messageType, context, fromClient, toClient);
        availableContexts = Context.T;
        availableStates   = State.VERIFY;
    }


    public PAS(MessageType messageType, int context, Client client)
    {
        this(messageType, context, client, (Client) null);
    }


    public PAS(MessageType messageType, int context, Client client, String rawCommand)
        throws CommandException, STAException
    {
        this(messageType, context, client);
        this.rawCommand = rawCommand;
        parse(rawCommand);
    }

    @Override
    public String toString()
    {
        return null;
    }

    @Override
    protected void parseIncoming()
            throws STAException
    {
        switch (messageType)
        {
            case H:
            {
                handleCommand();
                break;
            }

            default:
                break;
        }
    }

    private void handleCommand()
            throws STAException
    {
        StringTokenizer tokenizer = new StringTokenizer(rawCommand, " ");
        tokenizer.nextToken(); // Skip command prefix (HPAS)

        String receivedPassword = tokenizer.nextToken();
        String clientPassword   = fromClient.getClientHandler().getPassword();
        String calculatedPassword;
        byte[] salt             = Base32.decode(fromClient.getClientHandler().getEncryptionSalt());


        if (clientPassword == null || clientPassword.equals(""))
        {
            // Error: user does not exists
            loginError();
        }

        byte[] passwordBytes        = new byte[clientPassword.getBytes().length + salt.length];

        log.debug("Pass size: "  + clientPassword.getBytes().length);
        log.debug("Salt size: "  + salt.length);
        log.debug("Salt value: " + salt);

        System.arraycopy(clientPassword.getBytes(), 0, passwordBytes, 0,                                clientPassword.getBytes().length);
        System.arraycopy(salt,                      0, passwordBytes, clientPassword.getBytes().length, salt.length);

        // Get Tiger hash
        Tiger tiger = new Tiger();
        tiger.engineReset();
        tiger.init();

        tiger.update(passwordBytes, 0, passwordBytes.length);

        // Base32 encoded calculated password hash
        calculatedPassword = Base32.encode(tiger.engineDigest());

        log.debug("Passwords: " + receivedPassword + ", " + calculatedPassword);

        if (receivedPassword.equals(calculatedPassword))
        {
            // Password math
            fromClient.onLoggedIn();
            fromClient.onConnected();
        }
        else
        {
            // Error: password does not math
            loginError();
        }
    }


    private void loginError()
            throws STAException
    {
        try
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_PASSWORD,
                         Messages.get(Messages.LOGIN_ERROR_MESSAGE));
        }
        catch (STAException e)
        {
            throw e;
        }
        finally
        {
            fromClient.getClientHandler().getSession().close(true);
        }
    }
}
