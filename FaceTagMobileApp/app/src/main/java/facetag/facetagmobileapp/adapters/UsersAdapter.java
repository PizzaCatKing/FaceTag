package facetag.facetagmobileapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import facetag.facetagmobileapp.R;
import facetag.facetagmobileapp.entities.User;

/**
 * Created by chris-ubuntu on 22/07/15.
 */
public class UsersAdapter extends ArrayAdapter<User> {
    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.itemUserNameTextView);
        TextView userID = (TextView) convertView.findViewById(R.id.itemUserUserIDTextView);
        // Populate the data into the template view using the data object
        name.setText(user.getName());
        userID.setText(user.getUserID());
        // Return the completed view to render on screen
        return convertView;
    }
}