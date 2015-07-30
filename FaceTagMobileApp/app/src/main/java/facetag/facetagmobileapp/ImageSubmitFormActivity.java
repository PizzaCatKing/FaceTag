package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import facetag.facetagmobileapp.entities.Image;
import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.entities.Token;
import facetag.facetagmobileapp.singletons.ImageTool;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;

public class ImageSubmitFormActivity extends AppCompatActivity {
    ProgressDialog ringProgressDialog;
    ImageView imageView;
    EditText titleEditText;
    Button pickImageButton;
    Button cameraButton;
    Button submitButton;
    Bitmap image;
    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_submit_form);

        imageView = (ImageView) findViewById(R.id.imageSubmitImageView);
        titleEditText = (EditText) findViewById(R.id.imageSubmitTitleEditText);
        pickImageButton = (Button) findViewById(R.id.imageSubmitPickButton);
        submitButton = (Button) findViewById(R.id.imageSubmitPostButton);
        cameraButton = (Button) findViewById(R.id.imageSubmitCameraButton);

        token = getIntent().getParcelableExtra("token");

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Globals.PICK_IMAGE_CODE);
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(camIntent, Globals.PICK_IMAGE_CODE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SubmitImageTask(image,titleEditText.getText().toString()).execute(token);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Globals.PICK_IMAGE_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri chosenImageUri = data.getData();
                    Bitmap originalImage = ImageTool.resize(MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri), 2000, 2000);
                    int orientation =ExifInterface.ORIENTATION_NORMAL;
                    try {
                        ExifInterface ei = new ExifInterface(ImageTool.getRealPathFromUri(this.getApplicationContext(), chosenImageUri));
                       orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    }
                    catch (Exception e){
                    }


                    Matrix matrix = new Matrix();
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            matrix.postRotate(90);
                            image = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix, false);

                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            matrix.postRotate(180);
                            image = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix, false);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.postRotate(270);
                            image = Bitmap.createBitmap(originalImage, 0, 0, originalImage.getWidth(), originalImage.getHeight(), matrix, false);
                            break;
                        default:
                            image = originalImage;
                    }

                    imageView.setImageBitmap(ImageTool.resize(image, imageView.getWidth(), imageView.getHeight()));
                } catch (IOException e) {
                    e.printStackTrace();
                    image = null;
                }
            }
        }
    }

    private class SubmitImageTask extends AsyncTask<Token, Void, ResponseEntity<String>> {
        Bitmap imageToSubmit;
        String title;
        public SubmitImageTask(Bitmap imageToSubmit, String title){
            this.imageToSubmit = imageToSubmit;
            this.title = title;
        }

        @Override
        protected ResponseEntity<String> doInBackground(Token... params) {
            if (params.length != 1) {
                return null;
            }

            Token thisToken = params[0];


            if (token == null || title == null || imageToSubmit == null) {
                return null;
            }

            if (thisToken.getUserID().equals("") || thisToken.getToken().equals("") || title.equals("")) {
                return null;
            }
            //Convert image to BASE64
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageToSubmit.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.URL_SAFE);

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            body.add("userID", thisToken.getUserID());
            body.add("token", thisToken.getToken());
            body.add("title", title);
            body.add("base64Image", encodedImage);
            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());
            return restTemplate.postForEntity(Globals.SERVER_ADDRESS + "/image/", request, String.class);
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
            } else if (result.getStatusCode() == HttpStatus.OK) {
                try {
                Image resultImage = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), Image.class);
                //We got a good return!
                Context context = getApplicationContext();
                CharSequence text = "Image posted!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                    //save image to storage, get uri and send it to next activity
                File imageCache = File.createTempFile("FaceTagTempImage"+resultImage.getImageID(), null, getExternalCacheDir());
                FileOutputStream filecon = new FileOutputStream(imageCache);
                image.compress(Bitmap.CompressFormat.JPEG, 100, filecon);
                filecon.flush();
                filecon.close();


                    //Image is posted so go to rect generator
                Intent i = new Intent(getApplicationContext(), RectanglesActivity.class);
                i.putExtra("token", token);
                i.putExtra("image", resultImage);
                i.putExtra("imageBitmapLocation", imageCache.toURI().toString());
                i.putExtra("generate", true);
                startActivity(i);
                finish();
                } catch (IOException e) {
                    e.printStackTrace();
                    Context context = getApplicationContext();
                    CharSequence text = "IO error while mapping image!";
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
                } catch (Exception e) {
                    Context context = getApplicationContext();
                    CharSequence text = "Error: Error message could not be parsed";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            }

        }

        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(ImageSubmitFormActivity.this, "Please wait ...", "Submitting Image ...", true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_submit_form, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
