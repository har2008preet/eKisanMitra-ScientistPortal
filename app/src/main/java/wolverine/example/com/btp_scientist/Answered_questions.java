package wolverine.example.com.btp_scientist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wolverine.example.com.btp_scientist.adapter.CustomListAdapter;
import wolverine.example.com.btp_scientist.app.AppController;
import wolverine.example.com.btp_scientist.model.QuestionGetSet;

public class Answered_questions extends Activity {

    private ListView lvPrevious;
    private List<QuestionGetSet> movieList = new ArrayList<QuestionGetSet>();
    private CustomListAdapter adapter;
    private static final String TAG = MainActivity.class.getSimpleName();
    ProgressDialog pd;
    String scientist_number;
    private SwipeRefreshLayout swipeRefreshLayout;
    ImageView back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answered_questions);

        back_button = (ImageView)findViewById(R.id.back_profile);

        lvPrevious = (ListView)findViewById(R.id.ans_ques);
        adapter = new CustomListAdapter(this, movieList);
        lvPrevious.setAdapter(adapter);

        SharedPreferences preferences = this.getSharedPreferences("Info", Context.MODE_PRIVATE);
        scientist_number=preferences.getString("Number",null);

        PreviousByOwn();
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(Answered_questions.this,FinalScreen.class);
                back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(back,0);
            }
        });

    }
    private void PreviousByOwn(){
        //swipeRefreshLayout.setRefreshing(true);
        JsonArrayRequest quesReq = new JsonArrayRequest("http://192.168.43.17/scientist_portal/answered_queries.php/?number="+scientist_number,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        movieList.clear();
                            /*farmerName = new String[response.length()];
                            cropType = new String[response.length()];
                            relatedField = new String[response.length()];
                            number = new String[response.length()];*/

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                QuestionGetSet movie = new QuestionGetSet();
                                movie.setQues(obj.getString("ques"));
                                movie.setDate(obj.getString("replyDate"));
                                movie.setNumber(obj.getString("farmerName"));
                                    /*farmerName[i] = obj.getString("farmerName");
                                    cropType[i] = obj.getString("cropType");
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionGetSet selectedques = movieList.get(position);
                String Querry = selectedques.getQues();
                /*getFarmerInfo(Querry, number, date);*/
                Intent ansScreen = new Intent(Answered_questions.this, Your_Answered_queries.class);
                ansScreen.putExtra("Querry", Querry);
                startActivity(ansScreen);


            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(quesReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_answered_questions, menu);
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
