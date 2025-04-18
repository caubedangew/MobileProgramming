package com.btl.login.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.btl.login.R;
import com.btl.login.dto.StudentInClassDTO;

import java.util.List;

public class InputScoreAdapter extends BaseAdapter {
    private final List<StudentInClassDTO> listStudents;
    private final LayoutInflater inflater;

    public InputScoreAdapter(Context context, List<StudentInClassDTO> listStudents) {
        inflater = LayoutInflater.from(context);
        this.listStudents = listStudents;
    }

    @Override
    public int getCount() {
        return listStudents.size();
    }

    @Override
    public Object getItem(int position) {
        return listStudents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.custom_spinner_teaching_students_item, null);


        TextView txtStudentName = view.findViewById(R.id.txtStudentName);
        TextView txtStudentId = view.findViewById(R.id.txtStudentId);
        ImageView imgAlreadyHaveScore = view.findViewById(R.id.imgAlreadyHaveScore);

        txtStudentName.setText(listStudents.get(position).getFullName());
        txtStudentId.setText(String.valueOf(listStudents.get(position).getId()));

        if (listStudents.get(position).getNumberScore() == 3)
            imgAlreadyHaveScore.setImageResource(R.drawable.ic_tick);
        else if (listStudents.get(position).getNumberScore() == 0)
            imgAlreadyHaveScore.setImageResource(R.drawable.ic_cross);
        else
            imgAlreadyHaveScore.setImageResource(R.drawable.ic_warning);

        return view;
    }
}
