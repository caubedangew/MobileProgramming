package com.btl.login.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.btl.login.MainActivity;
import com.btl.login.R;
import com.btl.login.userViewModel.UserViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    Button btnLogin;
    EditText eTxtEmail, eTxtPassword;
    private static final String ARG_PARAM1 = "email";
    private String email;
    private UserViewModel userViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        SpannableString spannableString = new SpannableString("If you don't have an account, " +
                "please click Here");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle("Đăng ký");
                ((NavigationView) requireActivity().findViewById(R.id.nav_view)).getMenu().findItem(R.id.register).setChecked(true);
                ((NavigationView) requireActivity().findViewById(R.id.nav_view)).getMenu().findItem(R.id.login).setChecked(false);
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, registerFragment);
                fragmentTransaction.commit();
                ((MainActivity) getActivity()).checkBackStack();
            }
        };

        // Inflate the layout for this fragment
        spannableString.setSpan(clickableSpan, 43, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView txtRegisterRedirect = view.findViewById(R.id.txtRegisterRedirect);
        txtRegisterRedirect.setText(spannableString);
        txtRegisterRedirect.setMovementMethod(LinkMovementMethod.getInstance());

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        eTxtEmail = view.findViewById(R.id.eTxtEmail);
        if (getArguments() != null) {
            eTxtEmail.setText(getArguments().getString(ARG_PARAM1));
        }
        eTxtPassword = view.findViewById(R.id.eTxtPassword);

        btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> loginUser(eTxtEmail.getText().toString(), eTxtPassword.getText().toString()));

        return view;
    }

    private void loginUser(String email, String password) {
        if (eTxtEmail.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Email không được phép bỏ trống", Toast.LENGTH_LONG).show();
        } else if (eTxtPassword.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Password không được phép bỏ trống", Toast.LENGTH_LONG).show();
        } else {
            btnLogin.setEnabled(false);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            eTxtEmail.setText("");
                            eTxtPassword.setText("");
                            userViewModel.setLoggedIn(true);
                            HomeFragment homeFragment = new HomeFragment();
                            ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle("Trang chủ");
                            ((NavigationView) requireActivity().findViewById(R.id.nav_view)).getMenu().findItem(R.id.home).setChecked(true);
                            FragmentManager fragmentManager = getFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                            fragmentTransaction.commit();
                            ((MainActivity) getActivity()).checkBackStack();
                        } else {
                            btnLogin.setEnabled(true);
                            eTxtPassword.setText("");
                            Toast.makeText(getContext(), "Email or Password is incorrect", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}