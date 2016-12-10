package de.hwr_berlin.quizapp.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.Subscribe;

import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.activities.MainActivity;
import de.hwr_berlin.quizapp.activities.QuizActivity;
import de.hwr_berlin.quizapp.events.HighscoreUploadedEvent;
import de.hwr_berlin.quizapp.models.Game;
import de.hwr_berlin.quizapp.models.OnFragmentInteractionListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameScoreFragment extends Fragment {

    private static final String TAG = GameScoreFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private SelectionOnClickListener clickListener = new SelectionOnClickListener();

    private TextView score;
    private Button btn_uploadScore;
    private Button btn_shareScore;
    private Button btn_backToMainMenu;
    private EditText input_userName;

    public GameScoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GameScoreFragment.
     */
    public static GameScoreFragment newInstance() {
        GameScoreFragment fragment = new GameScoreFragment();
        MainActivity.eventBus.register(fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_score, container, false);

        Bundle extras = getArguments();
        if (extras == null) {
            return null;
        }
        Game quiz = extras.getParcelable(QuizActivity.EXTRA_GAME_INSTANCE);

        Log.d(TAG, "Initialize view components");
        score = (TextView) view.findViewById(R.id.fragment_game_score_txt_score);
        String scoreText = ((Integer) quiz.getFinalGameScore()).toString()
                + " " + getActivity().getResources().getString(R.string.text_points);
        score.setText(scoreText);

        btn_uploadScore = (Button) view.findViewById(R.id.btn_upload_score);
        btn_shareScore = (Button) view.findViewById(R.id.btn_share_score);
        btn_backToMainMenu = (Button) view.findViewById(R.id.btn_back_main);

        btn_uploadScore.setOnClickListener(clickListener);
        btn_shareScore.setOnClickListener(clickListener);
        btn_backToMainMenu.setOnClickListener(clickListener);

        input_userName = (EditText) view.findViewById(R.id.score_input_user_name);
        input_userName.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
                for (int i = 0; i < s.toString().length(); i++) {
                    if (!Character.isLetter(s.charAt(i)) && s.charAt(i) != 0x20) {
                        s.replace(i, i + 1,"");
                    }
                }
            }
        });

        Activity activity = getActivity();

        if (activity instanceof QuizActivity) {
            ((QuizActivity) getActivity()).setBackground(R.drawable.background_highscore, R.color.colorPrimaryDarkHighscoreFragment);
        }

        return view;
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

    private class SelectionOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_upload_score) {
                if (getUserName().equals("")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.error_no_name), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    btn_uploadScore.setEnabled(false);
                }
            }
            mListener.onFragmentViewInteraction(view);
        }
    }

    public String getUserName() {
        return input_userName.getText().toString();
    }

    @Subscribe
    public void onHighscoreUploadedEvent(HighscoreUploadedEvent event) {
        if (! event.success) {
            btn_uploadScore.setEnabled(true);
            Toast.makeText(getActivity(), getResources().getString(R.string.upload_highscore_failure), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.upload_highscore_success), Toast.LENGTH_SHORT).show();
        }
    }

}
