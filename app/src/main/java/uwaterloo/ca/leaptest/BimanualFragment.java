package uwaterloo.ca.leaptest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.leapmotion.leap.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class BimanualFragment extends Fragment {

    // Declare variables
    private static final AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
    private static final MediaPlayer mp = new MediaPlayer();
    private static TextView title = null;
    private static TextView connectionStatus = null;
    private static TextView handStatus = null;
    private static TextView trial = null;
    private static TextView patientID = null;
    private static Switch weight = null;
    private static Switch trialAutomation = null;
    private static TextView fileName = null;
    private static Button start = null;
    private static Button stop = null;
    private boolean isStream = false;
    private Frame currentFrame = null;
    private HandList hands = null;
    private float previous = 0;
    private boolean isFirstElement;
    private int counter = 0;
    private ArrayList<String> streamData = new ArrayList<String>();
    private static int trialNumber = 1;
    private LeapEventProducer leapEventProducer = null;
    private boolean isConnected = false;
    private Handler uiMessageHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message inputMessage) {
            switch (inputMessage.what) {
                case LeapEventProducer.CONNECT_MESSAGE:
                    connectEvent((Controller) inputMessage.obj);
                    break;
                case LeapEventProducer.DISCONNECT_MESSAGE:
                    disconnectEvent((Controller) inputMessage.obj);
                    break;
                case LeapEventProducer.FRAME_MESSAGE:
                    frameEvent((Controller) inputMessage.obj);
                    break;
                default:
                    super.handleMessage(inputMessage);
            }
        }
    };

    public BimanualFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_bimanual, container, false);

        // CHange state and configure animation
        MainActivity.state = 3;
        animateConfig();

        // Initialize  variables
        leapEventProducer = new LeapEventProducer(uiMessageHandler);
        title = (TextView)rootView.findViewById(R.id.title);
        title.startAnimation(fadeIn);

        connectionStatus = (TextView) rootView.findViewById(R.id.connectionStatus);
        connectionStatus.startAnimation(fadeIn);

        handStatus = (TextView)rootView.findViewById(R.id.handStatus);
        handStatus.startAnimation(fadeIn);

        trial = (TextView) rootView.findViewById(R.id.trial);
        trial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mp.isPlaying()) {
                    LayoutInflater layoutInflater = LayoutInflater.from(rootView.getContext());
                    View promptView = layoutInflater.inflate(R.layout.prompts, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rootView.getContext());

                    // set prompts.xml to be the layout file of the Alert Dialog builder
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
                                            if (weight.isChecked()) {
                                                fileName.setText("Data will be saved to SDCard/" + MainActivityFragment.getPatientID() + "_Trial_" + trialNumber + "_Weight.txt");
                                            } else {
                                                fileName.setText("Data will be saved to SDCard/" + MainActivityFragment.getPatientID() + "_Trial_" + trialNumber + ".txt");
                                            }
                                        } else
                                            Toast.makeText(rootView.getContext(), "Invalid Trial Number", Toast.LENGTH_SHORT).show();
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
        trial.startAnimation(fadeIn);

        patientID = (TextView)rootView.findViewById(R.id.patientID);
        patientID.setText("Patient ID: " + MainActivityFragment.getPatientID());
        patientID.startAnimation(fadeIn);

        weight = (Switch) rootView.findViewById(R.id.weight);
        weight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Prevent switching when playing sounds
                if (mp.isPlaying()) {
                    weight.setChecked(!isChecked);
                    Toast.makeText(rootView.getContext(), "You cannot change settings when doing trials", Toast.LENGTH_SHORT).show();
                } else {
                    if (isChecked) {
                        fileName.setText("Data will be saved to SDCard/" + MainActivityFragment.getPatientID() + "_Trial_" + trialNumber + "_Weight.txt");
                    } else {
                        fileName.setText("Data will be saved to SDCard/" + MainActivityFragment.getPatientID() + "_Trial_" + trialNumber + ".txt");
                    }
                }
            }
        });
        weight.startAnimation(fadeIn);

        trialAutomation = (Switch)rootView.findViewById(R.id.trialAutomation);
        trialAutomation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Prevent switching when playing sounds
                if (mp.isPlaying()) {
                    trialAutomation.setChecked(!isChecked);
                    Toast.makeText(rootView.getContext(), "You cannot change settings when doing trials", Toast.LENGTH_SHORT).show();
                }
            }
        });
        trialAutomation.startAnimation(fadeIn);

        fileName = (TextView)rootView.findViewById(R.id.fileName);
        fileName.setText("Data will be saved to SDCard/" + MainActivityFragment.getPatientID() + "_Trial_" + trialNumber + ".txt");
        fileName.startAnimation(fadeIn);

        start = (Button)rootView.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!mp.isPlaying() && isConnected) {
                        // Play 1Hz tone
                        mp.reset();
                        AssetFileDescriptor afd;
                        afd = getActivity().getAssets().openFd("1Hz.mp3");
                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();

                        // Start Streaming
                        startStreaming();
                    } else Toast.makeText(rootView.getContext(),"Please connect Leap Motion to continue ",Toast.LENGTH_SHORT).show();
                } catch (Exception e) { }
            }
        });
        start.startAnimation(fadeIn);

        stop = (Button)rootView.findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    // Stop streaming data
                    stopStreaming();
                    Toast.makeText(rootView.getContext(),"File saved to SDCard",Toast.LENGTH_SHORT).show();

                    // Increment trial number when stop
                    mp.stop();
                    trialNumber++;
                    trial.setText("Trial " + trialNumber);

                    // Change file name
                    if (weight.isChecked()) {
                        fileName.setText("Data will be saved to SDCard/" + MainActivityFragment.getPatientID() + "_Trial_" + trialNumber + "_Weight.txt");
                    } else {
                        fileName.setText("Data will be saved to SDCard/" + MainActivityFragment.getPatientID() + "_Trial_" + trialNumber + ".txt");
                    }
                }
            }
        });
        stop.startAnimation(fadeIn);

        return rootView;
    }

    public static void resetTrialNumber () {
        trialNumber = 1;
        trial.setText("Trial " + trialNumber);
        weight.setChecked(false);
        trialAutomation.setChecked(false);
        fileName.setText("Data will be saved to SDCard/" + MainActivityFragment.getPatientID() + "_Trial_" + trialNumber + ".txt");
    }

    public static boolean isPlaying() {
        return mp.isPlaying();
    }

    public void connectEvent(Controller controller) {
        isConnected = controller.isConnected();
        connectionStatus.setText("Connection Status: Leap Motion Connected");
        connectionStatus.setTextColor(Color.parseColor("#6BC300"));
    }

    public void disconnectEvent(Controller controller) {
        isConnected = controller.isConnected();
        connectionStatus.setText("Connection Status: Leap Motion Not Found");
        connectionStatus.setTextColor(Color.parseColor("#FF0000"));

        mp.stop();
        Toast.makeText(getActivity().getApplicationContext(),"Leap Motion Disconnected",Toast.LENGTH_SHORT).show();
    }

    public void frameEvent(Controller controller) {
        counter++;

        // Get Frame Data
        currentFrame = controller.frame();
        hands = currentFrame.hands();

        // Display hand status
        if (hands.count() == 0) {
            handStatus.setText("Hand Status: No Hands Detected");
            handStatus.setTextColor(Color.parseColor("#FF0000"));
        } else if (hands.count() == 1){
            handStatus.setTextColor(Color.parseColor("#6BC300"));
            if (hands.get(0).isLeft()) {
                handStatus.setText("Hand Status: Left Hand Detected");
            } else {
                handStatus.setText("Hand Status: Right Hand Detected");
            }
        } else {
            handStatus.setTextColor(Color.parseColor("#6BC300"));
            handStatus.setText("Hand Status: Both Hand Detected");
        }

        // Stream Data
        if (isStream && hands.count() == 1) {
            if (hands.get(0).isLeft()) {
                streamData.add("left");
            } else {
                streamData.add("right");
            }
            streamData.add(hands.get(0).palmPosition().toString());
            if (hands.get(0).palmPosition().getY() <= previous) {
                isFirstElement = true;
                streamData.add(currentFrame.timestamp() + " μs");
            } else {
                if (isFirstElement && counter > 40) {
                    streamData.add(currentFrame.timestamp() + " μs -");
                    isFirstElement = false;
                    counter = 0;
                } else {
                    streamData.add(currentFrame.timestamp() + " μs");
                }
            }
        } else if (isStream && hands.count() == 2) {
            if (hands.get(0).isLeft()) {
                streamData.add("leftright");
            } else {
                streamData.add("rightleft");
            }
            streamData.add(hands.get(0).palmPosition().toString());
            streamData.add(hands.get(1).palmPosition().toString());
            if (hands.get(0).palmPosition().getY() <= previous) {
                isFirstElement = true;
                streamData.add(currentFrame.timestamp() + " μs");
            } else {
                if (isFirstElement && counter > 40) {
                    streamData.add(currentFrame.timestamp() + " μs -");
                    isFirstElement = false;
                    counter = 0;
                } else {
                    streamData.add(currentFrame.timestamp() + " μs");
                }
            }
        }
        previous = hands.get(0).palmPosition().getY();
    }

    private void startStreaming() {
        isStream = true;
        streamData.clear();
    }

    private void stopStreaming() {
        isStream = false;
        try {
            // Check if there is data in the stream data ArrayList
            if (streamData.size() == 0) {
                return;
            }
            // Get file name
            File file = new File("/sdcard/" + fileName.getText().toString().substring(29,fileName.getText().toString().length()));
            file.createNewFile();

            // Get data from ArrayList and produce a string
            String temp = "";
            String previous = " ";
            for (int i = 0; i < streamData.size() - 4; ) {
                if (streamData.get(i) == "leftright") {
                    if (!previous.equals("leftright")) {
                        temp += "Left Hand Palm Position Streaming Data   Right Hand Palm Position Streaming Data\n";
                        previous = "leftright";
                    }
                    temp += String.format("%-40s%40s%40s\n",
                            streamData.get(i + 1), streamData.get(i + 2),
                            streamData.get(i + 3));
                    i += 4;
                } else if (streamData.get(i) == "rightleft") {
                    if (!previous.equals("rightleft")) {
                        temp += "Right Hand Palm Position Streaming Data   Left Hand Palm Position Streaming Data\n";
                        previous = "rightleft";
                    }
                    temp += String.format("%-40s%40s%40s\n",
                            streamData.get(i + 1), streamData.get(i + 2),
                            streamData.get(i + 3));
                    i += 4;
                } else if (streamData.get(i) == "right") {
                    if (!previous.equals("right")) {
                        temp += "Right Hand Palm Position Streaming Data\n";
                        previous = "right";
                    }
                    temp += String.format("%-40s%40s\n",
                            streamData.get(i + 1), streamData.get(i + 2));
                    i += 3;
                } else if (streamData.get(i) == "left") {
                    if (!previous.equals("left")) {
                        temp += "Left Hand Palm Position Streaming Data\n";
                        previous = "left";
                    }
                    temp += String.format("%-40s%40s\n",
                            streamData.get(i + 1), streamData.get(i + 2));
                    i += 3;
                }
            }

            // Save file to SD card
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(temp);
            myOutWriter.close();
            fOut.close();
        } catch (Exception e) { }
    }

    private void animateConfig() {
        fadeIn.setDuration(1500);
        fadeIn.setStartOffset(0);
    }
}