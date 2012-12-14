package ru.sincore.beans.rest.impl;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.beans.rest.UserManagment;
import ru.sincore.beans.rest.data.*;
import ru.sincore.beans.rest.utils.AccessManager;
import ru.sincore.beans.rest.utils.Session;
import ru.sincore.beans.rest.utils.SessionManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.ClientListDAO;
import ru.sincore.db.dao.ClientListDAOImpl;
import ru.sincore.db.pojo.ClientListPOJO;
import ru.sincore.util.AdcUtils;

/**
 * Class/file description
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 10.02.12
 *         Time: 11:33
 */
public class UserManagmentImpl implements UserManagment
{
    @Context
    private HttpServletRequest request;
    @Context
    private SecurityContext    ctx;

    @Override
    public Response onlineClients(UsersRequest data)
    {
        UsersResponse response = new UsersResponse();
        response.setUsers(new ArrayList<UserItem>());

        /*if (AccessManager.check(request, data, ConfigurationManager.getInstance().getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER)) == false)
        {
            response.setErrorStatus(Constants.Error.NO_ACCESS);
            return Response.status(Response.Status.FORBIDDEN).entity(response).build();
        }*/

        ClientManager manager = ClientManager.getInstance();

        for (AbstractClient client : manager.getClients())
        {
            if (data.getUserMask() == null || client.getNick().startsWith(data.getUserMask()))
            {
                UserItem item = new UserItem();

                item.setNick(AdcUtils.fromAdcString(client.getNick()));
                item.setComment(AdcUtils.fromAdcString(client.getDescription()));
                //item.setConnectionSpeed(client.getC);
                item.setEmail(AdcUtils.fromAdcString(client.getEmail()));
                item.setIp(client.getRealIP());
                item.setRegistered(client.isRegistred());
                item.setSharedFilesCount(client.getSharedFiles());
                item.setShareSize(client.getShareSize());
                item.setTag(AdcUtils.fromAdcString(client.getClientIdentificationVersion()));

                response.getUsers().add(item);
            }
        }

        return Response.ok(response).build();
    }


    @Override
    public Response registeredClients(UsersRequest data)
    {
        UsersResponse response = new UsersResponse();
        response.setUsers(new ArrayList<UserItem>());

        /*if (AccessManager.check(request, data, ConfigurationManager.getInstance().getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER)) == false)
        {
            response.setErrorStatus(Constants.Error.NO_ACCESS);
            return Response.status(Response.Status.FORBIDDEN).entity(response).build();
        }*/

        ClientListDAO dao = new ClientListDAOImpl();
        for (ClientListPOJO client : dao.getClientList(true))
        {
            if (data.getUserMask() == null || client.getNickName().startsWith(data.getUserMask()))
            {
                UserItem item = new UserItem();

                item.setNick(AdcUtils.fromAdcString(client.getNickName()));
                //item.setEmail(AdcUtils.fromAdcString(client.getEmail()));
                item.setIp(client.getRealIp());
                item.setRegistered(client.getRegistred());
                item.setSharedFilesCount(client.getSharedFilesCount());
                item.setShareSize(client.getShareSize());

                response.getUsers().add(item);
            }
        }

        return Response.ok(response).build();
    }


    @Override
    public Response setOptions(@PathParam("user") String user, SetUserOptionsRequest data)
    {
        CommonResponse response = new CommonResponse();

        /*if (AccessManager.check(request, data, ConfigurationManager.getInstance().getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER)) == false)
        {
            response.setErrorStatus(Constants.Error.NO_ACCESS);
            return Response.status(Response.Status.FORBIDDEN).entity(response).build();
        }*/

        ClientListDAO dao = new ClientListDAOImpl();
        ClientListPOJO client = dao.getClientByNick(user);

        if (client == null)
        {
            response.setErrorStatus(Constants.Error.USER_NOT_FOUND);
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        }

        if (data.getWeight() != null)
        {
            Integer weight = data.getWeight();

            if (weight < 0)
            {
                weight = 0;
            }
            else if (weight > 100)
            {
                weight = 100;
            }

            client.setWeight(weight);
        }


        if (data.getIgnoreHubFullParam() != null)
        {
            client.setOverrideFull(data.getIgnoreHubFullParam());
        }


        if (data.getIgnoreShareSizeParam() != null)
        {
            client.setOverrideShare(data.getIgnoreShareSizeParam());
        }


        if (data.getIgnoreSpamParam() != null)
        {
            client.setOverrideSpam(data.getIgnoreSpamParam());
        }

        dao.updateClient(client);

        return Response.ok(response).build();
    }


    @Override
    public Response unregister(@PathParam("user") String user, CommonRequest data)
    {
        CommonResponse response = new CommonResponse();

        /*if (AccessManager.check(request, data, ConfigurationManager.getInstance().getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER)) == false)
        {
            response.setErrorStatus(Constants.Error.NO_ACCESS);
            return Response.status(Response.Status.FORBIDDEN).entity(response).build();
        }*/

        ClientListDAO dao = new ClientListDAOImpl();
        ClientListPOJO client = dao.getClientByNick(user);

        if (client == null)
        {
            response.setErrorStatus(Constants.Error.USER_NOT_FOUND);
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        }


        client.setRegistred(false);
        dao.updateClient(client);

        return Response.ok(response).build();
    }


    @Override
    public Response delete(@PathParam("user") String user, CommonRequest data)
    {
        CommonResponse response = new CommonResponse();

        /*if (AccessManager.check(request, data, ConfigurationManager.getInstance().getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER)) == false)
        {
            response.setErrorStatus(Constants.Error.NO_ACCESS);
            return Response.status(Response.Status.FORBIDDEN).entity(response).build();
        }*/

        ClientListDAO dao = new ClientListDAOImpl();
        ClientListPOJO client = dao.getClientByNick(user);

        if (client == null)
        {
            response.setErrorStatus(Constants.Error.USER_NOT_FOUND);
            return Response.status(Response.Status.NOT_FOUND).entity(response).build();
        }

        dao.delClient(user);

        return Response.ok(response).build();
    }


    @Override
    public Response ban(@PathParam("user") String user,
                        @PathParam("type") String type,
                        KickBanRequest data)
    {
        CommonResponse response = new CommonResponse();

        if (AccessManager.check(request, data, ConfigurationManager.getInstance().getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER)) == false)
        {
            response.setErrorStatus(Constants.Error.NO_ACCESS);
            return Response.status(Response.Status.FORBIDDEN).entity(response).build();
        }

        Session session = SessionManager.getSession(request, data.getToken());

        // TODO:

        return Response.ok(response).build();
    }
}
