package com.sbgapps.numberpicker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import java.util.Vector;

/**
 * User: derek Date: 5/2/13 Time: 7:55 PM
 */
public class NumberPickerBuilder {

    private FragmentManager manager; // Required
    private Fragment targetFragment;
    private Integer minNumber;
    private Integer maxNumber;
    private Integer plusMinusVisibility;
    private Integer decimalVisibility;
    private int mReference;
    private Vector<NumberPickerDialogFragment.NumberPickerDialogHandler> mNumberPickerDialogHandlers = new Vector<NumberPickerDialogFragment.NumberPickerDialogHandler>();

    /**
     * Attach a FragmentManager. This is required for creation of the Fragment.
     *
     * @param manager the FragmentManager that handles the transaction
     * @return the current Builder object
     */
    public NumberPickerBuilder setFragmentManager(FragmentManager manager) {
        this.manager = manager;
        return this;
    }

    /**
     * Attach a target Fragment. This is optional and useful if creating a Picker within a Fragment.
     *
     * @param targetFragment the Fragment to attach to
     * @return the current Builder object
     */
    public NumberPickerBuilder setTargetFragment(Fragment targetFragment) {
        this.targetFragment = targetFragment;
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
        this.minNumber = minNumber;
        return this;
    }

    /**
     * Set a maximum number required
     *
     * @param maxNumber the maximum required number
     * @return the current Builder object
     */
    public NumberPickerBuilder setMaxNumber(int maxNumber) {
        this.maxNumber = maxNumber;
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
        this.plusMinusVisibility = plusMinusVisibility;
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
        this.decimalVisibility = decimalVisibility;
        return this;
    }

    /**
     * Attach universal objects as additional handlers for notification when the Picker is set. For most use cases, this
     * method is not necessary as attachment to an Activity or Fragment is done automatically.  If, however, you would
     * like additional objects to subscribe to this Picker being set, attach Handlers here.
     *
     * @param handler an Object implementing the appropriate Picker Handler
     * @return the current Builder object
     */
    public NumberPickerBuilder addNumberPickerDialogHandler(NumberPickerDialogFragment.NumberPickerDialogHandler handler) {
        this.mNumberPickerDialogHandlers.add(handler);
        return this;
    }

    /**
     * Remove objects previously added as handlers.
     *
     * @param handler the Object to remove
     * @return the current Builder object
     */
    public NumberPickerBuilder removeNumberPickerDialogHandler(NumberPickerDialogFragment.NumberPickerDialogHandler handler) {
        this.mNumberPickerDialogHandlers.remove(handler);
        return this;
    }

    /**
     * Instantiate and show the Picker
     */
    public void show() {
        if (manager == null) {
            Log.e("NumberPickerBuilder", "setFragmentManager() must be called.");
            return;
        }
        final FragmentTransaction ft = manager.beginTransaction();
        final Fragment prev = manager.findFragmentByTag("number_dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        final NumberPickerDialogFragment fragment = NumberPickerDialogFragment
                .newInstance(mReference, minNumber, maxNumber, plusMinusVisibility, decimalVisibility);
        if (targetFragment != null) {
            fragment.setTargetFragment(targetFragment, 0);
        }
        fragment.setNumberPickerDialogHandlers(mNumberPickerDialogHandlers);
        fragment.show(ft, "number_dialog");
    }
}
