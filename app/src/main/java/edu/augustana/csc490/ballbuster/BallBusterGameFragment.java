package edu.augustana.csc490.ballbuster;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Owner on 3/22/2015.
 */
public class BallBusterGameFragment extends Fragment {
    private BallBusterView ballBusterView;

    // called when Fragment's view needs to be created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        // get the BallBusterView
        ballBusterView = ((BallBusterView) view.findViewById(R.id.ballBusterView));
        return view;
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        ballBusterView.releaseResources();
    }


}
