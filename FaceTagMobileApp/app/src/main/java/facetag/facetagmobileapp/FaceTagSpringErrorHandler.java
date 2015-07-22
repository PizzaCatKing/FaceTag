package facetag.facetagmobileapp;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * Created by chris-ubuntu on 22/07/15.
 */
public class FaceTagSpringErrorHandler implements ResponseErrorHandler{
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = response.getStatusCode();
        return (statusCode != HttpStatus.OK && statusCode != HttpStatus.NOT_FOUND && statusCode != HttpStatus.BAD_REQUEST );
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        System.err.println("Error handling response: "+response.getRawStatusCode() + " - " + response.getStatusText());
    }
}
