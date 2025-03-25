package com.btl.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        customFormFields(view);

//        final CharSequence[] options = {"Login", "Register", "Cancel"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
//        builder.setTitle("Need to login before watch this page!!!");
//        builder.setItems(options, (dialog, which) -> {
//            if (!options[which].equals("Cancel")) {
//                if (options[which].equals("Login")) {
//                    LoginFragment loginFragment = new LoginFragment();
//
//                    fragmentTransaction.replace(android.R.id.content, loginFragment);
//                } else if (options[which].equals("Register")) {
//                    RegisterFragment registerFragment = new RegisterFragment();
//
//                    fragmentTransaction.replace(android.R.id.content, registerFragment);
//                }
//                fragmentTransaction.commit();
//            }
//            dialog.dismiss();
//        });
//
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
    }

    private static void customFormFields(@NonNull View view) {
        TextView txtFirstName = view.findViewById(R.id.userFirstName).findViewById(R.id.txtComponent);
        EditText eTxtFirstName = view.findViewById(R.id.userFirstName).findViewById(R.id.eTxtComponent);

        txtFirstName.setText("Tên");
        eTxtFirstName.setText("Abc");

        TextView txtLastName = view.findViewById(R.id.userLastName).findViewById(R.id.txtComponent);
        EditText eTxtLastName = view.findViewById(R.id.userLastName).findViewById(R.id.eTxtComponent);

        txtLastName.setText("Họ");
        eTxtLastName.setText("Abc");

        TextView txtDateOfBirth = view.findViewById(R.id.userDateOfBirth).findViewById(R.id.txtComponent);
        EditText eTxtDateOfBirth = view.findViewById(R.id.userDateOfBirth).findViewById(R.id.eTxtComponent);

        txtDateOfBirth.setText("Ngày sinh");
        eTxtDateOfBirth.setText("Abc");

        TextView txtGender = view.findViewById(R.id.txtUserGender);
        RadioButton male = view.findViewById(R.id.male);
        RadioButton female = view.findViewById(R.id.female);

        txtGender.setText("Giới tính");
        male.setChecked(true);

        TextView txtPhoneNumber = view.findViewById(R.id.userPhoneNumber).findViewById(R.id.txtComponent);
        EditText eTxtPhoneNumber = view.findViewById(R.id.userPhoneNumber).findViewById(R.id.eTxtComponent);

        txtPhoneNumber.setText("Số điện thoại");
        eTxtPhoneNumber.setText("0909009900");

        TextView txtAddress = view.findViewById(R.id.userAddress).findViewById(R.id.txtComponent);
        EditText eTxtAddress = view.findViewById(R.id.userAddress).findViewById(R.id.eTxtComponent);

        txtAddress.setText("Địa chỉ");
        eTxtAddress.setText("Abc");

        TextView txtEmail = view.findViewById(R.id.userEmail).findViewById(R.id.txtComponent);
        EditText eTxtEmail = view.findViewById(R.id.userEmail).findViewById(R.id.eTxtComponent);

        txtEmail.setText("Email");
        eTxtEmail.setText("Abc");
    }
}