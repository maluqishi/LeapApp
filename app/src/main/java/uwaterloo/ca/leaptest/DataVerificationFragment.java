package uwaterloo.ca.leaptest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class DataVerificationFragment extends Fragment {

    private static String temp;

    public DataVerificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_data_verification, container, false);

        TextView data = (TextView)rootView.findViewById(R.id.data);
        ArrayList<String> s = BimanualFragment.getData();

        temp = "";

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

    public static void downloadLog() {
        try {
            File file = new File("/sdcard/" + MainActivityFragment.getPatientID() + "_Data_Verification.txt");
            file.createNewFile();
            // Save file to SD card
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(temp);
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) { }
    }
}
