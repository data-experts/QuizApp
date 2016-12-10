package de.hwr_berlin.quizapp.activities.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.hwr_berlin.quizapp.AutoFitText;
import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.models.OnFragmentInteractionListener;
import de.hwr_berlin.quizapp.network.models.Category;
import de.hwr_berlin.quizapp.network.models.Question;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment {

    private static final String TAG = QuestionFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private Question question;

    // Passive UI Elements: All lables.
    private TextView txt_questCategory;
    private AutoFitText txt_questQuestion;
    public TextView txt_current_game_score;
    private RelativeLayout question_card;

    // List of categories to indicate answered questions
    private LinearLayout quiz_categories_indicator;
    private int fragmentWidth;
    private List<AutoFitText> category_indicator = new ArrayList<>();

    // Active UI Elements: Floating Action Button and Answerbuttons.
    private List<Button> answerButtonList;
    private FloatingActionButton fabNextQuestion;

    private boolean questionAnswered = false;

    // OnClickListener for all buttons.
    private AnswerOnClickListener answerOnClickListener = new AnswerOnClickListener();

    public QuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment QuestionFragment.
     */
    public static QuestionFragment newInstance(Question question) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.question = question;
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
        View view = inflater.inflate(R.layout.fragment_question, container, false);

        fragmentWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        Log.d(TAG, "Width of fragment quiz: " + fragmentWidth);

        txt_questCategory = (TextView) view.findViewById(R.id.quest_category);
        txt_questQuestion = (AutoFitText) view.findViewById(R.id.quest_question);
        txt_current_game_score = (TextView) view.findViewById(R.id.txt_current_game_score);

        fabNextQuestion = (FloatingActionButton) getActivity().findViewById(R.id.fab_next_question);
        fabNextQuestion.setOnClickListener(answerOnClickListener);

        answerButtonList = new ArrayList<>();

        answerButtonList.add((Button) view.findViewById(R.id.answer1));
        answerButtonList.add((Button) view.findViewById(R.id.answer2));
        answerButtonList.add((Button) view.findViewById(R.id.answer3));
        answerButtonList.add((Button) view.findViewById(R.id.answer4));

        question_card = (RelativeLayout) view.findViewById(R.id.question_card);

        quiz_categories_indicator = (LinearLayout) view.findViewById(R.id.quiz_categories_indicator);
        for (AutoFitText catView : this.category_indicator) {
            quiz_categories_indicator.addView(catView);
        }

        setQuestion(question);
        setGameScoreText(0);

        return view;
    }

    public void setQuestion(Question question) {
        if (question == null) {
            return;
        }
        this.question = question;

        setColorAccordingToCategory(question.getCategory());

        this.txt_questCategory.setText(question.getCategory().getName());
        this.txt_questQuestion.setText(question.getQuestion());

        List<String> solutions = Arrays.asList(question.getSolutions());

        // Shuffle solution array. So the answers are not at the same position every time.
        Collections.shuffle(solutions);

        for (int i = 0; i < answerButtonList.size(); i++) {
            Button btn = answerButtonList.get(i);

            btn.setText(solutions.get(i));
            btn.setOnClickListener(answerOnClickListener);
        }
    }

    private void setColorAccordingToCategory(Category category) {
        int color = Color.parseColor(category.getColor());
        int darkColor = saturateColor(color, 0.8f);
        int textColor = saturateColor(color, 0.55f);
        int lightColor = saturateColor(color, 1.3f);

        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] { lightColor, color });
        g.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        g.setGradientRadius(800.0f);
        g.setGradientCenter(0.3f, 0.3f);

        this.question_card.setBackground(g);

        // make the other category colors darker
        for (AutoFitText view : category_indicator) {
            if (view.getTag().equals(category.getName())) {
                view.getBackground().setAlpha(255);
            } else {
                view.getBackground().setAlpha(55);
            }
        }

        // set status color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(darkColor);
        }

        txt_questQuestion.setTextColor(textColor);

        // set floating action button color
        this.fabNextQuestion.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{0}}, new int[]{color}));
    }

    private static int saturateColor (int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor;
        return Color.HSVToColor(hsv);
    }

    public void markRightAnswer(String answer) {
        // mark the right answer
        for (Button btn : answerButtonList) {
            if (btn.getText().equals(answer)) {
                btn.setBackgroundColor(getResources().getColor(R.color.colorButtonBackgroundTrue));
                break;
            }
        }
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

    /**
     * Creates new Views to indicate the categories
     * @param categories The categories to indicate.
     */
    public void fillCategoryList(Context context, List<Category> categories) {
        AutoFitText view;

        if (fragmentWidth == 0) {
            fragmentWidth = context.getResources().getDisplayMetrics().widthPixels;
        }

        int viewWidth = fragmentWidth / categories.size();

        for (Category element : categories) {
            view = new AutoFitText(context);
            view.setLayoutParams(new LinearLayout.LayoutParams(viewWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            view.setBackgroundColor(Color.parseColor(element.getColor()));
            view.setGravity(Gravity.CENTER);
            view.setTextColor(Color.YELLOW);
            view.setPadding(0, 0, 0, 2);
            view.setTag(element.getName());
            category_indicator.add(view);
        }
    }

    public void disableCategoryFromIndicator(Category category) {
        TextView view = (TextView)quiz_categories_indicator.findViewWithTag(category.getName());
        view.setBackgroundColor(Color.BLACK);
        view.setText("★");
        view.getBackground().setAlpha(55);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private class AnswerOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (questionAnswered && view.getId() != R.id.fab_next_question) {
                // question was answered and another answerbutton was clicked.
                fabNextQuestion.show(); // maybe the fab isn't visible.
                return;
            }
            if (view.getId() == R.id.fab_next_question) {
                for (Button btn : answerButtonList) {
                    btn.setBackgroundResource(R.drawable.abc_btn_default_mtrl_shape);
                }
                fabNextQuestion.setEnabled(false);
                fabNextQuestion.hide();
                questionAnswered = false;
            } else {
                fabNextQuestion.show();
                fabNextQuestion.setEnabled(true);
                questionAnswered = true;
            }

            if (mListener != null) {
                mListener.onFragmentViewInteraction(view);
            }
        }

    }

    public void setGameScoreText(int score) {
        setGameScoreText(((Integer)score).toString());
    }

    public void setGameScoreText(String score) {
        if (txt_current_game_score != null) {
            txt_current_game_score.setText("SCORE " + score);
        }
    }

}
