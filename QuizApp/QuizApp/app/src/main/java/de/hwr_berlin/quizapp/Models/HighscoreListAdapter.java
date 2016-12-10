package de.hwr_berlin.quizapp.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hwr_berlin.quizapp.R;
import de.hwr_berlin.quizapp.network.models.Score;

/**
 * Tutorial Source: http://www.vogella.com/tutorials/AndroidRecyclerView/article.html
 * Created by oruckdeschel on 13.03.2016.
 */
public class HighscoreListAdapter extends RecyclerView.Adapter<HighscoreListAdapter.ViewHolder> {

    private static final String TAG = HighscoreListAdapter.class.getSimpleName();

    private List<Score> scoreDataset;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtCurrentPosition;
        public TextView txtName;
        public TextView txtScore;

        public ViewHolder(View v) {
            super(v);

            View currentPosition = v.findViewById(R.id.current_position);

            txtCurrentPosition = (TextView) currentPosition;
            txtName = (TextView) v.findViewById(R.id.username);
            txtScore = (TextView) v.findViewById(R.id.txt_score);
        }
    }

    public void add(int position, Score item) {
        Log.d(TAG, "Add new element on position: " + position + " (itemname: " + item.getName() + ")");
        scoreDataset.add(position, item);
        notifyItemChanged(position);
    }

    public void addAll(Score[] scores) {
        addAll(Arrays.asList(scores));
    }

    public void addAll (List<Score> scores) {
        //clear();
        this.scoreDataset.addAll(scores);
        /*for (int i = 0; i < scores.size(); i++) {
            add(i, scores.get(i));
        }*/
    }

    public void remove(String item) {
        int position = scoreDataset.indexOf(item);
        scoreDataset.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        if (scoreDataset != null) {
            scoreDataset.clear();
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public HighscoreListAdapter(Context context, ArrayList<Score> scoreList) {
        this.context = context;
        if (scoreList == null) {
            this.scoreDataset = new ArrayList<>();
        } else {
            this.scoreDataset = scoreList;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position < 3 ? position : 3;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public HighscoreListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highscore_list_item_normal, parent, false);

        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from dataset at this position
        // - replace the contents of the view with that element
        final Score score = scoreDataset.get(position);

        holder.txtName.setText(score.getName());
        holder.txtScore.setText(((Integer)score.getScore()).toString());
        holder.txtCurrentPosition.setText(((Integer)(position + 4)).toString()); // We add 4, because the position is 0-based and we cut of the first 3 positions. -> Thus 3 + 1 = 4
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (scoreDataset != null) {
            return scoreDataset.size();
        }
        return 0;
    }

}
