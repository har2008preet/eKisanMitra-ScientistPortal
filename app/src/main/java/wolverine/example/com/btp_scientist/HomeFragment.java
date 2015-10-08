package wolverine.example.com.btp_scientist;

/**
 * Created by Wolverine on 25-06-2015.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

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

public class HomeFragment extends Fragment implements AdapterView.OnItemClickListener, FetchDataListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://192.168.46.17/btp/unanswered_questions_list.php";
    private ProgressDialog pDialog;
    private List<QuestionGetSet> movieList = new ArrayList<QuestionGetSet>();
    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CustomListAdapter adapter;
    String tag,scientist_number;
    String[] farmerName;
    private String[] cropType;
    private String[] relatedField;
    private String[] number;
    public static final String farmName="Farmer_Name";
    public static final String cType="crop_Type";
    public static final String rFields="Related_Field";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
        tag = preferences.getString("Department",null);
        scientist_number=preferences.getString("Number",null);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        //initView();

        listView = (ListView) rootView.findViewById(R.id.list_ques);
        adapter = new CustomListAdapter(getActivity(), movieList);
        listView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        movieList.clear();
                                        fetchQuestions();
                                    }
                                }
        );

        return rootView;
    }
    @Override
    public void onRefresh() {
        movieList = new ArrayList<>();
        fetchQuestions();
    }
    private void fetchQuestions() {
        swipeRefreshLayout.setRefreshing(true);

        JsonArrayRequest quesReq = new JsonArrayRequest("http://192.168.43.17/scientist_portal/questions.php/?Tag="+tag+"&&number="+scientist_number,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        movieList.clear();
                        farmerName = new String[response.length()];
                        cropType = new String[response.length()];
                        relatedField = new String[response.length()];
                        number = new String[response.length()];

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                QuestionGetSet movie = new QuestionGetSet();
                                movie.setQues(obj.getString("ques"));
                                movie.setDate(obj.getString("askDate"));
                                movie.setNumber(obj.getString("farmerName"));
                                farmerName[i] = obj.getString("farmerName");
                                cropType[i] = obj.getString("cropType");
                                relatedField[i] = obj.getString("relatedFields");
                                number[i]=obj.getString("id");

                                // adding movie to movies array
                                movieList.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }

					
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                swipeRefreshLayout.setRefreshing(false);

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionGetSet selectedques = movieList.get(position);
                String Querry = selectedques.getQues();
                String date = selectedques.getDate();
                /*getFarmerInfo(Querry, number, date);*/
                //Toast.makeText(getActivity(),farmerName[position]+" "+cropType[position]+" "+relatedField[position],Toast.LENGTH_LONG).show();
                Intent ansScreen = new Intent(getActivity(), answer_query.class);
                ansScreen.putExtra("Querry", Querry);
                ansScreen.putExtra("Number", number[position]);
                ansScreen.putExtra("date", date);
                ansScreen.putExtra("farmerName",farmerName[position]);
                ansScreen.putExtra("cropType",cropType[position]);
                ansScreen.putExtra("relatedField",relatedField[position]);
                startActivity(ansScreen);
                getActivity().overridePendingTransition(0, R.anim.exit_slide_down);

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(quesReq);
    }

    private void getFarmerInfo(final String querry, final String number, final String date) {
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.POST, "http://192.168.43.17/scientist_portal/farmer_info.php", null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("info");

                                for (int i = 0; i < 1; i++) {

                                    JSONObject jObj1 = ja.getJSONObject(i);
                                    /*farmerName = jObj1.getString("farmerName");
                                    cropType = jObj1.getString("cropType");
                                    relatedField = jObj1.getString("relatedFields");*/

                                    /*Toast.makeText(getActivity(),farmerName,Toast.LENGTH_SHORT).show();*/

                                } // for loop ends


                            } // if ends

                        } catch (JSONException e) {
                            //Toast.makeText(getActivity(),"Error Here",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                /*Toast.makeText(getActivity(),querry+number+date+" Error Here 2",Toast.LENGTH_SHORT).show();*/
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("querry", querry);
                params.put("number", number);
                params.put("querry", date);

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
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
    public void onFetchComplete(List<Questions> data) {

    }

    @Override
    public void onFetchFailure(String msg) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

}


