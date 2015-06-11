package uwaterloo.ca.leaptest;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class BimanualFragment extends Fragment {

    // Declare variables
    private static final MediaPlayer mp = new MediaPlayer();
    private static TextView title = null;
    private static TextView connectionStatus = null;
    private static TextView handStatus = null;
    private static TextView trial = null;
    private static Switch weight = null;
    private static Switch trialAutomation = null;
    private static Button start = null;
    private static Button stop = null;
    private static int trialNumber = 1;

    public BimanualFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_bimanual, container, false);

        //  Initialize  variables
        title = (TextView)rootView.findViewById(R.id.title);
        connectionStatus = (TextView) rootView.findViewById(R.id.connectionStatus);
        handStatus = (TextView)rootView.findViewById(R.id.handStatus);
        trial = (TextView) rootView.findViewById(R.id.trial);
        trial.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (!mp.isPlaying()) {
                    LayoutInflater layoutInflater = LayoutInflater.from(rootView.getContext());
                    View promptView = layoutInflater.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());

                    // set prompts.xml to be the layout file of the alertdialog builder
                    alertDialogBuilder.setView(promptView);
                    final EditText input = (EditText) promptView.findViewById(R.id.userInput);

                    // setup a dialog window
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    try {
                                        int temp = Integer.parseInt(input.getText().toString());
                                        if (temp > 0) {
                                            trial.setText("Trial " + temp);
                                            trialNumber = temp;
                                        } else Toast.makeText(rootView.getContext(), "Invalid Trial Number", Toast.LENGTH_SHORT).show();
                                    } catch (NumberFormatException e) {
                                        Toast.makeText(rootView.getContext(), "Invalid Trial Number", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    // create an alert dialog
                    AlertDialog alertD = alertDialogBuilder.create();
                    alertD.show();
                }
            }
        });
        weight = (Switch) rootView.findViewById(R.id.weight);
        trialAutomation = (Switch)rootView.findViewById(R.id.trialAutomation);

        start = (Button)rootView.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!mp.isPlaying()) {
                        // Play 1Hz tone
                        mp.reset();
                        AssetFileDescriptor afd;
                        afd = getActivity().getAssets().openFd("1Hz.mp3");
                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();
                    }
                } catch (Exception e) { }
            }
        });
        stop = (Button)rootView.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    // Increment trial number when stop
                    mp.stop();
                    trialNumber++;
                    trial.setText("Trial " + trialNumber);
                }
            }
        });
        return rootView;
    }

    public static void resetTrialNumber () {
        trialNumber = 1;
        trial.setText("Trial " + trialNumber);
    }

    public static boolean isPlaying() {
        return mp.isPlaying();
    }
}
