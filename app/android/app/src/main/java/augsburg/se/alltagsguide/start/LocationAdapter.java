package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.utilities.BaseAdapter;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class LocationAdapter extends BaseAdapter<LocationAdapter.LocationViewHolder, Location> {

    private LocationClickListener mListener;
    private Context mContext;

    public interface LocationClickListener {
        void onLocationClick(Location location);
    }

    public LocationAdapter(LocationClickListener listener, Context context) {
        super(new ArrayList<Location>());
        mListener = listener;
        mContext = context;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        final Location location = get(position);
        holder.title.setText(location.getName());
        Picasso.with(mContext)
                .load(location.getIcon())
                .placeholder(R.drawable.placeholder_location)
                .error(R.drawable.placeholder_location)
                .fit()
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onLocationClick(location);
            }
        });
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageView image;

        public LocationViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
