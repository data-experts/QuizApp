package de.hwr_berlin.quizapp.network.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.hwr_berlin.quizapp.activities.MainActivity;

/**
 * Created by oruckdeschel on 29.02.2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question implements Parcelable {

    @JsonIgnore
    private Category category;

    @JsonProperty("ID")
    private int id;

    @JsonProperty("CID")
    private int categoryId;

    @JsonProperty("QUESTION")
    private String question;

    @JsonIgnore
    private String[] solutions;

    @JsonProperty("SUGGESTION1")
    private String choice1;

    @JsonProperty("SUGGESTION2")
    private String choice2;

    @JsonProperty("SUGGESTION3")
    private String choice3;

    @JsonProperty("ANSWER")
    private String answer;

    public Question() {
        // no code just for jackson
    }

    public Question(Category category, String question, String choice1, String choice2, String choice3, String answer) {
        this.category = category;
        this.question = question;
        this.choice1 = choice1;
        this.choice2 = choice2;
        this.choice3 = choice3;
        this.answer = answer;
    }

    public Question(Category category, int id, String question, String[] solutions, String answer) {
        this.category = category;
        this.id = id;
        this.question = question;
        this.solutions = solutions;
        this.answer = answer;
    }

    public Question(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);

        this.categoryId = Integer.parseInt(data[0]);
        this.category = MainActivity.CATEGORY_STORAGE.getCategory(this.categoryId);
        this.question = data[1];
        this.choice1 = data[2];
        this.choice2 = data[3];
        this.choice3 = data[4];
        this.answer = data[5];
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String[] getSolutions() {
        return new String[]{answer, choice1, choice2, choice3};
    }

    public void setSolutions(String[] solutions) {
        this.solutions = solutions;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Category getCategory() {
        if (MainActivity.CATEGORY_STORAGE.isDefined() && this.category == null) {
            this.category = MainActivity.CATEGORY_STORAGE.getCategory(this.categoryId);
        }
        return this.category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[] {
            ((Integer)this.categoryId).toString(),
            this.question,
            this.choice1,
            this.choice2,
            this.choice3,
            this.answer
        });
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Question)) {
            return false;
        }
        if (object == this) {
            return true;
        }
        Question question = (Question) object;
        return this.question.equals(question.getQuestion()) && this.category.equals(question.getCategory());
    }
}
