package de.hwr_berlin.quizapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.hwr_berlin.quizapp.GameElements;
import de.hwr_berlin.quizapp.activities.fragments.GameScoreFragment;
import de.hwr_berlin.quizapp.activities.fragments.QuestionFragment;
import de.hwr_berlin.quizapp.events.AnswerEvent;
import de.hwr_berlin.quizapp.events.HighscoreUploadedEvent;
import de.hwr_berlin.quizapp.events.QuestionPreloadedEvent;
import de.hwr_berlin.quizapp.models.Game;
import de.hwr_berlin.quizapp.models.OnFragmentInteractionListener;
import de.hwr_berlin.quizapp.network.HighscoreService;
import de.hwr_berlin.quizapp.network.models.Category;
import de.hwr_berlin.quizapp.network.models.Question;
import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.network.models.Score;

public class QuizActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String TAG = QuizActivity.class.getSimpleName();

    private static final String SHARE_TEXT = "Ich habe gerade %d Punkte im Quiz erzielt. Wie viel schaffst du?";
    public static final String EXTRA_GAME_INSTANCE = "extra_game_instance";

    private GameElements gameElements;

    private Fragment fragment;
    private ImageView img_background;
    private ProgressBar loading_indicator;

    public static Game quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        loading_indicator = (ProgressBar) findViewById(R.id.loading_indicator);
        loading_indicator.setVisibility(View.GONE);

        img_background = (ImageView) findViewById(R.id.background_highscore_image);
        // Reset the background image.
        //img_background.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.background_highscore, null));
        // Change the statusbar to default.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        quiz = new Game(this);

        gameElements = new GameElements(this);

        Game.addNewQuestion((Question) getIntent().getExtras().getParcelable(MainActivity.EXTRA_FIRST_QUESTION));

        if (quiz.isOver()) { //FIXME: remove the ! IMPORTANT
            // Clear the question store
            quiz = null;
            Game.reset();
            // Start the fragment
            fragment = GameScoreFragment.newInstance();
        } else {
            Question question = quiz.getQuestion(true);

            if (question == null) {
                // Whoops no preloaded questions available...
            }

            //color_background.setBackgroundColor(Color.parseColor(question.getCategory().getColor()));

            fragment = QuestionFragment.newInstance(question);
            ((QuestionFragment)fragment).fillCategoryList(this, MainActivity.CATEGORY_STORAGE.getCategoryList());
            ((QuestionFragment)fragment).setGameScoreText(quiz.getCurrentGameScore());
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.quiz_score_fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();

        // Start preloading of questions for all other categories
        for (Category category : MainActivity.CATEGORY_STORAGE.getUnansweredCategories()) {
            if (quiz.getQuestion(false) != null && category.getId() == quiz.getQuestion(false).getCategoryId()) {
                continue;
            }
            new de.hwr_berlin.quizapp.tasks.QuestionLoaderTask(this).execute(category);
        }

        quiz.startTimeCounter();
    }

    @Override
    public void onFragmentViewInteraction(View view) {
        Log.d(TAG, "New fragment interaction. " + view);
        switch (view.getId()) {
            case R.id.fab_next_question:
                if (fragment != null && MainActivity.CATEGORY_STORAGE.hasUnansweredCategories()) {
                    Question nextQuestion = quiz.getQuestion(true);

                    if (nextQuestion == null) {
                        // FIXME: App crashes as long as this is empty and the user answers real quick!
                        // TODO: load new questions!? And show loading indicator!!!
                        // TODO: if no internet connection just grab one of the already asked questions.
                        if (MainActivity.CATEGORY_STORAGE.hasUnansweredCategories() && ! MainActivity.eventBus.isRegistered(this)) {
                            MainActivity.eventBus.register(this);
                            loading_indicator.setVisibility(View.VISIBLE);
                            break;
                        }
                    }

                    if (fragment instanceof GameScoreFragment) {
                        fragment = QuestionFragment.newInstance(nextQuestion);

                        Bundle extras = new Bundle();
                        extras.putParcelable(EXTRA_GAME_INSTANCE, quiz);

                        fragment.setArguments(extras);

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.quiz_score_fragment_container, fragment)
                                .addToBackStack(null)
                                .commit();
                        break;
                    }

                    //color_background.setBackgroundColor(Color.parseColor(nextQuestion.getCategory().getColor()));

                    ((QuestionFragment) fragment).setQuestion(nextQuestion);
                    quiz.startTimeCounter();
                } else {
                    view.setEnabled(false);
                    Fragment oldFragment = fragment;
                    fragment = GameScoreFragment.newInstance();

                    Bundle extras = new Bundle();
                    extras.putParcelable(EXTRA_GAME_INSTANCE, quiz);

                    fragment.setArguments(extras);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && oldFragment != null) {
                        fragment.setSharedElementEnterTransition(new AutoTransition());
                        fragment.setEnterTransition(new Fade());
                        oldFragment.setExitTransition(new Fade());
                        fragment.setSharedElementReturnTransition(new AutoTransition());
                    }

                    //categories_indicator.setVisibility(View.INVISIBLE);
                    //color_background.setVisibility(View.INVISIBLE);

                    view.setEnabled(true);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    if(oldFragment instanceof QuestionFragment) {
                        transaction.addSharedElement(((QuestionFragment)oldFragment).txt_current_game_score, "fragment_game_score_gs");
                    }

                    transaction.replace(R.id.quiz_score_fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                break;
            case R.id.answer1:
            case R.id.answer2:
            case R.id.answer3:
            case R.id.answer4:
                handleAnswerButtonClicked((Button) view);
                break;
            case R.id.btn_upload_score:
                // Upload the Highscore
                new HighscoreUploader().execute(new Score(((GameScoreFragment)fragment).getUserName(), quiz.getFinalGameScore() /*name, score*/));
                break;
            case R.id.btn_share_score:
                // Share the Score with a social network
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, String.format(SHARE_TEXT, quiz.getFinalGameScore()))
                    .setType("text/plain");
                startActivity(sendIntent);
                break;
            case R.id.btn_back_main:
                // GOTO main menu.
                endGame();
                break;
            default:
                break;
        }
    }

    private void endGame() {
        quiz = null;
        Game.reset();
        MainActivity.eventBus.unregister(this);
        startActivity(new Intent(this, MainActivity.class));
    }

    private void handleAnswerButtonClicked(Button button) {
        long timeDelta = quiz.stopTimeCounter();

        boolean correctAnswer = quiz.checkAnswer(button.getText().toString());
        MainActivity.eventBus.post(new AnswerEvent(correctAnswer, timeDelta)); // TODO: is it called twice? or why does the log appear twice?

        if (correctAnswer) {
            Log.i(TAG, "Correct answer given");

            button.setBackgroundColor(getResources().getColor(R.color.colorButtonBackgroundTrue));
            gameElements.playSound(GameElements.Sound.CORRECT);
            gameElements.vibrate(GameElements.VibrateState.SHORT);

            ((QuestionFragment)fragment).disableCategoryFromIndicator(quiz.getQuestion(false).getCategory());
        } else {
            Log.i(TAG, "Wrong answer given.");

            button.setBackgroundColor(getResources().getColor(R.color.colorButtonBackgroundFalse));
            gameElements.playSound(GameElements.Sound.WRONG);
            gameElements.vibrate(GameElements.VibrateState.LONG);

            ((QuestionFragment)fragment).markRightAnswer(quiz.getQuestion(false).getAnswer());
        }

        ((QuestionFragment)fragment).setGameScoreText(quiz.getCurrentGameScore());
    }

    @Override
    public void onBackPressed() {
        // You cannot go baaaaaaack! Wahahaha
        quiz.pauseGame();
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.pause_dialog_title))
                .setMessage(getString(R.string.pause_dialog_content))
                .setPositiveButton(getString(R.string.pause_dialog_positiv), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        endGame();
                    }
                })
                .setNegativeButton(getString(R.string.pause_dialog_negativ), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        quiz.resumeGame();
                    }
                })
                .show();
    }

    private class HighscoreUploader extends AsyncTask<Score, Void, Void> {

        @Override
        protected Void doInBackground(Score... scores) {
            new HighscoreService(QuizActivity.this).uploadHighscore(scores[0].getName(), scores[0].getScore());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity.eventBus.post(new HighscoreUploadedEvent(true)); // TODO: statuscodes for success?
        }
    }

    /**
     * This method changes the background image and the statusbar tint of the activity.
     * Can be called from child fragments
     * @param drawable  The image resource id
     * @param color     The color resource id
     */
    public void setBackground(int drawable, int color) {
        // Set the background image
        img_background.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawable, null));
        // Change the statusbar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    @Subscribe
    public void onQuestionLoadedEvent(QuestionPreloadedEvent event) {
        MainActivity.eventBus.unregister(this);
        loading_indicator.setVisibility(View.GONE);
        ((QuestionFragment) fragment).setQuestion(quiz.getQuestion(true));
        quiz.startTimeCounter();
    }

}
