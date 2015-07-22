package facetag.facetagmobileapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import facetag.facetagmobileapp.adapters.UsersAdapter;
import facetag.facetagmobileapp.entities.User;


@SuppressWarnings("deprecation")
public class UserListActivity extends ActionBarActivity {
    ListView listView;
    EditText searchEditText;
    ArrayList<User> friends;
    UsersAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        searchEditText = (EditText) findViewById(R.id.userListSearchEditText);
        listView = (ListView) findViewById(R.id.userListUserListView);

        Intent i = getIntent();
        friends = i.getParcelableArrayListExtra("friends");
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
                UserListActivity.this.adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
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
