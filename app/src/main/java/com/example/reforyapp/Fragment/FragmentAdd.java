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
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reforyapp.MainActivity;
import com.example.reforyapp.R;
import com.example.reforyapp.RoomDataBase.DataBase;
import com.example.reforyapp.RoomDataBase.MyData;
import com.example.reforyapp.SharedDataViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.widget.CheckBox;

import android.view.View;

import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    //MyAdapter myAdapter;

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
        //Button btClear = rootView.findViewById(R.id.button_Delete);
        EditText edName = rootView.findViewById(R.id.editText_Name);
        EditText edCount = rootView.findViewById(R.id.editText_Count);
//        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
//        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));//設置分隔線
//        setRecyclerFunction(recyclerView);//設置RecyclerView左滑刪除


        btnTime = rootView.findViewById(R.id.button_Time);
        tvSelectedDate = rootView.findViewById(R.id.textView_Time);

        btnTime.setOnClickListener(v -> showDatePickerDialog());

        tvPicUrl = rootView.findViewById(R.id.textView_PicURL);

        sharedDataViewModel = new ViewModelProvider(requireActivity()).get(SharedDataViewModel.class);

        btnCamera = rootView.findViewById(R.id.button_Camera);

        // 拍照
        btnCamera.setOnClickListener(v -> verifyPermissions());

        displayImageView = rootView.findViewById(R.id.displayImageView);

        //設置修改資料的事件
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

        //清空資料
//        btClear.setOnClickListener((v -> {
//            edName.setText("");
//            edCount.setText("");
//            tvSelectedDate.setText("");
//            edPicURL.setText("");
//            nowSelectedData = null;
//        }));

        //新增資料
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

        //初始化RecyclerView
//        new Thread(() -> {
//            List<MyData> data = DataBase.getInstance(requireContext()).getDataUao().displayAll();
//            myAdapter = new MyAdapter(requireActivity(), data);
//            requireActivity().runOnUiThread(() -> {
//                recyclerView.setAdapter(myAdapter);
//
//                myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {//原本的樣貌
//                    @Override
//                    public void onItemClick(MyData myData) {}
//                });
//
//                //取得被選中的資料，並顯示於畫面
//                myAdapter.setOnItemClickListener((myData)-> {//匿名函式(原貌在上方)
//                    nowSelectedData = myData;
//                    edName.setText(myData.getName());
//                    edCount.setText(myData.getCount());
//                    tvSelectedDate.setText(myData.getTime());
//                    edPicURL.setText(String.valueOf(myData.getPicURL()));
//                });
//            });
//        }).start();

        return rootView;
    }

//    private static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
//
//        private List<MyData> myData;
//        private Activity activity;
//        private MyAdapter.OnItemClickListener onItemClickListener;
//
//        public MyAdapter(Activity activity, List<MyData> myData) {
//            this.activity = activity;
//            this.myData = myData;
//        }
//        //建立對外接口
//        public void setOnItemClickListener(MyAdapter.OnItemClickListener onItemClickListener){
//            this.onItemClickListener = onItemClickListener;
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//            TextView tvTitle;
//            View view;
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                tvTitle = itemView.findViewById(android.R.id.text1);
//                view = itemView;
//            }
//        }
//        //更新資料
//        public void refreshView() {
//            new Thread(()->{
//                List<MyData> data = DataBase.getInstance(activity).getDataUao().displayAll();
//                this.myData = data;
//                activity.runOnUiThread(() -> {
//                    notifyDataSetChanged();
//                });
//            }).start();
//        }
//        //刪除資料
//        public void deleteData(int position){
//            new Thread(()->{
//                DataBase.getInstance(activity).getDataUao().deleteData(myData.get(position).getId());
//                activity.runOnUiThread(()->{
//                    notifyItemRemoved(position);
//                    refreshView();
//                });
//            }).start();
//        }
//
//        @NonNull
//        @Override
//        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(android.R.layout.simple_list_item_1, null);
//            return new MyAdapter.ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
//            holder.tvTitle.setText(myData.get(position).getName());
//            holder.view.setOnClickListener((v)->{
//                onItemClickListener.onItemClick(myData.get(position));
//            });
//
//        }
//        @Override
//        public int getItemCount() {
//            return myData.size();
//        }
//        //建立對外接口
//        public interface OnItemClickListener {
//            void onItemClick(MyData myData);
//        }
//
//    }

    //設置RecyclerView的左滑刪除行為
//    private void setRecyclerFunction(RecyclerView recyclerView){
//        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {//設置RecyclerView手勢功能
//            @Override
//            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//                return makeMovementFlags(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
//            }
//
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                switch (direction){
//                    case ItemTouchHelper.LEFT:
//                    case ItemTouchHelper.RIGHT:
//                        myAdapter.deleteData(position);
//                        break;
//
//                }
//            }
//        });
//        helper.attachToRecyclerView(recyclerView);
//    }

    //執行DatePickerDialog
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
}