package com.pdf.and.image.cropper.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "pdf_name.db";
    private static final String PDF_TABLE = "pdf_table";


    private static final String IMAGEPATH="imagepath";


    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + PDF_TABLE + " (ID INTEGER  PRIMARY KEY ,IMAGEPATH String)");
      }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PDF_TABLE);
        onCreate(db);
    }


    public boolean insertPdfPath(String imagePath)
    {
        contentValues.put(IMAGEPATH,imagePath);
        long result = db.insert(PDF_TABLE, null, contentValues);
        return result != -1;
    }

    public Cursor getAllPdf()
    {
        return db.rawQuery("select * from "+ PDF_TABLE, null);
    }

     public int getCount()
    {
        Cursor res=db.rawQuery("select * from "+ PDF_TABLE, null);
        return   res.getCount();
    }




}

