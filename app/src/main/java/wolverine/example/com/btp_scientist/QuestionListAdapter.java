package wolverine.example.com.btp_scientist;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

public class QuestionListAdapter extends ArrayAdapter<Questions>{
    private List<Questions> items;
    private LayoutInflater mInflater;
    public QuestionListAdapter(Context context, List<Questions> items) {
        super(context, R.layout.custom_list_layout_home, items);
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        
        if(v == null) {
            LayoutInflater li = LayoutInflater.from(getContext());
            v = li.inflate(R.layout.custom_list_layout_home, null);
        }
        
        Questions app = items.get(position);
        
        if(app != null) {
            TextView titleText = (TextView) v.findViewById(R.id.txt_questions);
            TextView ratingCntr = (TextView) v.findViewById(R.id.txt_date);
            TextView dlText = (TextView) v.findViewById(R.id.txt_asked_by);

            
            if(titleText != null) titleText.setText(app.getQues());
            
            if(ratingCntr != null) {
                ratingCntr.setText(Integer.parseInt(String.valueOf(app.getNumber())));
            }
            
            if(dlText != null ) {
                dlText.setText(app.getDate());
            }
        }
        
        return v;
    }
}
