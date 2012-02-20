package ru.sincore.beans.rest.impl;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.beans.rest.Info;
import ru.sincore.beans.rest.data.*;
import ru.sincore.db.dao.ClientListDAOImpl;

/**
 * jDCHub info implementation
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 08.02.12
 *         Time: 15:59
 */
public class InfoImpl implements Info
{
    @Context
    private HttpServletRequest request;
    @Context
    private SecurityContext    ctx;

    @Override
    public HubInfoResponse hubInfo(CommonRequest data)
    {
        HubInfoResponse response = new HubInfoResponse();

        /*if (AccessManager.check(request, data) == false)
        {
            response.setError(Constants.Errors.NO_ACCESS);
            response.setMessage(Constants.Errors.NO_ACCESS_TEXT);
            return response;
        }*/

        ConfigurationManager configInstance = ConfigurationManager.instance();
        ClientListDAOImpl clientList = new ClientListDAOImpl();

        response.setName(configInstance.getString(ConfigurationManager.HUB_NAME));
        response.setDescription(configInstance.getString(ConfigurationManager.HUB_DESCRIPTION));
        response.setVersion(configInstance.getString(ConfigurationManager.HUB_VERSION));
        response.setShareSize(ClientManager.getInstance().getTotalShare() / (1024 * 1024));
        response.setSharedFilesCount(ClientManager.getInstance().getTotalFileCount());
        response.setOnlineUsersCount(ClientManager.getInstance().getClientsCount());

        return response;
    }


    @Override
    public RuntimeInfoResponse runtimeInfo(CommonRequest data)
    {
        return new RuntimeInfoResponse();
    }


    @Override
    public JvmInfoResponse jvmInfo(CommonRequest data)
    {
        return new JvmInfoResponse();
    }
}
