package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.dto.ScoreDTO;
import com.btl.login.interfaces.OnScoreActionListener;


import java.util.List;
public class ScoreAdapter extends RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder> {
    private final Context context;
    private final List<ScoreDTO> scoreList;
    private final OnScoreActionListener listener;

    public ScoreAdapter(Context context, List<ScoreDTO> scoreList, OnScoreActionListener listener) {
        this.context = context;
        this.scoreList = scoreList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cutsom_list_score, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        ScoreDTO scoreDTO = scoreList.get(position);
        holder.tvStudentName.setText(scoreDTO.getStudentFullName());

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onScoreRowLongClick(scoreDTO);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    public static class ScoreViewHolder extends RecyclerView.ViewHolder {
        TextView tvStudentName;

        public ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
        }
    }
}