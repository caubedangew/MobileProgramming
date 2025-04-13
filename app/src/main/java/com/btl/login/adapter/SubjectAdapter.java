package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.btl.login.R;
import com.btl.login.dto.SubjectDTO;
import com.btl.login.interfaces.OnSubjectActionListener;

import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {

    private final Context context;
    private final List<SubjectDTO> subjectList;
    private final OnSubjectActionListener listener;

    public SubjectAdapter(Context context, List<SubjectDTO> subjectList, OnSubjectActionListener listener) {
        this.context = context;
        this.subjectList = subjectList;
        this.listener = listener; // Gán giá trị cho listener
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_subject, parent, false); // Sử dụng subject_item.xml
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        // Lấy dữ liệu môn học tại vị trí hiện tại
        SubjectDTO subjectDTO = subjectList.get(position);

        // Hiển thị dữ liệu vào giao diện
        holder.tvSubjectName.setText("Tên môn học: " + subjectDTO.getSubject().getSubjectName());
        holder.tvCreditNumber.setText("Số tín chỉ: " + subjectDTO.getSubject().getCreditNumber());

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubjectClick(subjectDTO);
                listener.onSubjectTagForEdit(subjectDTO);
            }
        });

        // Xử lý sự kiện xóa môn học
        holder.imgDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubjectDelete(subjectDTO, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    // ViewHolder class
    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectName, tvCreditNumber;
        ImageView imgDelete;

        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectName = itemView.findViewById(R.id.tv_subject_name);
            tvCreditNumber = itemView.findViewById(R.id.tv_credit_number);
            imgDelete = itemView.findViewById(R.id.img_delete_subject);
        }
    }
}