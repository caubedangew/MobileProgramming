package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btl.login.R;
import com.btl.login.entities.Subject;

import java.util.ArrayList;

public class TeachingSubjectsAdapter extends BaseAdapter {
    private final ArrayList<Subject> listSubjects;
    private Context context;
    private final LayoutInflater inflater;
    public TeachingSubjectsAdapter(Context context, ArrayList<Subject> listSubjects) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.custom_grid_teaching_subjects_item, null);

        TextView txtSubjectName = view.findViewById(R.id.txtSubjectName);
        TextView txtCreditNumber = view.findViewById(R.id.txtCreditNumber);
        TextView txtNumberTeachingClasses = view.findViewById(R.id.txtNumberTeachingClasses);

        txtSubjectName.setText(listSubjects.get(position).getSubjectName());
        txtCreditNumber.setText(String.valueOf(listSubjects.get(position).getCreditNumber()));
        txtNumberTeachingClasses.setText("Số lượng lớp dạy học là: " + "3");

        return view;
    }
}
