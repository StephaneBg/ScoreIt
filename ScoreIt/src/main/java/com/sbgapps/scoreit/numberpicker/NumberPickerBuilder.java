/*
 * Copyright (c) 2015 SBG Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.numberpicker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class NumberPickerBuilder {

    private FragmentManager manager; // Required
    private Fragment targetFragment;
    private int mReference;

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

        final NumberPickerDialogFragment fragment = NumberPickerDialogFragment.newInstance(mReference);
        if (targetFragment != null) {
            fragment.setTargetFragment(targetFragment, 0);
        }

        fragment.show(ft, "number_dialog");
    }
}
