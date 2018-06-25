package com.example.android.movies2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.movies2.models.Review;

/**
 * Created by ayomide on 6/23/18.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {
    private Review[] mReviews;

    private final Context mContext;


    public ReviewsAdapter(Context context){
        mContext = context;
    }


    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("TrailerOnCreate", "trailer on create");
        int layoutID = R.layout.reviews;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutID, parent, false);
        return new ReviewsAdapterViewHolder(view);
    }

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder{
        public TextView mReviewAuthor;
        public TextView mReviewContent;
        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            mReviewAuthor = itemView.findViewById(R.id.review_author);
            mReviewContent = itemView.findViewById(R.id.review_content);
            //itemView.setOnClickListener(this);
            //mTrailerText.setText("Tailer "+getAdapterPosition());
        }

    }

    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder holder, int position) {
        holder.mReviewAuthor.setText(mReviews[position].getmAuthor());
        holder.mReviewContent.setText(mReviews[position].getmContent());
    }

    @Override
    public int getItemCount() {
        if(mReviews == null)
            return 0;
        else
            return mReviews.length;
    }

    public void setReviewsData(Review [] data){
        mReviews = data;
        notifyDataSetChanged();
    }
}
