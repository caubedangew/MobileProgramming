package com.btl.login.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.btl.login.MainActivity;
import com.btl.login.R;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.configurations.CloudinaryConfig;
import com.btl.login.entities.Teacher;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    public static final int REQUEST_CAMERA = 1;
    public static final int REQUEST_GALLERY = 2;
    public static final int REQUEST_PERMISSION_CODE = 100;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    AppDatabase appDatabase;
    Button btnRegister;
    EditText eTxtEmail, eTxtPassword, eTxtFirstName, eTxtLastName;
    ImageView imageViewAvatar;
    Uri imageUri;

    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                redirectToLoginPage(loginFragment);
            }
        };

        spannableText.setSpan(clickableSpan, 31, 36, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView txtLoginRedirect = view.findViewById(R.id.txtLoginRedirect);
        btnRegister = view.findViewById(R.id.btnRegister);
        eTxtEmail = view.findViewById(R.id.eTxtEmail);
        eTxtPassword = view.findViewById(R.id.eTxtPassword);
        eTxtFirstName = view.findViewById(R.id.eTxtFirstName);
        eTxtLastName = view.findViewById(R.id.eTxtLastName);
        txtLoginRedirect.setText(spannableText);
        txtLoginRedirect.setMovementMethod(LinkMovementMethod.getInstance());

        imageViewAvatar = view.findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(myView ->
        {
            final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Which options do you want?");
            builder.setItems(options, (dialog, which) -> {
                if (options[which].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } else
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

        return view;
    }

    private void redirectToLoginPage(LoginFragment loginFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, loginFragment);
        fragmentTransaction.commit();
        ((MainActivity) getActivity()).checkBackStack();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        appDatabase = AppDatabase.getDatabase(getContext());

        btnRegister.setOnClickListener(v -> registerUser(eTxtEmail.getText().toString(),
                eTxtPassword.getText().toString(), eTxtFirstName.getText().toString(),
                eTxtLastName.getText().toString(), imageUri, "teacher"));
    }

    private void registerUser(String email, String password, String firstName, String lastName, Uri imageUri, String role) {
        if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || imageUri == null) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ các trường!!!", Toast.LENGTH_LONG).show();
        } else {
            btnRegister.setEnabled(false);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            addRoleToFirestore(user.getUid(), "teacher", firestore);
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                                new AlertDialog.Builder(getContext())
                                        .setMessage("Ứng dụng cần quyền này để truy cập vào bộ nhớ của bạn.")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            requestPermissions(
                                                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                                                    REQUEST_PERMISSION_CODE);
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            } else {
                                Log.d("Permissions", "READ_EXTERNAL_STORAGE permission already granted.");
                                new Thread(() -> {
                                    uploadImageToCloudinary(user, firestore);
                                }).start();
                            }
                            createUserInstance(user.getEmail(), eTxtFirstName.getText().toString(), eTxtLastName.getText().toString());
                        } else {
                            Toast.makeText(getContext(), "This email has been used on another account",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        btnRegister.setEnabled(true);
    }

    private void addRoleToFirestore(String userId, String role, FirebaseFirestore firestore) {
        DocumentReference userRef = firestore.collection("users").document(userId);
        Map<String, String> userRole = new HashMap<>();
        userRole.put("role", role);
        userRef.set(userRole)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Role added successfully"))
                .addOnFailureListener(e -> Log.d("Firestore", "Error adding role", e));
    }

    private void uploadImageToCloudinary(FirebaseUser user, FirebaseFirestore firestore) {
        Cloudinary cloudinary = CloudinaryConfig.getCloudinaryClient();

        File file = null;
        if (imageUri.getScheme().equals("content")) {
            file = new File(getRealPathFromURI(imageUri));
        } else {
            file = new File(imageUri.getPath());
        }
        if (file.exists()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                String imageUrl = (String) uploadResult.get("url");
                updateUserProfile(user.getUid(), imageUrl, firestore, user);
            } catch (Exception e) {
                Log.e("Cloudinary", "Error uploading image", e);
            }
        } else {
            Log.e("Cloudinary", "File not found: " + imageUri.getPath());
        }
    }

    private void createUserInstance(String email, String firstName, String lastName) {
        Teacher teacher = new Teacher(firstName, lastName, email, 1);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(() -> {
            appDatabase.teacherDao().addTeachers(teacher);
            handler.post(() -> {
                LoginFragment loginFragment = LoginFragment.newInstance(eTxtEmail.getText().toString());
                redirectToLoginFragment(loginFragment);
                Toast.makeText(getContext(), "Tạo tài khoản thành công!", Toast.LENGTH_LONG).show();
            });
        });
    }

    private void updateUserProfile(String userId, String url, FirebaseFirestore firestore, FirebaseUser user) {
        firestore.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentReference userRef = firestore.collection("users").document(userId);
            transaction.update(userRef, "profileImageUrl", url.replace("http://", "https://"));
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("Firestore", "Profile updated successfully");
        }).addOnFailureListener(e -> {
            Log.d("Firestore", "Transaction failed", e);
            deleteUserAccount(user);
        });
    }

    private void redirectToLoginFragment(LoginFragment loginFragment) {
        ((Toolbar) requireActivity().findViewById(R.id.toolbar)).setTitle("Đăng nhập");
        ((NavigationView) requireActivity().findViewById(R.id.nav_view)).getMenu().findItem(R.id.login).setChecked(true);
        redirectToLoginPage(loginFragment);
    }

    private void deleteUserAccount(FirebaseUser user) {
        if (user != null) {
            firestore.collection("users").document(user.getUid()).delete();
            user.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Auth", "User account deleted due to failed image upload.");
                } else {
                    Log.d("Auth", "Failed to delete user account", task.getException());
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                imageViewAvatar.setImageBitmap(bitmap);

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

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        String path = null;
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            path = cursor.getString(columnIndex);
            cursor.close();
        }
        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("Try to request", "Process requesting permissions");

        if (requestCode == REQUEST_PERMISSION_CODE) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new Thread(() -> {
                    uploadImageToCloudinary(user, firestore);
                }).start();
            } else {
                deleteUserAccount(user);
                Toast.makeText(getContext(), "Không thể cấp quyền truy cập vào bộ nhớ", Toast.LENGTH_LONG).show();
            }
        }
    }
}