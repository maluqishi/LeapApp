package uwaterloo.ca.leaptest;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private static final AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
    private static final AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
    private static TextView title = null;
    private static TextView connectionStatus = null;
    private static TextView instruction = null;
    private static Button leftHand = null;
    private static Button rightHand = null;
    private static Button continueButton = null;
    private LeapEventProducer leapEventProducer;
    private boolean isConnected = false;
    private Frame currentFrame = null;
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

    public InfoFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        leapEventProducer = new LeapEventProducer(uiMessageHandler);

        MainActivity.state = 2;

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        animateConfig();

        title = (TextView)rootView.findViewById(R.id.title);
        title.startAnimation(fadeIn);

        connectionStatus = (TextView)rootView.findViewById(R.id.connectionStatus);
        connectionStatus.startAnimation(fadeIn);

        instruction = (TextView)rootView.findViewById(R.id.instruction);
        instruction.startAnimation(fadeIn);

        leftHand = (Button)rootView.findViewById(R.id.leftHand);
        leftHand.startAnimation(fadeIn);

        rightHand = (Button)rootView.findViewById(R.id.rightHand);
        rightHand.startAnimation(fadeIn);

        continueButton = (Button)rootView.findViewById(R.id.continueButton);
        continueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isConnected) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            continueButton.setBackgroundColor(Color.parseColor("#559C00"));
                            return true;
                        case MotionEvent.ACTION_UP:
                            continueButton.setBackgroundColor(Color.parseColor("#6BC300"));
                            return true;
                    }
                }
                return false;
            }
        });
        continueButton.setAnimation(fadeIn);

        return rootView;
    }

    private void animateConfig() {
        fadeIn.setDuration(1500);
        fadeIn.setStartOffset(0);
        fadeOut.setDuration(1500);
        fadeOut.setStartOffset(0);
        fadeOut.setFillAfter(true);
    }

    public static void backAnimation() {
        title.startAnimation(fadeOut);
        connectionStatus.startAnimation(fadeOut);
        instruction.startAnimation(fadeOut);
        leftHand.startAnimation(fadeOut);
        rightHand.startAnimation(fadeOut);
        continueButton.startAnimation(fadeOut);
    }

    public static void fadeInPlz() {
        fadeIn.setStartOffset(1500);
        title.startAnimation(fadeIn);
        connectionStatus.startAnimation(fadeIn);
        instruction.startAnimation(fadeIn);
        leftHand.startAnimation(fadeIn);
        rightHand.startAnimation(fadeIn);
        continueButton.startAnimation(fadeIn);
    }

    public void connectEvent(Controller controller) {
        isConnected = controller.isConnected();
        connectionStatus.setText("Connection Status: Leap Motion Connected");
        connectionStatus.setTextColor(Color.parseColor("#6BC300"));
        continueButton.setBackgroundColor(Color.parseColor("#6BC300"));
    }

    public void disconnectEvent(Controller controller) {
        isConnected = controller.isConnected();
        connectionStatus.setText("Connection Status: Leap Motion Not Found");
        connectionStatus.setTextColor(Color.parseColor("#FF0000"));
        continueButton.setBackgroundColor(Color.parseColor("#FF0000"));
        leftHand.setBackgroundColor(Color.parseColor("#C0C0C0"));
        rightHand.setBackgroundColor(Color.parseColor("#C0C0C0"));
        instruction.setText("1) Make sure Leap Daemon Skeletal Data Tracking App is running. A green Leap Motion icon will appear on notification bar.\n2) Try reconnecting Leap Motion using other cables\n3) If nothing happens, please restart the phone while Leap Motion is connected to the phone");
    }

    public void frameEvent(Controller controller) {
        currentFrame = controller.frame();

        // Turn left or right hand button to green
        if (currentFrame.hands().count() == 0) {
            leftHand.setBackgroundColor(Color.parseColor("#FF0000"));
            rightHand.setBackgroundColor(Color.parseColor("#FF0000"));
        } else if (currentFrame.hands().count() == 1) {
            if (currentFrame.hands().get(0).isLeft()) {
                leftHand.setBackgroundColor(Color.parseColor("#6BC300"));
                rightHand.setBackgroundColor(Color.parseColor("#FF0000"));
            } else {
                rightHand.setBackgroundColor(Color.parseColor("#6BC300"));
                leftHand.setBackgroundColor(Color.parseColor("#FF0000"));
            }
        } else {
            leftHand.setBackgroundColor(Color.parseColor("#6BC300"));
            rightHand.setBackgroundColor(Color.parseColor("#6BC300"));
        }

        // Display frames per second
        instruction.setText(String.format("Current Frames Per Seconds: %.0f", currentFrame.currentFramesPerSecond()));
    }
}
