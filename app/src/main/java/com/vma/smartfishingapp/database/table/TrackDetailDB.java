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

public class TrackDetailDB extends MasterDB {

    public static final String TAG          = "TrackDetailDB";
    public static final String TABLE_NAME   = "TrackDetailDB";

    public static final String ID = "_id";
    public static final String TRACK_ID = "track_id";
    public static final String LONGITUDE = "longitude";
    public static final String LATITUDE = "latitude";

    public int id = 0;
    public int trackId = 0;
    public double longitude = 0;
    public double latitude = 0;

    public String getCreateTable() {
        String create = "create table " + TABLE_NAME + " "
                + "(" +
                " " + ID + " INTEGER DEFAULT 0," +
                " " + TRACK_ID + " INTEGER DEFAULT 0," +
                " " + LONGITUDE + " varchar(20) NULL," +
                " " + LATITUDE + " varchar(20) NULL" +
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
    protected TrackDetailDB build(Cursor res) {
        TrackDetailDB jp = new TrackDetailDB();
        jp.id = res.getInt(res.getColumnIndex(ID));
        jp.trackId = res.getInt(res.getColumnIndex(TRACK_ID));
        jp.longitude = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
        jp.latitude = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
        return jp;
    }

    @SuppressLint("Range")
    @Override
    protected void buildSingle(Cursor res) {
        this.id = res.getInt(res.getColumnIndex(ID));
        this.trackId = res.getInt(res.getColumnIndex(TRACK_ID));
        this.longitude = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
        this.latitude = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
    }

    public ContentValues createContentValues(){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(TRACK_ID, trackId);
        contentValues.put(LONGITUDE, longitude);
        contentValues.put(LATITUDE, latitude);
        Log.d(TAG,"ContentValues = "+ contentValues);
        return contentValues;
    }


    public void delete(Context context, int id) {
        super.delete(context, TRACK_ID +"= "+id+"");
    }
    public void delete(Context context) {
        super.delete(context, TRACK_ID +"= "+ trackId +"");
    }

    @Override
    public boolean insert(Context context) {
        delete(context, trackId);
        return super.insert(context);
    }



    public ArrayList<TrackDetailDB> getAll(Context context, int trackId){
        ArrayList<TrackDetailDB> list = new ArrayList<>();

        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME +" where "+TRACK_ID+"="+trackId+" order by "+ ID +" DESC ", null);
        try {
            while (res.moveToNext()){
                list.add(build(res));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }
        return list;
    }


    @SuppressLint("Range")
    public int getNextID(Context context){
        int max = 0;
        DatabaseManager pDB = new DatabaseManager(context);
        SQLiteDatabase db = pDB.getReadableDatabase();
        Cursor res = db.rawQuery("select MAX("+ID+") as IDMAX  from " + TABLE_NAME , null);
        try {
            while (res.moveToNext()){
                max    = res.getInt(res.getColumnIndex("IDMAX"));
            }
            pDB.close();
        }catch (Exception e){
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
        finally {
            res.close();
            pDB.close();
        }

        return max+1;
    }



}
