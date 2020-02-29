package com.laioffer.matrix;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.laioffer.matrix.R;

import java.util.List;

public class EventAdapter extends BaseAdapter {
    Context context;
    List<Event> eventData;
    public EventAdapter(Context context) {
        this.context = context;
        eventData = DataService.getEventData();
    }

    /**
     * list view lenght
     */
    @Override
    public int getCount() {
        return eventData.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Event getItem(int position) {
        return eventData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @param position
     * @param convertView the old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using.
     *                    If it is not possible to convert this view to display the correct data, this method can create a new view.
     * @param parent the parent that this view will eventually be attached to
     * @return Gets a View that displays in the listView with the data at the specified position in the data set.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.event_item,
                    parent, false);
        }


        ImageView eventImage = (ImageView) convertView.findViewById(R.id.event_thumbnail);
        if (position % 2 == 1) {
            eventImage.setImageDrawable(context.getDrawable(R.drawable.event_thumbnail));
        } else {
            eventImage.setImageDrawable(context.getDrawable(R.drawable.lemon));
        }


        TextView eventTitle = (TextView) convertView.findViewById(
                R.id.event_title);
        TextView eventAddress = (TextView) convertView.findViewById(
                R.id.event_address);
        TextView eventDescription = (TextView) convertView.findViewById(
                R.id.event_description);

        Event r = eventData.get(position);
        eventTitle.setText(r.getTitle());
        eventAddress.setText(r.getAddress());
        eventDescription.setText(r.getDescription());
        return convertView;
    }
}
