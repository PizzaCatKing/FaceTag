package faceTag.controllers;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import faceTag.entities.ErrorCode;
import faceTag.entities.Image;
import faceTag.entities.Rectangle;
import faceTag.mongo.ImageCollectionManager;
import faceTag.recognizer.RecognizerInterface;

public class RecognizerController {

	// Get rectangles for iamge
		public static Response getRectangles(String _id, String token, String imageID) {
			
			if (!(StringTool.isValid(_id) && StringTool.isValid(token) && StringTool.isValid(imageID)
					&& StringTool.isValidObjectID(_id) && StringTool.isValidObjectID(imageID))) {
				BasicDBObject toReturn = new BasicDBObject();
				toReturn.put("message", "Invalid Parameters");
				toReturn.put("error", ErrorCode.ERROR_BAD_PARAMETERS);

				return Response.status(Response.Status.BAD_REQUEST).entity(JSON.serialize(toReturn))
						.type(MediaType.APPLICATION_JSON).build();
			}
			Response tokenValidation = TokenController.validateToken(_id, token);
			if (tokenValidation != null) {
				return tokenValidation;
			}
			
			Image image = ImageCollectionManager.getImage(new ObjectId(imageID));
			if (image == null) {
				// Image doesn't exist
				BasicDBObject toReturn = new BasicDBObject();
				toReturn.put("message", "The requested resource can't be found");
				toReturn.put("error", ErrorCode.ERROR_RESOURCE_NOT_FOUND);

				return Response.status(Response.Status.NOT_FOUND).entity(JSON.serialize(toReturn))
						.type(MediaType.APPLICATION_JSON).build();
			}
			
			Rectangle[] result = RecognizerInterface.getInstance().detectForRectangles(faceTag.entities.Globals.FILES_ROOT, image.getID().toHexString());
			RecognizerInterface.getInstance().swag();
			BasicDBList rectangles = new BasicDBList();
			for (Rectangle rect : result) {
				BasicDBObject rectangleToSerialize = new BasicDBObject(rect.toMap());
				rectangleToSerialize.remove("_id");
				rectangleToSerialize.put("imageID", imageID);
				rectangleToSerialize.put("ownerID", _id);
				rectangles.add(rectangleToSerialize);
			}
			return Response.ok(JSON.serialize(rectangles), MediaType.APPLICATION_JSON).build();
		}
}
