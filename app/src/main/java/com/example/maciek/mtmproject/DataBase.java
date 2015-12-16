package com.example.maciek.mtmproject;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by maciek on 2015-12-14.
 */
public class DataBase {
    SQLiteDatabase myDataBase;
    Context context;

    private static DataBase instance;

    private DataBase(Context context)
    {
        this.context = context;
        String test = Environment.getRootDirectory().getPath()+File.separator+"myDB";
        boolean a = dir_exists(test);
        myDataBase = this.context.openOrCreateDatabase("test.db", context.MODE_PRIVATE , null);
        //clearTable();
        createTable();
    }

    public static DataBase getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DataBase(context);
        }
        return  instance;
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
        //myDataBase.execSQL("Insert into objectTable Values('test', 2.3 , 2.3);");
    }

    private void clearTable()
    {
        myDataBase.execSQL("Delete from objectTable");
    }

    public void addObjectToTable(double lng, double lat, String name)
    {
        try {
            myDataBase.execSQL("Insert into objectTable Values('" + name + "', " + lat + " , " + lng + ");");
        }
        catch(android.database.sqlite.SQLiteException ex)
        {
            String log = ex.getMessage().toString();
        }
    }

    public List<Object> getObjectListFromDB()
    {
        List<Object> objectList = new ArrayList<>();
        Cursor objectTable = myDataBase.rawQuery("Select * from objectTable",null);

        objectTable.moveToFirst();
        for (int i = 0; i < objectTable.getCount(); i++)
        {
            String name = objectTable.getString(0);
            double lat = objectTable.getDouble(1);
            double lng = objectTable.getDouble(2);
            Object temp = new Object(lng, lat , name);
            objectList.add(temp);
            objectTable.moveToNext();
        }

        return objectList;
    }

    public String[] getNameList()
    {
        String[] list;

        Cursor objectTable = myDataBase.rawQuery("Select Name from objectTable",null);

        objectTable.moveToFirst();
        list = new String[objectTable.getCount()];
        for (int i = 0; i < objectTable.getCount(); i++)
        {
            list[i] = objectTable.getString(0);
            objectTable.moveToNext();
        }

        return list;
    }

}
