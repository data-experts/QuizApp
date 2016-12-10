package de.hwr_berlin.quizapp.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import org.greenrobot.eventbus.Subscribe;

import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.events.QuestionPreloadedEvent;
import de.hwr_berlin.quizapp.models.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainQuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainQuizFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final String TAG = MainQuizFragment.class.getSimpleName();

    private Button startQuizButton;
    private ImageView img_start;

    public MainQuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainQuizFragment.
     */
    public static MainQuizFragment newInstance() {
        MainQuizFragment fragment = new MainQuizFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Register Eventbus");
        MainActivity.eventBus.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_quiz, container, false);

        Log.d(TAG, "Initialize button");
        startQuizButton = (Button) view.findViewById(R.id.btn_start_quiz);
        startQuizButton.setOnClickListener(new StartQuizOnClickListener());
        // We have to wait till the categories are loaded.
        startQuizButton.setEnabled(MainActivity.firstQuestion != null); // prior: MainActivity.CATEGORY_STORAGE.isDefined()

        img_start = (ImageView) view.findViewById(R.id.img_start);
        img_start.setOnClickListener(new StartQuizOnClickListener());
        img_start.setClickable(MainActivity.firstQuestion != null);

        return view;
    }

    private class StartQuizOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onFragmentViewInteraction(view);
            }
        }
    }

    @Subscribe
    public void onEvent(QuestionPreloadedEvent event) {
        if (startQuizButton == null) {
            View view = getView();
            if (view != null) {
                startQuizButton = (Button) view.findViewById(R.id.btn_start_quiz);
            }
        }
        startQuizButton.setEnabled(event.question != null); // only enable the button, if really a question was loaded.
        img_start.setClickable(event.question != null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
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
        mListener = null;
    }

}
