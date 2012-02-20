package ru.sincore.beans.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import ru.sincore.beans.rest.data.CommonRequest;
import ru.sincore.beans.rest.data.HubInfoResponse;
import ru.sincore.beans.rest.data.JvmInfoResponse;
import ru.sincore.beans.rest.data.RuntimeInfoResponse;

/**
 * Various jDCHub info REST services
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 08.02.12
 *         Time: 15:11
 */
@Path("/rest/info")
public interface Info
{
    /**
     * Hub info service
     * @param data  service request data
     * @return  service response data
     */
    @POST
    @Path("/hub")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public HubInfoResponse hubInfo(CommonRequest data);

    /**
     * Runtime info service
     * @param data  service request data
     * @return  service response data
     */
    @POST
    @Path("/runtime")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public RuntimeInfoResponse runtimeInfo(CommonRequest data);

    /**
     * JVM info service
     * @param data  service request data
     * @return  service response data
     */
    @POST
    @Path("/jvm")
    @Produces({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, "application/*+json", MediaType.APPLICATION_XML})
    public JvmInfoResponse jvmInfo(CommonRequest data);
}
