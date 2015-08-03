package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;

import facetag.facetagmobileapp.entities.Image;
import facetag.facetagmobileapp.entities.Rectangle;
import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.entities.Token;
import facetag.facetagmobileapp.entities.User;
import facetag.facetagmobileapp.singletons.ImageTool;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;

// This class is used for generating new rectangles for an image. It should only appear after an image has been uploaded
public class RectanglesActivity extends AppCompatActivity {
    ProgressDialog ringProgressDialog;
    Token token;
    Bitmap imageBitmap; // The original image
    Bitmap rectBitmap; // A copy of the original we can edit
    Image image;
    Canvas canvas;
    ArrayList<Rectangle> rectangles;
    ArrayList<User> friends;
    Boolean generateRectangles;

    TextView titleTextView;
    ImageView imageView;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectangles);

        Intent i = getIntent();
        token = i.getParcelableExtra("token");
        image = i.getParcelableExtra("image");
        generateRectangles = i.getBooleanExtra("generate", false);
        Uri imageBitmapLocation = Uri.parse(i.getStringExtra("imageBitmapLocation"));

        try {
            imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageBitmapLocation);
        } catch (IOException e) {
            e.printStackTrace();
            e.printStackTrace();
            Context context = getApplicationContext();
            CharSequence text = "IO error reloading image!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            finish();
        }


        titleTextView = (TextView) findViewById(R.id.rectanglesTitleTextView);
        imageView = (ImageView) findViewById(R.id.rectanglesImageView);
        submitButton = (Button) findViewById(R.id.rectanglesSubmitButton);


        titleTextView.setText(image.getTitle());
        if(!token.getUserID().equals(image.getOwnerID())){
            submitButton.setVisibility(View.INVISIBLE);
        }
        else{
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new SetRectsTask(image, rectangles).execute(token);
                }
            });

            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    float[] coordsOnImage = getPointerCoords(imageView, motionEvent);

                    //Check which rectangle the user click on
                    for (Rectangle rect : rectangles) {
                        if (rect.isInside(coordsOnImage[0], coordsOnImage[1])) {
                            // In rectangle for user in rect
                            Intent i = new Intent(RectanglesActivity.this, FriendPickerActivity.class);
                            i.putExtra("users", friends);
                            i.putExtra("rectLoc", rectangles.indexOf(rect));
                            startActivityForResult(i, Globals.PICK_FRIEND_CODE);
                            break;
                        }
                    }

                    return false;
                }
            });
        }
        // Get friend profile and rectangles for image
        new getUserFriendsTask().execute(token);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Globals.PICK_FRIEND_CODE) {
            if (resultCode == RESULT_OK) {
                User user = data.getParcelableExtra("user");
                int rectLoc =  data.getIntExtra("rectLoc", -1);
                System.out.println("RET");
                Rectangle tempRect = rectangles.get(rectLoc);
                tempRect.setUserID(user.getUserID());
                if(!rectangles.get(rectLoc).getUserID().equals(user.getUserID())) {
                    rectangles.set(rectLoc, tempRect);

                }
                refreshImage();
            }
        }
    }

    private class GenerateRectsTask extends AsyncTask<Token, Void, ResponseEntity<String>> {
        Image targetImage;

        public GenerateRectsTask(Image targetImage) {
            this.targetImage = targetImage;
        }

        @Override
        protected ResponseEntity<String> doInBackground(Token... params) {
            if (params.length < 1) {
                return null;
            }
            Token thisToken = params[0];
            if (token == null) {
                return null;
            }

            if (thisToken.getUserID().equals("") || thisToken.getToken().equals("")) {
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/recognize/" + targetImage.getImageID() + "/rectangles/new")
                    .queryParam("userID", thisToken.getUserID())
                    .queryParam("token", thisToken.getToken());

            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());
            return restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {

            ringProgressDialog.dismiss();
            if (result == null) {
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Invalid token";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                finish();
            } else if (!result.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON)) {
                Context context = getApplicationContext();
                CharSequence text = "Error: Server returned status code " + result.getStatusCode().toString();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                finish();
            } else {
                if (result.getStatusCode() == HttpStatus.OK) {

                    try {
                        rectangles = ObjectMapperSingleton.getObjectMapper()
                                .readValue(result.getBody(), new TypeReference<ArrayList<Rectangle>>() {
                                });


                       refreshImage();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping token!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    }


                } else {
                    RestError error;
                    try {
                        error = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), RestError.class);
                        Context context = getApplicationContext();
                        CharSequence text = "Error: " + error.getMessage();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping error!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    }

                }
            }

        }

        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(RectanglesActivity.this, "Please wait ...", "Generating rectangles...", true);
        }
    }

    private class GetRectsTask extends AsyncTask<Token, Void, ResponseEntity<String>> {
        Image targetImage;

        public GetRectsTask(Image targetImage) {
            this.targetImage = targetImage;
        }

        @Override
        protected ResponseEntity<String> doInBackground(Token... params) {
            if (params.length < 1) {
                return null;
            }
            Token thisToken = params[0];
            if (token == null) {
                return null;
            }

            if (thisToken.getUserID().equals("") || thisToken.getToken().equals("")) {
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/recognize/" + targetImage.getImageID() + "/rectangles")
                    .queryParam("userID", thisToken.getUserID())
                    .queryParam("token", thisToken.getToken());

            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());

            return restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {

            ringProgressDialog.dismiss();
            if (result == null) {
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Invalid token";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                finish();
            } else if (!result.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON)) {
                Context context = getApplicationContext();
                CharSequence text = "Error: Server returned status code " + result.getStatusCode().toString();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                finish();
            } else {
                if (result.getStatusCode() == HttpStatus.OK) {

                    try {
                        rectangles = ObjectMapperSingleton.getObjectMapper()
                                .readValue(result.getBody(), new TypeReference<ArrayList<Rectangle>>() {
                                });

                        refreshImage();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping token!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    }


                } else {
                    RestError error;
                    try {
                        error = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), RestError.class);
                        Context context = getApplicationContext();
                        CharSequence text = "Error: " + error.getMessage();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping error!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    }

                }
            }

        }

        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(RectanglesActivity.this, "Please wait ...", "Generating rectangles...", true);
        }
    }

    private class SetRectsTask extends AsyncTask<Token, Void, ResponseEntity<String>> {
        Image targetImage;
        ArrayList<Rectangle> targetRectangles;

        public SetRectsTask(Image targetImage, ArrayList<Rectangle> targetRectangles) {
            this.targetImage = targetImage;
            this.targetRectangles = targetRectangles;
        }

        @Override
        protected ResponseEntity<String> doInBackground(Token... params) {
            if (params.length < 1) {
                return null;
            }
            Token thisToken = params[0];
            if (token == null) {
                return null;
            }

            if (thisToken.getUserID().equals("") || thisToken.getToken().equals("")) {
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            body.add("userID", thisToken.getUserID());
            body.add("token", thisToken.getToken());
            try {
                body.add("rectangles", ObjectMapperSingleton.getObjectMapper().writeValueAsString(rectangles));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());

            return restTemplate.postForEntity(Globals.SERVER_ADDRESS + "/recognize/" + targetImage.getImageID() + "/rectangles", request, String.class);
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {

            ringProgressDialog.dismiss();
            if (result == null) {
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Invalid token";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                finish();
            } else if (!result.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON)) {
                Context context = getApplicationContext();
                CharSequence text = "Error: Server returned status code " + result.getStatusCode().toString();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                finish();
            } else {
                if (result.getStatusCode() == HttpStatus.OK) {

                    try {
                        rectangles = ObjectMapperSingleton.getObjectMapper()
                                .readValue(result.getBody(), new TypeReference<ArrayList<Rectangle>>() {
                                });
                        Context context = getApplicationContext();
                        CharSequence text = "Rectangles updated!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping token!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    }


                } else {
                    RestError error;
                    try {
                        error = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), RestError.class);
                        Context context = getApplicationContext();
                        CharSequence text = "Error: " + error.getMessage();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping error!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        finish();
                    }

                }
            }

        }

        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(RectanglesActivity.this, "Please wait ...", "Generating rectangles...", true);
        }
    }

    private class getUserFriendsTask extends AsyncTask<Token, Void, ResponseEntity<String>> {

        @Override
        protected ResponseEntity<String> doInBackground(Token... params) {
            if (params.length != 1) {
                return null;
            }

            Token thisToken = params[0];


            if (token == null) {
                return null;
            }

            if (thisToken.getUserID().equals("") || thisToken.getToken().equals("")) {
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/user/" + thisToken.getUserID() + "/friend")
                    .queryParam("userID", thisToken.getUserID())
                    .queryParam("token", thisToken.getToken());

            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());

            return restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {
            ringProgressDialog.dismiss();
            if (result == null) {
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Error - unable to access token values";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                //We got a good return!
                if (result.getStatusCode() == HttpStatus.OK) try {
                    friends = ObjectMapperSingleton.getObjectMapper()
                            .readValue(result.getBody(), new TypeReference<ArrayList<User>>() {
                            });
                    new getThisUserTask().execute(token);


                } catch (IOException e) {
                    e.printStackTrace();
                    Context context = getApplicationContext();
                    CharSequence text = "IO error while mapping users!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    RestError error;
                    try {
                        error = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), RestError.class);
                        Context context = getApplicationContext();
                        CharSequence text = "Error: " + error.getMessage();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping error!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                }
            }
        }
        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(RectanglesActivity.this, "Please wait ...", "Getting Friends...", true);
        }
    }

    private class getThisUserTask extends AsyncTask<Token, Void, ResponseEntity<String>> {

        @Override
        protected ResponseEntity<String> doInBackground(Token... params) {
            if (params.length != 1) {
                return null;
            }

            Token thisToken = params[0];


            if (token == null) {
                return null;
            }

            if (thisToken.getUserID().equals("") || thisToken.getToken().equals("")) {
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/user/" + thisToken.getUserID())
                    .queryParam("userID", thisToken.getUserID())
                    .queryParam("token", thisToken.getToken());

            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());

            return restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {
            ringProgressDialog.dismiss();
            if (result == null) {
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Error - unable to access token values";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                //We got a good return!
                if (result.getStatusCode() == HttpStatus.OK) try {
                    User us = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), User.class);
                    friends.add(us);
                    friends.add(new User());

                    if(generateRectangles){
                        new GenerateRectsTask(image).execute(token);
                    }else{
                       new GetRectsTask(image).execute(token);
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                    Context context = getApplicationContext();
                    CharSequence text = "IO error while mapping users!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else {
                    RestError error;
                    try {
                        error = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), RestError.class);
                        Context context = getApplicationContext();
                        CharSequence text = "Error: " + error.getMessage();
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping error!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                }
            }

        }

        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(RectanglesActivity.this, "Please wait ...", "Getting users...", true);
        }
    }

    private void refreshImage(){
        rectBitmap = imageBitmap.copy(imageBitmap.getConfig(), true);
        canvas = new Canvas(rectBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(imageView.getHeight() / 60);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(imageView.getHeight() / 10);

        for (Rectangle rect : rectangles) {
            canvas.drawRect(rect.getX1(), rect.getY1(), rect.getX2(), rect.getY2(), paint);
            if(rect.getUserID() != null) {
                if (!rect.getUserID().equals("")) {
                    canvas.drawText(getFriendNameWithID(rect.getUserID()), rect.getX1() + 10, rect.getY1() + imageView.getHeight() / 10, textPaint);
                }
            }
        }

        imageView.setImageBitmap(ImageTool.resize(rectBitmap, imageView.getWidth(), imageView.getHeight()));
        imageView.invalidate();
    }

    private String getFriendNameWithID(String id) {
        for (User friend : friends) {
            if (friend.getUserID().equals(id))
                return friend.getName();
        }
        return "";
    }

    private float[] getPointerCoords(ImageView view, MotionEvent e) {
        float[] result = new float[2];
        int intrinsicHeight = imageBitmap.getHeight();
        int intrinsicWidth = imageBitmap.getWidth();
        int scaledHeight = view.getHeight();
        int scaledWidth = view.getWidth();
        float heightRatio = intrinsicHeight / (float)scaledHeight;
        float widthRatio = intrinsicWidth / (float)scaledWidth;
        result[0] = e.getX() * widthRatio;
        result[1] = e.getY() * heightRatio;
        return  result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rectangles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Force get new rectangles
        if (id == R.id.action_settings) {
            new GenerateRectsTask(image).execute(token);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
