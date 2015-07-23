package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.entities.Token;
import facetag.facetagmobileapp.entities.User;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;


@SuppressWarnings("deprecation")
public class MainMenuActivity extends ActionBarActivity {
    ProgressDialog ringProgressDialog;
    Button myFriendsButton;
    Button myImagesButton;
    Button newImageButton;
    Button searchForUsersButton;
    Button logoutButton;
    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        myFriendsButton = (Button) findViewById(R.id.mainMyFriendsButton);
        myImagesButton = (Button) findViewById(R.id.mainMyImagesButton);
        newImageButton = (Button) findViewById(R.id.mainNewImageButton);
        searchForUsersButton = (Button) findViewById(R.id.mainUserSearchButton);
        logoutButton = (Button) findViewById(R.id.mainLogoutButton);

        Intent i = getIntent();
        token = i.getParcelableExtra("token");

        myFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getCurrentUserFriendsTask().execute(token);
            }
        });
        myImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        newImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        searchForUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new logoutActivity().execute(token);
            }
        });


    }

    private class logoutActivity extends AsyncTask<Token, Void, ResponseEntity<String>> {

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

            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            body.add("userID", thisToken.getUserID());
            body.add("token", thisToken.getToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());
            return restTemplate.postForEntity(Globals.SERVER_ADDRESS + "/logout", request, String.class);
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
                System.out.println(result.getStatusCode());
                //We got a good return!
                Context context = getApplicationContext();
                CharSequence text = "Log Out successful!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                //Show the login screen and finish this activity
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
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

            ringProgressDialog = ProgressDialog.show(MainMenuActivity.this, "Please wait ...", "Logging Out ...", true);
        }

    }

    private class getCurrentUserFriendsTask extends AsyncTask<Token, Void, ResponseEntity<String>> {

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
                    ArrayList<User> friends = ObjectMapperSingleton.getObjectMapper()
                            .readValue(result.getBody(), new TypeReference<ArrayList<User>>() {
                            });

                    // We have a valid token - go to main menu
                    Intent i = new Intent(getApplicationContext(), UserListActivity.class);
                    i.putParcelableArrayListExtra("users", friends);
                    i.putExtra("token", token);
                    startActivity(i);

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

            ringProgressDialog = ProgressDialog.show(MainMenuActivity.this, "Please wait ...", "Logging in ...", true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }
}
