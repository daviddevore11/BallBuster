package edu.augustana.csc490.ballbuster;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainMenuFragment extends Fragment {

    private Button startButton;
    private Button howToButton;
    private Button backButton;
    private ImageView title;
    private LinearLayout linearLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // creates a reference to all of the objects within the view
        linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        title = (ImageView) view.findViewById(R.id.gameTitle);
        startButton = (Button) view.findViewById(R.id.startButton);
        howToButton = (Button) view.findViewById(R.id.infoButton);
        backButton = (Button) view.findViewById(R.id.backButton);
        // calls the method to add OnClickListeners to all of the button
        addListenerOnButton();

        return view;
    }

    // sets onClickListeners for each button
    public void addListenerOnButton(){
        // startButton will begin game
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().setContentView(R.layout.activity_main);
            }
        });
        // howToButton will cause other buttons to disappear, the back button will appear and the
        // background image of the layout will switch to a how to play image
        howToButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startButton.setVisibility(View.INVISIBLE);
                howToButton.setVisibility(View.INVISIBLE);
                title.setVisibility(View.INVISIBLE);
                backButton.setVisibility(View.VISIBLE);
                linearLayout.setBackgroundResource(R.mipmap.how_to);
            }
        });
        // backButton will cause all of the objects that where previously invisible to become
        // visible again and will change the background image back to the normal background
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startButton.setVisibility(View.VISIBLE);
                howToButton.setVisibility(View.VISIBLE);
                title.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.INVISIBLE);
                linearLayout.setBackgroundResource(R.mipmap.screen_shot);
            }
        });
    }
}
