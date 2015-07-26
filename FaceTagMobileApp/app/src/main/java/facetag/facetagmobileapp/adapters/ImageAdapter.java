package facetag.facetagmobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import facetag.facetagmobileapp.R;
import facetag.facetagmobileapp.entities.Image;
import facetag.facetagmobileapp.entities.User;

/**
 * Created by Chris_2 on 2015-07-25.
 */
public class ImageAdapter  extends ArrayAdapter<Image> {
    public ImageAdapter(Context context, ArrayList<Image> images) {
        super(context, 0, images);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Image image = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image, parent, false);
        }
        // Lookup view for data population
        TextView title = (TextView) convertView.findViewById(R.id.itemImageTitleTextView);
        TextView userID = (TextView) convertView.findViewById(R.id.itemImageUserIDTextView);
        // Populate the data into the template view using the data object
        title.setText(image.getTitle());
        userID.setText("Owner: " + image.getOwnerID());
        // Return the completed view to render on screen
        return convertView;
    }
}
