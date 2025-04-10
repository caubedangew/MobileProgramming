package com.btl.login.fragments;

import static com.btl.login.fragments.RegisterFragment.REQUEST_CAMERA;
import static com.btl.login.fragments.RegisterFragment.REQUEST_GALLERY;
import static com.btl.login.fragments.RegisterFragment.REQUEST_PERMISSION_CODE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.btl.login.MainActivity;
import com.btl.login.R;
import com.btl.login.configurations.AppDatabase;
import com.btl.login.configurations.CloudinaryConfig;
import com.btl.login.entities.Teacher;
import com.btl.login.userViewModel.UserViewModel;
import com.btl.login.utils.DateUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {
    AppDatabase appDatabase;
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Button btnChangeInfo, btnSubmitChanging;
    EditText eTxtFirstName, eTxtEmail, eTxtAddress, eTxtPhoneNumber, eTxtDateOfBirth, eTxtLastName;
    RadioButton male, female;
    UserViewModel userViewModel;
    ShapeableImageView imgAvatar;
    FirebaseUser currentUser;
    Teacher currentTeacher;
    Uri imageUri;
    Uri currentUri;

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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appDatabase = AppDatabase.getDatabase(getContext());
        currentUser = mAuth.getCurrentUser();

        btnChangeInfo = view.findViewById(R.id.btnChangeInfo);
        btnSubmitChanging = view.findViewById(R.id.btnSubmitChanging);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        imgAvatar.setEnabled(false);

        if (currentUser != null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executorService.execute(() -> {
                currentTeacher = appDatabase.teacherDao().getTeacherByEmail(currentUser.getEmail());
                handler.post(() -> {
                    customFormFields(view, currentTeacher);
                });
            });
            firestore.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot.exists()) {
                            if (!snapshot.getString("profileImageUrl").isEmpty()) {
                                currentUri = Uri.parse(snapshot.getString("profileImageUrl").replace("http://", "https://"));
                                imageUri = Uri.parse(snapshot.getString("profileImageUrl").replace("http://", "https://"));
                                Glide.with(requireContext())
                                        .load(currentUri)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imgAvatar);
                            }
                        } else {
                            Toast.makeText(getContext(), "Document của user này không tồn tại!!!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error in getting document", e.getMessage());
                    });
        } else {
            Toast.makeText(getContext(), "Không lấy được dữ liệu của user đang đăng nhập", Toast.LENGTH_LONG).show();
        }

        btnChangeInfo.setOnClickListener(v -> {
            btnSubmitChanging.setVisibility(View.VISIBLE);
            btnSubmitChanging.setEnabled(true);
            btnChangeInfo.setEnabled(false);
            eTxtFirstName.setEnabled(true);
            eTxtLastName.setEnabled(true);
            eTxtAddress.setEnabled(true);
            eTxtPhoneNumber.setEnabled(true);
            eTxtDateOfBirth.setEnabled(true);
            imgAvatar.setEnabled(true);
            male.setEnabled(true);
            female.setEnabled(true);
        });

        btnSubmitChanging.setOnClickListener(v -> {
            btnSubmitChanging.setEnabled(false);
            updateUserData(view, currentTeacher);
            btnSubmitChanging.setVisibility(View.GONE);
            btnChangeInfo.setEnabled(true);
        });

        imgAvatar.setOnClickListener(v -> {
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
    }

    private void updateUserData(@NonNull View view, Teacher currentTeacher) {
        if (currentTeacher != null) {
            currentTeacher.setFirstName(eTxtFirstName.getText().toString());
            currentTeacher.setLastName(eTxtLastName.getText().toString());
            currentTeacher.setDateOfBirth(!eTxtDateOfBirth.getText().toString().equals("00/00/0000") ? DateUtils.convertDateToTimestamp(eTxtDateOfBirth.getText().toString()) : null);
            currentTeacher.setGender(male.isChecked());
            currentTeacher.setPhoneNumber(eTxtPhoneNumber.getText().toString());
            currentTeacher.setAddress(eTxtAddress.getText().toString());
            currentTeacher.setUpdatedBy(System.currentTimeMillis());

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executorService.execute(() -> {
                appDatabase.teacherDao().updateTeacher(currentTeacher);
                handler.post(() -> {
                    customFormFields(view, currentTeacher);
                });
            });
        }
        if (!currentUri.equals(imageUri)) {
            new Thread(() -> {
//                CloudinaryConfig.deleteImage(currentUri.toString().substring(currentUri.toString().lastIndexOf("/") + 1, currentUri.toString().lastIndexOf(".")));
                uploadImageToCloudinary(currentUser, firestore);
            }).start();
        }
    }

    private void customFormFields(@NonNull View view, Teacher currentTeacher) {
        if (currentTeacher != null) {
            TextView txtFirstName = view.findViewById(R.id.userFirstName).findViewById(R.id.txtComponent);
            eTxtFirstName = view.findViewById(R.id.userFirstName).findViewById(R.id.eTxtComponent);

            txtFirstName.setText("Tên");
            eTxtFirstName.setText(currentTeacher.getFirstName());
            eTxtFirstName.setEnabled(false);

            TextView txtLastName = view.findViewById(R.id.userLastName).findViewById(R.id.txtComponent);
            eTxtLastName = view.findViewById(R.id.userLastName).findViewById(R.id.eTxtComponent);

            txtLastName.setText("Họ");
            eTxtLastName.setText(currentTeacher.getLastName());
            eTxtLastName.setEnabled(false);

            TextView txtDateOfBirth = view.findViewById(R.id.userDateOfBirth).findViewById(R.id.txtComponent);
            eTxtDateOfBirth = view.findViewById(R.id.userDateOfBirth).findViewById(R.id.eTxtComponent);

            txtDateOfBirth.setText("Ngày sinh");
            eTxtDateOfBirth.setText(currentTeacher.getDateOfBirth() != null && currentTeacher.getDateOfBirth() != 0 ? DateUtils.convertTimestampToDate(currentTeacher.getDateOfBirth()) : "00/00/0000");
            eTxtDateOfBirth.setEnabled(false);
            eTxtDateOfBirth.addTextChangedListener(new TextWatcher() {
                private boolean isUpdating;
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isUpdating) return;

                    isUpdating = true;

                    String current = s.toString();
                    String clean = current.replaceAll("[^\\d]", "");

                    int cursorPosition = eTxtDateOfBirth.getSelectionStart();

                    StringBuilder formatted = new StringBuilder("00/00/0000");

                    int i = 0;
                    for (char c : clean.toCharArray()) {
                        while (i < formatted.length() && formatted.charAt(i) == '/') i++;
                        if (i < formatted.length()) {
                            formatted.setCharAt(i, c);
                            i++;
                        }
                    }

                    eTxtDateOfBirth.removeTextChangedListener(this);
                    eTxtDateOfBirth.setText(formatted.toString());

                    // Tính toán lại vị trí con trỏ
                    int newCursorPosition = cursorPosition;
                    if (cursorPosition < formatted.length()) {
                        if (formatted.charAt(cursorPosition) == '/') {
                            newCursorPosition++;
                        }
                    }

                    if (newCursorPosition > formatted.length()) {
                        newCursorPosition = formatted.length();
                    }

                    eTxtDateOfBirth.setSelection(newCursorPosition);
                    eTxtDateOfBirth.addTextChangedListener(this);

                    isUpdating = false;
                }
            });

            TextView txtGender = view.findViewById(R.id.txtUserGender);
            male = view.findViewById(R.id.male);
            female = view.findViewById(R.id.female);

            txtGender.setText("Giới tính");
            male.setChecked(currentTeacher.isGender());
            female.setChecked(!currentTeacher.isGender());
            male.setEnabled(false);
            female.setEnabled(false);

            TextView txtPhoneNumber = view.findViewById(R.id.userPhoneNumber).findViewById(R.id.txtComponent);
            eTxtPhoneNumber = view.findViewById(R.id.userPhoneNumber).

                    findViewById(R.id.eTxtComponent);

            txtPhoneNumber.setText("Số điện thoại");
            eTxtPhoneNumber.setText(currentTeacher.getPhoneNumber());
            eTxtPhoneNumber.setEnabled(false);

            TextView txtAddress = view.findViewById(R.id.userAddress).findViewById(R.id.txtComponent);
            eTxtAddress = view.findViewById(R.id.userAddress).

                    findViewById(R.id.eTxtComponent);

            txtAddress.setText("Địa chỉ");
            eTxtAddress.setText(currentTeacher.getAddress());
            eTxtAddress.setEnabled(false);

            TextView txtEmail = view.findViewById(R.id.userEmail).findViewById(R.id.txtComponent);
            eTxtEmail = view.findViewById(R.id.userEmail).

                    findViewById(R.id.eTxtComponent);

            txtEmail.setText("Email");
            eTxtEmail.setText(currentUser.getEmail());
            eTxtEmail.setEnabled(false);
        }
    }

    private void redirectToSpecifiedFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        ((MainActivity) requireActivity()).checkBackStack();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                imgAvatar.setImageBitmap(bitmap);

                imageUri = getImageUriFromBitmap(bitmap);
            } else if (requestCode == REQUEST_GALLERY) {
                imageUri = data.getData();
                imgAvatar.setImageURI(imageUri);
            }
        }
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
                if (imageUrl != null) {
                    firestore.collection("users").document(user.getUid())
                            .update("profileImageUrl", imageUrl.replace("http://", "https://"))
                            .addOnSuccessListener(unused -> {
                                Log.i("Cloudinary", "Upload Successfully!!!");
                            }).addOnFailureListener(e -> {
                                e.printStackTrace();
                            });
                }
            } catch (Exception e) {
                Log.e("Cloudinary", "Error uploading image", e);
            }
        } else {
            Log.e("Cloudinary", "File not found: " + imageUri.getPath());
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
                Toast.makeText(getContext(), "Không thể cấp quyền truy cập vào bộ nhớ", Toast.LENGTH_LONG).show();
            }
        }
    }
}