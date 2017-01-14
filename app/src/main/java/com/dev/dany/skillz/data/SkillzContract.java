package com.dev.dany.skillz.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dany on 1/28/2015.
 */
public class SkillzContract {

    public static final String CONTENT_AUTHORITY = "com.dev.dany.skillz.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_JOBS_ENTRY = "jobsentry";
    public static final String PATH_USERS_ENTRY = "usersentry";
    public static final String PATH_USERS_DETAILS = "usersdetails";

    public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";

    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public static final class JobsEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_JOBS_ENTRY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_JOBS_ENTRY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_JOBS_ENTRY;

        public static final String TABLE_NAME = "jobs";
        public static final String COLUMN_JOB_ID = "job_id";
        public static final String COLUMN_JOB_TITLE = "job_title";
        public static final String COLUMN_JOB_DESC = "job_desc";
        public static final String COLUMN_UID = "uid";
        public static final String COLUMN_REQ_SKILLS = "req_skills";
        public static final String COLUMN_DATE_TIME = "updated_at";

        //Uri builder functions

        public static Uri buildJobsWithUUID(String UUID){
            return CONTENT_URI.buildUpon().appendPath(UUID).build();
        }
        public static Uri buildJobsWithStartDate(String startDate){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_DATE_TIME, startDate).build();
        }

        public static Uri buildJobsWithID(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildJobsWithDate(String date){
            return CONTENT_URI.buildUpon().appendPath(date).build();
        }
        public static String getDateFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
        public static String getStartDateFromUri(Uri uri){
            return uri.getQueryParameter(COLUMN_DATE_TIME);
        }

    }


    public static final class UsersEntry implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS_ENTRY).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_USERS_ENTRY;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_USERS_ENTRY;

        public static final String TABLE_NAME = "users";
        public static final String COLUMN_USER_NAME = "user_name";
        public static final String COLUMN_USER_EMAIL = "user_email";
        public static final String COLUMN_USER_ID = "uuid";
        public static final String COLUMN_DATE_TIME = "created_at";

        public static Uri buildUsersUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        public static Uri buildUserAndDetailUri(String id){
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
        public static String getUUIDFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }
    }
    public static class UsersDetails implements BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS_DETAILS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_USERS_DETAILS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_USERS_DETAILS;

        public static final String TABLE_NAME = "users_details";
        public static final String COLUMN_USER_ID = "uuid";
        public static final String COLUMN_USER_LOCATION = "user_location";
        public static final String COLUMN_USER_SKILLS = "user_skills";
        public static final String COLUMN_SELF_DESC = "self_desc";
        public static final String COLUMN_UPDATED_AT = "updated_at";

        public static Uri buildUserDetailUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }
}
