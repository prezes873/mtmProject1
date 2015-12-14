package com.example.maciek.mtmproject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import static android.app.PendingIntent.getActivity;

/**
 * Created by maciek on 2015-12-14.
 */
public class DataBase {
    SQLiteDatabase myDataBase;
    Context context;

    public DataBase(Context context)
    {
        this.context = context;
        String test = Environment.getRootDirectory().getPath()+File.separator+"myDB";
        boolean a = dir_exists(test);
        myDataBase = this.context.openOrCreateDatabase("test.db", context.MODE_PRIVATE , null);
        createTable();
    }

    public boolean dir_exists(String dir_path)
    {
        boolean ret = false;
        File dir = new File(dir_path);
        if(dir.exists() && dir.isDirectory())
            ret = true;
        return ret;
    }

    private void createTable()
    {
        myDataBase.execSQL("Create table if not exists objectTable(Name VARCHAR, Latitude double, Longitutde double);");
        myDataBase.execSQL("Insert into objectTable Values('test', 2.3 , 2.3);");
        Cursor test = myDataBase.rawQuery("Select * from objectTable",null);
        test.moveToFirst();
        for (int i = 0; i < test.getCount(); i++)
        {
            String name = test.getString(0);
            test.moveToNext();
        }

    }
}
