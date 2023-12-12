package com.allemustafa.newsaggergator;

import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.allemustafa.newsaggergator.databinding.ArticlesLayoutBinding;

public class ArticlesViewHolder extends RecyclerView.ViewHolder {
    ArticlesLayoutBinding binding;
    TextView Title;
    TextView Date;
    TextView Description;
    TextView Channel;
    TextView PageNumber;
    ImageView Image;
    public ArticlesViewHolder(@NonNull ArticlesLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
