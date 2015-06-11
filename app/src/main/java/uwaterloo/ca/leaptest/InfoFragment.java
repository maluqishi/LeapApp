package uwaterloo.ca.leaptest;


import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    // Declare variables
    private static final AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
    private static final AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
    private static TextView title = null;
    private static TextView connectionStatus = null;
    private static TextView instruction = null;
    private TextView rightHandData = null;
    private TextView leftHandData = null;
    private static Button leftHand = null;
    private static Button rightHand = null;
    private static Button continueButton = null;
    private LeapEventProducer leapEventProducer = null;
    private boolean isConnected = false;
    private Frame currentFrame = null;
    private HandList hands = null;
    private String rightHandInfo = "";
    private String leftHandInfo = "";
    private boolean displayRightHand = false;
    private boolean displayLeftHand = false;
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
        final View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        animateConfig();

        title = (TextView)rootView.findViewById(R.id.title);
        title.startAnimation(fadeIn);

        connectionStatus = (TextView)rootView.findViewById(R.id.connectionStatus);
        connectionStatus.startAnimation(fadeIn);

        instruction = (TextView)rootView.findViewById(R.id.instruction);
        instruction.startAnimation(fadeIn);

        leftHand = (Button)rootView.findViewById(R.id.leftHand);
        leftHand.startAnimation(fadeIn);
        leftHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    displayLeftHand = !displayLeftHand;
                }
            }
        });

        rightHand = (Button)rootView.findViewById(R.id.rightHand);
        rightHand.startAnimation(fadeIn);
        rightHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected) {
                    displayRightHand = !displayRightHand;
                }
            }
        });
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
                            new AlertDialog.Builder(rootView.getContext())
                                    .setTitle("Confirm")
                                    .setMessage("You cannot make changes to your information beyond this point. Do you want to proceed?")
                                    .setNegativeButton(android.R.string.no, null)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface arg0, int arg1) {
                                            title.startAnimation(fadeOut);
                                            connectionStatus.startAnimation(fadeOut);
                                            instruction.startAnimation(fadeOut);
                                            leftHand.startAnimation(fadeOut);
                                            rightHand.startAnimation(fadeOut);
                                            continueButton.startAnimation(fadeOut);
                                            Handler handler = new Handler();
                                            handler.postDelayed(
                                                    new Runnable() {
                                                        public void run() {
                                                            final FragmentTransaction ft = getFragmentManager().beginTransaction();
                                                            ft.replace(R.id.fragment, new BimanualFragment());
                                                            ft.commit();
                                                        }
                                                    }, 1500L);
                                        }
                                    }).create().show();

                            return true;
                    }
                } else {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            return true;
                        case MotionEvent.ACTION_UP:
                            Toast.makeText(rootView.getContext(), "Leap Motion Not Found", Toast.LENGTH_SHORT).show();
                            return true;
                    }
                }
                return false;
            }
        });
        continueButton.setAnimation(fadeIn);

        rightHandData = (TextView) rootView.findViewById(R.id.rightHandData);
        leftHandData = (TextView) rootView.findViewById(R.id.leftHandData);
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
        rightHandData.setText("");
        leftHandData.setText("");

    }

    public void frameEvent(Controller controller) {
        currentFrame = controller.frame();
        hands = currentFrame.hands();

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

        // Get hand info
        generateHandInfo(hands.count());

        // Display hand info
        if (displayRightHand) {
            rightHandData.setText(rightHandInfo);
        } else {
            rightHandData.setText("");
        }
        if (displayLeftHand) {
            leftHandData.setText(leftHandInfo);
        } else {
            leftHandData.setText("");
        }
    }

    private void generateHandInfo(int numberOfHands) {
        rightHandInfo = "";
        leftHandInfo = "";
        for (int i = 0; i < numberOfHands; i++) {
            if (hands.get(i).isLeft()) {
                leftHandInfo += "\n\nHand ID: " + hands.get(i).id();
                leftHandInfo += "\nType: left hand";
                leftHandInfo += "\nDirection: "
                        + String.format("%.1f,%.1f,%.1f", hands.get(i)
                        .direction().get(0), hands.get(i).direction()
                        .get(1), hands.get(i).direction().get(2))
                        + "\nPalm position: "
                        + String.format("%.0f,%.0f,%.0f", hands.get(i)
                        .palmPosition().get(0), hands.get(i).palmPosition()
                        .get(1), hands.get(i).palmPosition().get(2))
                        + " mm";
            } else {
                rightHandInfo += "\n\nHand ID: " + hands.get(i).id();
                rightHandInfo += "\nType: right hand";
                rightHandInfo += "\nDirection: "
                        + String.format("%.1f,%.1f,%.1f", hands.get(i)
                        .direction().get(0), hands.get(i).direction()
                        .get(1), hands.get(i).direction().get(2))
                        + "\nPalm position: "
                        + String.format("%.0f,%.0f,%.0f", hands.get(i)
                        .palmPosition().get(0), hands.get(i).palmPosition()
                        .get(1), hands.get(i).palmPosition().get(2))
                        + " mm";
            }
        }
    }
}
