package facetag.facetagmobileapp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import facetag.facetagmobileapp.R;
import facetag.facetagmobileapp.entities.SectionItem;
import facetag.facetagmobileapp.entities.ListItem;
import facetag.facetagmobileapp.entities.User;

/**
 * Created by Chris_2 on 2015-08-02.
 */


public class ListItemAdapter extends ArrayAdapter<ListItem> {
    public ListItemAdapter(Context context, ArrayList<ListItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ListItem item = getItem(position);
        if(item.isSection()){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_section, parent, false);
            convertView.setLongClickable(false);
            convertView.setClickable(false);
            convertView.setOnClickListener(null);
            convertView.setOnLongClickListener(null);
        }
        else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
        }

        if(item.isSection()) {

            SectionItem header = (SectionItem) item;

            final TextView sectionView = (TextView) convertView.findViewById(R.id.sectionTextView);
            if(sectionView != null) {
                sectionView.setText(header.getTitle());
            }
        }
        else{
            User user = (User)item;

            TextView name = (TextView) convertView.findViewById(R.id.itemUserNameTextView);
            TextView userID = (TextView) convertView.findViewById(R.id.itemUserUserIDTextView);

            if(name != null) {
                name.setText(user.getName());
            }
            if(userID != null) {
                userID.setText(user.getUserID());
             }
        }

        return convertView;
    }
}