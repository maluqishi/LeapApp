package uwaterloo.ca.leaptest;


import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private static final AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
    private static final AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);



    private static TextView title = null;
    private static Button continueButton = null;

    public InfoFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.state = 2;

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        animateConfig();

        title = (TextView)rootView.findViewById(R.id.title);
        title.setAnimation(fadeIn);


        continueButton = (Button)rootView.findViewById(R.id.continueButton);
        continueButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
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
        continueButton.startAnimation(fadeOut);
    }

    public static void fadeInPlz() {
        fadeIn.setStartOffset(1500);
        title.startAnimation(fadeIn);
        continueButton.startAnimation(fadeIn);
    }
}
