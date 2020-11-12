package com.prime.Dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.prime.R;

public class ChoiceDialog extends BaseDialog<ChoiceDialog> {

    //
    private static final String TAG = "ChoiceDialog";
    public boolean[] mCheckedItems;
    public boolean mIsSingleChoice;
    public int mCheckedItem = -1;
    public CharSequence[] mItems;
    public CheckedItemAdapter mAdapter;
    private ListView mListView;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;


    @Override
    protected ChoiceDialog getThis() {
        return this;
    }


    @Override
    protected int getContentLayout() {
        return R.layout.dialog_choice_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView = view.findViewById(R.id.dialog_single_choice_listView);
        int
                mItemLayout =
                mIsSingleChoice ?
                        R.layout.layout_single_choice_item :
                        R.layout.layout_multi_choice_item;
        mAdapter = new CheckedItemAdapter(getContext(),
                mItemLayout,
                R.id.text1,
                mItems) {
            @NonNull
            @Override
            public View getView(int position,
                                @Nullable View convertView,
                                @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                boolean
                        isItemChecked =
                        mIsSingleChoice ? position == mCheckedItem : mCheckedItems[position];
                if (isItemChecked) {
                    mListView.setItemChecked(position, true);
                }
                return view;
            }
        };
        mListView.setAdapter(mAdapter);
        if (mIsSingleChoice)
            mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        else
            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            if (mIsSingleChoice) {
                mCheckedItem = position;
            } else {
                mCheckedItems[position] = mListView.isItemChecked(position);
            }
        });
        // Attach a given OnItemSelectedListener to the ListView
        if (mOnItemSelectedListener != null) {
            mListView.setOnItemSelectedListener(mOnItemSelectedListener);
        }
        if (mPosBtnListener != null)
            mPositiveButton.setOnClickListener(v -> {
                if (mIsSingleChoice)
                    ((OnSingleChoiceClickListener) mPosBtnListener).onClick(
                            getThis(),
                            mCheckedItem);
                else
                    ((OnMultiChoiceClickListener) mPosBtnListener).onClick(
                            getThis(),
                            mCheckedItems);
            });
    }

    /**
     * Set a list of items to be displayed in the dialog as the content,
     * you will be notified of the selected item via the supplied listener.
     * This should be an array type, e.g. R.array.foo. The list will have
     * a check mark displayed to the right of the text for each checked
     * item. Clicking on an item in the list will not dismiss the dialog.
     * Clicking on a button will dismiss the dialog.
     *
     * @param itemsId      the resource id of an array i.e. R.array.foo
     * @param checkedItems specifies which items are checked. It should be null in which case no
     *                     items are checked. If non null it must be exactly the same length as
     *                     the array of
     *                     items.
     * @param listener     notified when an item on the list is clicked. The dialog will not be
     *                     dismissed when an item is clicked. It will only be dismissed if
     *                     clicked on a
     *                     button, if no buttons are supplied it's up to the user to dismiss the
     *                     dialog.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public ChoiceDialog setMultiChoiceItems(@ArrayRes int itemsId, @NonNull boolean[] checkedItems,
                                            final OnMultiChoiceClickListener listener) {
        mItems = getContext().getResources().getTextArray(itemsId);
        mPosBtnListener = listener;
        mCheckedItems = checkedItems;
        mIsSingleChoice = false;
        return this;
    }

    /**
     * Set a list of items to be displayed in the dialog as the content,
     * you will be notified of the selected item via the supplied listener.
     * The list will have a check mark displayed to the right of the text
     * for each checked item. Clicking on an item in the list will not
     * dismiss the dialog. Clicking on a button will dismiss the dialog.
     *
     * @param items        the text of the items to be displayed in the list.
     * @param checkedItems specifies which items are checked. It should be null in which case no
     *                     items are checked. If non null it must be exactly the same length as
     *                     the array of
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public ChoiceDialog setMultiChoiceItems(@NonNull CharSequence[] items,
                                            @NonNull boolean[] checkedItems) {
        mItems = items;
        mCheckedItems = checkedItems;
        mIsSingleChoice = false;
        return this;
    }


    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of
     * the selected item via the supplied listener. This should be an array type i.e.
     * R.array.foo The list will have a check mark displayed to the right of the text for the
     * checked item. Clicking on an item in the list will not dismiss the dialog. Clicking on a
     * button will dismiss the dialog.
     *
     * @param itemsId     the resource id of an array i.e. R.array.foo
     * @param checkedItem specifies which item is checked. If -1 no items are checked.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public ChoiceDialog setSingleChoiceItems(@ArrayRes int itemsId, int checkedItem) {
        mItems = getContext().getResources().getTextArray(itemsId);
        mCheckedItem = checkedItem;
        mIsSingleChoice = true;
        return this;
    }


    /**
     * sets listener
     *
     * @param listener specifies which item is checked. If -1 no items are checked.
     *                 notified when an item on the list is clicked. The dialog will not be
     *                 dismissed when an item is clicked. It will only be dismissed if clicked
     *                 on a
     *                 button, if no buttons are supplied it's up to the user to dismiss the
     *                 dialog.
     * @return this
     */
    public ChoiceDialog setChoiceListener(CharSequence text, OnSingleChoiceClickListener listener) {
        mPosBtnListener = listener;
        mPosBtnText = text;
        if (!mIsSingleChoice)
            throw new UnsupportedOperationException(TAG +
                    " use multi choice listener for multi = " +
                    mIsSingleChoice);
        return this;
    }

    /**
     * @param listener notified when an item on the list is clicked. The dialog will not be
     *                 dismissed when an item is clicked. It will only be dismissed if
     *                 clicked on a
     *                 button, if no buttons are supplied it's up to the user to dismiss the
     *                 dialog.
     * @return this
     */
    public ChoiceDialog setChoiceListener(CharSequence text,
                                          OnMultiChoiceClickListener listener) {
        mPosBtnListener = listener;
        mPosBtnText = text;
        if (mIsSingleChoice)
            throw new UnsupportedOperationException(TAG +
                    " use multi choice listener for multi = " +
                    mIsSingleChoice);
        return this;
    }

    public ChoiceDialog setChoiceListener(@StringRes int textID,
                                          OnMultiChoiceClickListener listener) {
        String text = getContext().getString(textID);
        setChoiceListener(text, listener);
        return this;
    }

    public ChoiceDialog setChoiceListener(@StringRes int textID,
                                          OnSingleChoiceClickListener listener) {
        String text = getContext().getString(textID);
        setChoiceListener(text, listener);
        return this;
    }

    @Override
    public ChoiceDialog setPositiveButton(int textId, OnClickListener listener) {
        throw new UnsupportedOperationException("Use setChoiceListener for positive buttons!!");
    }

    @Override
    public ChoiceDialog setPositiveButton(CharSequence text, OnClickListener listener) {
        throw new UnsupportedOperationException("Use setChoiceListener for positive buttons!!");
    }

    /**
     * Set a list of items to be displayed in the dialog as the content, you will be notified of
     * the selected item via the supplied listener. The list will have a check mark displayed to
     * the right of the text for the checked item. Clicking on an item in the list will not
     * dismiss the dialog. Clicking on a button will dismiss the dialog.
     *
     * @param items       the items to be displayed.
     * @param checkedItem specifies which item is checked. If -1 no items are checked.
     * @return This Builder object to allow for chaining of calls to set methods
     */
    public ChoiceDialog setSingleChoiceItems(CharSequence[] items,
                                             int checkedItem) {
        mItems = items;
        mCheckedItem = checkedItem;
        mIsSingleChoice = true;
        return this;
    }

    /**
     * Sets a listener to be invoked when an item in the list is selected.
     *
     * @param listener the listener to be invoked
     * @return this Builder object to allow for chaining of calls to set methods
     * @see AdapterView#setOnItemSelectedListener(AdapterView.OnItemSelectedListener)
     */
    public ChoiceDialog setOnItemSelectedListener(final AdapterView.OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
        return this;
    }

    private static class CheckedItemAdapter extends ArrayAdapter<CharSequence> {
        public CheckedItemAdapter(Context context, int resource, int textViewResourceId,
                                  CharSequence[] objects) {
            super(context, resource, textViewResourceId, objects);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
