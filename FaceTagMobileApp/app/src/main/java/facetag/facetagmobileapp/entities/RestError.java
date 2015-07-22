package facetag.facetagmobileapp.entities;

/**
 * Created by chris-ubuntu on 22/07/15.
 */
public class RestError {
    private String message;
    private int error;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
