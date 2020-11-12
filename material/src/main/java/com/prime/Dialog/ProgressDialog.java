package com.prime.Dialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.prime.R;

import java.text.NumberFormat;

public class ProgressDialog extends BaseDialog<ProgressDialog> {
    /**
     * Creates a ProgressDialog with a circular, spinning progress
     * bar. This is the default.
     */
    public static final int STYLE_SPINNER = 0;

    /**
     * Creates a ProgressDialog with a horizontal progress bar.
     */
    public static final int STYLE_HORIZONTAL = 1;


    private ProgressBar mProgress;

    private TextView mMessageView;

    private int mProgressStyle = STYLE_SPINNER;

    private TextView mProgressNumber;
    private String mProgressNumberFormat = "%1d/%2d";
    private TextView mProgressPercent;
    private NumberFormat mProgressPercentFormat = NumberFormat.getPercentInstance();

    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private CharSequence mMessage;
    private boolean mIndeterminate;

    private boolean mHasStarted;
    private Handler mViewUpdateHandler;


    @Override
    protected ProgressDialog getThis() {
        return this;
    }

    @Override
    protected int getContentLayout() {
        return mProgressStyle == STYLE_SPINNER ?
                R.layout.dialog_progress_spinner :
                R.layout.layout_progress_horizontal;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressPercentFormat.setMaximumFractionDigits(0);
        if (mProgressStyle == STYLE_HORIZONTAL) {

            /* Use a separate handler to update the text views as they
             * must be updated on the same thread that created them.
             */
            mViewUpdateHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    /* Update the number and percent */
                    int progress = mProgress.getProgress();
                    int max = mProgress.getMax();
                    if (mProgressNumberFormat != null) {
                        String format = mProgressNumberFormat;
                        mProgressNumber.setText(String.format(format, progress, max));
                    } else {
                        mProgressNumber.setText("");
                    }
                    if (mProgressPercentFormat != null) {
                        double percent = (double) progress / (double) max;
                        SpannableString
                                tmp =
                                new SpannableString(mProgressPercentFormat.format(percent));
                        tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                                0, tmp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        mProgressPercent.setText(tmp);
                    } else {
                        mProgressPercent.setText("");
                    }
                }
            };
            mProgress = (ProgressBar) view.findViewById(R.id.progress);
            mProgressNumber = (TextView) view.findViewById(R.id.progress_number);
            mProgressPercent = (TextView) view.findViewById(R.id.progress_percent);
            mMessageView = view.findViewById(R.id.message);
        } else {
            mProgress = (ProgressBar) view.findViewById(R.id.progress);
            mMessageView = (TextView) view.findViewById(R.id.message);
        }
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
        if (mSecondaryProgressVal > 0) {
            setSecondaryProgress(mSecondaryProgressVal);
        }
        if (mIncrementBy > 0) {
            incrementProgressBy(mIncrementBy);
        }
        if (mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy(mIncrementSecondaryBy);
        }
        if (mProgressDrawable != null) {
            setProgressDrawable(mProgressDrawable);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable(mIndeterminateDrawable);
        }
        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);
        onProgressChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        mHasStarted = false;
    }

    /**
     * Gets the current progress.
     *
     * @return the current progress, a value between 0 and {@link #getMax()}
     */
    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

    /**
     * Sets the current progress.
     *
     * @param value the current progress, a value between 0 and {@link #getMax()}
     * @see ProgressBar#setProgress(int)
     */
    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    /**
     * Gets the current secondary progress.
     *
     * @return the current secondary progress, a value between 0 and {@link #getMax()}
     */
    public int getSecondaryProgress() {
        if (mProgress != null) {
            return mProgress.getSecondaryProgress();
        }
        return mSecondaryProgressVal;
    }

    /**
     * Sets the secondary progress.
     *
     * @param secondaryProgress the current secondary progress, a value between 0 and
     *                          {@link #getMax()}
     * @see ProgressBar#setSecondaryProgress(int)
     */
    public void setSecondaryProgress(int secondaryProgress) {
        if (mProgress != null) {
            mProgress.setSecondaryProgress(secondaryProgress);
            onProgressChanged();
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    /**
     * Gets the maximum allowed progress value. The default value is 100.
     *
     * @return the maximum value
     */
    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    /**
     * Sets the maximum allowed progress value.
     */
    public ProgressDialog setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
        return this;
    }

    /**
     * Increments the current progress value.
     *
     * @param diff the amount by which the current progress will be incremented,
     *             up to {@link #getMax()}
     */
    public ProgressDialog incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
        return this;
    }

    /**
     * Increments the current secondary progress value.
     *
     * @param diff the amount by which the current secondary progress will be incremented,
     *             up to {@link #getMax()}
     */
    public ProgressDialog incrementSecondaryProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementSecondaryBy += diff;
        }
        return this;
    }

    /**
     * Sets the drawable to be used to display the progress value.
     *
     * @param d the drawable to be used
     * @see ProgressBar#setProgressDrawable(Drawable)
     */
    public ProgressDialog setProgressDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setProgressDrawable(d);
        } else {
            mProgressDrawable = d;
        }
        return this;
    }

    /**
     * Sets the drawable to be used to display the indeterminate progress value.
     *
     * @param d the drawable to be used
     * @see ProgressBar#setProgressDrawable(Drawable)
     * @see #setIndeterminate(boolean)
     */
    public ProgressDialog setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
        return this;
    }

    /**
     * Whether this ProgressDialog is in indeterminate mode.
     *
     * @return true if the dialog is in indeterminate mode, false otherwise
     */
    public boolean isIndeterminate() {
        if (mProgress != null) {
            return mProgress.isIndeterminate();
        }
        return mIndeterminate;
    }

    /**
     * Change the indeterminate mode for this ProgressDialog. In indeterminate
     * mode, the progress is ignored and the dialog shows an infinite
     * animation instead.
     *
     * <p><strong>Note:</strong> A ProgressDialog with style {@link #STYLE_SPINNER}
     * is always indeterminate and will ignore this setting.</p>
     *
     * @param indeterminate true to enable indeterminate mode, false otherwise
     * @see #setProgressStyle(int)
     */
    public ProgressDialog setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
        return this;
    }

    public ProgressDialog setMessage(CharSequence message) {
        if (mProgress != null) {
            mMessageView.setText(message);
        } else {
            mMessage = message;
        }
        return this;
    }

    /**
     * Sets the style of this ProgressDialog, either {@link #STYLE_SPINNER} or
     * {@link #STYLE_HORIZONTAL}. The default is {@link #STYLE_SPINNER}.
     *
     * <p><strong>Note:</strong> A ProgressDialog with style {@link #STYLE_SPINNER}
     * is always indeterminate and will ignore the {@link #setIndeterminate(boolean)
     * indeterminate} setting.</p>
     *
     * @param style the style of this ProgressDialog, either {@link #STYLE_SPINNER} or
     *              {@link #STYLE_HORIZONTAL}
     */
    public ProgressDialog setProgressStyle(int style) {
        mProgressStyle = style;
        return this;
    }

    /**
     * Change the format of the small text showing current and maximum units
     * of progress.  The default is "%1d/%2d".
     * Should not be called during the number is progressing.
     *
     * @param format A string passed to {@link String#format String.format()};
     *               use "%1d" for the current number and "%2d" for the maximum.  If null,
     *               nothing will be shown.
     */
    public ProgressDialog setProgressNumberFormat(String format) {
        mProgressNumberFormat = format;
        onProgressChanged();
        return getThis();
    }

    /**
     * Change the format of the small text showing the percentage of progress.
     * The default is
     * {@link NumberFormat#getPercentInstance() NumberFormat.getPercentageInstnace().}
     * Should not be called during the number is progressing.
     *
     * @param format An instance of a {@link NumberFormat} to generate the
     *               percentage text.  If null, nothing will be shown.
     */
    public ProgressDialog setProgressPercentFormat(NumberFormat format) {
        mProgressPercentFormat = format;
        onProgressChanged();
        return this;
    }

    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            if (mViewUpdateHandler != null && !mViewUpdateHandler.hasMessages(0)) {
                mViewUpdateHandler.sendEmptyMessage(0);
            }
        }
    }

}
