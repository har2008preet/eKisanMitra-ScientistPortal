package wolverine.example.com.btp_scientist;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wolverine.example.com.btp_scientist.adapter.CustomListAdapter;
import wolverine.example.com.btp_scientist.app.AppController;
import wolverine.example.com.btp_scientist.model.QuestionGetSet;


public class answer_query extends ActionBarActivity {
    String Querry,PhoneNumber,Date,answer,farmerName,cropType,relatedField;
    TextView QuestionTxt;
    EditText answer_txt;
    ProgressDialog PD,pDialog;
    private static final String url = "http://192.168.43.17/scientist_portal/answer.php";
    String update_url = "http://192.168.43.17/btp/unanswered_questions_list.php?question="
            + Querry + "&&answer=" + answer;
    InputStream is=null;
    String result=null;
    String line=null;
    String scientist_number,scientist_name ;
    int code;
    ListView lvPrevious;
    private List<QuestionGetSet> movieList = new ArrayList<QuestionGetSet>();
    private CustomListAdapter adapter;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_query);


        adapter = new CustomListAdapter(this, movieList);

        new PreviousByOwn().execute();

        Intent ansquerry =getIntent();
        Bundle data = ansquerry.getExtras();
        Querry = data.getString("Querry");
        PhoneNumber = data.getString("Number");
        //Toast.makeText(this, Querry+scientist_number, Toast.LENGTH_SHORT).show();
        Date = data.getString("date");
        farmerName = data.getString("farmerName");
        cropType = data.getString("cropType");
        relatedField = data.getString("relatedField");

        QuestionTxt = (TextView)findViewById(R.id.querry);
        QuestionTxt.setText(Querry);
        answer_txt = (EditText)findViewById(R.id.answer_querry);
        SharedPreferences preferences = this.getSharedPreferences("Info", Context.MODE_PRIVATE);
        scientist_number= preferences.getString("Number",null);
        scientist_name = preferences.getString("First_Name",null);
        //Toast.makeText(this, Querry+scientist_number, Toast.LENGTH_SHORT).show();
    }
    class PreviousByOwn extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            JsonArrayRequest quesReq = new JsonArrayRequest("http://192.168.43.17/scientist_portal/prevown.php",
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());
                            movieList.clear();
                            // Parsing json
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    QuestionGetSet movie = new QuestionGetSet();
                                    movie.setQues(obj.getString("ans"));
                                    movie.setDate(null);
                                    movie.setNumber(null);

                                    // adding movie to movies array
                                    movieList.add(movie);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            // notifying list adapter about data changes
                            // so that it renders the list view with updated data
                            adapter.notifyDataSetChanged();
                        }


                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());

                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("querry", Querry);
                    params.put("number", scientist_number);

                    return params;
                }
            };;
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(quesReq);
            return null;
        }
    }
    public void answerit(View view) {

        answer = answer_txt.getText().toString();

        WriteAnswer(Querry, answer,scientist_number,scientist_name,farmerName,cropType,relatedField,PhoneNumber);
        //update();

       // Toast.makeText(this,update_url,Toast.LENGTH_SHORT).show();
    }

    public void WriteAnswer(final String querry, final String answer, final String scientist_number,
                            final String scientist_name, final String farmerName,final String cropType,
                            final String relatedField,final String PhoneNumber)
    {
        pDialog = new ProgressDialog(answer_query.this);
        pDialog.setMessage("Submitting...");
        pDialog.setCancelable(false);
        pDialog.show();
        String ques = null,ans = null;

        try {
            ques = URLEncoder.encode(querry, "utf-8");
            ans = URLEncoder.encode(answer, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // String url = "http://192.168.43.17/register_scien/register.php;
        Log.d("url========","http://192.168.43.17/scientist_portal/answer.php/?querry="+
                ques+"&answer="+ans+"&scientist_number="+scientist_number+"&scientist_name="+scientist_name+"&farmerName="+farmerName
                +"&cropType="+cropType+"&relatedField="+relatedField+"&PhoneNumber="+PhoneNumber);
        StringRequest SpecialReq = new StringRequest(Request.Method.POST,"http://192.168.43.17/scientist_portal/answer.php/?querry="+
                ques+"&answer="+ans+"&scientist_number="+scientist_number+"&scientist_name="+scientist_name+"&farmerName="+farmerName
                +"&cropType="+cropType+"&relatedField="+relatedField+"&PhoneNumber="+PhoneNumber,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        pDialog.dismiss();
                        Log.d("Response==========",response);

                       JSONObject jObj = null;
                        try {
                            jObj = new JSONObject(response);
                            int success = jObj.getInt("success");


                            if (success == 1) {
                                // user successfully logged in
                                // Create login session
                                //session.setLogin(true);

                                // Launch main activity
                                Intent intent = new Intent(answer_query.this,
                                        FinalScreen.class);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(),"success ", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                // Error in login. Get the error message
                                String errorMsg = jObj.getString("message");
                                Toast.makeText(getApplicationContext(),
                                        errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            //Toast.makeText(getApplicationContext(),
                              //      "errorException ", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        hidePDialog();
                        //Toast.makeText(getApplicationContext(),"failure", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("querry", querry);
                params.put("answer", answer);
                params.put("scientist_number", scientist_number);
                params.put("scientist_name",scientist_name);
                params.put("farmerName",farmerName);
                params.put("cropType",cropType);
                params.put("relatedField",relatedField);
                params.put("farmer_Number",PhoneNumber);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answer_query, menu);
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

    public void toggle1(View view) {
        lvPrevious.setVisibility(lvPrevious.isShown()
        ?view.GONE
        :view.VISIBLE);
    }
}
