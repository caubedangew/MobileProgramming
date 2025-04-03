package com.btl.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.btl.login.userViewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Button btnChangeInfo, btnSubmitChanging;
    EditText eTxtFirstName, eTxtEmail, eTxtAddress, eTxtPhoneNumber, eTxtDateOfBirth, eTxtLastName;
    RadioButton male, female;
    UserViewModel userViewModel;

    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AtomicReference<View> view = new AtomicReference<>();
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getIsLoggedIn().observe(requireActivity(), isLoggedIn -> {
            if (isLoggedIn) {
                view.set(inflater.inflate(R.layout.fragment_user, container, false));
            }
            else {
                final CharSequence[] options = {"Login", "Register", "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Which options do you want?");
                builder.setItems(options, (dialog, which) -> {
                    if (options[which].equals("Login")) {
                        LoginFragment loginFragment = new LoginFragment();
                        redirectToSpecifiedFragment(loginFragment);
                    } else if (options[which].equals("Register")) {
                        RegisterFragment registerFragment = new RegisterFragment();
                        redirectToSpecifiedFragment(registerFragment);
                    } else if (options[which].equals("Cancel")) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
        return view.get();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        customFormFields(view);

        btnChangeInfo = view.findViewById(R.id.btnChangeInfo);
        btnSubmitChanging = view.findViewById(R.id.btnSubmitChanging);

        btnChangeInfo.setOnClickListener(v -> {
            btnSubmitChanging.setVisibility(View.VISIBLE);
            btnChangeInfo.setEnabled(false);
            eTxtFirstName.setEnabled(true);
            eTxtLastName.setEnabled(true);
            eTxtAddress.setEnabled(true);
            eTxtEmail.setEnabled(true);
            eTxtPhoneNumber.setEnabled(true);
            eTxtDateOfBirth.setEnabled(true);
            male.setEnabled(true);
            female.setEnabled(true);
        });
    }

    private void customFormFields(@NonNull View view) {
        TextView txtFirstName = view.findViewById(R.id.userFirstName).findViewById(R.id.txtComponent);
        eTxtFirstName = view.findViewById(R.id.userFirstName).findViewById(R.id.eTxtComponent);

        txtFirstName.setText("Tên");
        eTxtFirstName.setText("Abc");
        eTxtFirstName.setEnabled(false);

        TextView txtLastName = view.findViewById(R.id.userLastName).findViewById(R.id.txtComponent);
        eTxtLastName = view.findViewById(R.id.userLastName).findViewById(R.id.eTxtComponent);

        txtLastName.setText("Họ");
        eTxtLastName.setText("Abc");
        eTxtLastName.setEnabled(false);

        TextView txtDateOfBirth = view.findViewById(R.id.userDateOfBirth).findViewById(R.id.txtComponent);
        eTxtDateOfBirth = view.findViewById(R.id.userDateOfBirth).findViewById(R.id.eTxtComponent);

        txtDateOfBirth.setText("Ngày sinh");
        eTxtDateOfBirth.setText("Abc");
        eTxtDateOfBirth.setEnabled(false);

        TextView txtGender = view.findViewById(R.id.txtUserGender);
        male = view.findViewById(R.id.male);
        female = view.findViewById(R.id.female);

        txtGender.setText("Giới tính");
        male.setChecked(true);
        male.setEnabled(false);
        female.setEnabled(false);

        TextView txtPhoneNumber = view.findViewById(R.id.userPhoneNumber).findViewById(R.id.txtComponent);
        eTxtPhoneNumber = view.findViewById(R.id.userPhoneNumber).findViewById(R.id.eTxtComponent);

        txtPhoneNumber.setText("Số điện thoại");
        eTxtPhoneNumber.setText("0909009900");
        eTxtPhoneNumber.setEnabled(false);

        TextView txtAddress = view.findViewById(R.id.userAddress).findViewById(R.id.txtComponent);
        eTxtAddress = view.findViewById(R.id.userAddress).findViewById(R.id.eTxtComponent);

        txtAddress.setText("Địa chỉ");
        eTxtAddress.setText("Abc");
        eTxtAddress.setEnabled(false);

        TextView txtEmail = view.findViewById(R.id.userEmail).findViewById(R.id.txtComponent);
        eTxtEmail = view.findViewById(R.id.userEmail).findViewById(R.id.eTxtComponent);

        txtEmail.setText("Email");
        eTxtEmail.setText("Abc");
        eTxtEmail.setEnabled(false);
    }

    private void redirectToSpecifiedFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}