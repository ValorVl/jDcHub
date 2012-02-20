package ru.sincore.beans.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ru.sincore.beans.rest.data.HelloWorld;

/**
 * Class/file description
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 03.02.12
 *         Time: 12:52
 */
@Path("/ws/hello")
public class HelloWorldService
{
    @GET
    @Path("/world")
    @Produces({ MediaType.APPLICATION_JSON, "application/*+json" })
    public Response answer()
    {
        HelloWorld data = new HelloWorld();
        data.setText("Hello, world!");
        return Response.status(200).entity(data).build();
    }

    @GET
    @Path("/world")
    @Produces({"text/plain"})
    public Response answerText()
    {
        String data = "Hello, world!";
        return Response.status(200).entity(data).build();
    }
}
