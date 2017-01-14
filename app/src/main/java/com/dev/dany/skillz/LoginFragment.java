package com.dev.dany.skillz;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dev.dany.skillz.data.SkillzContract;
import com.dev.dany.skillz.data.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dany on 2/16/2015.
 */
public class LoginFragment extends Fragment {
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mBtnLogin;
    private Button mBtnRegister;
    private boolean isEmailValid;
    private boolean isPasswordValid;

    // JSON Response node names
    private static String KEY_SUCCESS = "success";
    private static String KEY_ERROR = "error";
    private static String KEY_ERROR_MSG = "error_msg";
    private static String KEY_UID = "uid";
    private static String KEY_UUID = "uuid";
    private static String KEY_NAME = "name";
    private static String KEY_EMAIL = "email";
    private static String KEY_CREATED_AT = "created_at";
    private static String KEY_ADDRESS = "address";
    private static String KEY_SKILL_SET = "skill_set";
    private static String KEY_SELF_DESC = "self_description";
    private static String KEY_UPDATED_AT = "updated_at";

    private void initView(View view) {

        mEditEmail = (EditText) view.findViewById(R.id.login_email_editText);
        mEditPassword = (EditText) view.findViewById(R.id.login_password_editText);
        mEditEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail(s.toString());
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        }); mEditPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validatePassword(s.toString());
                updateLoginButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBtnLogin = (Button) view.findViewById(R.id.login_login_button);
        mBtnLogin.setEnabled(false); // default state should be disabled
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isNetworkOn(getActivity())) {
                    Toast.makeText(getActivity(), "No network connection", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginAsyncTask().execute();
                }
            }
        });

        mBtnRegister = (Button) view.findViewById(R.id.login_register_button);
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),RegisterActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void validatePassword(String text) { isPasswordValid = !text.isEmpty();
    }

    private void validateEmail(String text) { isEmailValid = Patterns.EMAIL_ADDRESS.matcher(text).matches();
    }

    private void updateLoginButtonState() {
        if(isEmailValid && isPasswordValid) { mBtnLogin.setEnabled(true);
        } else { mBtnLogin.setEnabled(false);
        }
    }

    public boolean isNetworkOn( Context context) { ConnectivityManager connMgr =
            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        initView(rootView);

        return rootView;
    }

    public class LoginAsyncTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... voids) {
            String email = mEditEmail.getText().toString();
            String password = mEditPassword.getText().toString();
            UserFunctions userFunction = new UserFunctions();
            JSONObject json = userFunction.loginUser(email, password);
            boolean error = false;

            // check for login response
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);
                    if(Integer.parseInt(res) == 1){
                        // user successfully logged in
                        // Store user details in SQLite Database
                        //SkillzDbHelper db = new SkillzDbHelper(getActivity());
                        JSONObject json_user = json.getJSONObject("user");
                        JSONObject json_detail = json.getJSONObject("details");

                        // Clear all previous data in database
                        userFunction.logoutUser(getActivity());

                        ContentValues values = new ContentValues();
                        values.put(SkillzContract.UsersEntry.COLUMN_USER_NAME,json_user.getString(KEY_NAME)); // Name
                        values.put(SkillzContract.UsersEntry.COLUMN_USER_EMAIL, json_user.getString(KEY_EMAIL)); // Email
                        values.put(SkillzContract.UsersEntry.COLUMN_USER_ID, json.getString(KEY_UID)); // Email
                        values.put(SkillzContract.UsersEntry.COLUMN_DATE_TIME, json_user.getString(KEY_CREATED_AT)); // Created At

                        getActivity().getContentResolver().insert(SkillzContract.UsersEntry.CONTENT_URI,values);
                        ContentValues values1 = new ContentValues();
                        values1.put(SkillzContract.UsersDetails.COLUMN_USER_ID,json_detail.getString(KEY_UUID));
                        values1.put(SkillzContract.UsersDetails.COLUMN_USER_LOCATION,json_detail.getString(KEY_ADDRESS));
                        values1.put(SkillzContract.UsersDetails.COLUMN_USER_SKILLS,json_detail.getString(KEY_SKILL_SET));
                        values1.put(SkillzContract.UsersDetails.COLUMN_SELF_DESC,json_detail.getString(KEY_SELF_DESC));
                        values1.put(SkillzContract.UsersDetails.COLUMN_UPDATED_AT,json_detail.getString(KEY_UPDATED_AT));

                        getActivity().getContentResolver().insert(SkillzContract.UsersDetails.CONTENT_URI,values1);
                       // db.addUser(json_user.getString(KEY_NAME), json_user.getString(KEY_EMAIL), json.getString(KEY_UID), json_user.getString(KEY_CREATED_AT));
                        Log.d("Date: ", json_user.getString(KEY_CREATED_AT));

                        // Launch Dashboard Screen
                        Intent dashboard = new Intent(getActivity(), DashboardActivity.class);

                        // Close all views before launching Dashboard
                        dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(dashboard);

                        // Close Login Screen
                        getActivity().finish();
                    }else{
                        error = true;
                        // Error in login
                        // loginErrorMsg.setText("Incorrect username/password");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return error;
        }

        @Override
        protected void onPostExecute(Boolean error) {
            if (error){
                Toast.makeText(getActivity(), "Incorrect username/password", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

