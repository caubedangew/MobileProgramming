package com.btl.login.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btl.login.R;
import com.btl.login.dto.SubjectsTaughtByTeacherDTO;
import com.btl.login.entities.OpenClass;
import com.btl.login.entities.Subject;

import java.util.ArrayList;
import java.util.List;

public class TeachingSubjectsAdapter extends BaseAdapter {
    private final List<SubjectsTaughtByTeacherDTO> listSubjects;
    private Context context;
    private final LayoutInflater inflater;
    public TeachingSubjectsAdapter(Context context, List<SubjectsTaughtByTeacherDTO> listSubjects) {
        inflater = LayoutInflater.from(context);
        this.listSubjects = listSubjects;
    }
    @Override
    public int getCount() {
        return listSubjects.size();
    }

    @Override
    public Object getItem(int position) {
        return listSubjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.custom_grid_teaching_subjects_item, null);

        TextView txtSubjectName = view.findViewById(R.id.txtSubjectName);
        TextView txtCreditNumber = view.findViewById(R.id.txtCreditNumber);
        TextView txtNumberTeachingClasses = view.findViewById(R.id.txtNumberTeachingClasses);

        txtSubjectName.setText(listSubjects.get(position).getSubjectName());
        txtCreditNumber.setText("Số tín chỉ: " + listSubjects.get(position).getCreditNumber());
        txtNumberTeachingClasses.setText("Số lượng lớp dạy học là: " + listSubjects.get(position).getAssignmentCount());

        return view;
    }
}
