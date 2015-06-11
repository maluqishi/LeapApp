package uwaterloo.ca.leaptest;

import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.leapmotion.leap.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;



/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public static boolean isPause = false;
    public static boolean isConnect = false;
    private static boolean isStream = false;
    private static ArrayList<String> streamData = new ArrayList<String>();
    private boolean isAgree = false;
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

    // Declare variable
    public static TextView frameData;
    private Frame currentFrame;
    private FingerList fingers;
    private String frameInfo = "";
    private HandList hands;
    private LeapEventProducer leapEventProducer;
    private float[] originalX = new float[9];
    private boolean firstTimeVisit = true;

    private static final AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
    private static final AlphaAnimation backFadeIn = new AlphaAnimation(0f, 1f);
    private static final AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
    private static ImageView logo = null;
    private static TextView intro1 = null;
    private static TextView intro2 = null;
    private static EditText patientID = null;
    private static EditText age = null;
    private static RadioButton male = null;
    private static RadioButton female = null;
    private static CheckBox agree = null;
    private static Button continueButton = null;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity.state = 1;

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Configure animation setting
        animateConfig();

        // Find views and start animations
        logo = (ImageView)rootView.findViewById(R.id.logo);
        logo.startAnimation(fadeIn);

        intro1 = (TextView) rootView.findViewById(R.id.intro1);
        intro1.startAnimation(fadeIn);

        intro2 = (TextView) rootView.findViewById(R.id.intro2);
        intro2.startAnimation(fadeIn);

        patientID = (EditText) rootView.findViewById(R.id.patientID);
        patientID.startAnimation(fadeIn);

        age = (EditText) rootView.findViewById(R.id.age);
        age.startAnimation(fadeIn);

        male = (RadioButton) rootView.findViewById(R.id.male);
        male.startAnimation(fadeIn);
        male.setOnClickListener(new View.OnClickListener() {
            // Prevent double gender
            @Override
            public void onClick(View v) {
                male.setChecked(true);
                if (male.isChecked() && female.isChecked()) {
                    female.setChecked(false);
                }
            }
        });

        female = (RadioButton) rootView.findViewById(R.id.female);
        female.startAnimation(fadeIn);
        female.setOnClickListener(new View.OnClickListener() {
            // Prevent double gender
            @Override
            public void onClick(View v) {
                female.setChecked(true);
                if (male.isChecked() && female.isChecked()) {
                    male.setChecked(false);
                }
            }
        });

        agree = (CheckBox) rootView.findViewById(R.id.agree);
        agree.startAnimation(fadeIn);

        continueButton = (Button)rootView.findViewById(R.id.continueButton);
        continueButton.setAnimation(fadeIn);

        // Continue button and set onTouchListener (change color)
        continueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isAgree) {
                    Toast.makeText(rootView.getContext(), "You have to agree to participate in this research", Toast.LENGTH_SHORT).show();
                } else if (patientID.getText().toString().length() == 0 || age.getText().toString().length() == 0 || (!male.isChecked() && !female.isChecked())) {
                    Toast.makeText(rootView.getContext(), "Invalid Information", Toast.LENGTH_SHORT).show();
                } else {
                    switch (event.getAction()) {
                        case (MotionEvent.ACTION_DOWN):
                            continueButton.setBackgroundColor(Color.parseColor("#559C00"));
                            return true;
                        case (MotionEvent.ACTION_UP):
                            // Get original x position for later use
                            if (firstTimeVisit) {
                                originalX[0] = logo.getX();
                                originalX[1] = intro1.getX();
                                originalX[2] = intro2.getX();
                                originalX[3] = patientID.getX();
                                originalX[4] = age.getX();
                                originalX[5] = male.getX();
                                originalX[6] = female.getX();
                                originalX[7] = agree.getX();
                                originalX[8] = continueButton.getX();
                            }
                            // Fade out if every information is filled in
                            logo.startAnimation(fadeOut);
                            intro1.startAnimation(fadeOut);
                            intro2.startAnimation(fadeOut);
                            patientID.startAnimation(fadeOut);
                            age.startAnimation(fadeOut);
                            male.startAnimation(fadeOut);
                            female.startAnimation(fadeOut);
                            agree.startAnimation(fadeOut);
                            continueButton.startAnimation(fadeOut);
                            continueButton.setBackgroundColor(Color.parseColor("#6BC300"));
                            // If first time visit, then create an instance of info fragment
                            // else, just fade in the fragment
                            if (firstTimeVisit) {
                                Handler handler = new Handler();
                                handler.postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                ft.replace(R.id.fragment, new InfoFragment());
                                                ft.commit();
                                            }
                                        }, 1500L);
                            } else {
                                InfoFragment.fadeInPlz();
                                MainActivity.state = 2;
                            }
                            firstTimeVisit = false;
                            return true;
                    }
                }
                return false;
            }
        });


        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agree.isChecked()) {
                    isAgree = true;
                    // If agree is checked, then change the continue button to green
                    continueButton.setBackgroundColor(Color.parseColor("#6BC300"));
                } else {
                    isAgree = false;
                    continueButton.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }
        });

        return rootView;
    }

    public void connectEvent(Controller controller) {
        isConnect = controller.isConnected();
    }

    public void disconnectEvent(Controller controller) {
        frameData.setText("Leap motion disconnected.");
        isConnect = controller.isConnected();
    }

    public void frameEvent(Controller controller) {
        // Reset the text
        frameInfo = "";

        // Get all the data necessary to produce info
        if (!isPause) {
            currentFrame = controller.frame();
            hands = currentFrame.hands();
            fingers = currentFrame.fingers();
        }

        // Stream Data
        if (isStream && hands.count() == 1) {
            if (hands.get(0).isLeft()) {
                streamData.add("left");
            } else {
                streamData.add("right");
            }
            streamData.add(hands.get(0).palmPosition().toString());
            streamData.add(currentFrame.timestamp() + " μs");
        } else if (isStream && hands.count() == 2) {
            if (hands.get(0).isLeft()) {
                streamData.add("leftright");
            } else {
                streamData.add("rightleft");
            }
            streamData.add(hands.get(0).palmPosition().toString());
            streamData.add(hands.get(1).palmPosition().toString());
            streamData.add(currentFrame.timestamp() + " μs");
        }

        frameInfo = "Frame Data: "
                + String.format("\nCurrent Frames Per Seconds: %.0f",
                currentFrame.currentFramesPerSecond())
                + "\nTimestamp: " + currentFrame.timestamp() + " μs"
                + "\nNumber of hands: " + hands.count()
                + "\nNumber of fingers: " + fingers.count() + "\n\nHand data: ";

        if (hands.count() == 0) {
            frameInfo += "\n\nNo hands are detected \n\nFinger data:\n\nNo fingers are detected";
        } else {
            generateHandInfo(hands.count());
            frameInfo += "\n\nFinger data: ";
            generateFingerInfo(hands.count());
        }

        frameData.setText(frameInfo);
    }

    /**
     * Generate info for hands and add to an info string
     *
     * @param numberOfHands
     */
    private void generateHandInfo(int numberOfHands) {
        for (int i = 0; i < numberOfHands; i++) {
            frameInfo += "\n\nHand ID: " + hands.get(i).id();
            if (hands.get(i).isLeft()) {
                frameInfo += "\nType: left hand";
            } else {
                frameInfo += "\nType: right hand";
            }

            frameInfo += "\nDirection: "
                    + String.format("%.1f,%.1f,%.1f", hands.get(i)
                    .direction().get(0), hands.get(i).direction()
                    .get(1), hands.get(i).direction().get(2))
                    + "\nPalm position: "
                    + String.format("%.1f,%.1f,%.1f", hands.get(i)
                    .palmPosition().get(0), hands.get(i).palmPosition()
                    .get(1), hands.get(i).palmPosition().get(2))
                    + " mm";
        }
    }
    /**
     * Generate info for fingers and add to an info array
     *
     * @param numberOfHands
     */
    private void generateFingerInfo(int numberOfHands) {
        for (int i = 0; i < numberOfHands; i++) {
            FingerList temp = hands.get(i).fingers();
            for (int j = 0; j < temp.count(); j++) {
                frameInfo += "\n\nFinger ID: " + temp.get(j).id()
                        + "\nType: ";
                switch (j) {
                    case 0:
                        frameInfo += "Thumb";
                        break;
                    case 1:
                        frameInfo += "Index finger";
                        break;
                    case 2:
                        frameInfo += "Middle finger";
                        break;
                    case 3:
                        frameInfo += "Ring finger";
                        break;
                    case 4:
                        frameInfo += "Pinky finger";
                        break;
                }
                frameInfo += "\nBelongs to hand with ID: "
                        + hands.get(i).id()
                        + String.format("\nLength: %.1f mm", temp.get(j)
                        .length())
                        + String.format("\nTip position: %.1f,%.1f,%.1f",
                        temp.get(j).tipPosition().get(0), temp.get(j)
                                .tipPosition().get(1), temp.get(j)
                                .tipPosition().get(2));
            }
        }
    }

    public static void saveFile(){
        try {
            String timeStamp = new SimpleDateFormat(
                    "yyyy-MM-dd_HH-mm-ss").format(Calendar
                    .getInstance().getTime());

            File file = new File("/sdcard/Data_" + timeStamp + ".txt");
            file.createNewFile();

            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(MainActivityFragment.frameData.getText());
            myOutWriter.close();
            fOut.close();

        } catch (Exception e) {

        }
    }

    public static void startStreaming() {
        isStream = true;
    }

    public static void stopStreaming() {
        isStream = false;
        try {
            // Check if there is data in the stream data ArrayList
            if (streamData.size() == 0) {
                return;
            }
            // Get current system time
            String timeStamp = new SimpleDateFormat(
                    "yyyy-MM-dd_HH-mm-ss").format(Calendar
                    .getInstance().getTime());
            File file = new File("/sdcard/StreamData_" + timeStamp + ".txt");
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
        } catch (Exception e) {}
    }

    private void animateConfig() {
        fadeIn.setDuration(1500);
        fadeIn.setStartOffset(1500);
        fadeOut.setDuration(1500);
        fadeOut.setStartOffset(0);
        fadeOut.setFillAfter(true);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                logo.setX(1500);
                intro1.setX(1500);
                intro2.setX(1500);
                patientID.setX(1500);
                age.setX(1500);
                male.setX(1500);
                female.setX(1500);
                agree.setX(1500);
                continueButton.setX(1500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        backFadeIn.setDuration(1500);
        backFadeIn.setStartOffset(1500);
        backFadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                logo.setX(originalX[0]);
                intro1.setX(originalX[1]);
                intro2.setX(originalX[2]);
                patientID.setX(originalX[3]);
                age.setX(originalX[4]);
                male.setX(originalX[5]);
                female.setX(originalX[6]);
                agree.setX(originalX[7]);
                continueButton.setX(originalX[8]);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public static void backAnimation() {
        logo.startAnimation(backFadeIn);
        intro1.startAnimation(backFadeIn);
        intro2.startAnimation(backFadeIn);
        patientID.startAnimation(backFadeIn);
        age.startAnimation(backFadeIn);
        male.startAnimation(backFadeIn);
        female.startAnimation(backFadeIn);
        agree.startAnimation(backFadeIn);
        continueButton.startAnimation(backFadeIn);
    }

    public static String getPatientID() {
        return patientID.getText().toString();
    }
}
