package com.droibit.diddo.fragments.dialogs

import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.droibit.easycreator.fragment
import de.psdev.licensesdialog.LicensesDialog

/**
 * オープンソースライセンスを表示するためのダイアログ
 *
 * @auther kumagai
 */
public class LicenseDialogFragment: DialogFragment() {

    companion object {
        const private val ARG_NOTICES = "notices"

        /**
         * 新しいインスタンスを作成する
         */
        fun newInstance(noticesId: Int): LicenseDialogFragment {
            return fragment { args ->
                args.putInt(ARG_NOTICES, noticesId)
            }
        }
    }

    /** {@inheritDoc} */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val noticesId = arguments.getInt(ARG_NOTICES)
        val dialog = LicensesDialog.Builder(context)
                                   .setNotices(noticesId)
                                   .setShowFullLicenseText(true)
                                   .setCloseText(android.R.string.ok)
                                   .build()
                                   .create();
        dialog.setOnDismissListener(null)

        return dialog
    }
}