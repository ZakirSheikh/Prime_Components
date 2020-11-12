package com.prime.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.android.material.textfield.TextInputLayout;
import com.prime.R;

public class InputDialog extends BaseDialog<InputDialog> {
    private CharSequence mText;
    private EditText mInputView;
    private TextInputLayout mTextInputLayout;
    @Nullable
    private CharSequence mHintText;

    private int mInputType = -1;

    @Override
    protected InputDialog getThis() {
        return this;
    }

    @Override
    protected int getContentLayout() {
        return R.layout.dialog_input_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputView = view.findViewById(R.id.dialog_input_text_edit_text);
        mTextInputLayout = view.findViewById(R.id.dialog_input_text_input_layout);
        if (mHintText != null)
            mTextInputLayout.setHint(mHintText);
        mPositiveButton.setOnClickListener(v -> {
            if (mPosBtnListener != null && mPosBtnListener instanceof OnInputClickListener)
                ((OnInputClickListener) mPosBtnListener).onClick(getThis(),
                        mInputView.getText());
            InputDialog.this.dismiss();
        });

        if (mInputType != -1)
            mInputView.setInputType(mInputType);

        if (mText != null) {
            mInputView.setText(mText);
            mInputView.setSelection(0,
                    mText.toString().lastIndexOf(".") != -1 ? mText.toString()
                            .lastIndexOf(".") : mText.length());
            mInputView.scrollTo(mInputView.getRight(), 0);
        }
        mInputView.requestFocus();
        InputMethodManager
                imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onDismiss(@NonNull android.content.DialogInterface dialog) {
        InputMethodManager
                imm =
                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0);
        super.onDismiss(dialog);
    }

    public InputDialog setText(CharSequence text) {
        this.mText = text;
        return getThis();
    }

    public InputDialog setText(@StringRes int textResID) {
        mText = getContext().getString(textResID);
        return getThis();
    }

    public InputDialog setHintText(@Nullable CharSequence hintText) {
        this.mHintText = hintText;
        return getThis();
    }

    public InputDialog setInputType(int type) {
        mInputType = type;
        return getThis();
    }

    public InputDialog setHintText(@StringRes int hintTextRes) {
        this.mHintText = getContext().getString(hintTextRes);
        return getThis();
    }

}
