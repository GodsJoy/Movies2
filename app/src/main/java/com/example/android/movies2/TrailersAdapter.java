package com.example.android.movies2;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ayomide on 6/23/18.
 */
public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {
    private String [] mTrailers;

    private final Context mContext;

    private final TrailersAdapterOnClickListener mClickHandler;

    public TrailersAdapter(Context context, TrailersAdapterOnClickListener clickListener){
        mContext = context;
        mClickHandler = clickListener;
    }

    public interface TrailersAdapterOnClickListener {
        void onClickTrailer(String trailerUrl);
    }

    @Override
    public TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("TrailerOnCreate", "trailer on create");
        int layoutID = R.layout.trailers;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);
        return new TrailersAdapterViewHolder(view);
    }

    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        public TextView mTrailerText;
        public TrailersAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerText = itemView.findViewById(R.id.trailer_text);
            itemView.setOnClickListener(this);
            //mTrailerText.setText("Tailer "+getAdapterPosition());
        }

        public void onClick(View v){
            //Movie selectedMovie = allMovies[getAdapterPosition()];
            /*Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra(DetailsActivity.EXTRA_POSITION, selectedMovie);
            startActivity(intent);*/
            int adapterPos = getAdapterPosition();
            mClickHandler.onClickTrailer(mTrailers[adapterPos]);

        }

    }

    @Override
    public void onBindViewHolder(TrailersAdapterViewHolder holder, int position) {
        holder.mTrailerText.setText("Trailer "+(position+1));
    }

    @Override
    public int getItemCount() {
        if(mTrailers == null)
            return 0;
        else
            return mTrailers.length;
    }

    public void setTrailerData(String [] data){
        mTrailers = data;
        notifyDataSetChanged();
    }
}

