package app.com.weatherzone.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.weatherzone.R;
import app.com.weatherzone.core.MainActivity;
import app.com.weatherzone.pojo.APIResponse;
import app.com.weatherzone.pojo.Image;
import app.com.weatherzone.utils.Constants;

/**
 * This class is a custom adapter that assists the population of data in the list of images
 * and data.
 * Author : Varun
 * Date : 2/11/2016.
 */

public class ImageLoaderAdapter extends BaseAdapter{

    //List of image objects
    private List<Image> imageList;
    //Instance of the main activity
    private MainActivity mainActivity;
    //Layout inflater instance to inflate new rows
    private LayoutInflater layoutInflater;
    //Instance of a Viewholder to improve performance by bypassing the repeated findViewById lookups
    private ViewHolder holder;

    /**
     * This constructor initialises mandatory parameters like mainactivity and list of image objects
     * It also initialises a new Layout Inflater instance
     * @param mainActivity - main activity
     * @param imageList - list of image objects
     */
    public ImageLoaderAdapter(MainActivity mainActivity, List<Image> imageList)
    {
        //Initialise instance of main activity
        this.mainActivity = mainActivity;
        //Initialise list of image objects
        this.imageList = imageList;
        //Create a new inflater instance to inflate an individual custom row for each image
        layoutInflater = (LayoutInflater)mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * This method returns the total number of items in the dataset represented by this adapter
     * @return total number of items
     */
    public int getCount() {
        return imageList.size();
    }

    /**
     * This method returns the data item associated with the position in the list of items as specified.
     * @param position - position of each item in the list
     * @return List row object at the specified position
     */
    public Object getItem(int position) {
        return imageList.get(position);
    }

    /**
     * This method returns the id of the row/object associated with the specified position in that list.
     * @param position The position of the item in this adapter's data.
     * @return The id of the item at the position specified.
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * The method returns the number of view types that will be created by the getView below
     * @return number of view types
     */
    @Override
    public int getViewTypeCount() {
        //Return 2 as there are more than 1 view type returned
        return 2;
    }

    /**
     * This method returns the item view type and returns 1 if at the last position else returns 0
     * @param position
     * @return item view type
     */
    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? 1 : 0; //if we are at the last position then return 1, for any other position return 0
    }

    /**
     * This method is responsible for creating a new view instance for every item in the adapter data list
     * @param position - position of the item in the data list
     * @param convertView - instance of the view
     * @param parent - instance of the parent view container
     * @return the created view
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        holder = null;
        //Get the first image object instance from the list of image objects
        final Image image = imageList.get(position);

        //Inflate a new row object of the view is not created and held in memory
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.image_container_layout, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.title = (TextView) convertView.findViewById(R.id.title); // title
            holder.fullName = (TextView) convertView.findViewById(R.id.fullName); // title
            holder.country = (TextView) convertView.findViewById(R.id.country); // title
            holder.creationDate = (TextView) convertView.findViewById(R.id.creationDate);
            convertView.setTag(holder);
        }
        //Use existising inflated row
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        //Set Image title
        holder.title.setText(image.getTitle());
        //Set Image author name
        holder.fullName.setText(image.getFullName());
        //Set Image country
        holder.country.setText(image.getCountry());
        //Set Creation time
        holder.creationDate.setText(image.getCreationTime());
        //Set Image onclick listener to handle image click
        holder.imageView.setOnClickListener(thumbnailClickListener);
        //Set the imageview tag as URL of the image
        holder.imageView.setTag(image.getImageURL());
        //Load Image using Picasso. Load from cache if present
        Picasso.with(mainActivity)
                .load(image.getThumbnailURL())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Load from the network when cache fails
                        Picasso.with(mainActivity)
                                .load(image.getThumbnailURL())
                                .error(R.mipmap.ic_launcher)
                                .into(holder.imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v(Constants.TAG,"Could not fetch image");
                                    }
                                });
                    }
                });
        return convertView;
    }

    /**
     * This method deletes all instances in the imageList Array
     */
    public void clearData()
    {
        imageList.clear();
    }

    /**
     * This static class holds an inflated view to bypass finding View by Id that can
     * potentially result in performance degradation
     */
    static class ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView fullName;
        public TextView country;
        public TextView creationDate;
    }

    /**
     * This listener opens the imageurl in the phone's default browser
     */
    private View.OnClickListener thumbnailClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getTag() != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(v.getTag().toString()));
                mainActivity.startActivity(i);
            }
        }
    };
}
