package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;

import facetag.facetagmobileapp.entities.Image;
import facetag.facetagmobileapp.entities.Rectangle;
import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.entities.Token;
import facetag.facetagmobileapp.singletons.ImageTool;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;

public class ImageViewActivity extends AppCompatActivity {
    ProgressDialog ringProgressDialog;
    Token token;
    Bitmap imageBitmap;
    Bitmap rectBitmap;
    Image image;
    Canvas canvas;

    TextView titleTextView;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Intent i = getIntent();
        token = i.getParcelableExtra("token");
        image = i.getParcelableExtra("image");

        titleTextView = (TextView) findViewById(R.id.imageViewTitleTextView);
        imageView = (ImageView) findViewById(R.id.imageViewImageView);

        titleTextView.setText(image.getTitle());
        imageView.setImageBitmap(rectBitmap);

        new GetImageTask(image).execute(token);

    }


    private class GetImageTask extends AsyncTask<Token, Void, ResponseEntity<String>> {
        Image targetImage;
        public GetImageTask(Image targetImage){
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

            if(thisToken.getUserID().equals("") || thisToken.getToken().equals("")){
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/image/"+targetImage.getImageID())
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
            } else {
                if (result.getStatusCode() == HttpStatus.OK) {
                    //We got a good return!

                    try {
                        // We have a valid token - go to main menu
                        image = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), Image.class);
                        new ConvertImageTask().execute(image.getBase64Image());

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

            ringProgressDialog = ProgressDialog.show(ImageViewActivity.this, "Please wait ...", "Logging in ...", true);
        }
    }

    private class GetRectsTask extends AsyncTask<Token, Void, ResponseEntity<String>> {
        Image targetImage;
        public GetRectsTask(Image targetImage){
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

            if(thisToken.getUserID().equals("") || thisToken.getToken().equals("")){
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/recognize/"+targetImage.getImageID() + "/rectangles")
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
            }
            else if(!result.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON)){
                Context context = getApplicationContext();
                CharSequence text = "Error: Server returned status code " + result.getStatusCode().toString();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                finish();
            }
            else {
                if (result.getStatusCode() == HttpStatus.OK) {

                    try {
                        ArrayList<Rectangle> rectangles = ObjectMapperSingleton.getObjectMapper()
                                .readValue(result.getBody(), new TypeReference<ArrayList<Rectangle>>() {
                                });


                        Paint paint = new Paint();
                        paint.setColor(Color.GREEN);
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(2);

                        for(Rectangle rect : rectangles){
                            canvas.drawRect(rect.getX1(), rect.getY1(), rect.getX2(), rect.getY2(), paint);
                        }
                        imageView.setImageBitmap(ImageTool.resize(rectBitmap, imageView.getWidth(), imageView.getHeight()));

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

            ringProgressDialog = ProgressDialog.show(ImageViewActivity.this, "Please wait ...", "Logging in ...", true);
        }
    }

    private class ConvertImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... strings) {
            if(strings.length < 1) return null;
            if(strings[0] == null) return null;

            byte[] imageAsBytes = Base64.decode(strings[0].getBytes(), Base64.URL_SAFE);
            return  BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageBitmap = bitmap;
            rectBitmap = imageBitmap.copy(imageBitmap.getConfig(), true);
            canvas = new Canvas(rectBitmap);
            imageView.setImageBitmap(ImageTool.resize(rectBitmap, imageView.getWidth(), imageView.getHeight()));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            new GetRectsTask(image).execute(token);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
