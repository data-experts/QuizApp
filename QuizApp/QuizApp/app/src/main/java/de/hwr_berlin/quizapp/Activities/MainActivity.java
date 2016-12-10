package de.hwr_berlin.quizapp.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import de.hwr_berlin.quizapp.GameElements;
import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.activities.fragments.MainHighscoreFragment;
import de.hwr_berlin.quizapp.activities.fragments.MainLegalFragment;
import de.hwr_berlin.quizapp.activities.fragments.MainQuizFragment;
import de.hwr_berlin.quizapp.events.CategoriesLoadedEvent;
import de.hwr_berlin.quizapp.events.QuestionPreloadedEvent;
import de.hwr_berlin.quizapp.events.broadcasts.ConnectivityBroadcastReceiver;
import de.hwr_berlin.quizapp.models.OnFragmentInteractionListener;
import de.hwr_berlin.quizapp.network.CategoryStorage;
import de.hwr_berlin.quizapp.network.models.Category;
import de.hwr_berlin.quizapp.network.models.Question;
import de.hwr_berlin.quizapp.tasks.CategoryLoaderTask;
import de.hwr_berlin.quizapp.tasks.QuestionLoaderTask;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String EXTRA_FIRST_QUESTION = "extra_first_question";

    /**
     * The number of fragments in the pagerview.
     */
    private static final int NUM_PAGES = 3;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter pagerAdapter;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next pages steps.
     */
    private ViewPager fragmentPager;

    private CirclePageIndicator pageIndicator;

    private GameElements gameElements;

    public static final EventBus eventBus = new EventBus();

    public static Question firstQuestion;

    /**
     * Storage for the preloaded categories.
     */
    public static final CategoryStorage CATEGORY_STORAGE = new CategoryStorage();
    private ConnectivityBroadcastReceiver connectivityBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate of MainActivity");

        // Make me fullscreen!!!
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            // Hide the status bar.
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_main);

        if (!eventBus.isRegistered(this)) {
            MainActivity.eventBus.register(this);
        }

        // Preload all categories.
        Log.i(TAG, "Prefetch all categories");
        if (CATEGORY_STORAGE.getCategoryCount() != 0) {
            // first clear the cache!
            CATEGORY_STORAGE.clear();
        }
        new CategoryLoaderTask(this).execute();

        gameElements = new GameElements(this);

        Log.d(TAG, "Prepare ViewPager");
        fragmentPager = (ViewPager)findViewById(R.id.main_fragment_view_pager);
        pagerAdapter = new DashboardFragmentPageAdapter(getSupportFragmentManager());
        fragmentPager.setAdapter(pagerAdapter);
        fragmentPager.setOffscreenPageLimit(1);

        pageIndicator = (CirclePageIndicator) findViewById(R.id.main_page_indicator);
        pageIndicator.setViewPager(fragmentPager);

        Fragment mainQuizFragment = new MainQuizFragment();

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(new MainQuizFragment(), MainQuizFragment.class.getCanonicalName());
        transaction.replace(R.id.frame_layout, mainQuizFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        // Do nothing so we don't go back to the Quiz.
    }

    @Subscribe
    public void onEvent(CategoriesLoadedEvent event) {
        // As soon as the categories are loaded -> load the first question.
        Log.d(TAG, "Categories were preloaded. Start loading of the first question.");
        Category firstCategory = CATEGORY_STORAGE.getNextUnansweredCategory();
        if (firstCategory != null) {
            new QuestionLoaderTask(this).execute(CATEGORY_STORAGE.getNextUnansweredCategory());
        } else {
            // Oh shit maybe we don't have network access - better register the brc..
            final IntentFilter networkIntentFilter = new IntentFilter();
            networkIntentFilter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
            connectivityBroadcastReceiver = new ConnectivityBroadcastReceiver();
            registerReceiver(connectivityBroadcastReceiver, networkIntentFilter);
        }
    }

    @Subscribe
    public void onEvent(QuestionPreloadedEvent event) {
        firstQuestion = event.question;
    }

    @Override
    public void onFragmentViewInteraction(View view) {
        gameElements.playSound(GameElements.Sound.BUTTON_CLICK);

        switch (view.getId()) {
            case R.id.btn_start_quiz:
            case R.id.img_start:
                // Start new quiz
                Log.d(TAG, "Start button clicked");
                if (this.firstQuestion == null) {
                    Toast.makeText(this, "Keine Frage geladen.", Toast.LENGTH_LONG).show();
                    // TODO: Load a question?!
                } else {
                    Log.d(TAG, "First question: " + firstQuestion.getId());
                    // First unregister brc to not manipulate the quiz
                    if (connectivityBroadcastReceiver != null) {
                        unregisterReceiver(connectivityBroadcastReceiver);
                    }
                    eventBus.unregister(this);
                    // Second start the quiz
                    Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                    intent.putExtra(EXTRA_FIRST_QUESTION, firstQuestion);
                    startActivity(intent);
                }
                break;
            case R.id.btn_show_highscore:
            case R.id.img_highscore:
                // Show highscore
                Log.d(TAG, "Highscore button clicked");
                startActivity(new Intent(MainActivity.this, HighscoreActivity.class));
                break;
        }
    }

    /**
     * A simple pager adapter that represents the 2 Screenfragment objects, in
     * sequence.
     */
    private class DashboardFragmentPageAdapter extends FragmentStatePagerAdapter {
        public DashboardFragmentPageAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public float getPageWidth(int position) {
            // TODO: For tablets we may want to show multiple Fragments side by side. (Multiplication)
            return(1f);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MainQuizFragment();
                case 1:
                    return new MainHighscoreFragment();
                case 2:
                    return new MainLegalFragment();
                default:
                    return new MainQuizFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}
