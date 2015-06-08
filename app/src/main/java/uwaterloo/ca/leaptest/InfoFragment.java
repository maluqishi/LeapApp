package uwaterloo.ca.leaptest;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private final TranslateAnimation translation1 = new TranslateAnimation(1500,new DisplayMetrics().widthPixels/2,0,0);
    private static final TranslateAnimation backTranslation = new TranslateAnimation(new DisplayMetrics().widthPixels/2,1500,0,0);
    private static TextView title = null;

    private static FragmentActivity activity;
    private static FragmentManager manager;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.state = 2;

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);
        activity = (FragmentActivity)rootView.getContext();
        manager = activity.getFragmentManager();

        animateConfig();

        title = (TextView)rootView.findViewById(R.id.title);
        title.setAnimation(translation1);

        return rootView;
    }

    private void animateConfig() {
        translation1.setDuration(1500);
        translation1.setStartOffset(0);
        backTranslation.setDuration(1500);
        backTranslation.setStartOffset(0);
        backTranslation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                title.setX(1500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public static void backAnimation() {
        title.startAnimation(backTranslation);
    }
}
