package com.dev.dany.skillz.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.dev.dany.skillz.data.SkillzContract.JobsEntry;
import com.dev.dany.skillz.data.SkillzContract.UsersDetails;
import com.dev.dany.skillz.data.SkillzContract.UsersEntry;

/**
 * Created by dany on 2/21/2015.
 */
public class SkillzProvider extends ContentProvider {

    public static final int USERS = 100;
    public static final int USERS_DETAILS = 200;
    public static final int USERS_AND_USERS_DETAILS = 101;
    public static final int JOBS = 300;
    public static final int JOBS_AND_USERS = 301;
    public static final int JOBS_WITH_DATE = 302;

    private SkillzDbHelper mSkillzDbHelper ;

    private static UriMatcher sUriMatcher = buildUriMatcher();
    private static SQLiteQueryBuilder sJobsAndUsers;
    private static SQLiteQueryBuilder sUserAndUserDetail;

    private static final String sJobsWithStartDateSelection = JobsEntry.TABLE_NAME + "." +
            JobsEntry.COLUMN_JOB_ID + " > 0";
    private static final String sJobsWithDateSelection = JobsEntry.TABLE_NAME + "." +
            JobsEntry.COLUMN_DATE_TIME + " = ?";

    static {
        sJobsAndUsers = new SQLiteQueryBuilder();
        sJobsAndUsers.setTables(JobsEntry.TABLE_NAME + " INNER JOIN " +
                UsersEntry.TABLE_NAME + " ON " + JobsEntry.TABLE_NAME + "." +
                JobsEntry.COLUMN_UID  + " = " + UsersEntry.TABLE_NAME + "." +
                UsersEntry.COLUMN_USER_ID);
        sUserAndUserDetail = new SQLiteQueryBuilder();
        sUserAndUserDetail.setTables(UsersEntry.TABLE_NAME + " INNER JOIN " +
                UsersDetails.TABLE_NAME + " ON " + UsersEntry.TABLE_NAME + "." +
                UsersEntry.COLUMN_USER_ID + " = " + UsersDetails.TABLE_NAME + "." +
                UsersDetails.COLUMN_USER_ID);
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = SkillzContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SkillzContract.PATH_USERS_ENTRY,USERS );
        matcher.addURI(authority,SkillzContract.PATH_USERS_DETAILS,USERS_DETAILS);
        matcher.addURI(authority,SkillzContract.PATH_USERS_ENTRY + "/*",USERS_AND_USERS_DETAILS);
        matcher.addURI(authority,SkillzContract.PATH_JOBS_ENTRY,JOBS);
        matcher.addURI(authority,SkillzContract.PATH_JOBS_ENTRY + "/*",JOBS_AND_USERS);
        matcher.addURI(authority,SkillzContract.PATH_JOBS_ENTRY + "/*/*",JOBS_WITH_DATE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mSkillzDbHelper = new SkillzDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor = null;
        SQLiteDatabase db = mSkillzDbHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)){
            case USERS :{
                retCursor = db.query(UsersEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case JOBS :{
                retCursor = db.query(JobsEntry.TABLE_NAME,
                        projection,
                        sJobsWithStartDateSelection,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case JOBS_WITH_DATE :{
                retCursor = db.query(JobsEntry.TABLE_NAME,
                        projection,
                        sJobsWithDateSelection,
                        new String[]{JobsEntry.getDateFromUri(uri)},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case USERS_AND_USERS_DETAILS: {
                retCursor = sUserAndUserDetail.query(db,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.d("Cursor: ",Integer.toString(retCursor.getCount()));
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri retUri = null;
        final SQLiteDatabase db = mSkillzDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case USERS: {
                long id = db.insert(UsersEntry.TABLE_NAME,null,contentValues);

                if(id > 0){
                    retUri = UsersEntry.buildUsersUri(id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case USERS_DETAILS: {
                long id = db.insert(UsersDetails.TABLE_NAME,null,contentValues);

                if(id > 0){
                    retUri = UsersDetails.buildUserDetailUri(id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case JOBS: {
                long id = db.insert(JobsEntry.TABLE_NAME,null,contentValues);

                if(id > 0){
                    retUri = JobsEntry.buildJobsWithID(id);
                }
                else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }


            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }
}
