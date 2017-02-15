package uk.q3c.rest.base;

import com.google.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
@Produces(MediaType.TEXT_PLAIN)
@Api(value = "hello", description = "Endpoint for Hello specific operations")
public class MessageResource2 {

    private final SampleInjectee sampleInjectee;

    @Inject
    public MessageResource2(SampleInjectee sampleInjectee) {
        this.sampleInjectee = sampleInjectee;
    }

    @GET
    @Path("/{param}")
    @ApiOperation(value = "Returns param", notes = "Returns param", response = MessageResource2.class)
    @ApiResponses(@ApiResponse(code = 200, message = "Successful retrieval of param value", response = MessageResource2.class))
    public Response printMessage(@PathParam("param") String msg) {
        String result = "A very loud hello from " + sampleInjectee.getBlip() + "  " + msg + "!";
        return Response.status(200).entity(result).build();
    }
}