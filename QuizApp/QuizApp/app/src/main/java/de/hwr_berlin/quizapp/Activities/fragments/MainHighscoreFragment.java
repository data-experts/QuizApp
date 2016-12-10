package de.hwr_berlin.quizapp.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.models.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainHighscoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainHighscoreFragment extends Fragment {

    private static final String TAG = MainHighscoreFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private Button showHighscore;
    private ImageView img_highscore;

    public MainHighscoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainHighscoreFragment.
     */
    public static MainHighscoreFragment newInstance() {
        MainHighscoreFragment fragment = new MainHighscoreFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate of HighscoreFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView of HighscoreFragment");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_highscore, container, false);

        Log.d(TAG, "Initialize button");
        showHighscore = (Button) view.findViewById(R.id.btn_show_highscore);
        showHighscore.setOnClickListener(new ShowHighscoreOnClickListener());

        img_highscore = (ImageView) view.findViewById(R.id.img_highscore);
        img_highscore.setOnClickListener(new ShowHighscoreOnClickListener());

        return view;
    }

    private class ShowHighscoreOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onFragmentViewInteraction(view);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach of HighscoreFragment");
        if (activity instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) activity;
        } else {
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach of HighscoreFragment");
        mListener = null;
    }

}
