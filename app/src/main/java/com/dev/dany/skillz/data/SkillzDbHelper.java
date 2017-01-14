package com.dev.dany.skillz.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.dev.dany.skillz.data.SkillzContract.*;

import java.util.HashMap;

/**
 * Created by dany on 1/28/2015.
 */
public class SkillzDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "skillz.db";
    private static final int DATABASE_VERSION = 1;
    public SkillzDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_JOBS_TABLE = "CREATE TABLE " + JobsEntry.TABLE_NAME + " ( " +
                JobsEntry._ID +" INTEGER PRIMARY KEY, " +
                JobsEntry.COLUMN_UID + " INTEGER NOT NULL, " +
                JobsEntry.COLUMN_JOB_ID + " INTEGER NOT NULL, " +
                JobsEntry.COLUMN_JOB_TITLE + " TEXT NOT NULL, " +
                JobsEntry.COLUMN_JOB_DESC + " TEXT NOT NULL, " +
                JobsEntry.COLUMN_REQ_SKILLS + " TEXT NOT NULL, " +
                JobsEntry.COLUMN_DATE_TIME + " TEXT NOT NULL, " +
                "FOREIGN KEY (" + JobsEntry.COLUMN_UID + ") REFERENCES " +
                UsersEntry.TABLE_NAME + " (" + UsersEntry.COLUMN_USER_ID + ") " +
                " UNIQUE (" + JobsEntry.COLUMN_JOB_ID  +") ON CONFLICT REPLACE);";

        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + UsersEntry.TABLE_NAME + " ( " +
                UsersEntry._ID + " INTEGER PRIMARY KEY, " +
                UsersEntry.COLUMN_USER_NAME + " TEXT NOT NULL, " +
                UsersEntry.COLUMN_USER_EMAIL + " TEXT NOT NULL, " +
                UsersEntry.COLUMN_USER_ID + " TEXT UNIQUE, " +
                UsersEntry.COLUMN_DATE_TIME + " TEXT NOT NULL " +
                ");";
        final String SQL_CREATE_USERS_DETAILS_TABLE = "CREATE TABLE " + UsersDetails.TABLE_NAME + " ( " +
                UsersDetails._ID + " INTEGER PRIMARY KEY, " +
                UsersDetails.COLUMN_USER_ID + " INTEGER UNIQUE, " +
                UsersDetails.COLUMN_SELF_DESC + " TEXT, " +
                UsersDetails.COLUMN_USER_LOCATION + " TEXT, " +
                UsersDetails.COLUMN_USER_SKILLS + " TEXT, " +
                UsersDetails.COLUMN_UPDATED_AT + " TEXT " +
                ");";



        sqLiteDatabase.execSQL(SQL_CREATE_USERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_JOBS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USERS_DETAILS_TABLE);
     }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JobsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UsersDetails.TABLE_NAME);
        onCreate(sqLiteDatabase);


    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UsersEntry.COLUMN_USER_NAME, name); // Name
        values.put(UsersEntry.COLUMN_USER_EMAIL, email); // Email
        values.put(UsersEntry.COLUMN_USER_ID, uid); // Email
        values.put(UsersEntry.COLUMN_DATE_TIME, created_at); // Created At
        // Inserting Row
        db.insert(UsersEntry.TABLE_NAME, null, values);
        db.close(); // Closing database connection
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        String selectQuery = "SELECT  * FROM " + UsersEntry.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if(cursor.getCount() > 0){
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
        }
        cursor.close();
        db.close();
        // return user
        return user;
    }

    /**
     * Getting user login status
     * return true if rows are there in table
     * */
    public int getRowCount() {
        String countQuery = "SELECT  * FROM " + UsersEntry.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();

        // return row count
        return rowCount;
    }

    /**
     * Re crate database
     * Delete all tables and create them again
     * */
    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(UsersEntry.TABLE_NAME, null, null);
        db.close();
    }
}
