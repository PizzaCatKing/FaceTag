package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import facetag.facetagmobileapp.entities.RestError;
import facetag.facetagmobileapp.singletons.ObjectMapperSingleton;


public class SignupActivity extends ActionBarActivity {
    ProgressDialog ringProgressDialog;
    Button signupButton;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText nameEditText;
    final private int ACCOUNT_MADE_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupButton = (Button) findViewById(R.id.signUpSignUpButton);
        usernameEditText = (EditText) findViewById(R.id.signUpUsernameEditText);
        passwordEditText = (EditText) findViewById(R.id.signUpPasswordEditText);
        nameEditText     = (EditText) findViewById(R.id.signUpNameEditText);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            new signUpTask().execute(
                    usernameEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    nameEditText.getText().toString());

            }
        });



    }


    private class signUpTask extends AsyncTask<String, Void, ResponseEntity<String>> {

        @Override
        protected ResponseEntity<String> doInBackground(String... params) {
            if(params.length <3){
                return null;
            }


            String username = params[0];
            String password = params[1];
            String name = params[2];

            if(username == null || password == null || name == null){
                return null;
            }

            if(username.equals("") || password.equals("") || name.equals("")){
                return null;
            }

            RestTemplate restTemplate = new RestTemplate();

            restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

            body.add("username", username);
            body.add("password", password);
            body.add("name", name);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(body, headers);
            restTemplate.setErrorHandler(new FaceTagSpringErrorHandler());
            ResponseEntity<String> result = restTemplate.postForEntity(Globals.SERVER_ADDRESS + "/signup", request, String.class);


            return result;
        }

        @Override
        protected void onPostExecute(ResponseEntity<String> result) {
            ringProgressDialog.dismiss();
            if(result == null){
                //Invalid input! Tell the user to make better entries
                Context context = getApplicationContext();
                CharSequence text = "Please fill in all the fields";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            else if(result.getStatusCode() == HttpStatus.OK){
                System.out.println(result.getStatusCode() );
                //We got a good return!
                Context context = getApplicationContext();
                CharSequence text = "Sign Up successful!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("username",usernameEditText.getText().toString());
                returnIntent.putExtra("password",passwordEditText.getText().toString());
                setResult(RESULT_OK,returnIntent);
                finish();

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

                }

            }

        }

        @Override
        protected void onPreExecute() {

            ringProgressDialog = ProgressDialog.show(SignupActivity.this, "Please wait ...", "Signing Up ...", true);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }
}
