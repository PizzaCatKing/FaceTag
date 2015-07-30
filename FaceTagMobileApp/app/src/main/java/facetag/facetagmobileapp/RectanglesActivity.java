package facetag.facetagmobileapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import facetag.facetagmobileapp.entities.Image;
import facetag.facetagmobileapp.entities.Token;

public class RectanglesActivity extends AppCompatActivity {
    ProgressDialog ringProgressDialog;
    Token token;
    Bitmap imageBitmap;
    Image image;

    TextView titleTextView;
    ImageView imageView;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectangles);

        Intent i = getIntent();
        token = i.getParcelableExtra("token");
        image = i.getParcelableExtra("image");
        imageBitmap = i.getParcelableExtra("imageBitmap");

        titleTextView = (TextView) findViewById(R.id.rectanglesTitleTextView);
        imageView = (ImageView) findViewById(R.id.rectanglesImageView);
        submitButton = (Button) findViewById(R.id.rectanglesSubmitButton);

        titleTextView.setText(image.getTitle());



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rectangles, menu);
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
