/*
 * Copyright 2017 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.app.header

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kunzisoft.androidclearchroma.ChromaDialog
import com.kunzisoft.androidclearchroma.colormode.ColorMode
import com.kunzisoft.androidclearchroma.listener.OnColorSelectedListener
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.base.BaseFragment
import com.sbgapps.scoreit.app.utils.LinearListHelper
import kotlinx.android.synthetic.main.dialog_edit_name.view.*
import kotlinx.android.synthetic.main.fragment_header.*
import kotlinx.android.synthetic.main.list_item_header.view.*
import java.util.*

class HeaderFragment : BaseFragment<HeaderView, HeaderPresenter>(),
        HeaderView, OnColorSelectedListener {

    lateinit var headerItems: ArrayList<HeaderItemHolder>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_header, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getPresenter().start()
    }

    override fun createPresenter(): HeaderPresenter = HeaderPresenter()

    override fun setName(player: Int, name: String) {
        headerItems[player].view.name.text = name
    }

    @SuppressLint("SetTextI18n")
    override fun setScore(player: Int, score: Int) {
        headerItems[player].view.score.text = Integer.toString(score)
    }

    override fun setColor(player: Int, @ColorInt color: Int) {
        headerItems[player].view.score.setTextColor(color)
    }

    override fun setIndicator(player: Int) {
        headerItems.indices.forEach { i ->
            headerItems[i].view.indicator.visibility =
                    if (i == player) View.VISIBLE else View.INVISIBLE
        }
    }

    override fun setupPlayerCount(count: Int) {
        val helper = HeaderLinearListHelper()
        headerItems = helper.populateItems(
                headerContainer,
                R.layout.list_item_header,
                count)
    }

    override fun showColorSelectorDialog(@ColorInt initialColor: Int) {
        ChromaDialog.Builder()
                .initialColor(initialColor)
                .colorMode(ColorMode.RGB)
                .create()
                .show(childFragmentManager, TAG_FRAGMENT_COLOR)
    }

    override fun showNameActionsDialog() {
        AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title_edit_name)
                .setItems(R.array.dialog_edit_name_actions) { dialog, which ->
                    when (which) {
                        0 -> if (isContactPermissionGranted) {
                            pickContact()
                        } else {
                            requestContactPermission()
                        }
                        1 -> showEditNameDialog()
                    }
                }
                .create()
                .show()
    }

    private val isContactPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    private fun requestContactPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQ_CODE_PERMISSION_CONTACTS)
    }

    private fun showEditNameDialog() {
        val view = activity.layoutInflater.inflate(R.layout.dialog_edit_name, null)
        val editText = view.nameEditText

        AlertDialog.Builder(context)
                .setTitle(R.string.dialog_title_edit_name)
                .setView(view)
                .setPositiveButton(R.string.button_action_ok) { dialog, which ->
                    val name = editText.text.toString()
                    if (!name.isEmpty()) getPresenter().onNameEdited(name)
                }
                .setNegativeButton(R.string.button_action_cancel, null)
                .create()
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Activity.RESULT_OK != resultCode && REQ_CODE_RESULT_CONTACT == requestCode) {
            val cursor = context.contentResolver.query(data!!.data,
                    arrayOf(ContactsContract.Contacts.DISPLAY_NAME), null, null, null) ?: return

            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                var name = cursor.getString(columnIndex)
                name = name.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                getPresenter().onNameEdited(name)
            }
            cursor.close()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (REQ_CODE_PERMISSION_CONTACTS == requestCode
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickContact()
        }
    }

    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        startActivityForResult(intent, REQ_CODE_RESULT_CONTACT)
    }

    override fun onPositiveButtonClick(@ColorInt color: Int) {
        getPresenter().onColorSelected(color)
    }

    override fun onNegativeButtonClick(@ColorInt color: Int) {}

    class HeaderItemHolder(var view: View) {
    }

    private inner class HeaderLinearListHelper : LinearListHelper<HeaderItemHolder>() {

        override fun onCreateItem(view: View, player: Int): HeaderFragment.HeaderItemHolder {
            val item = HeaderFragment.HeaderItemHolder(view)
            item.view.name.setOnClickListener { _ -> getPresenter().onNameSelectionStarted(player) }
            item.view.score.setOnClickListener { _ -> getPresenter().onColorSelectionStarted(player) }
            return item
        }
    }

    companion object {

        private val TAG_FRAGMENT_COLOR = "ColorDialog"
        private val REQ_CODE_PERMISSION_CONTACTS = 100
        private val REQ_CODE_RESULT_CONTACT = 101

        fun newInstance(): HeaderFragment {
            return HeaderFragment()
        }
    }
}
