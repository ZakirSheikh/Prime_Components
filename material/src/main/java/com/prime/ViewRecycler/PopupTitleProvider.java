package com.prime.ViewRecycler;

/**
 * Interface Must be implemented in Adapter to notify about the title of the first visible view.
 * Calls exactly the adapter after change in first visible View in recyclerView
 */
public interface PopupTitleProvider {

    /**
     * Returns the title for the View
     *
     * @param position
     *
     * @return
     */
    String getPopupTitle(int position);
}