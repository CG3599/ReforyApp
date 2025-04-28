package com.example.reforyapp.RoomDataBase;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "MyTable")//這邊要先取好table的名字，稍後的table設置必須與他相同
public class MyData {


    @PrimaryKey(autoGenerate = true)//設置是否使ID自動累加
    private int id;
    private String name;
    private String count;
    private String time;

    public MyData(String name, String count, String time) {
        this.name = name;
        this.count = count;
        this.time = time;
    }
    @Ignore//如果要使用多形的建構子，必須加入@Ignore
    public MyData(int id,String name, String count, String time) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
