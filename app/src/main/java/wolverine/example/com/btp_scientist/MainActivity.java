package wolverine.example.com.btp_scientist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wolverine.example.com.btp_scientist.app.AppController;


public class MainActivity extends ActionBarActivity {
    private ProgressDialog pDialog;

    private SessionManager session;

    String firstName,lastName,department,district,organization,post,number;
    public static final String fName="First_Name";
    public static final String lName="Last_Name";
    public static final String dment="Department";
    public static final String drict="District";
    public static final String ozation="Organization";
    public static final String pst="Post";
    public static final String nbr="Number";
    SharedPreferences sharedpreferences;
    Activity context = this;

    HttpPost httppost;
    StringBuffer buffer;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;

    String PROJECT_NUMBER = "80066653863";
    JSONParser jsonParser = new JSONParser();
    private static final String TAG = Registration.class.getSimpleName();
    private static final String LOGIN_URL = "http://192.168.43.17/scientist_portal/login.php";
    private static final String S_INFO = "http://192.168.43.17/register_scien/scientist_info.PHP";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    ArrayList<HashMap<String, String>> Item_List;

    EditText phoneno;
    String phonenumber;
    String regId;
    private GCMClientManager pushClientManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(MainActivity.this, FinalScreen.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        return super.onOptionsItemSelected(item);
    }

