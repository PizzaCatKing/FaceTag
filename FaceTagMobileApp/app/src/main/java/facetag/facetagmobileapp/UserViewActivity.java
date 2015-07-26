package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import facetag.facetagmobileapp.entities.Image;
import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.entities.Token;
import facetag.facetagmobileapp.entities.User;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;

@SuppressWarnings("deprecation")
public class UserViewActivity extends ActionBarActivity {
    ProgressDialog ringProgressDialog;
    Token token;
    User user;
    ArrayList<User> friends;
    boolean isFriend;

    TextView nameTextView;
    TextView userIDTextView;
    Button getFriendsButton;
    Button getImagesButton;
    Button addFriendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        nameTextView = (TextView) findViewById(R.id.userViewNameTextView);
        userIDTextView = (TextView) findViewById(R.id.userViewuserIDTextView);
        getFriendsButton = (Button) findViewById(R.id.userViewFriendsButton);
        getImagesButton = (Button) findViewById(R.id.userViewImagesButton);
        addFriendButton = (Button) findViewById(R.id.userViewaddFriendButton);

        Intent i = getIntent();
        token = i.getParcelableExtra("token");
        user = i.getParcelableExtra("user");
        friends = i.getParcelableArrayListExtra("users");
        if (user == null) {
            //Error - no user with this name
            Context context = getApplicationContext();
            CharSequence text = "Invalid user data.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            finish();
        }
        isFriend = false;
        for (User u : friends) {
            System.out.println(u.getUserID() + " " + token.getUserID());
            if (u.getUserID().equals(token.getUserID())) {
                isFriend = true;
                break;
            }
        }
        System.out.println("Has Friends: " + friends.size());
        if(token.getUserID().equals(user.getUserID())){
            addFriendButton.setVisibility(View.INVISIBLE);
        }
        if (isFriend) {
            addFriendButton.setText("Remove Friend");
        } else {
            addFriendButton.setText("Add Friend");
        }
        nameTextView.setText(user.getName());
        userIDTextView.setText(user.getUserID());

        getFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getUserFriendsTask(user).execute(token);
            }
        });

        getImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new getImagesForUserTask(user).execute(token);
            }
        });
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFriend) {
                    //Remove friend
                    new RemoveFriendTask(token).execute(user);

                } else {
                    //Add friend
                    new AddFriendTask(token).execute(user);
                }
            }
        });

    }


    private class AddFriendTask extends AsyncTask<User, Void, ResponseEntity<String>> {
        Token token;
        public AddFriendTask(Token token){
            this.token = token;
        }
        @Override
        protected ResponseEntity<String> doInBackground(User... params) {
            if(params.length < 1){
                return null;
            }
            User user = params[0];
            if(token== null || user == null){
                return null;
            }
            if(token.getUserID().equals("") || token.getToken().equals("") || user.getUserID().equals("")){
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            body.add("token", token.getToken());
            body.add("userID", token.getUserID());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());


            return restTemplate.postForEntity(Globals.SERVER_ADDRESS + "/user/"+user.getUserID()+"/friend", request, String.class);
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {
            ringProgressDialog.dismiss();
            if(result == null){
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Parameter error";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            else if(result.getStatusCode() == HttpStatus.OK){
                System.out.println(result.getStatusCode() );
                //We got a good return!
                Context context = getApplicationContext();
                CharSequence text = "Friend added!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                isFriend = true;
                addFriendButton.setText("Remove Friend");
            }
            else{
                RestError error;
                try {
                    error = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), RestError.class);
                    Context context = getApplicationContext();
                    CharSequence text = "Error: " + error.getMessage();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } catch (Exception e){
                    Context context = getApplicationContext();
                    CharSequence text = "Error parsing error message";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    e.printStackTrace();
                }

            }

        }

        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(UserViewActivity.this, "Please wait ...", "Signing Up ...", true);
        }

    }

    private class RemoveFriendTask extends AsyncTask<User, Void, ResponseEntity<String>> {
        Token token;
        public RemoveFriendTask(Token token){
            this.token = token;
        }
        @Override
        protected ResponseEntity<String> doInBackground(User... params) {
            if(params.length < 1){
                return null;
            }
            User user = params[0];
            if(token== null || user == null){
                return null;
            }
            if(token.getUserID().equals("") || token.getToken().equals("") || user.getUserID().equals("")){
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();



            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());


            String url=Globals.SERVER_ADDRESS + "/user/"+user.getUserID()+"/friend/delete";

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            body.add("token", token.getToken());
            body.add("userID", token.getUserID());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());

            return restTemplate.postForEntity(url, request, String.class);
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {
            ringProgressDialog.dismiss();
            if(result == null){
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Parameter error";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            else if(result.getStatusCode() == HttpStatus.OK){
                System.out.println(result.getStatusCode() );
                //We got a good return!
                Context context = getApplicationContext();
                CharSequence text = "Friend removed!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                isFriend = false;
                addFriendButton.setText("Add Friend");
            }
            else{
                RestError error;
                try {
                    error = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), RestError.class);
                    Context context = getApplicationContext();
                    CharSequence text = "Error: " + error.getMessage();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } catch (Exception e){
                    Context context = getApplicationContext();
                    CharSequence text = "Error parsing error message";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    e.printStackTrace();

                }

            }

        }

        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(UserViewActivity.this, "Please wait ...", "Signing Up ...", true);
        }

    }

    private class getImagesForUserTask extends AsyncTask<Token, Void, ResponseEntity<String>> {
        User user;
        public getImagesForUserTask(User user){
            this.user = user;
        }
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

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/user/" + user.getUserID() + "/images")
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
                    ArrayList<Image> images = ObjectMapperSingleton.getObjectMapper()
                            .readValue(result.getBody(), new TypeReference<ArrayList<Image>>() {
                            });

                    // We have a valid token - go to main menu
                    Intent i = new Intent(getApplicationContext(), ImageListActivity.class);
                    i.putParcelableArrayListExtra("images", images);
                    i.putExtra("token", token);
                    startActivity(i);

                } catch (IOException e) {
                    e.printStackTrace();
                    Context context = getApplicationContext();
                    CharSequence text = "IO error while mapping images!";
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

            ringProgressDialog = ProgressDialog.show(UserViewActivity.this, "Please wait ...", "Logging in ...", true);
        }
    }

    private class getUserFriendsTask extends AsyncTask<Token, Void, ResponseEntity<String>> {
        User user;
        public getUserFriendsTask(User user){
            this.user = user;
        }
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

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/user/" + user.getUserID() + "/friend")
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
                    i.putExtra("user", user);
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

            ringProgressDialog = ProgressDialog.show(UserViewActivity.this, "Please wait ...", "Logging in ...", true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_view, menu);
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
