package com.prime.Dialog;

/**
 * Interface that defines a dialog-type class that can be shown, dismissed, or
 * canceled, and may have buttons that can be clicked.
 */
public interface DialogInterface {

    /**
     * Cancels the dialog, invoking the {@link OnCancelListener}.
     * <p>
     * The {@link OnDismissListener} may also be called if cancellation
     * dismisses the dialog.
     */
    void cancel();

    /**
     * Dismisses the dialog, invoking the {@link OnDismissListener}.
     */
    void dismiss();

    /**
     * Interface used to allow the creator of a dialog to run some code when the
     * dialog is canceled.
     * <p>
     * This will only be called when the dialog is canceled, if the creator
     * needs to know when it is dismissed in general, use
     * {@link OnDismissListener}.
     */
    interface OnCancelListener {
        /**
         * This method will be invoked when the dialog is canceled.
         *
         * @param dialog the dialog that was canceled will be passed into the
         *               method
         */
        void onCancel(DialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when the
     * dialog is dismissed.
     */
    interface OnDismissListener {
        /**
         * This method will be invoked when the dialog is dismissed.
         *
         * @param dialog the dialog that was dismissed will be passed into the
         *               method
         */
        void onDismiss(DialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when the
     * dialog is shown.
     */
    interface OnShowListener {
        /**
         * This method will be invoked when the dialog is shown.
         *
         * @param dialog the dialog that was shown will be passed into the
         *               method
         */
        void onShow(DialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when an
     * item on the dialog is clicked.
     */
    interface OnClickListener {
        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog the dialog that received the click
         */
        void onClick(DialogInterface dialog);
    }

    /**
     * Interface used to allow the creator of a dialog to Know weather check box is clicked
     * special case of {@link OnClickListener}
     */
    interface onAlertClickListener extends OnClickListener {
        void onAlertClick(DialogInterface dialog, boolean isChecked);
    }

    /**
     * Interface used to allow the creator of a dialog to pass entered {@link CharSequence} text
     * when an
     * Positive Button is Clicked
     */
    interface OnInputClickListener extends OnClickListener {
        /**
         * This method will be invoked when an item in the dialog is clicked.
         *
         * @param dialog the dialog where the selection was made
         * @param text   The text entered By user
         */
        void onClick(DialogInterface dialog, CharSequence text);
    }


    /**
     * Interface used to allow the creator of a dialog to run some code when an
     * item in a multi-choice dialog is clicked.
     */
    interface OnSingleChoiceClickListener extends OnClickListener {
        /**
         * This method will be invoked when an item in the dialog is clicked.
         *
         * @param dialog the dialog where the selection was made
         * @param which  {@code id} of the checked the item, else
         *               {@code -1}
         */
        void onClick(DialogInterface dialog, int which);
    }

    /**
     * Interface used to allow the creator of a dialog to run some code when an
     * item in a multi-choice dialog is clicked.
     */
    interface OnMultiChoiceClickListener extends OnClickListener {
        /**
         * This method will be invoked when an item in the dialog is clicked.
         *
         * @param dialog    the dialog where the selection was made
         * @param isChecked {@code true} if the click checked the item, else
         *                  {@code false}
         */
        void onClick(DialogInterface dialog, boolean[] isChecked);
    }

}
