package com.mobileappsco.training.mymovies.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileappsco.training.mymovies.R;
import com.mobileappsco.training.mymovies.entities.Result;
import com.mobileappsco.training.mymovies.entities.Review;

import java.util.List;

public class RVReviewsAdapter extends RecyclerView.Adapter<RVReviewsAdapter.ReviewViewHolder>  {

    List<Review> reviews;

    public RVReviewsAdapter(List<Review> reviews){
        this.reviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_review, parent, false);
        ReviewViewHolder cvh = new ReviewViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int i) {
        holder.card_author.setText(reviews.get(i).getAuthor());
        holder.card_content.setText(reviews.get(i).getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void addResult(Review review) {
        reviews.add(review);
        notifyItemInserted(getItemCount() - 1);
    }

    public void addResultList(List<Review> list) {
        if (reviews == null)
            reviews = list;
        else
            reviews.addAll(list);
        notifyDataSetChanged();
    }

    public void clearResults() {
        int size = this.reviews.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.reviews.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        CardView card_view;
        TextView card_author;
        TextView card_content;

        ReviewViewHolder(View itemView) {
            super(itemView);
            card_view = (CardView)itemView.findViewById(R.id.card_view_review);
            card_author = (TextView)itemView.findViewById(R.id.card_author);
            card_content = (TextView)itemView.findViewById(R.id.card_content);
        }

    }

}
