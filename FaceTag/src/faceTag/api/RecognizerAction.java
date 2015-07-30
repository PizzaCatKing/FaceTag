package faceTag.api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import faceTag.controllers.RecognizerController;

@Path("/recognize")
public class RecognizerAction {

	@GET
	@Path("/{imageid}/rectangles")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRectangles(@PathParam("imageid") String imageid, @QueryParam("userID") String userID,
			@QueryParam("token") String token) {
		
		return RecognizerController.getRectangles(userID, token, imageid);

	}
	
	// Manually changes the values of the rectangle (based off of rectID)
	// Changes are an array of Rect objects (with rectIDs)
	@POST
	@Path("/{imageid}/rectangles")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setRectangles(@PathParam("imageid") String imageid, MultivaluedMap<String, String> multivaluedMap) {
		
		String userID = null;
		String token = null;
		String rectangles = null;
		if (multivaluedMap != null) {
			if (multivaluedMap.containsKey("userID")) {
				userID = multivaluedMap.getFirst("userID");
			}

			if (multivaluedMap.containsKey("token")) {
				token = multivaluedMap.getFirst("token");
			}
			
			if (multivaluedMap.containsKey("rectangles")) {
				rectangles = multivaluedMap.getFirst("rectangles");
			}
		}
		System.out.println("Token: " + token + " id" + userID + " rect: " + rectangles);
		return RecognizerController.setRectangles(userID, token, imageid, rectangles);
	}
	
	// Generates new rectangles and gets determined faces from recognizer - deletes any old data if successful
	// User must be he owner of the image
	@GET
	@Path("/{imageid}/rectangles/new")
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateRectangles(@PathParam("imageid") String imageid, @QueryParam("userID") String userID,
			@QueryParam("token") String token) {
		
		return RecognizerController.getNewRectangles(userID, token, imageid);
	}
}