    public void login(View view) {
        phoneno = (EditText)findViewById(R.id.phoneno);
        phonenumber = phoneno.getText().toString();
        if(phonenumber.length()!=10)
        {
            Toast.makeText(MainActivity.this,"Enter 10 digit phone number",Toast.LENGTH_SHORT).show();
        }
        else {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Confirm your number:+91-" + phonenumber)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            checkLogin(phonenumber);

                                }
                            })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }



    }


    private void checkLogin(final String phonenumber) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                LOGIN_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();


                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(response);
                    int success = jObj.getInt("success");


                    if (success == 1) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        scientist_info(phonenumber);

                        pushClientManager = new GCMClientManager(MainActivity.this, PROJECT_NUMBER);

                        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {

                            @Override

                            public void onSuccess(String registrationId, boolean isNewRegistration) {

                                regId = registrationId;

                            }

                            @Override
                            public void onFailure(String ex) {
                                super.onFailure(ex);
                                Toast.makeText(getApplicationContext(), "Retry", Toast.LENGTH_LONG).show();
                            }

                        });

                        if(jObj.getString("regId").matches("null")||jObj.getString("regId")!=regId)
                        {


                            final String PhoneNumber=jObj.getString("PhoneNumber");

                            UpdateUser(PhoneNumber, regId);
                        }
                        else {
                            // Launch main activity
                            Intent intent = new Intent(MainActivity.this,
                                    FinalScreen.class);
                            startActivity(intent);
                            //Toast.makeText(getApplicationContext(),"success ", Toast.LENGTH_LONG).show();
                            //      finish();
                        }
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    //Toast.makeText(getApplicationContext(),
                     //       "errorException ", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                //Toast.makeText(getApplicationContext(),
                  //      "Login Error", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("PhoneNumber",phonenumber);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void scientist_info(final String phonenumber) {
        pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Logging in ...");
        pDialog.setCancelable(false);
        pDialog.show();
        // String url = "http://192.168.43.17/register_scien/register.php;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.POST, "http://192.168.43.17/scientist_portal/scientist_info.PHP?PhoneNumber="+phonenumber, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("info");

                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject jObj1 = ja.getJSONObject(i);
                                    firstName = jObj1.getString("FirstName");
                                    lastName = jObj1.getString("LastName");
                                    department = jObj1.getString("Department");
                                    district = jObj1.getString("District");
                                    organization = jObj1.getString("Organization");
                                    post = jObj1.getString("Post");
                                    number = jObj1.getString("Number");
                                    SharedPreferences.Editor editor = getSharedPreferences("Info",
                                            Context.MODE_PRIVATE).edit();
                                    editor.putString(fName, firstName);
                                    editor.putString(lName, lastName);
                                    editor.putString(nbr,number);
                                    editor.putString(dment, department);
                                    editor.putString(drict, district);
                                    editor.putString(ozation, organization);
                                    editor.putString(pst, post);
                                    editor.commit();

                                    //Toast.makeText(MainActivity.this,number,Toast.LENGTH_SHORT).show();

                                } // for loop ends



                                pDialog.dismiss();

                            } // if ends

                        } catch (JSONException e) {
                            //Toast.makeText(getApplicationContext(),"Error Here",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),"Error Here 2",Toast.LENGTH_SHORT).show();
                pDialog.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
    }

    private void UpdateUser(final String phoneNumber,final String regId) {
        StringRequest SpecialReq = new StringRequest(Request.Method.POST,"http://192.168.43.17/scientist_portal/login_update.php",
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {


                        JSONObject jObj1 = null;
                        try {
                            jObj1 = new JSONObject(response);
                            int success = jObj1.getInt("success");


                            if (success == 1) {
                                // user successfully logged in
                                // Create login session
                                session.setLogin(true);

                                // Launch main activity
                                Intent intent = new Intent(MainActivity.this,
                                        FinalScreen.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"success ", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj1.getString("message");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),
                                    "errorException ", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        hideDialog();
                        Toast.makeText(getApplicationContext(),"failure", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("PhoneNumber",phoneNumber );
                params.put("regId", regId);

                return params;
            }
        };



        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(SpecialReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void CheckUser()
    {
        pDialog = new ProgressDialog(getApplicationContext());
        pDialog.setMessage("registering ...");
        pDialog.setCancelable(false);
        pDialog.show();
        // String url = "http://192.168.43.17/register_scien/register.php;

        StringRequest SpecialReq = new StringRequest(Request.Method.POST,"http://192.168.65.168/register_scien/login.php",
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        hidePDialog();

                        Intent i = new Intent(getApplicationContext(), FinalScreen.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),"success ", Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        hidePDialog();
                        Toast.makeText(getApplicationContext(),"failure", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("phonenumber",phonenumber);

                return params;
            }
        };



        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(SpecialReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    void loginCheck() {
        try{

            httpclient=new DefaultHttpClient();
            httppost= new HttpPost("http://192.168.65.168/register_scien/login.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("phonenumber",phonenumber));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //Execute HTTP Post Request
            response=httpclient.execute(httppost);
            // edited by James from coderzheaven.. from here....
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            final String response = httpclient.execute(httppost, responseHandler);
            runOnUiThread(new Runnable() {
                public void run() {
                    dialog.dismiss();
                }
            });

            if(response.equalsIgnoreCase("User Found")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(),"Login Success", Toast.LENGTH_SHORT).show();
                    }
                });

                startActivity(new Intent(getApplicationContext(), FinalScreen.class));
            }else{
                showAlert();
            }

        }catch(Exception e){
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }
    public void showAlert(){
        runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Login Error.");
                builder.setMessage("User not Found.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void register(View view) {
        Intent intent1 = new Intent(MainActivity.this,Registration.class);
        startActivity(intent1);
    }

    class LoginCheck extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Checking. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {

            int success;
            try {

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("phonenumber",phonenumber));

                Log.d("request!", "starting");

                JSONObject json = jsonParser.makeHttpRequest(
                        LOGIN_URL, "POST", params);

                // checking  log for json response
                Log.d("Login attempt", json.toString());

                // success tag for json
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Log.d("Successfully Login!", json.toString());

                    Intent ii = new Intent(getApplicationContext(),FinalScreen.class);
                    finish();
                    // this finish() method is used to tell android os that we are done with current //activity now! Moving to other activity
                    startActivity(ii);
                    return json.getString(TAG_MESSAGE);
                }else{

                    return json.getString(TAG_MESSAGE);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            /*if (message != null){
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }*/

        }

    }

    class Register extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Checking. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {

            for(int i=0;i<=20000;i++)
            {
                System.out.println(i);
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(MainActivity.this,Register.class);
                    startActivity(intent);
                }
            });

        }

    }
}
