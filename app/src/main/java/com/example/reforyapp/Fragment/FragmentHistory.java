package com.example.reforyapp.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reforyapp.CheckBoxAdapter;
import com.example.reforyapp.R;
import com.example.reforyapp.RoomDataBase.DataBase;
import com.example.reforyapp.RoomDataBase.MyData;
import com.example.reforyapp.SharedDataViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.Manifest;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import android.widget.CheckBox;

import android.view.View;

import android.content.ContentResolver;

import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.*;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHistory#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHistory extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentHistory() {
        // Required empty public constructor
    }

    MyData nowSelectedData;
    String photoURL = "";

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    //ImageView selectedImage;
    Button cameraBtn;
    String currentPhotoPath;

    CheckBoxAdapter checkBoxAdapter;
    ListView listView;
    List<MyData> dataList = new ArrayList<>();

    TextView tvUrl, tvSelectedDate;
    Button btCreate, btModify;

    FloatingActionButton btDelete;

    SharedDataViewModel sharedDataViewModel;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHistory.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHistory newInstance(String param1, String param2) {
        FragmentHistory fragment = new FragmentHistory();
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

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        // UI 初始化
        listView = rootView.findViewById(R.id.listView);
        btDelete = rootView.findViewById(R.id.button_Delete);

        checkBoxAdapter = new CheckBoxAdapter(getContext(), dataList);
        listView.setAdapter(checkBoxAdapter);

        // ListView 點擊顯示資料
        listView.setOnItemClickListener((parent, view, position, id) -> {
            MyData selectedData = dataList.get(position);
            showData(selectedData);
        });

        sharedDataViewModel = new ViewModelProvider(requireActivity()).get(SharedDataViewModel.class);

        // 觀察 LiveData
        sharedDataViewModel.getAllDataLive().observe(getViewLifecycleOwner(), updatedList -> {
            dataList.clear();
            dataList.addAll(updatedList);
            checkBoxAdapter.notifyDataSetChanged();
        });

        // 刪除資料（刪除勾選的項目）
        btDelete.setOnClickListener(v -> {
            Set<Integer> idsToDelete = checkBoxAdapter.getSelectedIds();

            if (idsToDelete.isEmpty()) {
                Toast.makeText(getContext(), "請先勾選要刪除的資料", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                for (MyData data : dataList) {
                    if (idsToDelete.contains(data.getId())) {
                        DataBase.getInstance(getActivity()).getDataUao().deleteData(data);
                    }
                }

                requireActivity().runOnUiThread(() -> {
                    nowSelectedData = null;
                    //selectedImage.setImageResource(android.R.drawable.ic_menu_report_image);

                    checkBoxAdapter.clearSelections(); // 刪完後清除勾選
                    //refreshListView(false);
                    Toast.makeText(getContext(), "已刪除勾選資料", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        //refreshListView(false);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void showData(MyData data) {
        nowSelectedData = data;
//        tvUrl.setText(data.getPicURL());
//        tvName.setText(data.getName());

        try {
            Uri uri = Uri.parse(data.getPicURL());
            InputStream imageStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            //selectedImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            //selectedImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }

//    private void refreshListView(boolean autoShowLast) {
//        new Thread(() -> {
//            List<MyData> allData = DataBase.getInstance(getActivity()).getDataUao().displayAll();
//            requireActivity().runOnUiThread(() -> {
//                dataList.clear();
//                dataList.addAll(allData);
//                checkBoxAdapter.notifyDataSetChanged();
//            });
//        }).start();
//    }
}