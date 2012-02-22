package ru.sincore.beans.rest.impl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import ru.sincore.ConfigurationManager;
import ru.sincore.beans.rest.Auth;
import ru.sincore.beans.rest.data.AuthRequest;
import ru.sincore.beans.rest.data.AuthResponse;
import ru.sincore.beans.rest.data.Constants;
import ru.sincore.beans.rest.utils.Session;
import ru.sincore.beans.rest.utils.SessionManager;
import ru.sincore.db.dao.ClientListDAOImpl;
import ru.sincore.db.pojo.ClientListPOJO;

/**
 * Rest Auth implementation
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 08.02.12
 *         Time: 10:09
 */
public class AuthImpl implements Auth
{
    @Context
    private HttpServletRequest request;
    @Context
    private SecurityContext    ctx;

    @Override
    public AuthResponse doAuth(AuthRequest data)
    {
        AuthResponse response  = new AuthResponse();

        String login = data.getLogin();
        String pwd   = data.getPassword();

        ConfigurationManager configInstance = ConfigurationManager.getInstance();
        try{


            if (login == null || pwd == null || login.isEmpty() || pwd.isEmpty())
            {
                response.setErrorStatus(Constants.Error.AUTH_ERROR);
                return response;
            }

            ClientListDAOImpl clientInstance = new ClientListDAOImpl();
            ClientListPOJO client = clientInstance.getClientByNick(login.trim());

            if(client == null)
            {
                response.setErrorStatus(Constants.Error.AUTH_ERROR);
                return response;
            }

            String nick = client.getNickName();
            String password = client.getPassword();
            String cid = client.getCid();
            Integer weight = client.getWeight();
            Boolean isReg = client.isRegistred();
            String  ip = client.getCurrentIp();

            if (!pwd.equals(password))
            {
                response.setErrorStatus(Constants.Error.AUTH_ERROR);
                return response;
            }

            Session session = SessionManager.newSession(request);

            session.set("nick", nick);
            session.set("pwd", password.hashCode());
            session.set("cid", cid);
            session.set("ip", ip);
            session.set("weight", weight);
            session.set("isreg", isReg);
            session.set("console_ip", request.getRemoteAddr());

            System.out.println("IP: " + request.getRemoteAddr());

            response.setToken(session.getToken());

            if (weight >= configInstance.getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER))
            {
                response.setUserStatus(Constants.UserStatus.SUPER_USER);
            }
            else if (weight >= configInstance.getInt(ConfigurationManager.CLIENT_WEIGHT_UNREGISTRED))
            {
                response.setUserStatus(Constants.UserStatus.REGISTERED_USER);
            }
            else
            {
                response.setUserStatus(Constants.UserStatus.UNREGISTERED_USER);
            }

        }
        finally
        {
        }

        return response;
    }
}
