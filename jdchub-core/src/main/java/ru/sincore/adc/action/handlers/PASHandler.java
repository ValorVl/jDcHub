package ru.sincore.adc.action.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.TigerImpl.Tiger;
import ru.sincore.adc.action.actions.PAS;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * Handler for incoming PAS action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 12:19
 */
public class PASHandler extends AbstractActionHandler<PAS>
{
    private static final Logger log = LoggerFactory.getLogger(PASHandler.class);


    public PASHandler(AbstractClient sourceClient, PAS action)
    {
        super(sourceClient, action);
    }


    @Override
    public void handle()
            throws STAException
    {
        try
        {
            action.tryParse();

            String receivedPassword = action.getPassword();
            String clientPassword   = client.getPassword();
            String calculatedPassword;
            byte[] salt             = Base32.decode(client.getEncryptionSalt());


            if (clientPassword == null || clientPassword.equals(""))
            {
                // Error: user does not exists
                loginError();
            }

            byte[] passwordBytes        = new byte[clientPassword.getBytes().length + salt.length];

            log.debug("Pass size: " + clientPassword.getBytes().length);
            log.debug("Salt size: " + salt.length);
            log.debug("Salt value: " + salt);

            System.arraycopy(clientPassword.getBytes(), 0, passwordBytes, 0,                                clientPassword.getBytes().length);
            System.arraycopy(salt, 0, passwordBytes, clientPassword.getBytes().length, salt.length);

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
                // Password match
                client.onLoggedIn();
                client.onConnected();
            }
            else
            {
                // Error: password does not match
                loginError();
            }
        }
        catch (CommandException e)
        {
            e.printStackTrace();
        }
        catch (STAException e)
        {
            e.printStackTrace();
        }
    }


    private void loginError()
            throws STAException
    {
        try
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_PASSWORD,
                         Messages.LOGIN_ERROR_MESSAGE).send();
        }
        catch (STAException e)
        {
            throw e;
        }
        finally
        {
                client.disconnect();
        }
    }
}
