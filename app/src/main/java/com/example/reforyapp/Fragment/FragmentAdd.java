package com.example.reforyapp.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reforyapp.R;
import com.example.reforyapp.RoomDataBase.DataBase;
import com.example.reforyapp.RoomDataBase.MyData;
import com.example.reforyapp.SharedDataViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FragmentAdd extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentAdd() {
        // Required empty public constructor
    }

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;

    String currentPhotoPath;

    ImageView displayImageView;

    MyData nowSelectedData;//取得在畫面上顯示中的資料內容

    private Button btnTime, btnCamera;
    private TextView tvSelectedDate, tvPicUrl;

    String photoURL = "";

    SharedDataViewModel sharedDataViewModel;

    // TODO: Rename and change types and number of parameters
    public static FragmentAdd newInstance(String param1, String param2) {
        FragmentAdd fragment = new FragmentAdd();
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

        View rootView = inflater.inflate(R.layout.fragment_add, container, false);

        Button btCreate = rootView.findViewById(R.id.button_Create);
        Button btModify = rootView.findViewById(R.id.button_Modify);
        EditText edName = rootView.findViewById(R.id.editText_Name);
        EditText edCount = rootView.findViewById(R.id.editText_Count);

        btnTime = rootView.findViewById(R.id.button_Time);
        tvSelectedDate = rootView.findViewById(R.id.textView_Time);

        btnTime.setOnClickListener(v -> showDatePickerDialog());

        tvPicUrl = rootView.findViewById(R.id.textView_PicURL);

        sharedDataViewModel = new ViewModelProvider(requireActivity()).get(SharedDataViewModel.class);

        btnCamera = rootView.findViewById(R.id.button_Camera);

        // 拍照
        btnCamera.setOnClickListener(v -> verifyPermissions());

        displayImageView = rootView.findViewById(R.id.displayImageView);

        // 設置修改資料的事件
        btModify.setOnClickListener((v) -> {
            new Thread(() -> {
                if(nowSelectedData ==null) return;//如果目前沒前台沒有資料，則以下程序不執行
                String name = edName.getText().toString();
                String count = edCount.getText().toString();
                String time = tvSelectedDate.getText().toString();
                String picURL = tvPicUrl.getText().toString();
                MyData data = new MyData(nowSelectedData.getId(), name, count, time, picURL);
                DataBase.getInstance(requireActivity()).getDataUao().updateData(data);
                requireActivity().runOnUiThread(() -> {
                    edName.setText("");
                    edCount.setText("");
                    tvSelectedDate.setText("");
                    tvPicUrl.setText("");
                    nowSelectedData = null;
                    //myAdapter.refreshView();
                    Toast.makeText(requireContext(), "已更新資訊！", Toast.LENGTH_LONG).show();
                });
            }).start();

        });

        // 新增資料
        btCreate.setOnClickListener((v -> {
            new Thread(() -> {
                String name = edName.getText().toString();
                String count = edCount.getText().toString();
                String time = tvSelectedDate.getText().toString();
                String picURL = tvPicUrl.getText().toString();
                if (name.length() == 0) return;//如果名字欄沒填入任何東西，則不執行下面的程序
                MyData data = new MyData(name, count, time, picURL);
                DataBase.getInstance(requireActivity()).getDataUao().insertData(data);
                requireActivity().runOnUiThread(() -> {
                    //myAdapter.refreshView();
                    edName.setText("");
                    edCount.setText("");
                    tvSelectedDate.setText("");
                    tvPicUrl.setText("");
                });
            }).start();
        }));

        hideKeyboardOnOutsideTouch(rootView);

        return rootView;
    }

    // 執行DatePickerDialog
    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    //月從0開始，要加1
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        String selectedDate = selectedYear + "/" + (selectedMonth + 1) + "/" + selectedDayOfMonth;
                        tvSelectedDate.setText(selectedDate);
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // 拍照權限
    private void verifyPermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if (ContextCompat.checkSelfPermission(requireActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireActivity(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), permissions, CAMERA_PERM_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(requireActivity(), "需要相機權限才能使用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            File f = new File(currentPhotoPath);
            displayImageView.setImageURI(Uri.fromFile(f));

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            requireActivity().sendBroadcast(mediaScanIntent);

            photoURL = contentUri.toString();
            tvPicUrl.setText(photoURL);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            File photoFile;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                return;
            }

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireActivity(), "com.example.reforyapp.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    // 點擊空白處隱藏鍵盤
    private void hideKeyboardOnOutsideTouch(View view) {
        // 如果不是EditText，則加入touch listener
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hideKeyboard();
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.performClick();  // 通知系統這是一次點擊
                }
                return false;  // 不消耗事件，繼續傳遞給其他View
            });
        }
    }

    // 隱藏鍵盤
    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view == null) return;

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}