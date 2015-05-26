package uwaterloo.ca.leaptest;

import android.os.Message;
import com.leapmotion.leap.*;
import android.os.Handler;

public class LeapEventProducer extends Listener {

    private Controller controller;
    private Handler uiMessageHandler;
    private long pollingInterval = 1000/60; // 1000ms/60 = 60Hz = 60FPS
    private long lastFrameID = 0;
    private boolean leapConnected = false;
    public final static int CONNECT_MESSAGE = 5000;
    public final static int DISCONNECT_MESSAGE = 5001;
    public final static int FRAME_MESSAGE = 5002;

    public LeapEventProducer (Handler messageHandler) {
        controller = new Controller(this);
        this.uiMessageHandler = messageHandler;
        runPollLoop();
    }

    private void runPollLoop(){
        uiMessageHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (controller.isConnected()) {
                    //Checks if user has connected to leap motion
                    if (!leapConnected) {
                        leapConnected = true;
                        onConnect();
                    }
                    //If a new frame is ready
                    if (controller.frame().id() > lastFrameID) {
                        onFrame();
                        lastFrameID = controller.frame().id();
                    }
                } else {
                    //User has disconnected the leap motion
                    if (leapConnected) {
                        leapConnected = false;
                        onDisconnect();
                    }
                }
                runPollLoop();
            }
        }, pollingInterval);
    }

    public void onConnect() {
        controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
        controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
        controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
        controller.enableGesture(Gesture.Type.TYPE_SWIPE);

        //Send the Message
        Message message = uiMessageHandler.obtainMessage(CONNECT_MESSAGE, controller);
        uiMessageHandler.sendMessage(message);
        runPollLoop();
    }

    public void onDisconnect(){
        Message message = uiMessageHandler.obtainMessage(DISCONNECT_MESSAGE, controller);
        uiMessageHandler.sendMessage(message);
    }

    // Frame_events
    public void onFrame(){
        Message message = uiMessageHandler.obtainMessage(FRAME_MESSAGE, controller);
        uiMessageHandler.sendMessage(message);
    }
}
