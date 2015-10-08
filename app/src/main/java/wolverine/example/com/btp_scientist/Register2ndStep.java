package wolverine.example.com.btp_scientist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wolverine.example.com.btp_scientist.app.AppController;


public class Register2ndStep extends ActionBarActivity {
    private ProgressDialog pDialog;
    String userfname,userlname,userRfield,userpno,userdist,userorg,userpos;
    TextView fname;
    EditText district,organisation,position;
    JSONParser jsonParser = new JSONParser();
    // url to create new product
    private static String url= "http://192.168.43.17/register_scien/register.php";
    int success;
    // JSON Node names

    String id;
    InputStream is=null;
    String result=null;
    String line=null;
    int code;

    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    private GCMClientManager pushClientManager;

    String PROJECT_NUMBER = "80066653863";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ERROR = "Error";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_register2nd_step);


        Intent i =getIntent();
        Bundle data = i.getExtras();
        userfname = data.getString("fn");
        userlname = data.getString("ln");
        userRfield = data.getString("rf");
        userpno = data.getString("pn");
        Toast.makeText(getApplicationContext(),userRfield,Toast.LENGTH_SHORT).show();

        district = (EditText)findViewById(R.id.district_text);
        organisation = (EditText)findViewById(R.id.organisation_text);
        position = (EditText)findViewById(R.id.position_text);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register2nd_step, menu);
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

    public void registerfinish(View view) {
        userdist = district.getText().toString();
        userorg = organisation.getText().toString();
        userpos = position.getText().toString();

        pushClientManager = new GCMClientManager(Register2ndStep.this, PROJECT_NUMBER);

        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {

            @Override

            public void onSuccess(String registrationId, boolean isNewRegistration) {

                //Toast.makeText(getApplicationContext(), registrationId, Toast.LENGTH_SHORT).show();
                CreateNewUser(registrationId);

            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
                Toast.makeText(getApplicationContext(), "Retry", Toast.LENGTH_LONG).show();
            }

        });


        //new finalRegister().execute();
        //insert();
    }

    public void CreateNewUser(final String registrationId)
    {
        pDialog = new ProgressDialog(Register2ndStep.this);
        pDialog.setMessage("registering ...");
        pDialog.setCancelable(false);
        pDialog.show();
       // String url = "http://192.168.43.17/register_scien/register.php;

        StringRequest SpecialReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        hidePDialog();

                        Intent i = new Intent(Register2ndStep.this, MainActivity.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),registrationId, Toast.LENGTH_LONG).show();

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
                params.put("FirstName", userfname);
                params.put("LastName", userlname);
                params.put("Department", userRfield);
                params.put("PhoneNumber", userpno);
                params.put("District", userdist);
                params.put("Organisation", userorg);
                params.put("Post", userpos);
                params.put("regId",registrationId);

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


    public void insert()
    {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        /*nameValuePairs.add(new BasicNameValuePair("id",id));
        nameValuePairs.add(new BasicNameValuePair("name",name));*/
        nameValuePairs.add(new BasicNameValuePair("FirstName", userfname));
        nameValuePairs.add(new BasicNameValuePair("LastName", userlname));
        nameValuePairs.add(new BasicNameValuePair("Department", userRfield));
        nameValuePairs.add(new BasicNameValuePair("PhoneNumber", userpno));
        nameValuePairs.add(new BasicNameValuePair("District", userdist));
        nameValuePairs.add(new BasicNameValuePair("Organisation", userorg));
        nameValuePairs.add(new BasicNameValuePair("Post", userpos));

        try
        {
            // request method is GET
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
            url += "?" + paramString;
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
        }

        try
        {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

            if(code==1)
            {
                Toast.makeText(getBaseContext(), "Inserted Successfully",
                        Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Sorry, Try Again",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }
    }

    class finalRegister extends AsyncTask<String, String, String> {
        JSONObject json;

        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Register2ndStep.this);
            pDialog.setMessage("Registering. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("FirstName", userfname));
            params.add(new BasicNameValuePair("LastName", userlname));
            params.add(new BasicNameValuePair("Department", userRfield));
            params.add(new BasicNameValuePair("PhoneNumber", userpno));
            params.add(new BasicNameValuePair("District", userdist));
            params.add(new BasicNameValuePair("Organisation", userorg));
            params.add(new BasicNameValuePair("Post", userpos));

            // getting JSON Object
            // Note that create product url accepts POST method

            json = jsonParser.makeHttpRequest(url,
                    "POST", params);
            Log.d("Create Response", json.toString());

            try {
                success = json.getInt(TAG_SUCCESS);


            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
                if (success == 1) {
                    // successfully created a user
                    Toast.makeText(getApplicationContext(),"You have successfully registered",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                    // closing this screen
                    finish();
                } else {
                    // failed to create user
                    Log.d("failed to create user", json.toString());
                }


            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products


            pDialog.dismiss();



        }

    }
}
