package com.btl.login;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.btl.login.adapter.TeachingClassesAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TeachingClassesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeachingClassesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TeachingClassesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeachingClassesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeachingClassesFragment newInstance(String param1, String param2) {
        TeachingClassesFragment fragment = new TeachingClassesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teaching_classes, container, false);
        ArrayList<Object> listTeachingClasses = new ArrayList<>();
        listTeachingClasses.add("");
        listTeachingClasses.add("");
        listTeachingClasses.add("");
        listTeachingClasses.add("");
        listTeachingClasses.add("");

        TeachingClassesAdapter teachingClassesAdapter = new TeachingClassesAdapter(getContext(), listTeachingClasses);

        ListView listView = view.findViewById(R.id.listTeachingSubjects);
        listView.setAdapter(teachingClassesAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            TeachingClassesFragment teachingClassesFragment = new TeachingClassesFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, teachingClassesFragment);
            fragmentTransaction.commit();
        });
        return view;
    }
}