package uwaterloo.ca.leaptest;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        LinearLayout lmain = (LinearLayout)rootView.findViewById(R.id.layout);

        this.leapEventProducer = new LeapEventProducer(uiMessageHandler);
        frameData = new TextView(rootView.getContext());

        frameData.setText("Leap motion not found" +
                "\n1) Make sure your leap motion is connected to your phone " +
                "\n2) Make sure you have the leap motion data tracking app installed on your phone" +
                "\n3) Make sure you have a green leap motion displayed on the status bar" +
                "\n4) Reset the phone if everything does not work (Make sure leap motion is connected while resetting");

        // Add TextView to UI
        lmain.addView(frameData);

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
}