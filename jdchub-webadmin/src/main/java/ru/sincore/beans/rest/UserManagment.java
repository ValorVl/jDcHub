package ru.sincore.beans.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import ru.sincore.beans.rest.data.CommonRequest;
import ru.sincore.beans.rest.data.KickBanRequest;
import ru.sincore.beans.rest.data.SetUserOptionsRequest;
import ru.sincore.beans.rest.data.UsersRequest;

/**
 * JDCHub REST services for user managment
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 09.02.12
 *         Time: 12:22
 */
@Path("/rest/users")
public interface UserManagment
{
    /**
     * List or search by mask online users service
     * @param data  service request data, if <code>mask</code> is set used as search by nick
     * @return  service response data
     */
    @POST
    @Path("/online")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public Response onlineClients(UsersRequest data);

    /**
     * List or search by mask registered users service
     * @param data  service request data, if <code>mask</code> is set used as search by nick
     * @return  service response data
     */
    @POST
    @Path("/registered")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public Response registeredClients(UsersRequest data);

    // Actions

    /**
     * Set user options params: weight, ignore hub full, ignore share size, ignore spam
     * @param user      user nick
     * @param data      request data
     * @return request response
     */
    @POST
    @Path("/{user}/set_options")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public Response setOptions(@PathParam("user") String user, SetUserOptionsRequest data);

    /**
     * Unregister given user
     * @param user      user nick
     * @param data      request data
     * @return request response
     */
    @POST
    @Path("/{user}/unregister")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public Response unregister(@PathParam("user") String user, CommonRequest data);

    /**
     * Delete user service
     * @param user      user nick
     * @param data      request data
     * @return request response
     */
    @POST
    @Path("/{user}/delete")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public Response delete(@PathParam("user") String user, CommonRequest data);

    /**
     * Bun or kick user service
     * @param user      user nick
     * @param data      request data
     * @return request response
     */
    @POST
    @Path("/{user}/kick_ban/{type}")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public Response ban(@PathParam("user") String user,
                        @PathParam("type") String type,
                        KickBanRequest data);

    /*
    @POST
    @Path("/{user}/mute")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public Response mute(@PathParam("user") String user, String data);
    */

    /*
    @POST
    @Path("/{user}/notransfer")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public Response notransfer(@PathParam("user") String user, String data);
    */
}
