/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dev.dany.skillz;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.dev.dany.skillz.data.SkillzContract;
import com.dev.dany.skillz.data.SkillzContract.JobsEntry;
import com.dev.dany.skillz.data.UserFunctions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * A sample which shows how to use {@link android.support.v4.widget.SwipeRefreshLayout} within a
 * {@link android.support.v4.app.ListFragment} to add the 'swipe-to-refresh' gesture to a
 * {@link android.widget.ListView}. This is provided through the provided re-usable
 * {@link SwipeRefreshListFragment} class.
 *
 * <p>To provide an accessible way to trigger the refresh, this app also provides a refresh
 * action item. This item should be displayed in the Action Bar's overflow item.
 *
 * <p>In this sample app, the refresh updates the ListView with a random set of new items.
 *
 * <p>This sample also provides the functionality to change the colors displayed in the
 * {@link android.support.v4.widget.SwipeRefreshLayout} through the options menu. This is meant to
 * showcase the use of color rather than being something that should be integrated into apps.
 */
public class JobsFragment extends SwipeRefreshListFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = JobsFragment.class.getSimpleName();

    private static final int JOBS_LOADER = 0;

    private static final String[] JOBS_COLUMNS = {
            JobsEntry._ID,
            JobsEntry.COLUMN_DATE_TIME,
            JobsEntry.COLUMN_JOB_TITLE,
            JobsEntry.COLUMN_REQ_SKILLS
    };
    private SimpleCursorAdapter mAdapter;


    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UUID = "uuid";
    private static String KEY_JOB_ID = "id";
    private static String KEY_TITLE = "name";
    private static String KEY_REQ_SKILLS = "skill_set";
    private static String KEY_DESC = "description";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_UPDATED_AT = "updated_at";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Notify the system to allow an options menu for this fragment.
        setHasOptionsMenu(true);
    }

    // BEGIN_INCLUDE (setup_views)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * Create an ArrayAdapter to contain the data for the ListView. Each item in the ListView
         * uses the system-defined simple_list_item_1 layout that contains one TextView.
         */
        mAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_jobs,
                null,
                JOBS_COLUMNS,
                new int[]{0,R.id.job_date_textView,R.id.job_title_textView,R.id.job_skill_textView},
                0);

        // the adapterSet  between the ListView and its backing data.
        setListAdapter(mAdapter);

        // BEGIN_INCLUDE (setup_refreshlistener)
        /**
         * Implement {@link android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener}. When users do the "swipe to
         * refresh" gesture, SwipeRefreshLayout invokes
         * {@link android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}. In
         * {@link android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener#onRefresh onRefresh()}, call a method that
         * refreshes the content. Call the same method in response to the Refresh action from the
         * action bar.
         */
        setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(LOG_TAG, "onRefresh called from SwipeRefreshLayout");

                initiateRefresh();
            }
        });
        // END_INCLUDE (setup_refreshlistener)
    }
    // END_INCLUDE (setup_views)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_jobs, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.logout){
            UserFunctions uf = new UserFunctions();
            uf.logoutUser(getActivity());
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        if(id == R.id.details){
            Intent intent = new Intent(getActivity(),UserDetailActivity.class);
            startActivity(intent);
        }
        if(id == R.id.action_add){
            Intent intent = new Intent(getActivity(),JobPostActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    // BEGIN_INCLUDE (initiate_refresh)
    /**
     * By abstracting the refresh process to a single method, the app allows both the
     * SwipeGestureLayout onRefresh() method and the Refresh action item to refresh the content.
     */
    private void initiateRefresh() {
        Log.i(LOG_TAG, "initiateRefresh");

        /**
         * Execute the background task, which uses {@link android.os.AsyncTask} to load the data.
         */
        new JobsAsyncTask().execute();
    }
    // END_INCLUDE (initiate_refresh)

    // BEGIN_INCLUDE (refresh_complete)
    /**
     * When the AsyncTask finishes, it calls onRefreshComplete(), which updates the data in the
     * ListAdapter and turns off the progress bar.
     */
    private void onRefreshComplete(boolean error) {
        Log.i(LOG_TAG, "onRefreshComplete");

        // Remove all items from the ListAdapter, and then replace them with the new items
        if(!error){
            // Stop the refreshing indicator
            setRefreshing(false);
        }
        else {
            // Stop the refreshing indicator
            setRefreshing(false);
            Toast.makeText(getActivity(), "Error.", Toast.LENGTH_SHORT).show();
        }
    }
    // END_INCLUDE (refresh_complete)


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(JOBS_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String date = SkillzContract.getDbDateString(new Date());
        Uri uri = JobsEntry.buildJobsWithStartDate(date);

        String sortOrder = JobsEntry.COLUMN_DATE_TIME + " DESC";

        return new CursorLoader(
                getActivity(),
                uri,
                JOBS_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    /**
     * Dummy {@link android.os.AsyncTask} which simulates a long running task to fetch new cheeses.
     */
    private class JobsAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            // Sleep for a small amount of time to simulate a background-task
            boolean error = false;
            UserFunctions userFunctions = new UserFunctions();
            JSONObject jsonObject = userFunctions.getJobs();
            try {
                if(jsonObject.getString(KEY_SUCCESS) != null) {
                    String res = jsonObject.getString(KEY_SUCCESS);
                    if (Integer.parseInt(res) != 1) {
                        error = true;

                    } else {
                        JSONArray jsonArray = jsonObject.getJSONArray("jobs");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ContentValues values = new ContentValues();
                            values.put(JobsEntry.COLUMN_UID, jsonArray.getJSONObject(i).getString(KEY_UUID));
                            values.put(JobsEntry.COLUMN_JOB_ID, jsonArray.getJSONObject(i).getInt(KEY_JOB_ID));
                            values.put(JobsEntry.COLUMN_JOB_TITLE, jsonArray.getJSONObject(i).getString(KEY_TITLE));
                            values.put(JobsEntry.COLUMN_REQ_SKILLS, jsonArray.getJSONObject(i).getString(KEY_REQ_SKILLS));
                            values.put(JobsEntry.COLUMN_JOB_DESC, jsonArray.getJSONObject(i).getString(KEY_DESC));
                            values.put(JobsEntry.COLUMN_DATE_TIME, jsonArray.getJSONObject(i).getString(KEY_UPDATED_AT));
                            getActivity().getContentResolver().insert(JobsEntry.CONTENT_URI, values);





                        }

                    }
                }


            }
            catch (JSONException e){
                e.printStackTrace();
            }

            return error;
        }

        @Override
        protected void onPostExecute(Boolean error) {
            onRefreshComplete(error);
        }

    }

}
