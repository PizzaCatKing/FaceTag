package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
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

import java.util.ArrayList;

import facetag.facetagmobileapp.adapters.ImageAdapter;
import facetag.facetagmobileapp.entities.Image;
import facetag.facetagmobileapp.entities.Token;


@SuppressWarnings("deprecation")
public class ImageListActivity extends ActionBarActivity {
    ProgressDialog ringProgressDialog;
    ListView listView;
    EditText searchEditText;
    ArrayList<Image> images;
    ImageAdapter adapter;
    Token token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        searchEditText = (EditText) findViewById(R.id.imageListSearchEditText);
        listView = (ListView) findViewById(R.id.imageListImageListView);

        Intent i = getIntent();
        images = i.getParcelableArrayListExtra("images");
        if(images == null){
            images = new ArrayList<>();
        }
        System.out.println(images.size());
        token = i.getParcelableExtra("token");

        adapter = new ImageAdapter(this, images);

        listView.setAdapter(adapter);

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                ImageListActivity.this.adapter.getFilter().filter(cs);
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
                Intent i = new Intent(getApplicationContext(), ImageViewActivity.class);
                i.putExtra("token", token);
                i.putExtra("image", ImageListActivity.this.adapter.getItem(position));
                startActivity(i);
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_list, menu);
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
