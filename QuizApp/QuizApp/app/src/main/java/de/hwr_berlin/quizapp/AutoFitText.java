package de.hwr_berlin.quizapp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

@SuppressWarnings("unused")
public class AutoFitText extends TextView {

    private final int MIN_TEXT_SIZE = 10;
    private final int MAX_TEXT_SIZE = 160;

    private TextView mTestView;

    private final float mScaledDensityFactor;

    private final float mThreshold = 0.5f;

    public AutoFitText(Context context) {
        this(context, null);
    }

    public AutoFitText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mScaledDensityFactor = context.getResources().getDisplayMetrics().scaledDensity;
        mTestView = new TextView(context);
        mTestView.setTypeface(this.getTypeface());

        this.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                onSizeChanged(AutoFitText.this.getWidth(), AutoFitText.this.getHeight(), 0, 0);
                AutoFitText.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void refitText(String text, int targetFieldWidth, int targetFieldHeight) {

        float lowerTextSize = MIN_TEXT_SIZE;
        float upperTextSize = MAX_TEXT_SIZE;

        this.setMaxWidth(targetFieldWidth);

        targetFieldWidth = targetFieldWidth - this.getPaddingLeft() - this.getPaddingRight();
        targetFieldHeight = targetFieldHeight - this.getPaddingTop() - this.getPaddingBottom();

        mTestView.setLayoutParams(new LayoutParams(targetFieldWidth, targetFieldHeight));
        mTestView.setMaxWidth(targetFieldWidth);

        for (float testSize; (upperTextSize - lowerTextSize) > mThreshold;) {

            testSize = (upperTextSize + lowerTextSize) / 2;

            mTestView.setTextSize(TypedValue.COMPLEX_UNIT_SP, testSize / mScaledDensityFactor);
            mTestView.setText(text);

            mTestView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int tempHeight = mTestView.getMeasuredHeight();
            int tempWidth = mTestView.getMeasuredWidth();

            if (tempHeight >= targetFieldHeight) {
                upperTextSize = testSize; // Font is too big, decrease upperSize
            }
            else {
                lowerTextSize = testSize; // Font is too small, increase lowerSize
            }
        }

        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, lowerTextSize / mScaledDensityFactor);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        if (width != oldWidth && height != oldHeight) {
            refitText(this.getText().toString(), width, height);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {

        int targetFieldWidth = this.getWidth();
        int targetFieldHeight = this.getHeight();

        if (targetFieldWidth <= 0 || targetFieldHeight <= 0 || text.equals("")) {
            Log.d(this.getClass().getName(), "Some values are empty, AutoFitText was not able to construct properly");
        }
        else {
            refitText(text.toString(), targetFieldWidth, targetFieldHeight);
        }
        super.setText(text, type);
    }
}