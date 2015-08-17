package augsburg.se.alltagsguide.information;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import augsburg.se.alltagsguide.R;
import augsburg.se.alltagsguide.common.Content;
import augsburg.se.alltagsguide.common.Information;

/**
 * Created by Daniel-L on 16.08.2015.
 */
public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {

    private Content mContent;
    private ContentFragment.OnContentFragmentInteractionListener mListener;

    public void replace(Content content) {
        notifyItemRangeRemoved(0, mContent.getInformation().size());
        mContent = content;
        notifyItemRangeInserted(0, mContent.getInformation().size());
    }

    public interface OnItemClickListener {
        void onClick(Information information);
    }

    public ContentAdapter(Content content, ContentFragment.OnContentFragmentInteractionListener listener) {
        mContent = content;
        mListener = listener;
    }

    @Override
    public ContentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //TODO use viewType for which nested level it is
        return new ContentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_information, parent, false));
    }

    @Override
    public void onBindViewHolder(ContentViewHolder holder, int position) {
        final Information information = mContent.getInformation().get(position);
        holder.title.setText(information.getTitle());
        holder.description.setText(information.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onInformationClicked(information);
            }
        });
        //TODO inject holder.image.setText(information.getImage());
    }

    @Override
    public int getItemCount() {
        return mContent.getInformation().size(); //TODO maybe show subitems as well
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView description;
        ImageView image;

        public ContentViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);
            image = (ImageView) itemView.findViewById(R.id.image);
        }
    }
}
