package com.example.reforyapp.RoomDataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import androidx.lifecycle.LiveData;

import java.util.List;

@Dao
public interface DataUao {

    String tableName = "MyTable";

    // 撈取全部資料(倒敘顯示)
    @Query("SELECT * FROM " + tableName + " ORDER BY id DESC")
    LiveData<List<MyData>> getAllDataLive();

    // 簡易新增所有資料的方法
    @Insert(onConflict = OnConflictStrategy.REPLACE)// 預設萬一執行出錯怎麼辦，REPLACE為覆蓋
    void insertData(MyData myData);

    // 完整新增所有資料的方法
    @Query("INSERT INTO "+tableName+"(name,count,time,picURL) VALUES(:name,:count,:time,:picURL)")
    void insertData(String name,String count,String time,String picURL);

    // 撈取全部資料
    @Query("SELECT * FROM " + tableName + " ORDER BY id DESC")
    List<MyData> displayAll();

    // 撈取某個名字的相關資料
    @Query("SELECT * FROM " + tableName +" WHERE name = :name")
    List<MyData> findDataByName(String name);

    // 簡易更新資料的方法
    @Update
    void updateData(MyData myData);

    // 完整更新資料的方法
    @Query("UPDATE "+tableName+" SET name = :name,count=:count,time=:time,picURL=:picURL WHERE id = :id" )
    void updateData(int id,String name,String count,String time,String picURL);

    // 簡單刪除資料的方法
    @Delete
    void deleteData(MyData myData);

    // 完整刪除資料的方法
    @Query("DELETE  FROM " + tableName + " WHERE id = :id")
    void deleteData(int id);

}