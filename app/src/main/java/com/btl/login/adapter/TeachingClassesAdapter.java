package com.btl.login.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.btl.login.R;

import java.util.ArrayList;

public class TeachingClassesAdapter extends BaseAdapter {
    private ArrayList<Object> listTeachingClasses;
    private Context context;
    private LayoutInflater inflater;

    public TeachingClassesAdapter(Context context, ArrayList<Object> listTeachingClasses) {
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

        txtClassesName.setText("ABc");
        txtNumberStudentsInClass.setText("Số lượng sinh viên đăng ký học là: 60");
        txtNumberStudentsHaveScore.setText("Số lượng sinh viên đã được nhâp điểm là: 30");

        return view;
    }
}
