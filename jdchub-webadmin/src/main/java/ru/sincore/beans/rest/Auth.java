package ru.sincore.beans.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import ru.sincore.beans.rest.data.AuthRequest;
import ru.sincore.beans.rest.data.AuthResponse;

/**
 * JDCHub REST Auth service
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 07.02.12
 *         Time: 15:10
 */
@Path("/rest")
public interface Auth
{
    /**
     * Auth service
     *
     * @param data  service data
     * @return  response with valid data
     */
    @POST
    @Path("/auth")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public AuthResponse doAuth(AuthRequest data);
}
