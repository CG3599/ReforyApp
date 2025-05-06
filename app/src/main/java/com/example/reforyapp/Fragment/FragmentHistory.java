package com.example.reforyapp.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reforyapp.Adapter.ListViewAdapter;
import com.example.reforyapp.R;
import com.example.reforyapp.RoomDataBase.DataBase;
import com.example.reforyapp.RoomDataBase.MyData;
import com.example.reforyapp.SharedDataViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.*;

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

    ListViewAdapter ListViewAdapter;
    ListView listView;
    List<MyData> dataList = new ArrayList<>();

    FloatingActionButton btDelete;

    SharedDataViewModel sharedDataViewModel;

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

        ListViewAdapter = new ListViewAdapter(getContext(), dataList);
        listView.setAdapter(ListViewAdapter);

        // ListView 點擊顯示資料
        listView.setOnItemClickListener((parent, view, position, id) -> {
            MyData selectedData = dataList.get(position);
            showData(selectedData);
            AlertDialog.Builder alert1 = createAlertDialog(selectedData);
            alert1.show();//將設置的內容顯示
        });

        sharedDataViewModel = new ViewModelProvider(requireActivity()).get(SharedDataViewModel.class);

        // 觀察 LiveData
        sharedDataViewModel.getAllDataLive().observe(getViewLifecycleOwner(), updatedList -> {
            dataList.clear();
            dataList.addAll(updatedList);
            ListViewAdapter.notifyDataSetChanged();
        });

        // 刪除資料（刪除勾選的項目）
        btDelete.setOnClickListener(v -> {
            Set<Integer> idsToDelete = ListViewAdapter.getSelectedIds();

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

                    ListViewAdapter.clearSelections(); // 刪完後清除勾選
                    Toast.makeText(getContext(), "已刪除勾選資料", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

        return rootView;
    }

    private void showData(MyData data) {
        nowSelectedData = data;
    }

    //AlertDialog設置
    private AlertDialog.Builder createAlertDialog(MyData data) {
        AlertDialog.Builder listViewItemAlert = new AlertDialog.Builder(requireActivity());

        // layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);
        listViewItemAlert.setView(dialogView);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        ImageView imageView = dialogView.findViewById(R.id.dialog_image);

        titleTextView.setText("名稱: " + data.getName());
        messageTextView.setText("數量: " + data.getCount() + "\n時間: " + data.getTime());

        try {
            Uri uri = Uri.parse(data.getPicURL());
            InputStream imageStream = requireActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            // 自動縮小圖片
            int maxWidth = 800;
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            if (width > maxWidth) {
                float ratio = (float) maxWidth / width;
                bitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, (int)(height * ratio), true);
            }

            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        listViewItemAlert.setPositiveButton("關閉", (dialog, which) -> {});

        return listViewItemAlert;
    }
}