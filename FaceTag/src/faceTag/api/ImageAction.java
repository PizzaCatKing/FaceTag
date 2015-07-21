package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import faceTag.controllers.ImageController;

@Path("/image")
public class ImageAction {

	@GET
	@Path("/{imageid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getImage(@PathParam("imageid") String imageid, @QueryParam("userID") String username,
			@QueryParam("token") String token) {
		
		return ImageController.getImage(username, token, imageid);

	}

	@DELETE
	@Path("/{imageid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteImage(@PathParam("imageid") String imageid, @QueryParam("userID") String username,
			@QueryParam("token") String token) {
		return ImageController.deleteImage(username, token, imageid);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadImage(MultivaluedMap<String,String> multivaluedMap) {
		return ImageController.uploadImage(multivaluedMap.getFirst("userID"), multivaluedMap.getFirst("token"), 
				multivaluedMap.getFirst("title"), multivaluedMap.getFirst("base64Image"));
	}
}
