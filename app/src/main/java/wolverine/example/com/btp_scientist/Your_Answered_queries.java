package wolverine.example.com.btp_scientist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import wolverine.example.com.btp_scientist.adapter.CustomListAdapter;
import wolverine.example.com.btp_scientist.app.AppController;
import wolverine.example.com.btp_scientist.model.QuestionGetSet;

public class Your_Answered_queries extends Activity {
    String querry,scientist_number;

    private ListView lvPrevious;
    private List<QuestionGetSet> movieList = new ArrayList<QuestionGetSet>();
    private CustomListAdapter adapter;
    private static final String TAG = MainActivity.class.getSimpleName();
    String[] farmerNumber;
    String[] farmerName;
    String[] cropType;
    String[] relatedFields;
    String[] scientistName;

    String[] prev_answer;
    TextView Question;
    EditText Answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your__answered_queries);

        Question= (TextView)findViewById(R.id.querry);

        Intent ansquerry =getIntent();
        Bundle data = ansquerry.getExtras();
        querry = data.getString("Querry");
        Question.setText(querry);

        lvPrevious = (ListView)findViewById(R.id.pre_ans);
        adapter = new CustomListAdapter(this, movieList);
        lvPrevious.setAdapter(adapter);

        SharedPreferences preferences = this.getSharedPreferences("Info", Context.MODE_PRIVATE);
        scientist_number=preferences.getString("Number",null);
        //Toast.makeText(Your_Answered_queries.this, "http://192.168.43.17/allquestions/prev_ans.php/?number="+scientist_number+"&&querry="+querry, Toast.LENGTH_LONG).show();
        try {
            PreviousByOwn();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void PreviousByOwn() throws UnsupportedEncodingException {
        //swipeRefreshLayout.setRefreshing(true);
        //Toast.makeText(Your_Answered_queries.this, "Function Called", Toast.LENGTH_SHORT).show();
        final String query=URLEncoder.encode(querry, "utf-8");
        JsonArrayRequest quesReq = new JsonArrayRequest("http://192.168.43.17/scientist_portal/prev_ans.php/?number="+scientist_number+"&&querry="+ query,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        movieList.clear();
                        farmerNumber = new String[response.length()];
                        farmerName = new String[response.length()];
                        cropType = new String[response.length()];
                        relatedFields = new String[response.length()];
                        scientistName = new String[response.length()];

                        prev_answer = new String[response.length()];

                        for (int i = 0; i <= response.length(); i++) {
                            try {
                                //Toast.makeText(Your_Answered_queries.this, "http://192.168.43.17/allquestions/prev_ans.php/?number="+scientist_number+"&&querry="+ query, Toast.LENGTH_SHORT).show();
                                JSONObject obj = response.getJSONObject(i);
                                QuestionGetSet movie = new QuestionGetSet();
                                movie.setQues(obj.getString("ans"));
                                movie.setDate(obj.getString("replyDate"));
                                movie.setNumber(obj.getString("farmerName"));
                                farmerNumber[i] = obj.getString("farmerId");
                                farmerName[i] = obj.getString("farmerName");
                                cropType[i] = obj.getString("cropType");
                                relatedFields[i] = obj.getString("relatedFields");
                                scientistName[i] = obj.getString("scientistName");

                                prev_answer[i] = obj.getString("ans");
                                    /*cropType[i] = obj.getString("cropType");
                                    relatedField[i] = obj.getString("relatedFields");
                                    number[i]=obj.getString("id");*/

                                // adding movie to movies array
                                movieList.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                        //              swipeRefreshLayout.setRefreshing(false);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //      swipeRefreshLayout.setRefreshing(false);

            }
        });
        lvPrevious.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(Your_Answered_queries.this)
                        .setMessage("Do you want to change your answer or write a new answer?")
                        .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i2 = new Intent(Your_Answered_queries.this,Edit_Answer_from_your_ans.class);
                                i2.putExtra("querry",querry);
                                i2.putExtra("farmerNumber",farmerNumber[position]);
                                i2.putExtra("farmerName",farmerName[position]);
                                i2.putExtra("cropType",cropType[position]);
                                i2.putExtra("relatedFields",relatedFields[position]);
                                i2.putExtra("scientistName",scientistName[position]);
                                i2.putExtra("Prev_Ans",prev_answer[position]);
                                startActivity(i2);
                                finish();
                            }
                        })
                        .setNegativeButton("Write New", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i1 = new Intent(Your_Answered_queries.this,new_answer_from_your_answer.class);
                                i1.putExtra("querry",querry);
                                i1.putExtra("farmerNumber",farmerNumber[position]);
                                i1.putExtra("farmerName",farmerName[position]);
                                i1.putExtra("cropType",cropType[position]);
                                i1.putExtra("relatedFields",relatedFields[position]);
                                i1.putExtra("scientistName",scientistName[position]);
                                i1.putExtra("Prev_Ans",prev_answer[position]);
                                startActivity(i1);
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(quesReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_your__answered_queries, menu);
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
