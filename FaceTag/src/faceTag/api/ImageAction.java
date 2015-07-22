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
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteImage(@PathParam("imageid") String imageid, MultivaluedMap<String, String> multivaluedMap) {
		String userID = null;
		String token = null;

		if (multivaluedMap != null) {
			if (multivaluedMap.containsKey("userID")) {
				userID = multivaluedMap.getFirst("userID");
			}
			if (multivaluedMap.containsKey("token")) {
				token = multivaluedMap.getFirst("token");
			}
		}
		return ImageController.deleteImage(userID, token, imageid);
	}

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response uploadImage(MultivaluedMap<String, String> multivaluedMap) {
		String userID = null;
		String base64Image = null;
		String title = null;
		String token = null;
		
		if (multivaluedMap != null) {
			if (multivaluedMap.containsKey("userID")) {
				userID = multivaluedMap.getFirst("userID");
			}

			if (multivaluedMap.containsKey("token")) {
				token = multivaluedMap.getFirst("token");
			}

			if (multivaluedMap.containsKey("title")) {
				title = multivaluedMap.getFirst("title");
			}

			if (multivaluedMap.containsKey("base64Image")) {
				base64Image = multivaluedMap.getFirst("base64Image");
			}
		}
		return ImageController.uploadImage(userID, token, title, base64Image);
	}
}
