package com.dev.dany.skillz;

import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.dany.skillz.data.SkillzContract;
import com.dev.dany.skillz.data.SkillzContract.UsersDetails;
import com.dev.dany.skillz.data.SkillzContract.UsersEntry;
import com.dev.dany.skillz.data.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by dany on 3/2/2015.
 */
public class UserDetailFragment  extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private TextView userNameTextView;
    private TextView userEmailTextView;
    private EditText userAddressEditText;
    private Spinner userSkillSpinner;
    private EditText userDescEditText;
    private Button doneButton;

    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";

    private static int DETAIL_LOADER = 0;

    private String[] DETAIL_COLUMNS = {
            UsersEntry.COLUMN_USER_NAME,
            UsersEntry.COLUMN_USER_EMAIL,
            UsersDetails.COLUMN_USER_LOCATION,
            UsersDetails.COLUMN_USER_SKILLS,
            UsersDetails.COLUMN_SELF_DESC
    };
    public UserDetailFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_detail, container, false);
        userNameTextView = (TextView) rootView.findViewById(R.id.user_detail_name_textView2);
        userEmailTextView = (TextView) rootView.findViewById(R.id.user_detail_email_textView2);
        userAddressEditText = (EditText) rootView.findViewById(R.id.user_detail_address_editText);
        userSkillSpinner = (Spinner) rootView.findViewById(R.id.user_detail_skill_spinner);
        userDescEditText = (EditText) rootView.findViewById(R.id.user_detail_dsc_editText);
        doneButton = (Button) rootView.findViewById(R.id.user_detail_button);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.skill_set,
                android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        userSkillSpinner.setAdapter(arrayAdapter);

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new UserDetailAsyncTask().execute();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String uuid = null;
        Cursor cursor = getActivity().getContentResolver().query(SkillzContract.UsersEntry.CONTENT_URI,
                new String[]{SkillzContract.UsersEntry.COLUMN_USER_ID},null,null,null);
        if(cursor.moveToFirst()){
            uuid = cursor.getString(cursor.getColumnIndex(SkillzContract.UsersEntry.COLUMN_USER_ID));
        }
        Uri userDetailUri = UsersEntry.buildUserAndDetailUri(uuid);

        return new CursorLoader(
                getActivity(),
                userDetailUri,
                DETAIL_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (!cursor.moveToFirst()) { return; }
        userNameTextView.setText(cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_USER_NAME)));
        userEmailTextView.setText(cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_USER_EMAIL)));
        userAddressEditText.setText(cursor.getString(cursor.getColumnIndex(UsersDetails.COLUMN_USER_LOCATION)));
        userDescEditText.setText(cursor.getString(cursor.getColumnIndex(UsersDetails.COLUMN_SELF_DESC)));
        String[] array = getActivity().getResources().getStringArray(R.array.skill_set);
        String skill = cursor.getString(cursor.getColumnIndex(UsersDetails.COLUMN_USER_SKILLS));
        for (int i = 0;i<array.length ; i++){
            if(array[i].equals(skill))
            userSkillSpinner.setSelection(i);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    public class UserDetailAsyncTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            String address = userAddressEditText.getText().toString();
            String skill_set = userSkillSpinner.getSelectedItem().toString();
            String self_description = userDescEditText.getText().toString();
            String uuid = null;
            boolean error = false ;
            Cursor cursor = getActivity().getContentResolver().query(SkillzContract.UsersEntry.CONTENT_URI,
                    new String[]{SkillzContract.UsersEntry.COLUMN_USER_ID},null,null,null);
            if(cursor.moveToFirst()){
                uuid = cursor.getString(cursor.getColumnIndex(SkillzContract.UsersEntry.COLUMN_USER_ID));
            }
            UserFunctions userFunctions = new UserFunctions();
            JSONObject jsonObject = userFunctions.postUserDetails(uuid,address,skill_set,self_description );
            try {
                if(jsonObject.getString(KEY_SUCCESS) != null){
                    String res = jsonObject.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) != 1){
                        error = true;
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
            if(!error){

                Toast.makeText(getActivity(), "Details updated successfully.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getActivity(),"Error, updating details.",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
