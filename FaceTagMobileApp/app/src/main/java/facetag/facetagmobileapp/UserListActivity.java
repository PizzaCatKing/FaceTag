package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;

import facetag.facetagmobileapp.adapters.UsersAdapter;
import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.entities.Token;
import facetag.facetagmobileapp.entities.User;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;


@SuppressWarnings("deprecation")
public class UserListActivity extends ActionBarActivity {
    ProgressDialog ringProgressDialog;
    ListView listView;
    EditText searchEditText;
    ArrayList<User> friends;
    UsersAdapter adapter;
    Token token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        searchEditText = (EditText) findViewById(R.id.userListSearchEditText);
        listView = (ListView) findViewById(R.id.userListUserListView);

        Intent i = getIntent();
        friends = i.getParcelableArrayListExtra("users");
        if(friends == null){
            friends = new ArrayList<>();
        }
        token = i.getParcelableExtra("token");

        // Create the adapter to convert the array to views
        adapter = new UsersAdapter(this, friends);
        // Attach the adapter to a ListView

        listView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                UserListActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {}

            @Override
            public void afterTextChanged(Editable arg0) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView l, View v, int position, long id) {
                //Go to user page for user
                new getUserFriendsTask(UserListActivity.this.adapter.getItem(position)).execute(token);
            }

        });
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
                    Intent i = new Intent(getApplicationContext(), UserViewActivity.class);
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

            ringProgressDialog = ProgressDialog.show(UserListActivity.this, "Please wait ...", "Logging in ...", true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friends_list, menu);
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
