package wolverine.example.com.btp_scientist.adapter;

import java.util.List;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wolverine.example.com.btp_scientist.R;
import wolverine.example.com.btp_scientist.app.AppController;
import wolverine.example.com.btp_scientist.model.QuestionGetSet;


public class CustomListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<QuestionGetSet> quesItems;
    String number;


    public CustomListAdapter(Activity activity, List<QuestionGetSet> quesItems) {
        this.activity = activity;
        this.quesItems = quesItems;
    }


    @Override
    public int getCount() {
        return quesItems.size();
    }

    @Override
    public Object getItem(int location) {
        return quesItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SharedPreferences preferences = this.activity.getSharedPreferences
                ("Info", Context.MODE_PRIVATE);
        number = preferences.getString("Number", null);
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.custom_list_layout_home, null);

        TextView question = (TextView) convertView.findViewById(R.id.txt_questions);
        TextView date = (TextView) convertView.findViewById(R.id.txt_date);
        TextView number = (TextView) convertView.findViewById(R.id.txt_asked_by);

        // getting movie data for the row
        QuestionGetSet m = quesItems.get(position);

        // title
        question.setText(m.getQues());

        // rating
        date.setText(m.getDate());

        // release year
        number.setText(m.getNumber());
        if (ifAnswered(question, number)) {
            convertView.setBackgroundColor(Color.GREEN);
        }

        return convertView;
    }

    private boolean ifAnswered(TextView question, TextView number) {
        final int[] temp = {0};
        boolean check=false;
        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.POST, "http://192.168.43.17/answer_update/answer_check_mark.php?querry=" + question + "&&scientist_number="+number, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                temp[0] =1;
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);
        if(temp[0]==1){
            return true;
        }
        return  false;
    }



}


