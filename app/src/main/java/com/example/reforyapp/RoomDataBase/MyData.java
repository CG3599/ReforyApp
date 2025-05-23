package com.example.reforyapp.RoomDataBase;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "MyTable")
public class MyData {


    @PrimaryKey(autoGenerate = true)// 設置是否使ID自動累加
    private int id;
    private String name;
    private String count;
    private String time;

    private String picURL;

    public MyData(String name, String count, String time, String picURL) {
        this.name = name;
        this.count = count;
        this.time = time;
        this.picURL = picURL;
    }
    @Ignore// 如果要使用多形的建構子，必須加入@Ignore
    public MyData(int id,String name, String count, String time, String picURL) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.time = time;
        this.picURL = picURL;
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

    public String getPicURL() {
        return picURL;
    }

    public void setPicURL(String picURL) {
        this.picURL = picURL;
    }
}
