package com.cocofla.sasamifry.timeline;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cocofla.sasamifry.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * タイムライン用アダプター
 * Created by yamotonalds on 2014/07/17.
 */
public class TimeLineAdapter extends ArrayAdapter<TwitterContent.Tweet> {
    private static final String TAG = "TimeLineAdapter";

    private final LayoutInflater inflater;

    private static final ConcurrentHashMap<String, Bitmap> imageCache = new ConcurrentHashMap<String, Bitmap>();

    public TimeLineAdapter(Context context, int resource, int textViewResourceId, List<TwitterContent.Tweet> objects) {
        super(context, resource, textViewResourceId, objects);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, String.valueOf(position));
        View view;
        if (convertView == null) {
            view = this.inflater.inflate(R.layout.tweet, null);
        } else {
            view = convertView;
            ImageView profileImageView = (ImageView) view.findViewById(R.id.profileImage);
            if (profileImageView.getTag() != null) {
                ((ExternalImageLoader) profileImageView.getTag()).cancel(true);
            }
            profileImageView.setImageDrawable(null);    // clear
        }

        TwitterContent.Tweet item = TwitterContent.ITEMS.get(position);

        String urlString = item.status.getUser().getProfileImageURL();
        ImageView profileImageView = (ImageView) view.findViewById(R.id.profileImage);
        if (imageCache.containsKey(urlString)) {
            profileImageView.setImageBitmap(imageCache.get(urlString));
        } else {
            ExternalImageLoader loader = new ExternalImageLoader(profileImageView, imageCache);
            loader.execute(urlString);
            profileImageView.setTag(loader);
        }
        ((TextView) view.findViewById(R.id.userName)).setText(item.status.getUser().getName());
        ((TextView) view.findViewById(R.id.userScreenName)).setText("@" + item.status.getUser().getScreenName());
        ((TextView) view.findViewById(R.id.createdAt)).setText(new SimpleDateFormat("yyyy/MM/dd").format(item.status.getCreatedAt()));
        ((TextView) view.findViewById(R.id.text)).setText(item.status.getText());

        return view;
    }
}
