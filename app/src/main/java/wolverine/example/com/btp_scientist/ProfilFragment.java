package wolverine.example.com.btp_scientist;

/**
 * Created by Wolverine on 25-06-2015.
 */
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfilFragment extends Fragment {
    String[] values = new String[]{"Edit Profile", "Answered Queries", "Settings"};
    ListView lvprofile;
    private SessionManager session;
    TextView uname,organization;
    String firstname,lastname,userDisplay,organisation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //session = new SessionManager(getActivity().getApplicationContext());
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        uname = (TextView)rootView.findViewById(R.id.user_name);
        organization = (TextView)rootView.findViewById(R.id.organisation);
        SharedPreferences preferences = this.getActivity().getSharedPreferences
                ("Info", Context.MODE_PRIVATE);
        firstname = preferences.getString("First_Name",null);
        lastname = preferences.getString("Last_Name",null);
        organisation = preferences.getString("Organization",null);
        userDisplay = firstname + " "+ lastname;
        uname.setText(userDisplay);
        organization.setText(organisation);

        perform(rootView);


        return rootView;

    }
    public void perform(View v) {
        lvprofile = (ListView) v.findViewById(R.id.ListProfile);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, values);
        lvprofile.setAdapter(adapter);

        lvprofile.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition = position;

                // ListView Clicked item value
                String itemValue = (String) lvprofile.getItemAtPosition(position);

                if(itemValue.matches("Answered Queries")){
                    Intent i = new Intent(getActivity().getBaseContext(),Answered_questions.class);
                    startActivityForResult(i, 0);
                }


            }

        });
    }

    private void logoutUser() {
        session.setLogin(false);

        //db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
