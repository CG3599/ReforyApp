package com.example.reforyapp;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.reforyapp.RoomDataBase.DataBase;
import com.example.reforyapp.RoomDataBase.MyData;

import java.util.List;

public class SharedDataViewModel extends AndroidViewModel {
    private LiveData<List<MyData>> allDataLive;

    public SharedDataViewModel(@NonNull Application application) {
        super(application);
        allDataLive = DataBase.getInstance(application).getDataUao().getAllDataLive();
    }

    public LiveData<List<MyData>> getAllDataLive() {
        return allDataLive;
    }
}
