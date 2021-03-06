package com.droibit.diddo.fragments

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.text.TextUtils
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.activeandroid.Model
import com.droibit.diddo.ItemListActivity
import com.droibit.diddo.R
import com.droibit.diddo.extension.bindView
import com.droibit.diddo.fragments.dialogs.ActivityMemoDialogFragment
import com.droibit.diddo.fragments.dialogs.CalendarDialogFragment
import com.droibit.diddo.models.ActivityDate
import com.droibit.diddo.models.RefreshEvent
import com.droibit.diddo.models.UserActivity
import com.droibit.diddo.utils.ViewAnimationUtils
import com.droibit.diddo.views.adapters.ActivityDateAdapter
import com.droibit.easycreator.compat.fragment
import com.droibit.easycreator.compat.getInteger
import com.droibit.easycreator.compat.show
import com.droibit.easycreator.sendMessage
import com.droibit.easycreator.showToast
import com.melnykov.fab.FloatingActionButton
import java.util.ArrayList
import kotlin.properties.Delegates

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ActivityDetailFragment : Fragment(), ActivityMemoDialogFragment.Callbacks, View.OnLayoutChangeListener {

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        const val ARG_ITEM_TITLE = "item_title"

        /**
         * 新しいインスタンスを作成する。
         */
        fun newInstance(id: Long): ActivityDetailFragment {
           return fragment() { args ->
                args.putLong(ARG_ITEM_ID, id)
            }
        }
    }

    /**
     * The dummy content this fragment is presenting.
     */
    private var mUserActivity: UserActivity by Delegates.notNull()
    private val mElapsedDateText: TextView by bindView(R.id.elapsed_date)
    private val mDateText: TextView by bindView(R.id.date)
    private val mAddActionButton: FloatingActionButton by bindView(R.id.fab_add)
    private val mCalendarActionButton: FloatingActionButton by bindView(R.id.fab_calendar)
    private val mListView: ListView by bindView(android.R.id.list)

    private var mHandler: Handler? = null

    /** {@inheritDoc} */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments?.containsKey(ARG_ITEM_ID) == true) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mUserActivity = Model.load(UserActivity::class.java, arguments.getLong(ARG_ITEM_ID))
        }
    }

    /** {@inheritDoc} */
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        if (activity is Handler.Callback) {
            mHandler = Handler(activity)
        }
    }

    /** {@inheritDoc} */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_item_detail, container, false)

        // Android5.0以上の場合はヘッダにリップルエフェクトを適用する。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rootView.addOnLayoutChangeListener(this)
        }
        return rootView
    }

    /** {@inheritDoc} */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 日付テキストをアニメーション表示する。
        ViewCompat.setTransitionName(mDateText, getString(R.string.transition_date))

        val adapter = ActivityDateAdapter(getActivity())
        mListView.adapter = adapter
        mListView.emptyView = view.findViewById(android.R.id.empty)

        mListView.setOnItemClickListener { adapterView, view, position, l ->
            // クリックされたらメモの内容をトーストで表示する。
            showMemoToast(adapter.getItem(position))
        }
        mListView.setOnItemLongClickListener { adapterView, view, position, l ->
            // 長押しで編集用のダイアログを表示する。
            showActivityMemoDialog(adapter.getItem(position))
        }

        mAddActionButton.setOnClickListener { showActivityMemoDialog(null) }
        mCalendarActionButton.setOnClickListener { showActivityDateCalendar() }
    }

    /** {@inheritDoc} */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activityDates = mUserActivity.details
        if (activityDates.isNotEmpty()) {
            (mListView.adapter as ActivityDateAdapter).addAll(activityDates)
        }
        updateElapsedViews()
    }

    /** {@inheritDoc} */
    override fun onActivityDateEntered(activityDate: ActivityDate) {
        val adapter = mListView.adapter as ActivityDateAdapter
        if (activityDate.isNew) {
            adapter.add(activityDate)
            // 修正した場合に最新の日付を変更しないようにする。
            mUserActivity.recentlyDate = activityDate.date
            // 新規追加したことをマスタ側に通知する。
            sendRefreshEvent(mUserActivity)
        } else {
            adapter.notifyDataSetChanged()
        }

        val messageRes = if (activityDate.isNew)
                            R.string.toast_create_activity_date
                        else
                            R.string.toast_modify_activity_memo
        // アクティビティの日付も更新しておく。
        mUserActivity.save()
        // メッセージの都合、このタイミングでDBに保存する。
        activityDate.activity = mUserActivity
        activityDate.save()

        showToast(context, messageRes, Toast.LENGTH_SHORT)
        // 日をまたいで活動日を追加した場合のために画面を更新する。
        updateElapsedViews()

        // TODO: 削除した時も
        activity?.setResult(Activity.RESULT_OK)
    }

    /** {@inheritDoc} */
    override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        v.removeOnLayoutChangeListener(this)

        val header = v.findViewById(R.id.header)
        ViewAnimationUtils.animationCircularReveal(header, getInteger(R.integer.medium_animation_millis).toLong())

        ViewAnimationUtils.animationScaleUp(mAddActionButton, getInteger(R.integer.short_animation_millis).toLong())
        ViewAnimationUtils.animationScaleUp(mCalendarActionButton, getInteger(R.integer.short_animation_millis).toLong())
    }

    // アクティビティのメモ編集用のダイアログを表示する。
    private fun showActivityMemoDialog(srcActivityDate: ActivityDate?): Boolean {
        ActivityMemoDialogFragment.newInstance(srcActivityDate).show(this)
        return true
    }

    // アクティビティのメモをトーストで表示する。
    private fun showMemoToast(activityDate: ActivityDate) {
        val text = if (!TextUtils.isEmpty(activityDate.memo))
                        activityDate.memo!!
                   else
                        getString(R.string.toast_empty_memo)

        showToast(context, text, Toast.LENGTH_LONG)
    }

    private fun updateElapsedViews() {
        if (mListView.adapter.isEmpty) {
            mElapsedDateText.setText(R.string.empty_text)
            mDateText.setText(R.string.empty_text)
            return
        }

        val recentlyActivityDate = (mListView.adapter as ActivityDateAdapter).getLastItem()!!
        mElapsedDateText.text = recentlyActivityDate.getElapsedDateFromNow(context)

        val date = DateFormat.getDateFormat(context).format(recentlyActivityDate.date)
        val hour = DateFormat.getTimeFormat(context).format(recentlyActivityDate.date)
        mDateText.text = "$date $hour"
    }

    private fun showActivityDateCalendar() {
        if (mListView.adapter.isEmpty) {
            showToast(context, R.string.text_no_activity_date, Toast.LENGTH_SHORT)
            return
        }

        val activityDates = mUserActivity.details
        CalendarDialogFragment.newInstance(ArrayList(activityDates))
                .show(this)
    }

    private fun sendRefreshEvent(userActivity: UserActivity) {
        // 2画面表示しているならマスタ側に変更が反映されるようにする。
        mHandler?.sendMessage {
            what = ItemListActivity.MESSAGE_REFRESH
            obj = RefreshEvent(userActivity)
        }
    }
}