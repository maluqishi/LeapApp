package uwaterloo.ca.leaptest;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leapmotion.leap.*;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

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
    private TextView frameData;
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

        frameData.setText("Leap motion not found. \nMake sure your leap motion is connected to your phone " +
                "and you have the leap motion data tracking app installed on your phone");

        // Add TextView to UI
        lmain.addView(frameData);

        return rootView;
    }

    public void connectEvent(Controller controller) {
        Log.i("LeapMotionTutorial", "Leap Motion Controller connected.");
    }

    public void disconnectEvent(Controller controller) {
        frameData.setText("Leap motion disconnected.");
    }

    public void frameEvent(Controller controller) {
        // Reset the text
        frameInfo = "";

        // Get all the data necessary to produce info
        currentFrame = controller.frame();
        hands = currentFrame.hands();
        fingers = currentFrame.fingers();

        frameInfo = "Frame Data: "
                + String.format("\nCurrent Frames Per Seconds: %.0f",
                currentFrame.currentFramesPerSecond())
                + "\nTimestamp: " + currentFrame.timestamp() + " μs"
                + "\nNumber of hands: " + hands.count()
                + "\nNumber of fingers: " + fingers.count() + "\n\nHand data: ";

        if (hands.count() == 0) {
            frameInfo += "\n\n No hands are detected \n\nFinger data:\n\n No fingers are detected";
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
                    + String.format("(%.1f,%.1f,%.1f)", hands.get(i)
                    .direction().get(0), hands.get(i).direction()
                    .get(1), hands.get(i).direction().get(2))
                    + "\nPalm position: "
                    + String.format("(%.1f,%.1f,%.1f)", hands.get(i)
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
                        + String.format("\nTip position: (%.1f,%.1f,%.1f)",
                        temp.get(j).tipPosition().get(0), temp.get(j)
                                .tipPosition().get(1), temp.get(j)
                                .tipPosition().get(2));
            }
        }
    }

}
