/*
 * Command.java
 *
 * Created on 06 martie 2007, 16:20
 *
 * DSHub AdcUtils HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.sincore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.*;
import ru.sincore.adc.action.handlers.*;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.signals.*;
import ru.sincore.signalservice.Signal;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * Provides a parsing for each AdcUtils actionName received from client, and makes the states transitions
 * Updates all information and ensures stability.
 *
 * @author Eugen Hristev
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */

public class Command
{
    private static final Logger log = LoggerFactory.getLogger(Command.class);

    private static Map<String, ActionHandlerInfo> handlers = new HashMap<String, ActionHandlerInfo>();

    static
    {
        handlers.put("MSG", new ActionHandlerInfo(MSGHandler.class, MSG.class, MsgAdcActionSignal.class));
        handlers.put("INF", new ActionHandlerInfo(INFHandler.class, INF.class, InfAdcActionSignal.class));
        handlers.put("SUP", new ActionHandlerInfo(SUPHandler.class, SUP.class, SupAdcActionSignal.class));
        handlers.put("PAS", new ActionHandlerInfo(PASHandler.class, PAS.class, PasAdcActionSignal.class));
        handlers.put("SCH", new ActionHandlerInfo(SCHHandler.class, SCH.class, SchAdcActionSignal.class));
        handlers.put("STA", new ActionHandlerInfo(STAHandler.class, STA.class, StaAdcActionSignal.class));
        handlers.put("CTM", new ActionHandlerInfo(CTMHandler.class, CTM.class, CtmAdcActionSignal.class));
        handlers.put("RCM", new ActionHandlerInfo(RCMHandler.class, RCM.class, RcmAdcActionSignal.class));
        handlers.put("RES", new ActionHandlerInfo(RESHandler.class, RES.class, null));
    }

    /**
     * Main actionName handling function.
     * @param client Client from whom actionName was recieved
     * @param rawCommand    Issued_command of String type actually identifies the given actionName
     *                      state also of type String Identifies tha state in which tha connection is,
     *                      meaning [ accordingly to arne's draft]:
     *                      PROTOCOL (feature support discovery), IDENTIFY (user identification, static checks),
     *                      VERIFY (password check), NORMAL (normal operation) and DATA (for binary transfers).
     *                      Calling function should send one of this params, that is calling function
     *                      request... Command class does not check params.
     * @throws CommandException Something wrong happend
     * @throws STAException
     */
    public static void handle(AbstractClient client, String rawCommand)
            throws CommandException, STAException
    {
        // Empty keep alive message from client
        if (rawCommand.equals(""))
        {
            return;
        }

        // Check for minimal command length... I think, this check should be removed
        if (rawCommand.length() < 4)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.INCORRECT_COMMAND).send();
            return;
        }

        try
        {
            // In this case we only check for valid MessageType
            MessageType.valueOf(rawCommand.substring(0, 1));
        }
        catch (IllegalArgumentException iae)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.INCORRECT_MESSAGE_TYPE,
                         rawCommand).send();
        }

        String actionName = rawCommand.substring(1,4);

        if (handlers.containsKey(actionName))
        {
            ActionHandlerInfo info = handlers.get(actionName);

            if (info.getHandler() == null || info.getAction() == null)
            {
                log.info("Unhandled action received: " + actionName + "[" + rawCommand + "]");
            }
            else
            {
                try
                {
                    AbstractAction        action  = info.getAction().newInstance();
                    action.setRawCommand(rawCommand);

                    // Emit signals
                    if (info.getSignal() != null)
                    {
                        try
                        {
                            Constructor signalConstructor = info.getSignal().getConstructor(AbstractClient.class, action.getClass());
                            if (signalConstructor != null)
                            {
                                signalConstructor.setAccessible(true);
                                GenericAdcActionSignal signal =
                                        (GenericAdcActionSignal) signalConstructor.newInstance(client, action);

                                Signal.emit(signal);
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    Constructor handlerConstructor =
                            info.getHandler().getConstructor(AbstractClient.class, action.getClass());


                    handlerConstructor.setAccessible(true);

                    AbstractActionHandler handler =
                            (AbstractActionHandler) handlerConstructor.newInstance(client, action);

                    handler.handle();

                }
                catch (InstantiationException e)
                {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e)
                {
                    e.printStackTrace();
                }
                catch (InvocationTargetException e)
                {
                    e.printStackTrace();
                }
                catch (NoSuchMethodException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            // Unknown action, log it?
            log.info("Unknown action received: " + actionName + "[" + rawCommand + "]");
        }

        /** calling plugins...*/
        // Publish async event
        // TODO
        // Emit sync signal
        Signal.emit(new RawAdcActionSignal(client, rawCommand));
    }



    private static class ActionHandlerInfo
    {
        private Class<? extends AbstractActionHandler>  handler;
        private Class<? extends AbstractAction>         action;
        private Class<? extends GenericAdcActionSignal> signal;

        public ActionHandlerInfo(Class<? extends AbstractActionHandler>  handler,
                                 Class<? extends AbstractAction>         action,
                                 Class<? extends GenericAdcActionSignal> signal)
        {
            this.handler = handler;
            this.action  = action;
            this.signal  = signal;
        }


        public Class<? extends AbstractActionHandler> getHandler()
        {
            return handler;
        }


        public Class<? extends AbstractAction> getAction()
        {
            return action;
        }


        public Class<? extends GenericAdcActionSignal> getSignal()
        {
            return signal;
        }
    }
}
