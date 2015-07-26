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
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.entities.Token;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;


@SuppressWarnings("deprecation")
public class LoginActivity extends ActionBarActivity {
    ProgressDialog ringProgressDialog;
    Button loginButton;
    Button signupButton;
    EditText usernameEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginButton = (Button) findViewById(R.id.loginLoginButton);
        signupButton = (Button) findViewById(R.id.loginSignUpButton);
        usernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);
        passwordEditText = (EditText) findViewById(R.id.loginPasswordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getTokenTask().execute(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivityForResult(i, Globals.ACCOUNT_MADE_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the user made a new account automatically fill the fields with their info
        //Otherwise just keep whatever they had there before
        if (requestCode == Globals.ACCOUNT_MADE_CODE) {
            if (resultCode == RESULT_OK) {
                String newUsername = data.getStringExtra("username");
                String newPassword = data.getStringExtra("password");

                usernameEditText.setText(newUsername);
                passwordEditText.setText(newPassword);
            }
        }
    }


    private class getTokenTask extends AsyncTask<String, Void, ResponseEntity<String>> {

        @Override
        protected ResponseEntity<String> doInBackground(String... params) {
            if (params.length < 2) {
                return null;
            }

            String username = params[0];
            String password = params[1];

            if (username == null || password == null) {
                return null;
            }

            if (username.equals("") || password.equals("")) {
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(Globals.SERVER_ADDRESS + "/login")
                    .queryParam("username", username)
                    .queryParam("password", password);

            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());

            return restTemplate.getForEntity(builder.build().encode().toUri(), String.class);
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {
            ringProgressDialog.dismiss();
            if (result == null) {
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Please fill in all the fields";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            } else {
                if (result.getStatusCode() == HttpStatus.OK) {
                    //We got a good return!

                    try {
                        Token token = ObjectMapperSingleton.getObjectMapper().readValue(result.getBody(), Token.class);
                        // We have a valid token - go to main menu
                        Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                        i.putExtra("token", token);
                        startActivity(i);
                        finish();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Context context = getApplicationContext();
                        CharSequence text = "IO error while mapping token!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
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

            ringProgressDialog = ProgressDialog.show(LoginActivity.this, "Please wait ...", "Logging in ...", true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
