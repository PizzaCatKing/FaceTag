package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.Collections;
import java.util.Comparator;

import facetag.facetagmobileapp.adapters.ListItemAdapter;
import facetag.facetagmobileapp.entities.Image;
import facetag.facetagmobileapp.entities.ListItem;
import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.entities.SectionItem;
import facetag.facetagmobileapp.entities.Token;
import facetag.facetagmobileapp.entities.User;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;

public class SearchActivity extends AppCompatActivity {
    ProgressDialog ringProgressDialog;
    ArrayList<ListItem> listItems;
    ArrayList<User> includes;
    ArrayList<User> excludes;
    ArrayList<User> potentials;
    ArrayList<User> friends;


    ListView listView;
    Button addIncludeButton;
    Button addExcludeButton;
    Button searchButton;



    ListItemAdapter adapter;
    Token token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.searchListView);
        addIncludeButton = (Button) findViewById(R.id.searchaddIncButton);
        addExcludeButton = (Button) findViewById(R.id.searchAddExcludeButton);
        searchButton = (Button) findViewById(R.id.searchSearchButton);

        Intent i = getIntent();
        token = i.getParcelableExtra("token");

        listItems = new ArrayList<>();
        includes = new ArrayList<>();
        excludes = new ArrayList<>();
        friends = new ArrayList<>();
        potentials = new ArrayList<>();

        adapter = new ListItemAdapter(this, listItems);
        listView.setAdapter(adapter);


        addIncludeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchActivity.this, FriendPickerActivity.class);
                i.putExtra("includes", true);
                i.putExtra("users", potentials);
                startActivityForResult(i, Globals.PICK_FRIEND_CODE2);
            }
        });

        addExcludeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SearchActivity.this, FriendPickerActivity.class);
                i.putExtra("includes", false);
                i.putExtra("users", potentials);
                startActivityForResult(i, Globals.PICK_FRIEND_CODE2);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView l, View v, int position, long id) {
                ListItem target = listItems.get(position);
                if (!target.isSection()) {
                    Intent i = new Intent(SearchActivity.this, FriendPickerActivity.class);
                    if (includes.contains(listItems.get(position))) {
                        i.putExtra("includes", true);
                        i.putExtra("rectLoc", includes.indexOf(target));
                    } else if (excludes.contains(listItems.get(position))) {
                        i.putExtra("includes", false);
                        i.putExtra("rectLoc", excludes.indexOf(target));
                    }
                    i.putExtra("users", potentials);
                    startActivityForResult(i, Globals.PICK_FRIEND_CODE2);
                }
            }

        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(" ---- BEEP --- ");
                new GetSearch(includes, excludes).execute(token);
            }
        });
        new GetUserFriendsTask().execute(token);
        updateList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Globals.PICK_FRIEND_CODE2){
            if(resultCode == RESULT_OK){
                Log.d("DEBUG --- --- ", "RET");
                int targetPosition = data.getIntExtra("rectLoc", -1);
                boolean inc = data.getBooleanExtra("includes", false);
                User user = data.getParcelableExtra("user");

                Log.d("DEBUG --- --- ", "RET " + targetPosition);
                Log.d("DEBUG --- --- ", "RET " + inc);
                Log.d("DEBUG --- --- ", "RET " + user);


                if(inc){
                    if(!user.getUserID().equals("")) {
                        if(targetPosition != -1) {
                            includes.set(targetPosition, user);
                        }
                        else{
                            includes.add(user);
                            for(User u : potentials){
                                if(u.equalsUser(user)){
                                    potentials.remove(u);
                                    break;
                                }
                            }
                        }
                    }
                    else{
                        if(targetPosition != -1) {
                            potentials.add(new User(includes.get(targetPosition)));
                            includes.remove(targetPosition);
                        }
                    }
                }
                else{
                    if(!user.getUserID().equals("")) {

                        if(targetPosition != -1) {
                            excludes.set(targetPosition, user);
                        }
                        else{
                            excludes.add(user);
                            for(User u : potentials){
                                if(u.equalsUser(user)){
                                    potentials.remove(u);
                                    break;
                                }
                            }
                        }
                    }
                    else{
                        if(targetPosition != -1) {
                            potentials.add(new User(excludes.get(targetPosition)));
                            excludes.remove(targetPosition);
                        }
                    }
                }
                Collections.sort(potentials, new Comparator<User>() {
                    public int compare(User o1, User o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });

                updateList();
            }
        }
    }


    private class GetUserFriendsTask extends AsyncTask<Token, Void, ResponseEntity<String>> {

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

            ringProgressDialog = ProgressDialog.show(SearchActivity.this, "Please wait ...", "Getting Friends...", true);
        }
    }

    private class GetSearch extends AsyncTask<Token, Void, ResponseEntity<String>> {
        ArrayList<User> inc;
        ArrayList<User> exc;
        public GetSearch(ArrayList<User> inc, ArrayList<User> exc){
            this.inc = inc;
            this.exc = exc;
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

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/image/search")
                    .queryParam("userID", thisToken.getUserID())
                    .queryParam("token", thisToken.getToken());
                    for(User u : inc){
                        builder.queryParam("include", u.getUserID());
                    }
                    for(User u : exc){
                        builder.queryParam("exclude", u.getUserID());
                    }
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

            ringProgressDialog = ProgressDialog.show(SearchActivity.this, "Please wait ...", "Getting Friends...", true);
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
                    potentials.addAll(friends);
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

            ringProgressDialog = ProgressDialog.show(SearchActivity.this, "Please wait ...", "Getting user ...", true);
        }
    }

    private void updateList(){
        adapter.clear();
        adapter.add(new SectionItem("Include"));
        adapter.addAll(includes);
        adapter.add(new SectionItem("Exclude"));
        adapter.addAll(excludes);
        adapter.notifyDataSetChanged();
    }

}
