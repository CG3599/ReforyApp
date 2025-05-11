package com.example.reforyapp.RoomDataBase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {MyData.class},version = 2,exportSchema = true)// 資料綁定的Getter-Setter,資料庫版本,是否將資料導出至文件
public abstract class DataBase extends RoomDatabase {
    public static final String DB_NAME = "MyData.db";// 資料庫名稱
    private static volatile DataBase instance;

    public static synchronized DataBase getInstance(Context context){
//        if(instance == null){
//            instance = create(context);// 創立新的資料庫
//        }
//        return instance;
        if(instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    DataBase.class,DB_NAME).addMigrations(MAEGIN_1to2).build();
        }
        return instance;
    }

    private static DataBase create(final Context context){
        return Room.databaseBuilder(context,DataBase.class,DB_NAME).build();
    }

    // 新增picURL欄位
    public static Migration MAEGIN_1to2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE \"MyTable\"  ADD COLUMN picURL TEXT");
        }
    };
    public abstract DataUao getDataUao();//設置對外接口
}
