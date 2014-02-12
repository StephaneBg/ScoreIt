package com.sbgapps.lib.numberpicker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

/**
 * User: derek Date: 5/2/13 Time: 7:55 PM
 */
public class NumberPickerBuilder {

    private FragmentManager mManager; // Required
    private Fragment mTargetFragment;
    private Integer mMinNumber;
    private Integer mMaxNumber;
    private Integer mPlusMinusVisibility;
    private Integer mDecimalVisibility;
    private Integer mPlayer;
    private int mReference;

    /**
     * Attach a FragmentManager. This is required for creation of the Fragment.
     *
     * @param manager the FragmentManager that handles the transaction
     * @return the current Builder object
     */
    public NumberPickerBuilder setFragmentManager(FragmentManager manager) {
        this.mManager = manager;
        return this;
    }

    /**
     * Attach a target Fragment. This is optional and useful if creating a Picker within a Fragment.
     *
     * @param targetFragment the Fragment to attach to
     * @return the current Builder object
     */
    public NumberPickerBuilder setTargetFragment(Fragment targetFragment) {
        this.mTargetFragment = targetFragment;
        return this;
    }

    /**
     * Attach a reference to this Picker instance. This is used to track multiple pickers, if the user wishes.
     *
     * @param reference a user-defined int intended for Picker tracking
     * @return the current Builder object
     */
    public NumberPickerBuilder setReference(int reference) {
        this.mReference = reference;
        return this;
    }

    /**
     * Set a minimum number required
     *
     * @param minNumber the minimum required number
     * @return the current Builder object
     */
    public NumberPickerBuilder setMinNumber(int minNumber) {
        this.mMinNumber = minNumber;
        return this;
    }

    /**
     * Set a maximum number required
     *
     * @param maxNumber the maximum required number
     * @return the current Builder object
     */
    public NumberPickerBuilder setMaxNumber(int maxNumber) {
        this.mMaxNumber = maxNumber;
        return this;
    }

    public NumberPickerBuilder setPlayer(int player) {
        this.mPlayer = player;
        return this;
    }

    /**
     * Set the visibility of the +/- button. This takes an int corresponding to Android's View.VISIBLE, View.INVISIBLE,
     * or View.GONE.  When using View.INVISIBLE, the +/- button will still be present in the layout but be
     * non-clickable. When set to View.GONE, the +/- button will disappear entirely, and the "0" button will occupy its
     * space.
     *
     * @param plusMinusVisibility an int corresponding to View.VISIBLE, View.INVISIBLE, or View.GONE
     * @return the current Builder object
     */
    public NumberPickerBuilder setPlusMinusVisibility(int plusMinusVisibility) {
        this.mPlusMinusVisibility = plusMinusVisibility;
        return this;
    }

    /**
     * Set the visibility of the decimal button. This takes an int corresponding to Android's View.VISIBLE,
     * View.INVISIBLE, or View.GONE.  When using View.INVISIBLE, the decimal button will still be present in the layout
     * but be non-clickable. When set to View.GONE, the decimal button will disappear entirely, and the "0" button will
     * occupy its space.
     *
     * @param decimalVisibility an int corresponding to View.VISIBLE, View.INVISIBLE, or View.GONE
     * @return the current Builder object
     */
    public NumberPickerBuilder setDecimalVisibility(int decimalVisibility) {
        this.mDecimalVisibility = decimalVisibility;
        return this;
    }

    /**
     * Instantiate and show the Picker
     */
    public void show() {
        if (mManager == null) {
            Log.e("NumberPickerBuilder", "setFragmentManager() must be called.");
            return;
        }
        final FragmentTransaction ft = mManager.beginTransaction();
        final Fragment prev = mManager.findFragmentByTag("number_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final NumberPickerDialogFragment fragment = NumberPickerDialogFragment
                .newInstance(mReference, mMinNumber, mMaxNumber, mPlusMinusVisibility, mDecimalVisibility, mPlayer);
        if (mTargetFragment != null) {
            fragment.setTargetFragment(mTargetFragment, 0);
        }
        fragment.show(ft, "number_dialog");
    }
}
