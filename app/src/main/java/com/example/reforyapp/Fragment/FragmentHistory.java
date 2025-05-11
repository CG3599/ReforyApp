package com.example.reforyapp.Fragment;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.reforyapp.Adapter.RecyclerViewAdapter;
import com.example.reforyapp.R;
import com.example.reforyapp.RoomDataBase.DataBase;
import com.example.reforyapp.RoomDataBase.MyData;
import com.example.reforyapp.SharedDataViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.*;

public class FragmentHistory extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private List<MyData> dataList = new ArrayList<>();
    private FloatingActionButton btDelete;
    private SharedDataViewModel sharedDataViewModel;
    private MyData nowSelectedData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        btDelete = rootView.findViewById(R.id.button_Delete);

        adapter = new RecyclerViewAdapter(dataList, selectedData -> {
            nowSelectedData = selectedData;
            showDataDialog(selectedData);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        sharedDataViewModel = new ViewModelProvider(requireActivity()).get(SharedDataViewModel.class);
        sharedDataViewModel.getAllDataLive().observe(getViewLifecycleOwner(), updatedList -> {
            dataList.clear();
            dataList.addAll(updatedList);
            adapter.notifyDataSetChanged();
        });

        btDelete.setOnClickListener(v -> {
            Set<Integer> idsToDelete = adapter.getSelectedIds();
            if (idsToDelete.isEmpty()) {
                Toast.makeText(getContext(), "請先勾選要刪除的資料", Toast.LENGTH_SHORT).show();
                return;
            }

            // 有機會造成ConcurrentModificationException，原因邊迭代(for-each)dataList，又同時刪資料
//            new Thread(() -> {
//                for (MyData data : dataList) {
//                    if (idsToDelete.contains(data.getId())) {
//                        DataBase.getInstance(getActivity()).getDataUao().deleteData(data);
//                    }
//                }
//
//                requireActivity().runOnUiThread(() -> {
//                    nowSelectedData = null;
//                    adapter.clearSelections();
//                    Toast.makeText(getContext(), "已刪除勾選資料", Toast.LENGTH_SHORT).show();
//                });
//            }).start();

            //先把要刪除的資料暫存起來，完全不動dataList，之後再處理刪除
            new Thread(() -> {
                List<MyData> toDeleteList = new ArrayList<>();
                for (MyData data : dataList) {
                    if (idsToDelete.contains(data.getId())) {
                        // 過濾出要刪的清單
                        toDeleteList.add(data);
                    }
                }

                for (MyData data : toDeleteList) {
                    DataBase.getInstance(getActivity()).getDataUao().deleteData(data);
                }

                requireActivity().runOnUiThread(() -> {
                    adapter.clearSelections();
                    Toast.makeText(getContext(), "已刪除勾選資料", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });
        return rootView;
    }

    private void showDataDialog(MyData data) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);
        builder.setView(dialogView);

        AlertDialog alert = builder.create();
        alert.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView titleTextView = dialogView.findViewById(R.id.dialog_title);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_message);
        ShapeableImageView imageView = dialogView.findViewById(R.id.dialog_image);
        Button btnClose = dialogView.findViewById(R.id.btn_close);

        // AlertDialog文字顯示判斷，數量是否為空，顯示方式不同
        if(data.getCount().isEmpty()) {
            titleTextView.setText("名稱: " + data.getName());
            messageTextView.setText("時間: " + data.getTime());
        } else {
            titleTextView.setText("名稱: " + data.getName());
            messageTextView.setText("數量: " + data.getCount() + "\n時間: " + data.getTime());
        }
        try {
            Uri uri = Uri.parse(data.getPicURL());
            InputStream imageStream = requireActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);

            int imgWidth = bitmap.getWidth();
            int imgHeight = bitmap.getHeight();

            float density = getResources().getDisplayMetrics().density;
            int maxWidthPx = (int) (300 * density);   // 300dp->px
            int maxHeightPx = (int) (400 * density);  // 400dp->px

            // 計算兩個縮放比例
            float widthRatio = (float) maxWidthPx / imgWidth;
            float heightRatio = (float) maxHeightPx / imgHeight;

            float scaleRatio = Math.min(1.0f, Math.min(widthRatio, heightRatio)); // 只縮小

            int newWidth = imgWidth;
            int newHeight = imgHeight;

            if (scaleRatio < 1.0f) {
                newWidth = Math.round(imgWidth * scaleRatio);
                newHeight = Math.round(imgHeight * scaleRatio);
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            }

            imageView.setImageBitmap(bitmap);

            // 如果高度=400dp(最大高度)，而寬度小於maxWidth，調整ImageView寬度
            if (newHeight == maxHeightPx && newWidth < maxWidthPx) {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = newWidth;  // 設定為圖片寬度(px)
                imageView.setLayoutParams(params);
            } else {
                // 其他狀況，讓imageView寬度=match_parent(maxWidth)
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                imageView.setLayoutParams(params);
            }
        } catch (Exception e) {

            // 取消邊框
            imageView.setStrokeWidth(0);
            imageView.setStrokeColor(null);

            imageView.setImageResource(R.drawable.no_image_avaliable);
        }

        btnClose.setOnClickListener(v -> alert.dismiss());
        alert.show();
    }

}
