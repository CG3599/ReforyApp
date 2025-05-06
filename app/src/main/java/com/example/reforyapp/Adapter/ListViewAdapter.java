package com.example.reforyapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.*;
import android.widget.*;

import com.example.reforyapp.R;
import com.example.reforyapp.RoomDataBase.MyData;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public class ListViewAdapter extends ArrayAdapter<MyData> {
    private Context mContext;
    private List<MyData> dataList;
    private Set<Integer> selectedIds = new HashSet<>(); //記錄勾選ID

    public ListViewAdapter(Context context, List<MyData> list) {
        super(context, 0, list);
        mContext = context;
        dataList = list;
    }

    //獲取checkbox勾選id
    public Set<Integer> getSelectedIds() {
        return selectedIds;
    }

    public void clearSelections() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyData data = dataList.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.item_image);
        TextView textView = convertView.findViewById(R.id.item_text);
        CheckBox checkBox = convertView.findViewById(R.id.item_checkBox);

        //設定 ID
        textView.setText("ID: " + data.getId());

        //設定縮圖
        try {
            Uri uri = Uri.parse(data.getPicURL());
            InputStream imageStream = mContext.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            imageView.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        //設定 CheckBox 狀態
        checkBox.setOnCheckedChangeListener(null); //防止重複觸發
        checkBox.setChecked(selectedIds.contains(data.getId()));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedIds.add(data.getId());
            } else {
                selectedIds.remove(data.getId());
            }
        });

        return convertView;
    }
}
