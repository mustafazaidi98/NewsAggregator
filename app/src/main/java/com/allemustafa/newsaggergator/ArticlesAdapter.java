package com.allemustafa.newsaggergator;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.allemustafa.newsaggergator.databinding.ArticlesLayoutBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesViewHolder> {
    private final MainActivity mainActivity;
    private final ArrayList<NewsArticles> articles;
    public ArticlesAdapter(MainActivity mainActivity, ArrayList<NewsArticles> articles) {
        this.mainActivity = mainActivity;
        this.articles = articles;
    }

    @NonNull
    @Override
    public ArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ArticlesLayoutBinding binding = ArticlesLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()),parent,false);
        return new ArticlesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticlesViewHolder holder, int position) {
        NewsArticles c = articles.get(position);
        String imgUrl = c.getUrlToImage();
        if(imgUrl.isEmpty()==false){
            Picasso.get().load(imgUrl).error(R.drawable.brokenimage).placeholder(R.drawable.loading)
                    .into(holder.binding.Image, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: ");
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d(TAG, "onError: " + e);
                        }
                    });
        }
        holder.binding.Title.setText(c.getTitle());
        holder.binding.Description.setText(c.getDescription());
        holder.binding.Channel.setText(c.getChannel());
        holder.binding.Date.setText(c.getPublishedAt());
        holder.binding.PageNumber.setText(String.format(
                Locale.getDefault(),"%d of %d", (position+1), articles.size()));
        holder.binding.Title.setOnClickListener(v -> click(c.getUrl()));
        holder.binding.Image.setOnClickListener(v -> click(c.getUrl()));
        holder.binding.Description.setOnClickListener(v -> click(c.getUrl()));
    }
    private void click(String name) {
        Uri Uri = android.net.Uri.parse(name);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri);
        mainActivity.startActivity(intent);
    }
    @Override
    public int getItemCount() {
        return articles.size();
    }
}
