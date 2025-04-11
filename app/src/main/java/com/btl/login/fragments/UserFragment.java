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

        // Kiểm tra người dùng hiện tại
        if (currentUser != null && currentUser.getEmail() != null) {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            // Lấy thông tin Teacher từ cơ sở dữ liệu
            executorService.execute(() -> {
                currentTeacher = appDatabase.teacherDao().getTeacherByEmail(currentUser.getEmail());
                handler.post(() -> {
                    if (currentTeacher != null) {
                        customFormFields(view, currentTeacher);
                    } else {
                        Log.d("UserFragment", "currentTeacher is null.");
                    }
                });
            });

            // Lấy thông tin từ Firestore
            firestore.collection("users").document(currentUser.getUid()).get()
                    .addOnCompleteListener(task -> {
                        DocumentSnapshot snapshot = task.getResult();
                        if (snapshot != null && snapshot.exists()) {
                            String profileImageUrl = snapshot.getString("profileImageUrl");
                            if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                                currentUri = Uri.parse(profileImageUrl.replace("http://", "https://"));
                                imageUri = Uri.parse(profileImageUrl.replace("http://", "https://"));
                                Glide.with(requireContext())
                                        .load(currentUri)
                                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imgAvatar);
                            } else {
                                Log.d("UserFragment", "profileImageUrl is null or empty.");
                            }
                        } else {
                            Log.d("UserFragment", "Document does not exist.");
                            Toast.makeText(getContext(), "Document của user này không tồn tại!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Error in getting document", e.getMessage());
                        Toast.makeText(getContext(), "Lỗi khi truy vấn Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(getContext(), "Không lấy được dữ liệu của user đang đăng nhập", Toast.LENGTH_LONG).show();
        }

        // Xử lý sự kiện nút "Change Info"
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

        // Xử lý sự kiện nút "Submit Changing"
        btnSubmitChanging.setOnClickListener(v -> {
            btnSubmitChanging.setEnabled(false);
            updateUserData(view, currentTeacher);
            btnSubmitChanging.setVisibility(View.GONE);
            btnChangeInfo.setEnabled(true);
        });

        // Xử lý sự kiện khi nhấn vào avatar
        imgAvatar.setOnClickListener(v -> {
            final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Which options do you want?");
            builder.setItems(options, (dialog, which) -> {
                if (options[which].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } else {
                        Toast.makeText(getContext(), "Không tìm thấy ứng dụng hỗ trợ chụp ảnh!", Toast.LENGTH_LONG).show();
                    }
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
            currentTeacher.setFirstName(eTxtFirstName.getText() != null ? eTxtFirstName.getText().toString() : "");
            currentTeacher.setLastName(eTxtLastName.getText() != null ? eTxtLastName.getText().toString() : "");
            currentTeacher.setDateOfBirth(!eTxtDateOfBirth.getText().toString().equals("00/00/0000") ?
                    DateUtils.convertDateToTimestamp(eTxtDateOfBirth.getText().toString()) : null);
            currentTeacher.setGender(male.isChecked());
            currentTeacher.setPhoneNumber(eTxtPhoneNumber.getText() != null ? eTxtPhoneNumber.getText().toString() : "");
            currentTeacher.setAddress(eTxtAddress.getText() != null ? eTxtAddress.getText().toString() : "");
            currentTeacher.setUpdatedBy(System.currentTimeMillis());

            ExecutorService executorService = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executorService.execute(() -> {
                appDatabase.teacherDao().updateTeacher(currentTeacher);
                handler.post(() -> {
                    if (currentTeacher != null) {
                        customFormFields(view, currentTeacher);
                    }
                });
            });
        }

        if (currentUri != null && imageUri != null && !currentUri.equals(imageUri)) {
            new Thread(() -> {
                if (currentUser != null && firestore != null) {
                    uploadImageToCloudinary(currentUser, firestore);
                } else {
                    Log.e("updateUserData", "Cloudinary upload failed due to null values.");
                }
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
        if (isAdded() && fragment != null) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.commit();

            // Kiểm tra BackStack trong MainActivity
            ((MainActivity) requireActivity()).checkBackStack();
        } else {
            Log.e("FragmentRedirect", "Fragment hoặc Activity không hợp lệ.");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult", "requestCode: " + requestCode + ", resultCode: " + resultCode);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                if (data != null && data.getExtras() != null) {
                    Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                    imgAvatar.setImageBitmap(bitmap);
                    imageUri = getImageUriFromBitmap(bitmap);
                } else {
                    Log.e("onActivityResult", "Dữ liệu camera không hợp lệ.");
                    Toast.makeText(getContext(), "Không lấy được ảnh từ camera.", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == REQUEST_GALLERY) {
                if (data != null && data.getData() != null) {
                    imageUri = data.getData();
                    imgAvatar.setImageURI(imageUri);
                } else {
                    Log.e("onActivityResult", "Dữ liệu thư viện ảnh không hợp lệ.");
                    Toast.makeText(getContext(), "Không lấy được ảnh từ thư viện.", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.d("onActivityResult", "Người dùng đã hủy thao tác.");
        } else {
            Log.e("onActivityResult", "Kết quả trả về không hợp lệ: resultCode = " + resultCode);
        }
    }

    private void uploadImageToCloudinary(FirebaseUser user, FirebaseFirestore firestore) {
        Cloudinary cloudinary = CloudinaryConfig.getCloudinaryClient();

        if (imageUri == null || imageUri.getScheme() == null) {
            Log.e("Cloudinary", "imageUri is null or invalid");
            Toast.makeText(getContext(), "URI của hình ảnh không hợp lệ.", Toast.LENGTH_LONG).show();
            return;
        }

        File file = null;
        if (imageUri.getScheme().equals("content")) {
            String realPath = getRealPathFromURI(imageUri);
            if (realPath != null && !realPath.isEmpty()) {
                file = new File(realPath);
            } else {
                Log.e("Cloudinary", "Invalid real path from URI.");
                Toast.makeText(getContext(), "Không thể lấy đường dẫn thực từ URI.", Toast.LENGTH_LONG).show();
                return;
            }
        } else {
            file = new File(imageUri.getPath());
        }

        if (file == null || !file.exists()) {
            Log.e("Cloudinary", "File not found: " + (imageUri.getPath() != null ? imageUri.getPath() : "null"));
            Toast.makeText(getContext(), "File không tồn tại. Hãy kiểm tra lại!", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Map uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
            String imageUrl = (String) uploadResult.get("url");

            if (imageUrl != null) {
                firestore.collection("users").document(user.getUid())
                        .update("profileImageUrl", imageUrl.replace("http://", "https://"))
                        .addOnSuccessListener(unused -> {
                            Log.i("Cloudinary", "Upload Successfully!!!");
                            Toast.makeText(getContext(), "Upload ảnh thành công!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Error updating Firestore", e);
                            Toast.makeText(getContext(), "Không thể cập nhật URL ảnh lên Firestore.", Toast.LENGTH_LONG).show();
                        });
            }
        } catch (Exception e) {
            Log.e("Cloudinary", "Error uploading image", e);
            Toast.makeText(getContext(), "Lỗi khi upload ảnh lên Cloudinary.", Toast.LENGTH_LONG).show();
        }
    }

    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e("getImageUriFromBitmap", "Bitmap is null.");
            Toast.makeText(requireContext(), "Hình ảnh không hợp lệ.", Toast.LENGTH_LONG).show();
            return null; // Trả về null nếu bitmap không hợp lệ
        }

        File file = new File(requireContext().getCacheDir(), "temp_" + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out); // Giảm chất lượng nén xuống 80
        } catch (IOException e) {
            Log.e("getImageUriFromBitmap", "Error writing bitmap to file", e);
            Toast.makeText(requireContext(), "Lỗi khi lưu hình ảnh!", Toast.LENGTH_LONG).show();
            return null; // Trả về null nếu xảy ra lỗi
        }

        if (!file.exists()) {
            Log.e("getImageUriFromBitmap", "File not created successfully.");
            Toast.makeText(requireContext(), "Không thể tạo tệp hình ảnh.", Toast.LENGTH_LONG).show();
            return null;
        }

        return Uri.fromFile(file);
    }

    public String getRealPathFromURI(Uri uri) {
        if (uri == null) {
            Log.e("getRealPathFromURI", "URI không hợp lệ.");
            return null;
        }

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        String path = null;

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    path = cursor.getString(columnIndex);
                }
            } catch (IllegalArgumentException e) {
                Log.e("getRealPathFromURI", "Cột DATA không tồn tại.", e);
            } finally {
                cursor.close();
            }
        } else {
            Log.e("getRealPathFromURI", "Không thể truy vấn URI: " + uri.toString());
        }

        return path;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("onRequestPermissionsResult", "Process requesting permissions");

        if (requestCode == REQUEST_PERMISSION_CODE) {
            FirebaseUser user = mAuth.getCurrentUser();

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (user != null) {
                    Log.i("onRequestPermissionsResult", "Quyền được chấp thuận.");
                    new Thread(() -> {
                        uploadImageToCloudinary(user, firestore);
                    }).start();
                } else {
                    Log.e("onRequestPermissionsResult", "Người dùng chưa đăng nhập.");
                    Toast.makeText(getContext(), "Người dùng chưa đăng nhập.", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.w("onRequestPermissionsResult", "Quyền bị từ chối.");
                Toast.makeText(getContext(), "Không thể cấp quyền truy cập vào bộ nhớ", Toast.LENGTH_LONG).show();
            }
        }
    }
}