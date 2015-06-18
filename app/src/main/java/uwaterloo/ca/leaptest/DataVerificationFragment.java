package uwaterloo.ca.leaptest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class DataVerificationFragment extends Fragment {

    public DataVerificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data_verification, container, false);

        TextView data = (TextView)rootView.findViewById(R.id.data);
        ArrayList<String> s = BimanualFragment.getData();

        String temp = "";
        try {
            for (int i = 0; i < s.size(); i = i + 3) {
                temp += String.format("File: %s\nMaximum Time Elapsed Per Frame: %sms\nAverage Frequency: %sHz\n\n", s.get(i), s.get(i + 1), s.get(i + 2));
            }
        } catch (Exception e) {
            temp = "No Saved Data";
        }

        data.setText(temp);
        return rootView;
    }
}
