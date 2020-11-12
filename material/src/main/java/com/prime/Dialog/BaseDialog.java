package com.prime.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.prime.R;
import com.prime.Utils.Utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public abstract class BaseDialog<T extends DialogFragment> extends DialogFragment
        implements DialogInterface {

    public static final int INVALID = -1;
    public static final int LAYOUT_DEFAULT = 0;
    public static final int LAYOUT_PLAY_BOOKS = 1;
    public static final int LAYOUT_LIBRA = 2;
    public static final int LAYOUT_IPHONE = 3;
    //
    private static final String TAG = "BaseDialog";
    public OnCancelListener mOnCancelListener;
    public OnDismissListener mOnDismissListener;
    @Nullable
    protected OnClickListener mPosBtnListener;
    @Nullable
    protected OnClickListener mNegBtnListener;
    @Nullable
    protected OnClickListener mNeuBtnListener;
    //Buttons
    protected Button mPositiveButton;
    protected Button mNegativeButton;
    protected Button mNeutralButton;
    @Nullable
    protected CharSequence mPosBtnText;
    @Layout
    private int mLayout;
    private Context context;
    @Nullable
    private String mTag;
    @Nullable
    private CharSequence mTitle;
    @Nullable
    private CharSequence mSubTitle;
    @Nullable
    private CharSequence mNegBtnText;
    @Nullable
    private CharSequence mNeuBtnText;
    @Nullable
    private Drawable mIcon;
    @Nullable
    private Drawable mPosBtnDrawable;
    @Nullable
    private Drawable mNegBtnDrawable;
    @Nullable
    private Drawable mNeuBtnDrawable;
    @Nullable
    private Drawable mBackground;
    // views
    private ImageView mIconView;
    private TextView mTitleView;
    private TextView mSubtitleView;
    private ImageView mIconImageView;

    private boolean mShowAsBottomSheet = false;
    private boolean mHideHeader = false;
    private boolean mHideFooter = false;

    public BaseDialog() {
    }

    @Nullable
    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater,
                                   @Nullable ViewGroup container,
                                   @Nullable Bundle savedInstanceState) {
        LinearLayout layout;
        switch (mLayout) {
            case LAYOUT_IPHONE:
                layout = (LinearLayout) inflater.inflate(R.layout.dialog_layout_iphone,
                        container,
                        false);
                break;
            case LAYOUT_PLAY_BOOKS:
                layout = (LinearLayout) inflater.inflate(R.layout.dialog_layout_playbooks,
                        container,
                        false);
                break;
            case LAYOUT_LIBRA:
                layout = (LinearLayout) inflater.inflate(R.layout.dialog_layout_libra,
                        container,
                        false);
                break;
            default:
                layout = (LinearLayout) inflater.inflate(R.layout.dialog_layout_default,
                        container,
                        false);
        }
        ViewStub stub = layout.findViewById(R.id.dialog_content);
        stub.setLayoutResource(getContentLayout());
        stub.inflate();
        return layout;
    }

    public T showAsBottomSheet() {
        mShowAsBottomSheet = true;
        return getThis();
    }

    public T hideHeader() {
        mHideHeader = true;
        return getThis();
    }

    public T hideFooter() {
        mHideFooter = true;
        return getThis();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = mShowAsBottomSheet ?
                new BottomSheetDialog(context, getTheme()) :
                new Dialog(context, getTheme());
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null && !mShowAsBottomSheet) {

            float width = Utils.getDisplayWidth();
            //float height = Utils.getDisplayHeight();
            if (getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT) {
                window.setLayout((int) (width * 0.85),
                        WindowManager.LayoutParams.WRAP_CONTENT); // 85% when in port
                window.setBackgroundDrawableResource(R.drawable.background_dialog_window_portrate);
            } else {
                window.setLayout((int) (width * 0.6),
                        WindowManager.LayoutParams.WRAP_CONTENT);// 50% when in land
                window.setBackgroundDrawableResource(R.drawable.background_dialog_window_landscape);
            }
            window.setGravity(Gravity.CENTER);
            window.getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIconImageView = view.findViewById(R.id.dialog_icon);
        mNegativeButton = view.findViewById(R.id.dialog_negative_button);
        mPositiveButton = view.findViewById(R.id.dialog_positive_button);
        mNeutralButton = view.findViewById(R.id.dialog_neutral_button);
        mTitleView = view.findViewById(R.id.dialog_title);
        mSubtitleView = view.findViewById(R.id.dialog_subtitle);
        View header = view.findViewById(R.id.dialog_header);
        View footer = view.findViewById(R.id.dialog_footer);
        if (mHideHeader)
            header.setVisibility(View.GONE);
        if (mHideFooter)
            footer.setVisibility(View.GONE);

        // setup wrapper
        if (mIcon != null)
            mIconImageView.setImageDrawable(mIcon);
        else
            mIconImageView.setVisibility(View.GONE);
        // positive btn
        if (mPosBtnText == null && mPosBtnDrawable == null)
            mPositiveButton.setVisibility(View.GONE);
        else {
            mPositiveButton.setText(mPosBtnText);
            mPositiveButton.setCompoundDrawables(mPosBtnDrawable, null, null, null);
        }
        // negative btn
        if (mNegBtnText == null && mNegBtnDrawable == null)
            mNegativeButton.setVisibility(View.GONE);
        else {
            mNegativeButton.setText(mNegBtnText);
            mNegativeButton.setCompoundDrawables(mNegBtnDrawable, null, null, null);
        }
        // neutral btn
        if (mNeuBtnText == null && mNegBtnDrawable == null)
            mNeutralButton.setVisibility(View.GONE);
        else {
            mNeutralButton.setText(mNeuBtnText);
            mNeutralButton.setCompoundDrawables(mNeuBtnDrawable, null, null, null);
        }
        if (mTitle == null)
            mTitleView.setVisibility(View.GONE);
        else
            mTitleView.setText(mTitle);
        if (mSubTitle == null)
            mSubtitleView.setVisibility(View.GONE);
        else
            mSubtitleView.setText(mSubTitle);
        mPositiveButton.setOnClickListener(v -> {
            if (mPosBtnListener != null)
                mPosBtnListener.onClick((DialogInterface) getThis());
            dismiss();
        });
        mNegativeButton.setOnClickListener(v -> {
            if (mNegBtnListener != null)
                mNegBtnListener.onClick((DialogInterface) getThis());
            dismiss();
        });
        mNeutralButton.setOnClickListener(v -> {
            if (mNeuBtnListener != null)
                mNeuBtnListener.onClick((DialogInterface) getThis());
            dismiss();
        });
    }

    public T shouldRetainState(boolean retain) {
        setRetainInstance(retain);
        return getThis();
    }

    /**
     * Set the title displayed in the {@link BaseDialog}.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setTitle(@Nullable CharSequence title) {
        this.mTitle = title;
        return getThis();
    }

    /**
     * Set the title displayed in the {@link BaseDialog}.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setTitle(@StringRes int textId) {
        this.mTitle = context.getString(textId);
        return getThis();
    }

    /**
     * Set the subtitle displayed in the {@link BaseDialog}.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setSubTitle(@Nullable CharSequence title) {
        this.mSubTitle = title;
        return getThis();
    }


    /**
     * Set the subtitle displayed in the {@link BaseDialog}.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setSubTitle(@StringRes int textId) {
        this.mSubTitle = context.getString(textId);
        return getThis();
    }

    /**
     * Set the resource id of the {@link Drawable} to be used in the title.
     * <p>
     * Takes precedence over values set using {@link #setIcon(Drawable)}.
     *
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setIcon(@DrawableRes int iconId) {
        this.mIcon = ContextCompat.getDrawable(context, iconId);
        return getThis();
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     *
     * @param textId   The resource id of the text to display in the positive button
     * @param listener The {@link DialogInterface} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setPositiveButton(@StringRes int textId,
                               final OnClickListener listener) {
        mPosBtnText = context.getText(textId);
        mPosBtnListener = listener;
        return getThis();
    }

    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     *
     * @param text     The text to display in the positive button
     * @param listener The {# DialogInterface.OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setPositiveButton(CharSequence text, final OnClickListener listener) {
        mPosBtnText = text;
        mPosBtnListener = listener;
        return getThis();
    }


    /**
     * Set an icon to be displayed for the positive button.
     *
     * @param icon The icon to be displayed
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setPositiveButtonIcon(Drawable icon) {
        mPosBtnDrawable = icon;
        return getThis();
    }


    /**
     * Set an icon to be displayed for the positive button.
     *
     * @param icon The icon to be displayed
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setPositiveButtonIcon(@DrawableRes int icon) {
        mPosBtnDrawable = ContextCompat.getDrawable(context, icon);
        return getThis();
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param textId   The resource id of the text to display in the negative button
     * @param listener The {@link android.content.DialogInterface.OnClickListener} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setNegativeButton(@StringRes int textId,
                               final OnClickListener listener) {
        mNegBtnText = context.getText(textId);
        mNegBtnListener = listener;
        return getThis();
    }

    /**
     * Set a listener to be invoked when the negative button of the dialog is pressed.
     *
     * @param text     The text to display in the negative button
     * @param listener The {@link android.content.DialogInterface.OnClickListener} to use.
     * @return this Builder object to allow for chaining of calls to set methods
     */
    public T setNegativeButton(CharSequence text, final OnClickListener listener) {
        mNegBtnText = text;
        mNegBtnListener = listener;
        return getThis();
    }

    /**
     * Set an icon to be displayed for the negative button.
     *
     * @param icon The icon to be displayed
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setNegativeButtonIcon(Drawable icon) {
        mNegBtnDrawable = icon;
        return getThis();
    }

    /**
     * Set an icon to be displayed for the positive button.
     *
     * @param icon The icon to be displayed
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setNegativeButtonIcon(@DrawableRes int icon) {
        mNegBtnDrawable = ContextCompat.getDrawable(context, icon);
        return getThis();
    }


    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     *
     * @param textId   The resource id of the text to display in the neutral button
     * @param listener The {@link DialogInterface} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setNeutralButton(@StringRes int textId,
                              final OnClickListener listener) {
        mNeuBtnText = context.getText(textId);
        mNeuBtnListener = listener;
        return getThis();
    }

    /**
     * Set a listener to be invoked when the neutral button of the dialog is pressed.
     *
     * @param text     The text to display in the neutral button
     * @param listener The {@link DialogInterface} to use.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setNeutralButton(CharSequence text, final OnClickListener listener) {
        mNeuBtnText = text;
        mNeuBtnListener = listener;
        return getThis();
    }

    /**
     * Set an icon to be displayed for the neutral button.
     *
     * @param icon The icon to be displayed
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setNeutralButtonIcon(Drawable icon) {
        mNeuBtnDrawable = icon;
        return getThis();
    }

    /**
     * Set an icon to be displayed for the neutral button.
     *
     * @param icon The icon to be displayed
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public T setNeutralButtonIcon(@DrawableRes int icon) {
        mNeuBtnDrawable = ContextCompat.getDrawable(context, icon);
        return getThis();
    }


    /**
     * Set the {@link Drawable} to be used in the title.
     *
     * @param icon Drawable to use as the icon or null if you don't want an icon.
     */
    public T setIcon(Drawable icon) {
        this.mIcon = icon;
        return getThis();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public T setTag(@Nullable String tag) {
        this.mTag = tag;
        return getThis();
    }

    /**
     * Creates an {@link BaseDialog} with the arguments supplied to this
     * builder and immediately displays the dialog.
     * <p>
     * Calling this method is functionally identical to:
     * <pre>
     *     AlertDialog dialog = builder.create();
     *     dialog.show();
     * </pre>
     */
    public void show() {
        if (context instanceof AppCompatActivity)
            show(((AppCompatActivity) context).getSupportFragmentManager(), mTag);
        else
            throw new IllegalStateException(TAG + ": Context not of required type");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!getRetainInstance())
            dismiss();
    }

    protected abstract T getThis();

    @LayoutRes
    protected abstract int getContentLayout();

    public T setLayout(@Layout int layout) {
        this.mLayout = layout;
        return getThis();
    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public void cancel() {

    }

    public T with(@NonNull Context context) {
        this.context = context;
        return getThis();
    }

    @Retention(RetentionPolicy.SOURCE)
    @Target({ElementType.PARAMETER, ElementType.FIELD})
    @IntDef({
            LAYOUT_DEFAULT,
            LAYOUT_PLAY_BOOKS,
            LAYOUT_LIBRA,
            LAYOUT_IPHONE
    })
    @interface Layout {}

}
