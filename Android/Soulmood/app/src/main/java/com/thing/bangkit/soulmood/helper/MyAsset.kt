package com.thing.bangkit.soulmood.helper

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import com.thing.bangkit.soulmood.R

object MyAsset {

    const val SOULMOOD_CHATBOT_NAME = "soulmood0280_chatbot"
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

    fun Activity.getRootView(): View {
        return findViewById<View>(android.R.id.content)
    }
    fun Context.convertDpToPx(dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        )
    }
    fun Activity.isKeyboardOpen(): Boolean {
        val visibleBounds = Rect()
        this.getRootView().getWindowVisibleDisplayFrame(visibleBounds)
        val heightDiff = getRootView().height - visibleBounds.height()
        val marginOfError = Math.round(this.convertDpToPx(50F))
        return heightDiff > marginOfError
    }

    fun Activity.isKeyboardClosed(): Boolean {
        return !this.isKeyboardOpen()
    }
}