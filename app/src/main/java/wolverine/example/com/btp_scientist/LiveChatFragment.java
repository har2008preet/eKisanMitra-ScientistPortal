package wolverine.example.com.btp_scientist;

/**
 * Created by Wolverine on 25-06-2015.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wolverine.example.com.btp_scientist.adapter.CustomListAdapter;
import wolverine.example.com.btp_scientist.app.AppController;
import wolverine.example.com.btp_scientist.model.QuestionGetSet;

public class LiveChatFragment extends Fragment implements AdapterView.OnItemClickListener, FetchDataListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://192.168.43.17/scientist_portal/allquestions.php";
    private ProgressDialog pDialog;
    private List<QuestionGetSet> movieList = new ArrayList<QuestionGetSet>();
    private ListView listView;
    private CustomListAdapter adapter;
    Spinner tagQ;
    String scientist_number;
    private String tag= "ALL";
    String[] farmerName;
    private String[] cropType;
    private String[] relatedField;
    private String[] number;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_live_chat, container, false);
        SharedPreferences preferences = this.getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
        scientist_number=preferences.getString("Number",null);

        tagQ = (Spinner)rootView.findViewById(R.id.tag);
        listView = (ListView) rootView.findViewById(R.id.list_ques);
        adapter = new CustomListAdapter(getActivity(), movieList);
        listView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_2);
        swipeRefreshLayout.setOnRefreshListener(this);
        tagQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tag = tagQ.getSelectedItem().toString();
                //Toast.makeText(getActivity().getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(true);
                                                fetchQuestions();
                                            }
                                        }
                );

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }

        });

        return rootView;
    }

    private void fetchQuestions() {
        swipeRefreshLayout.setRefreshing(true);
        //Toast.makeText(getActivity().getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
        // Creating volley request obj
        JsonArrayRequest quesReq;
        if(tag.matches("ALL")) {
            quesReq = new JsonArrayRequest("http://192.168.43.17/scientist_portal/allquestions.php/?number="+scientist_number,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());

                            movieList.clear();
                            // Parsing json
                            farmerName = new String[response.length()];
                            cropType = new String[response.length()];
                            relatedField = new String[response.length()];
                            number = new String[response.length()];
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
        }
        else {
            quesReq = new JsonArrayRequest("http://192.168.43.17/scientist_portal/questions.php?Tag="+tag+"&&number="+scientist_number,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d(TAG, response.toString());

                            movieList.clear();
                            // Parsing json
                            farmerName = new String[response.length()];
                            cropType = new String[response.length()];
                            relatedField = new String[response.length()];
                            number = new String[response.length()];
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
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                QuestionGetSet selectedques = movieList.get(position);
                String Querry = selectedques.getQues();
                String date = selectedques.getDate();
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


    @Override
    public void onRefresh() {
        movieList = new ArrayList<>();
        fetchQuestions();
    }
}
