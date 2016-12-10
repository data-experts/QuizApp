package de.hwr_berlin.quizapp.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.hwr_berlin.quizapp.models.EndlessRecyclerOnScrollListener;
import de.hwr_berlin.quizapp.models.HighscoreListAdapter;
import de.hwr_berlin.quizapp.network.models.Score;
import de.hwr_berlin.quizapp.network.HighscoreService;
import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.network.NetworkUtil;

public class HighscoreActivity extends AppCompatActivity {

    private static final String TAG = HighscoreActivity.class.getSimpleName();

    private RecyclerView highscoreList;
    private RecyclerView.Adapter highscoreListAdapter;
    private RecyclerView.LayoutManager layoutManager;

    private TextView txtFirstName;
    private TextView txtFirstScore;
    private TextView txtSecondName;
    private TextView txtSecondScore;
    private TextView txtThirdName;
    private TextView txtThirdScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate of HighscoreActivity");

        setContentView(R.layout.activity_highscore);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtFirstName = (TextView) findViewById(R.id.highscore_first_name);
        txtFirstScore = (TextView) findViewById(R.id.highscore_first_score);
        txtSecondName = (TextView) findViewById(R.id.highscore_second_name);
        txtSecondScore = (TextView) findViewById(R.id.highscore_second_score);
        txtThirdName = (TextView) findViewById(R.id.highscore_third_name);
        txtThirdScore = (TextView) findViewById(R.id.highscore_third_score);

        // Initialize RecyclerView
        highscoreList = (RecyclerView) findViewById(R.id.highscore_list);

        // Set layout manager
        layoutManager = new LinearLayoutManager(this);
        highscoreList.setLayoutManager(layoutManager);

        // Add on scroll listener for infinity scroll
        highscoreList.addOnScrollListener(new EndlessRecyclerOnScrollListener((LinearLayoutManager)layoutManager) {

            @Override
            public void onLoadMore(int current_page) {
                new HighscoreLoaderTask().execute(current_page);
            }
        });

        // Set adapter
        highscoreListAdapter = new HighscoreListAdapter(HighscoreActivity.this, null);
        highscoreList.setAdapter(highscoreListAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new HighscoreLoaderTask().execute(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class HighscoreLoaderTask extends AsyncTask<Integer, Void, List<Score>> {

        private int page = 0;

        @Override
        protected List<Score> doInBackground(Integer... params) {
            if (params != null && params[0] != null) {
                page = params[0];
            }

            HighscoreService service = new HighscoreService(HighscoreActivity.this);
            return service.getHighscore(page);
        }

        @Override
        protected void onPostExecute(List<Score> result) {
            if (result == null) {
                // No result was returned from server (Network available?)
                if (NetworkUtil.hasInternetConnection(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), R.string.error_no_connection, Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_unknown, Toast.LENGTH_SHORT);
                }
                return;
            }

            // For the first page remove the first three elements
            // They will be shown on below the trophies.
            if (page == 0) {

                txtFirstName.setText(result.get(0).getName());
                txtFirstScore.setText(((Integer)result.get(0).getScore()).toString());
                txtSecondName.setText(result.get(1).getName());
                txtSecondScore.setText(((Integer)result.get(1).getScore()).toString());
                txtThirdName.setText(result.get(2).getName());
                txtThirdScore.setText(((Integer)result.get(2).getScore()).toString());

                List<Score> scoreList = new ArrayList<>();

                for (int i = 3; i < result.size(); i++) {
                    scoreList.add(result.get(i));
                }

                // Update data
                ((HighscoreListAdapter)highscoreListAdapter).addAll(scoreList);

            } else {
                // Update data
                ((HighscoreListAdapter)highscoreListAdapter).addAll(result);
            }

            highscoreListAdapter.notifyDataSetChanged();

        }
    }

}
