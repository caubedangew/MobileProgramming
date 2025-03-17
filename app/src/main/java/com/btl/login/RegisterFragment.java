package com.btl.login;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragemnt.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        SpannableString spannableText = new SpannableString("Already have an account? Press Login");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                LoginFragment loginFragment = new LoginFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(android.R.id.content, loginFragment);
                fragmentTransaction.commit();
            }
        };

        spannableText.setSpan(clickableSpan, 31, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView txtLoginRedirect = view.findViewById(R.id.txtLoginRedirect);
        txtLoginRedirect.setText(spannableText);
        txtLoginRedirect.setMovementMethod(LinkMovementMethod.getInstance());

        ImageView imageView = view.findViewById(R.id.imageViewAvatar);
        imageView.setOnClickListener(myView ->
        {
            final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Which options do you want?");
            builder.setItems(options, (dialog, which) -> {
                if (options[which].equals("Take photo")) {
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        if (intent.resolveActivity(getPackageManager()) != null) {
//                            startActivityForResult(intent, 1);
//                        } else {
//                          Toast.makeText(getApplicationContext(), "Camera isn't available", Toast.LENGTH_LONG).show();
//                        }
//                        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                } else if (options[which].equals("Choose From Gallery")) {
//                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        intent.setType("image/*");
//                        startActivityForResult(intent, 2);
                } else if (options[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
        });
        return view;
    }
}