package com.example.mainscreen;

// GuideAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.GuideViewHolder> {
    private List<Guide> guideList;

    public GuideAdapter(List<Guide> guideList) {
        this.guideList = guideList;
    }

    @NonNull
    @Override
    public GuideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_guide, parent, false);
        return new GuideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GuideViewHolder holder, int position) {
        Guide guide = guideList.get(position);
        holder.guideImage.setImageResource(guide.getImage());
        holder.guideTitle.setText(guide.getTitle());
        holder.guideContent.setText(guide.getContent());
    }

    @Override
    public int getItemCount() {
        return guideList.size();
    }

    class GuideViewHolder extends RecyclerView.ViewHolder {
        ImageView guideImage;
        TextView guideTitle;
        TextView guideContent;

        public GuideViewHolder(@NonNull View itemView) {
            super(itemView);
            guideImage = itemView.findViewById(R.id.guide_image);
            guideTitle = itemView.findViewById(R.id.guide_title);
            guideContent = itemView.findViewById(R.id.guide_content);
        }
    }
}
