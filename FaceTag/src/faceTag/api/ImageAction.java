package faceTag.api;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/image")
public class ImageAction {

	@GET
	@Path("/{imageid}")
	@Produces("image/jpeg")
	public Response getImage(@PathParam("imageid") String imageid, @QueryParam("username") String username,
			@QueryParam("token") String token) {
		// TokenManager.validateToken(username,token);
		if (imageid == null) {
			throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
		}
		if (imageid.equals("")) {
			throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
		}

		File imageFile = new File("./images/" + imageid);
		BufferedImage image;

		try {

			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "jpg", baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] imageData = baos.toByteArray();

		return Response.ok(imageData).build();

	}

	@DELETE
	@Path("/{imageid}")
	public void deleteImage(@PathParam("imageid") String imageid, @QueryParam("username") String username,
			@QueryParam("token") String token) {
		// TokenManager.validateToken(username,token);
		if (imageid == null) {
			throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
		}
		if (imageid.equals("")) {
			throw new WebApplicationException(Response.Status.METHOD_NOT_ALLOWED);
		}

		File imageFile = new File("./images/" + imageid);
		if (!imageFile.exists()) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		}
		imageFile.delete();

	}

	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public void uploadImage(@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail, @QueryParam("username") String username,
			@QueryParam("token") String token) {
		// TokenManager.validateToken(username,token);
		if (uploadedInputStream == null || fileDetail == null) {
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		OutputStream os = null;
		try {
			File imageDir = new File("./images");
			if (!imageDir.exists()) {
				// Create dir
				imageDir.mkdir();
			}
			// Get name for new file
			File image = new File("./images/" + "imageHAHAHA");
			os = new FileOutputStream(image);
			byte[] b = new byte[2048];
			int length;
			while ((length = uploadedInputStream.read(b)) != -1) {
				os.write(b, 0, length);
			}
		} catch (IOException ex) {
			Logger.getGlobal().log(Level.SEVERE, null, ex);
		} finally {
			try {
				os.close();
			} catch (IOException ex) {
				Logger.getGlobal().log(Level.SEVERE, null, ex);
			}
		}

	}
}
