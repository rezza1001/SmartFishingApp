package com.vma.smartfishingapp.database.table;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vma.smartfishingapp.database.DatabaseManager;
import com.vma.smartfishingapp.database.MasterDB;

import java.util.ArrayList;
import java.util.Objects;

public class ResultDB extends MasterDB {

    public static final String TAG          = "RESULT_DB";
    public static final String TABLE_NAME   = "RESULT_DB";

    public static final String ID   = "_id";
    public static final String EXPRESION         = "expresion";
    public static final String NUM_A     = "numbA";
    public static final String NUM_B        = "numb_B";
    public static final String VALUE        = "VALUE_DATA";

    public int id = 0;
    public int numA = 0;
    public int numB = 0;
    public int value = 0;
    public String expresion = "";

    @Override
    protected ContentValues createContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(NUM_A, numA);
        contentValues.put(NUM_B, numB);
        contentValues.put(VALUE, value);
        contentValues.put(EXPRESION, expresion);
        return contentValues;
    }

    @Override
    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " integer default 0," +
                " " + EXPRESION    + " varchar(2) NULL," +
                " " + NUM_A    + " integer default 0," +
                " " + NUM_B    + " integer default 0," +
                " " + VALUE    + " integer default 0" +
                "  )";
        Log.d(TAG,create);
        return create;
    }

    @Override
    public String tableName() {
        return TABLE_NAME;
    }

    @SuppressLint("Range")
    @Override
    protected ResultDB build(Cursor res) {
        ResultDB resultDB = new ResultDB();
        resultDB.id = res.getInt(res.getColumnIndex(ID));
        resultDB.numA = res.getInt(res.getColumnIndex(NUM_A));
        resultDB.numB = res.getInt(res.getColumnIndex(NUM_B));
        resultDB.value = res.getInt(res.getColumnIndex(VALUE));
        resultDB.expresion = res.getString(res.getColumnIndex(EXPRESION));

        return resultDB;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getInt(res.getColumnIndex(ID));
    }

    public ArrayList<ResultDB> getData(Context context){
        ArrayList<ResultDB> data = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select *  from " + TABLE_NAME , null);
        try {
            while (res.moveToNext()){
                data.add(build(res));

            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        Log.d(TAG,"DATA "+data.size());
        return  data;
    }

    @SuppressLint("Range")
    public int getNextID(Context context){
        int max = 0;
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select MAX("+ID+") as IDMAX  from " + TABLE_NAME , null);
        try {
            while (res.moveToNext()){
                max    = res.getInt(res.getColumnIndex("IDMAX")) + 1;
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }

        return max;
    }
}
