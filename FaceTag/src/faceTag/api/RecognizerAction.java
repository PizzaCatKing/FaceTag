package faceTag.api;

import java.nio.file.Paths;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import faceTag.controllers.RecognizerController;

@Path("/recognize")
public class RecognizerAction {

	@GET
	@Path("/{imageid}/rectangles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getImage(@PathParam("imageid") String imageid, @QueryParam("userID") String username,
			@QueryParam("token") String token) {
		System.out.println(Paths.get("").toAbsolutePath().toString());
		return RecognizerController.getRectangles(username, token, imageid);

	}
}
