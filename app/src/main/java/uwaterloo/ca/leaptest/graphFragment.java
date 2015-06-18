package uwaterloo.ca.leaptest;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class graphFragment extends Fragment {

    public static LineGraphView graph;
    public static boolean isInitialize = false;
    public graphFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        isInitialize = true;

        graph = new LineGraphView(rootView.getContext(),100, Arrays.asList(
                "x", "y", "z"));

        RelativeLayout rl = (RelativeLayout)rootView.findViewById(R.id.layout);
        rl.addView(graph);
        graph.setVisibility(View.VISIBLE);

        return rootView;
    }
}
