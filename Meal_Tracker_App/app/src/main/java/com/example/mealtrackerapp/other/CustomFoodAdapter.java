package com.example.mealtrackerapp.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.example.mealtrackerapp.R;
import com.example.mealtrackerapp.databases.FoodLog;

import java.util.List;

/**
 * Custom ArrayAdapter for displaying a delete button on each row of a list
 */
public class CustomFoodAdapter extends ArrayAdapter<FoodLog> {
    private Context context;
    //list of food logs to display
    private List<FoodLog> arrayList;
    //resource id of the custom layout for list items
    int resource;

    public CustomFoodAdapter(@NonNull Context context, int resource, @NonNull List<FoodLog> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.arrayList = objects;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public FoodLog getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Provides the views for the list with delete buttons for each row
     * @param position position of an item in the list
     * @param convertView view created with the designated custom layout
     * @param parent list view
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //creating the layout and the views
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.list_item_with_delete, null);

        //refer to the text views to be displayed
        TextView text = (TextView) convertView.findViewById(R.id.list_item_text);
        //setting the text of each row to the corresponding value in the array list (here food log string)
        text.setText(arrayList.get(position).getStringGrams());

        //refer to the button to display from the resource
        ImageView button = (ImageView) convertView.findViewById(R.id.list_item_delete);
        //buttons are given their list position as tag
        button.setTag(position);

        //implement delete button function (remove item from the list)
        button.setOnClickListener(v -> {
            int positionToRemove = (int) v.getTag();
            arrayList.remove(positionToRemove);
            notifyDataSetChanged();
        });
        return convertView;
    }
}
