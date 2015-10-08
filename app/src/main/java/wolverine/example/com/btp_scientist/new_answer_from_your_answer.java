package wolverine.example.com.btp_scientist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import wolverine.example.com.btp_scientist.app.AppController;

public class new_answer_from_your_answer extends Activity  {

    String Querry,scientist_number,answer,farmerId,farmerName,cropType,relatedFields,scientistName,query,ans;
    TextView QuestionTxt,answer_txt;
    Button submit;
    ProgressDialog PD,pDialog;

    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_answer_from_your_answer);

        Intent i1 =getIntent();
        Bundle data = i1.getExtras();
        Querry = data.getString("querry");
        farmerId = data.getString("farmerNumber");
        farmerName = data.getString("farmerName");
        cropType = data.getString("cropType");
        relatedFields = data.getString("relatedFields");
        scientistName = data.getString("scientistName");

        //Toast.makeText(getApplicationContext(),farmerId,Toast.LENGTH_SHORT).show();
        QuestionTxt = (TextView)findViewById(R.id.querry);
        QuestionTxt.setText(Querry);
        answer_txt = (EditText)findViewById(R.id.answer_querry);
        SharedPreferences preferences = this.getSharedPreferences("Info", Context.MODE_PRIVATE);
        scientist_number= preferences.getString("Number",null);
        submit = (Button)findViewById(R.id.submitButton);

    }
    public void answerit(View view) {
        if (answer_txt.getText().toString().isEmpty()) {
            Toast.makeText(new_answer_from_your_answer.this, "Type Your answer", Toast.LENGTH_SHORT).show();
        } else {
            answer = answer_txt.getText().toString();
            try {
                ans = URLEncoder.encode(answer, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            update();
        }
    }
    public void update() {
        PD = new ProgressDialog(this);
        PD.setMessage("please wait.....");
        PD.setCancelable(false);
        PD.show();

        answer = answer_txt.getText().toString();
        try {
            ans= URLEncoder.encode(answer, "utf-8");
            query= URLEncoder.encode(Querry, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String update_url = "http://192.168.43.17/scientist_portal/new_answer.php/?querry="
                + query +"&&answer=" + ans+"&&scientist_number="+scientist_number+"&&farmer_Number="+farmerId+"&&farmer_Name="+farmerName+"&&cropType="+cropType+"&&relatedFields="+relatedFields+"&&scientistName="+scientistName;
        Log.d("update_url======", update_url);
        JsonObjectRequest update_request = new JsonObjectRequest(update_url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                try {
                    int success = response.getInt("success");

                    if (success == 1) {

                        Intent intent = new Intent(new_answer_from_your_answer.this,
                                Your_Answered_queries.class);
                        intent.putExtra("Querry", Querry);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),
                                "Updated Successfully",
                                Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        PD.dismiss();
                        Toast.makeText(getApplicationContext(),
                                "failed to update", Toast.LENGTH_SHORT)
                                .show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(update_request);
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
        getMenuInflater().inflate(R.menu.menu_new_answer_from_your_answer, menu);
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
}
