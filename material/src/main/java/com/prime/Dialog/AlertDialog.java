package com.prime.Dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.prime.R;

public class AlertDialog extends BaseDialog<AlertDialog> {

    private static final String TAG = "AlertDialog";
    //
    private TextView mMessageView;
    private CharSequence message;
    private CheckBox mCheckBox;

    private boolean mChecked;
    @Nullable
    private CharSequence mCheckMessage;

    @ColorInt
    private int mMessageTextColor = -1;


    @Override
    protected AlertDialog getThis() {
        return this;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_alert_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMessageView = view.findViewById(R.id.alert_dialog_message);
        if (mMessageTextColor != -1)
            mMessageView.setTextColor(mMessageTextColor);
        mMessageView.setText(message);
        mCheckBox = view.findViewById(R.id.dialog_check_for_all);
        if (mCheckMessage != null) {
            mCheckBox.setVisibility(View.VISIBLE);
            mCheckBox.setText(mCheckMessage);
            if (mChecked)
                mCheckBox.setChecked(true);
            mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> mChecked = isChecked);
            if (mPosBtnListener != null && mPosBtnListener instanceof onAlertClickListener)
                ((onAlertClickListener) mPosBtnListener).onAlertClick(getThis(), mChecked);
        }

    }

    public AlertDialog setMessageTextColor(@ColorRes int color) {
        mMessageTextColor = ContextCompat.getColor(getContext(), color);
        return this;
    }

    public AlertDialog setDefaultChecked(boolean checked) {
        mChecked = checked;
        return this;
    }


    public AlertDialog setCheckBoxLabel(@StringRes int labelResId) {
        mCheckMessage = getString(labelResId);
        return this;
    }

    public AlertDialog setCheckBoxLabel(@NonNull String label) {
        mCheckMessage = label;
        return this;
    }


    /**
     * Set the message to display using the given resource id.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public AlertDialog setMessage(@StringRes int messageId) {
        message = getContext().getString(messageId);
        return getThis();
    }

    /**
     * Set the message to display.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public AlertDialog setMessage(@Nullable CharSequence message) {
        this.message = message;
        return getThis();
    }
}
