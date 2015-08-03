package facetag.facetagmobileapp;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import facetag.facetagmobileapp.adapters.UsersAdapter;
import facetag.facetagmobileapp.entities.Rectangle;
import facetag.facetagmobileapp.entities.User;

public class FriendPickerActivity extends AppCompatActivity {

    ListView listView;
    EditText searchEditText;
    ArrayList<User> friends;
    int rectLoc; // Extra that the sender will want when this gets returned
    UsersAdapter adapter;
    boolean includes; // Is this going into the includes or excludes area
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_picker);

        searchEditText = (EditText) findViewById(R.id.friendPickerSearchEditText);
        listView = (ListView) findViewById(R.id.friendPickerListView);

        Intent i = getIntent();
        friends = i.getParcelableArrayListExtra("users");
        rectLoc = i.getIntExtra("rectLoc", -1);
        includes = i.getBooleanExtra("includes", false);
        if(friends == null){
            friends = new ArrayList<>();
        }

        // Create the adapter to convert the array to views
        adapter = new UsersAdapter(this, friends);
        // Attach the adapter to a ListView

        listView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                FriendPickerActivity.this.adapter.getFilter().filter(cs);
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
                Intent returnIntent = new Intent();
                returnIntent.putExtra("user",FriendPickerActivity.this.adapter.getItem(position));
                returnIntent.putExtra("rectLoc", rectLoc);
                returnIntent.putExtra("includes", includes);
                setResult(RESULT_OK, returnIntent);
                finish();

            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_friend_picker, menu);
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
