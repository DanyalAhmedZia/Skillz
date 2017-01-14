package com.dev.dany.skillz.data;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserFunctions {

    private JSONParser jsonParser;

    // Testing in localhost using wamp or xampp
    // use http://10.0.2.2/ to connect to your localhost ie http://localhost/
    private static String loginURL = "http://10.0.3.2/skillz/login/";
    private static String registerURL = "http://10.0.3.2/skillz/login/";
    private static String postJobUrl = "http://10.0.3.2/skillz/cr_android/postjob.php";
    private static String userDetailURL = "http://10.0.3.2/skillz/cr_android/update_user_details.php";
    private static String getJobsURL = "http://10.0.3.2/skillz/cr_android/find_all_jobs.php";

    private static String login_tag = "login";
    private static String register_tag = "register";
    private static String postjob_tag = "postjob";
    private static String getJobs_tag = "findjobs";

    // constructor
    public UserFunctions(){
        jsonParser = new JSONParser();
    }

    /**
     * function make Login Request
     * @param email
     * @param password
     * */
    public JSONObject loginUser(String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", login_tag));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));
        JSONObject json = jsonParser.getJSONFromUrl(loginURL, params);
        // return json
        // Log.e("JSON", json.toString());
        return json;
    }

    /**
     * function make Login Request
     * @param name
     * @param email
     * @param password
     * */
    public JSONObject registerUser(String name, String email, String password){
        // Building Parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag", register_tag));
        params.add(new BasicNameValuePair("name", name));
        params.add(new BasicNameValuePair("email", email));
        params.add(new BasicNameValuePair("password", password));

        // getting JSON Object
        JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
        // return json
        return json;
    }
    /**
     * Function to post jobs
     */
    public JSONObject postJob(String uuid,String jobTitle,String jobDesc,String jobSkill){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag",postjob_tag));
        params.add(new BasicNameValuePair("uuid",uuid));
        params.add(new BasicNameValuePair("name",jobTitle));
        params.add(new BasicNameValuePair("description",jobDesc));
        params.add(new BasicNameValuePair("skill_set",jobSkill));

        JSONObject jsonObject = jsonParser.getJSONFromUrl(postJobUrl,params);
        return jsonObject;
    }
    public JSONObject postUserDetails(String uuid, String address,String skill_set,String self_description){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("uuid",uuid));
        params.add(new BasicNameValuePair("address",address));
        params.add(new BasicNameValuePair("skill_set",skill_set));
        params.add(new BasicNameValuePair("self_description",self_description));
        JSONObject jsonObject = jsonParser.getJSONFromUrl(userDetailURL,params);
        return jsonObject;
    }
    public JSONObject getJobs(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("tag",getJobs_tag));
        JSONObject jsonObject = jsonParser.getJSONFromUrl(getJobsURL,params);
        return jsonObject;
    }


    /**
     * Function get Login status
     * */
    public boolean isUserLoggedIn(Context context){
        SkillzDbHelper db = new SkillzDbHelper(context);
        int count = db.getRowCount();
        if(count > 0){
            // user logged in
            return true;
        }
        return false;
    }

    /**
     * Function to logout user
     * Reset Database
     * */
    public boolean logoutUser(Context context){
        SkillzDbHelper db = new SkillzDbHelper(context);
        db.resetTables();
        return true;
    }

}