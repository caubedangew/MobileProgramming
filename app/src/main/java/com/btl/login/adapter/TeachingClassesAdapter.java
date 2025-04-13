package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btl.login.R;
import com.btl.login.dto.TeachingClassesDTO;

import java.util.List;

public class TeachingClassesAdapter extends BaseAdapter {
    private final List<TeachingClassesDTO> listTeachingClasses;
    private final LayoutInflater inflater;
    private Integer numberStudentHavingFullScores;
    private Context context;

    public TeachingClassesAdapter(Context context, List<TeachingClassesDTO> listTeachingClasses) {
        inflater = LayoutInflater.from(context);
        this.listTeachingClasses = listTeachingClasses;
    }

    @Override
    public int getCount() {
        return listTeachingClasses.size();
    }

    @Override
    public Object getItem(int position) {
        return listTeachingClasses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = inflater.inflate(R.layout.custom_list_teaching_classes_item, null);

        TextView txtClassesName = view.findViewById(R.id.txtClassesName);
        TextView txtNumberStudentsInClass = view.findViewById(R.id.txtNumberStudentInClass);
        TextView txtNumberStudentsHaveScore = view.findViewById(R.id.txtNumberStudentHaveScore);

        txtClassesName.setText(listTeachingClasses.get(position).getClassName());
        txtNumberStudentsInClass.setText("Số lượng sinh viên đăng ký học là: " + listTeachingClasses.get(position).getNumberSubjectRegistration());
        txtNumberStudentsHaveScore.setText("Số lượng sinh viên đã được nhâp điểm là: " + listTeachingClasses.get(position).getNumberStudentHaveScore());

        return view;
    }
}
