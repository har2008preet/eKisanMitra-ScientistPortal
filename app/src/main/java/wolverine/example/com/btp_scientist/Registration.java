package wolverine.example.com.btp_scientist;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wolverine.example.com.btp_scientist.app.AppController;


public class Registration extends ActionBarActivity {
    private ProgressDialog pDialog;
    private CheckBox cHarvesting,cPesticide,cIrrigation,cSeed;
    EditText firstName, lastName,organization,email,phoneNumber;
    String Firstname,Lastname,Organization,Email,Phoneno;
    Spinner spinner1,spinner2;

    public ArrayList<String> departments = new ArrayList<String>();
    //public Set<String> departments = new HashSet<String>();
    public int count;
    //static String fn,ln,rf,pn;
    GoogleCloudMessaging gcm;
    String regid;
    Context context;
    private GCMClientManager pushClientManager;

    String PROJECT_NUMBER = "80066653863";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ERROR = "Error";
    private static String url= "http://192.168.43.17/scientist_portal/register.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firstName = (EditText)findViewById(R.id.fName);
        lastName = (EditText)findViewById(R.id.lName);
        phoneNumber = (EditText)findViewById(R.id.pnumber);
        organization = (EditText)findViewById(R.id.org);
        email = (EditText)findViewById(R.id.email);

        spinner1 = (Spinner)findViewById(R.id.spinner1);
        spinner2 = (Spinner)findViewById(R.id.spinner2);

    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
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
    }//Menu and its option

    public void register1(View view) {

        //Toast.makeText(getApplicationContext(),spinner1.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();

            if(firstName.getText().toString().isEmpty()||lastName.getText().toString().isEmpty()
                    ||organization.getText().toString().isEmpty()||email.getText().toString().isEmpty()
                    ||phoneNumber.getText().toString().isEmpty()||phoneNumber.getText().toString().length()!=10
                    ||spinner1.getSelectedItem().toString().isEmpty()||spinner1.getSelectedItem().toString().matches("Choose from here")
                    ||spinner2.getSelectedItem().toString().isEmpty()||spinner2.getSelectedItem().toString().matches("Choose from here"))
            {
                if(firstName.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Enter your First Name",Toast.LENGTH_SHORT).show();
                }
                else if(lastName.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Enter your Last Name",Toast.LENGTH_SHORT).show();
                }
                else if(spinner1.getSelectedItem().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Select your organization type",Toast.LENGTH_SHORT).show();
                }
                else if(organization.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Enter your Organization Name",Toast.LENGTH_SHORT).show();
                }
                else if(spinner2.getSelectedItem().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Select your specialised field",Toast.LENGTH_SHORT).show();
                }
                else if(email.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Enter your email",Toast.LENGTH_SHORT).show();
                }
                else if(phoneNumber.getText().toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please Enter your Phone Number",Toast.LENGTH_SHORT).show();
                }
                else if(phoneNumber.getText().toString().length() !=10)
                {
                    Toast.makeText(getApplicationContext(),"Phone Number Should be of length  10",Toast.LENGTH_SHORT).show();
                }
            }
            else {
                new AlertDialog.Builder(Registration.this)
                        .setMessage("Confirm your data")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                firstName = (EditText)findViewById(R.id.fName);
                                lastName = (EditText)findViewById(R.id.lName);
                                phoneNumber = (EditText)findViewById(R.id.pnumber);
                                organization = (EditText)findViewById(R.id.org);
                                email = (EditText)findViewById(R.id.email);

                                spinner1 = (Spinner)findViewById(R.id.spinner1);
                                spinner2 = (Spinner)findViewById(R.id.spinner2);

                                Firstname = firstName.getText().toString();//First Name
                                Lastname = lastName.getText().toString();//Last Name
                                Organization = organization.getText().toString();
                                Email = email.getText().toString();
                                Phoneno = phoneNumber.getText().toString();//Phone number

                                pushClientManager = new GCMClientManager(Registration.this, PROJECT_NUMBER);

                                pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {

                                    @Override

                                    public void onSuccess(String registrationId, boolean isNewRegistration) {
                                         CreateNewUser(Firstname,Lastname,spinner1.getSelectedItem().toString(),Organization,spinner2.getSelectedItem().toString(),Email,Phoneno,registrationId);


                                    }

                                    @Override
                                    public void onFailure(String ex) {
                                        super.onFailure(ex);
                                        Toast.makeText(getApplicationContext(), "Retry", Toast.LENGTH_LONG).show();
                                    }

                                });

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

    private void CreateNewUser(final String firstName, final String lastName, final String orgType, final String organization, final String relatedField, final String email, final String mobile, final String regId) {
        pDialog = new ProgressDialog(this);
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

                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        Toast.makeText(getApplicationContext(),"Registered Successfully.LogIn again.", Toast.LENGTH_LONG).show();
                        finish();

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
                params.put("firstName", firstName);
                params.put("lastName", lastName);
                params.put("organization", organization);
                params.put("orgType", orgType);
                params.put("relatedField", relatedField);
                params.put("mobile", mobile);
                params.put("email", email);
                params.put("regId",regId);

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
}
