package com.example.android.movies2.models;

/**
 * Created by ayomide on 6/24/18.
 */
//POJO for review. A review has an author and content
public class Review {
    private String mAuthor;
    private String mContent;

    public Review(String mAuthor, String mContent) {
        this.mAuthor = mAuthor;
        this.mContent = mContent;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }
}
