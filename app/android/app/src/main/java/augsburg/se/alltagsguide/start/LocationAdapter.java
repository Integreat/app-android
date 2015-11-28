/*
 * This file is part of Integreat.
 *
 * Integreat is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Integreat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Integreat.  If not, see <http://www.gnu.org/licenses/>.
 */

package augsburg.se.alltagsguide.start;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.util.List;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Location;
import augsburg.se.alltagsguide.utilities.Objects;
import augsburg.se.alltagsguide.utilities.ui.BaseAdapter;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class LocationAdapter extends BaseAdapter<LocationAdapter.LocationViewHolder, Location> {

    @NonNull private LocationClickListener mListener;
    @Inject private Picasso mPicasso;

    public interface LocationClickListener {
        void onLocationClick(Location location);
    }

    public LocationAdapter(@NonNull List<Location> locations, @NonNull LocationClickListener listener, @NonNull Context context) {
        super(locations, context);
        mListener = listener;
    }

    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LocationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.location_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {
        final Location location = get(position);
        holder.title.setText(location.getName());
        if (!Objects.isNullOrEmpty(location.getCityImage())) {
            mPicasso.load(location.getCityImage())
                    .placeholder(R.drawable.icon_location_loading)
                    .error(R.drawable.icon_location_loading_error)
                    .fit().centerCrop()
                    .into(holder.image);
        } else {
            mPicasso.load(R.drawable.icon_location_loading);
        }

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
