package com.example.maciek.mtmproject;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
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
        myDataBase = this.context.openOrCreateDatabase("DB.db", context.MODE_PRIVATE , null);
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


    private void createTable()
    {
try {
    myDataBase.execSQL("Create table if not exists ObjectTable(ID INTEGER PRIMARY KEY AUTOINCREMENT , Name VARCHAR, Latitude double, Longitutde double, Altitude double);");
    /*addObjectToTable(52.45, 18.12, "test1");
    addObjectToTable(52.45 , 18.12 , "test2");
    addObjectToTable(52.45 , 18.12 , "test3");*/
}
catch (SQLiteException ex)
{
    String t = ex.getMessage().toString();
}
    }

    private void clearTable()
    {
        myDataBase.execSQL("Delete * from objectTable");
    }

    public void addObjectToTable(double lng, double lat, double h, String name)
    {
        try {
            myDataBase.execSQL("Insert into ObjectTable(Name, Latitude,Longitutde, Altitude  ) Values('" + name + "', " + lat + " , " + lng + " , " + h+");");
        }
        catch(android.database.sqlite.SQLiteException ex)
        {
            String log = ex.getMessage().toString();
        }
    }

    public List<Object> getObjectListFromDB()
    {
        List<Object> objectList = new ArrayList<>();
        Cursor objectTable = myDataBase.rawQuery("Select * from ObjectTable",null);

        objectTable.moveToFirst();
        for (int i = 0; i < objectTable.getCount(); i++)
        {
            int id = objectTable.getInt(0);
            String name = objectTable.getString(1);
            double lat = objectTable.getDouble(2);
            double lng = objectTable.getDouble(3);
            double h = objectTable.getDouble(4);
            Object temp = new Object(id, lng, lat ,h , name);
            objectList.add(temp);
            objectTable.moveToNext();
        }

        return objectList;
    }

    public String[] getNameList()
    {
        String[] list;

        Cursor objectTable = myDataBase.rawQuery("Select Name from ObjectTable",null);

        objectTable.moveToFirst();
        list = new String[objectTable.getCount()];
        for (int i = 0; i < objectTable.getCount(); i++)
        {
            list[i] = objectTable.getString(0);
            objectTable.moveToNext();
        }

        return list;
    }

    public void deleteObject(int id)
    {
        myDataBase.execSQL("DELETE FROM ObjectTable WHERE ID = "+id);
    }


}
