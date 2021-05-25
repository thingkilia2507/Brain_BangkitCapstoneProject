package com.thing.bangkit.soulmood.helper

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.thing.bangkit.soulmood.R

object MyAsset {

    const val SOULMOOD_CHATBOT_NAME = "soulmood0280_chatbot"
    const val GROUP_NAME_ACTIVITY = "GroupNameActivity"
    const val HOME_FRAGMENT = "HomeFragment"
    const val KEY_NAME = "key_name"
    const val KEY_EMAIL = "key_email"
    const val KEY_USER_ID = "key_user_id"

    fun sweetAlertDialog(
        context: Context,
        titleLoading: String,
        cancelableLoading: Boolean
    ): SweetAlertDialog {
        val pDialog = SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
        pDialog.progressHelper.barColor = R.color.soulmood_primary_color
        pDialog.titleText = titleLoading
        pDialog.setCancelable(cancelableLoading) //usually false
        return pDialog
    }

    fun sweetAlertDialog(context: Context, titleSuccess: String): SweetAlertDialog? {
        return SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
            .setTitleText(titleSuccess).hideConfirmButton()
    }

    fun sweetAlertDialog(
        context: Context,
        titleError: String,
        messageError: String
    ): SweetAlertDialog {
        return SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText(titleError)
            .setContentText(messageError)
    }

    fun makeSweetAlertDialog(
        context: Context,
        titleConfirmation: String,
        messageConfirmation: String,
        positiveBtnText: String,
        negativeBtnText: String
    ): SweetAlertDialog {
        return SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
            .setTitleText(titleConfirmation) //"Are you sure?"
            .setContentText(messageConfirmation) //"Won't be able to recover this file!"
            .setConfirmText(positiveBtnText) //"Yes,delete it!"
            .setCancelText(negativeBtnText) //"No,cancel plx!"
            .showCancelButton(true)
    }


    fun makeSweetAlertDialog(
        context: Context,
        typeSweetAlert: Int,
        titleConfirmation: String,
        messageConfirmation: String,
        positiveBtnText: String,
        negativeBtnText: String
    ): SweetAlertDialog {
        return SweetAlertDialog(context, typeSweetAlert)
            .setTitleText(titleConfirmation) //"Are you sure?"
            .setContentText(messageConfirmation) //"Won't be able to recover this file!"
            .setConfirmText(positiveBtnText) //"Yes,delete it!"
            .setCancelText(negativeBtnText) //"No,cancel plx!
            .showCancelButton(true)
    }

    fun makeSweetAlertDialog(
        context: Context,
        titleConfirmation: String,
        messageConfirmation: String,
        typeSweetAlert: Int
    ): SweetAlertDialog {
        return SweetAlertDialog(context, typeSweetAlert)
            .setTitleText(titleConfirmation) //"Are you sure?"
            .setContentText(messageConfirmation) //"Won't be able to recover this file!"
    }
}