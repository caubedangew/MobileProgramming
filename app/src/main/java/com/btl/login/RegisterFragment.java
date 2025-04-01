package com.btl.login;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private FirebaseAuth mAuth;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    Button btnRegister;
    EditText eTxtEmail, eTxtPassword;
    ImageView imageViewAvatar;
    String currentPhotoPath;
    Uri imageUri;
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
        mAuth = FirebaseAuth.getInstance();
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
                fragmentTransaction.replace(R.id.fragment_container, loginFragment);
                fragmentTransaction.commit();
            }
        };

        spannableText.setSpan(clickableSpan, 31, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView txtLoginRedirect = view.findViewById(R.id.txtLoginRedirect);
        btnRegister = view.findViewById(R.id.btnRegister);
        eTxtEmail = view.findViewById(R.id.eTxtEmail);
        eTxtPassword = view.findViewById(R.id.eTxtPassword);
        txtLoginRedirect.setText(spannableText);
        txtLoginRedirect.setMovementMethod(LinkMovementMethod.getInstance());

        imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(myView ->
        {
//            Toast.makeText(getContext(), "Open", Toast.LENGTH_SHORT).show();
            final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Which options do you want?");
            builder.setItems(options, (dialog, which) -> {
                if (options[which].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                    else
                        Toast.makeText(getContext(), intent.resolveActivity(requireContext().getPackageManager()).toString(), Toast.LENGTH_LONG).show();
                } else if (options[which].equals("Choose From Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_GALLERY);
                } else if (options[which].equals("Cancel")) {
                    dialog.dismiss();
                }
            });
            builder.show();
        });

        btnRegister.setOnClickListener(v -> registerUser(eTxtEmail.getText().toString(), eTxtPassword.getText().toString(), "teacher"));

        return view;
    }

    private void registerUser(String email, String password, String role) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Map<String, Object> userRole = new HashMap<>();
                            userRole.put("role", role);
                            db.collection("userRole").document(user.getUid())
                                    .set(userRole)
                                    .addOnSuccessListener(unused -> {
                                        Log.d("Complete to add role", "Add role successfully");
                                        uploadImageToFirebase();
                                        Toast.makeText(getContext(), "Success", Toast.LENGTH_LONG).show();
                                        mAuth.signOut();
                                        LoginFragment loginFragment = LoginFragment.newInstance(email);
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
                                        fragmentTransaction.commit();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.d("Can't add role", e.getMessage());
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        }
                    } else {
                        Toast.makeText(getContext(), "Authentication failed!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            String path = "user_avatars/" + mAuth.getCurrentUser().getUid() + ".jpg";
            StorageReference imageRef = storageRef.child(path);

            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveImageUrlToFirestore(uri.toString());
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = mAuth.getCurrentUser().getUid(); // Lấy UID người dùng

        db.collection("users").document(userId)
                .update("profile_image_url", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Profile image URL saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to save image URL", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                imageViewAvatar.setImageBitmap(bitmap);

                // Lưu URI tạm thời
                imageUri = getImageUriFromBitmap(bitmap);
            } else if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                imageViewAvatar.setImageURI(imageUri);
            }
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        File file = new File(requireContext().getCacheDir(), "temp.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Uri.fromFile(file);
    }
}