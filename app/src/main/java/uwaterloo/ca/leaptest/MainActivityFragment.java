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
    private String handInfo = "";
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
        this.frameData = new TextView(rootView.getContext());
        frameData.setText("Leap motion not found. \nMake sure your leap motion is connected to your phone " +
                "and you have the leap motion data tracking app installed on your phone");
        lmain.addView(frameData);

        return rootView;
    }

    public void connectEvent(Controller controller) {
        Log.i("LeapMotionTutorial", "Leap Motion Controller connected.");
    }

    public void disconnectEvent(Controller controller) {
        Log.i("LeapMotionTutorial", "Leap Motion Controller disconnect.");
    }

    public void frameEvent(Controller controller) {
        currentFrame = controller.frame();
        hands = currentFrame.hands();
        fingers = currentFrame.fingers();

        handInfo = "Frame Data: "
                + String.format("\nCurrent Frames Per Seconds: %.0f",
                currentFrame.currentFramesPerSecond())
                + "\nTimestamp: " + currentFrame.timestamp() + " μs"
                + "\nNumber of hands: " + hands.count()
                + "\nNumber of fingers: " + fingers.count() + "\n\nHand data: ";

        if (hands.count() == 0) {
            handInfo += "\n\n No hands are detected";
        } else {
            generateHandInfo(hands.count());
        }

        frameData.setText(handInfo);
    }

    /**
     * Generate info for hands and add to an info string
     *
     * @param numberOfHands
     */
    private void generateHandInfo(int numberOfHands) {
        for (int i = 0; i < numberOfHands; i++) {
            handInfo += "\n\nHand ID: " + hands.get(i).id();
            if (hands.get(i).isLeft()) {
                handInfo += "\nType: left hand";
            } else {
                handInfo += "\nType: right hand";
            }
            handInfo += "\nDirection: "
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

}
