package com.dev.dany.skillz;

import android.app.Fragment;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.dev.dany.skillz.data.SkillzContract;
import com.dev.dany.skillz.data.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dany on 2/23/2015.
 */
public class JobPostFragment extends Fragment{

    private EditText jobTitleEditText;
    private EditText jobDescEditText;
    private Spinner jobSkillSpinner;
    private Button jobPostButton;
    private boolean jobTitleFilled ;
    private boolean jobDescFilled ;

    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";

    public JobPostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_job_post, container, false);
        jobTitleEditText = (EditText) rootView.findViewById(R.id.jobPost_title_editText);
        jobDescEditText = (EditText) rootView.findViewById(R.id.jobPost_desc_editText);
        jobSkillSpinner = (Spinner) rootView.findViewById(R.id.jobPost_spinner);
        jobPostButton = (Button) rootView.findViewById(R.id.jobPost_button);
        jobPostButton.setEnabled(false);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.skill_set,
                android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        jobSkillSpinner.setAdapter(arrayAdapter);


        //text watcher for post button enabling
        jobTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                jobTitleFilled = (charSequence.length() > 0 ? true : false);
                updatePostButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        jobDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                jobDescFilled =  (charSequence.length() > 0 ? true : false);
                updatePostButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //posting
        jobPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JobPostAsyncTask().execute();
            }
        });
        return rootView;
    }

    private void updatePostButton(){
        if(jobTitleFilled && jobDescFilled ){
            jobPostButton.setEnabled(true);
        }
        else{
            jobPostButton.setEnabled(false);
        }
    }

    public class JobPostAsyncTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            String jobTitle = jobTitleEditText.getText().toString();
            String jobDesc = jobDescEditText.getText().toString();
            String jobSkill = jobSkillSpinner.getSelectedItem().toString();
            String uuid = null;
            boolean error = false ;
            Cursor cursor = getActivity().getContentResolver().query(SkillzContract.UsersEntry.CONTENT_URI,
                    new String[]{SkillzContract.UsersEntry.COLUMN_USER_ID},null,null,null);
            if(cursor.moveToFirst()){
                uuid = cursor.getString(cursor.getColumnIndex(SkillzContract.UsersEntry.COLUMN_USER_ID));
            }
            UserFunctions userFunctions = new UserFunctions();
            JSONObject jsonObject = userFunctions.postJob(uuid,jobTitle,jobDesc,jobSkill);

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

                Toast.makeText(getActivity(),"Job posted successfully.",Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            else{
                Toast.makeText(getActivity(),"Error, posting job.",Toast.LENGTH_SHORT).show();
            }
        }

    }

}

