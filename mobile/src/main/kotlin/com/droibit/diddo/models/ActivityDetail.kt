package com.droibit.diddo.models

import com.activeandroid.Model
import java.io.Serializable
import java.util.Date
import com.activeandroid.annotation.Table
import com.activeandroid.annotation.Column

/**
 * {@link UserActivity}の詳細情報。
 * 活動記録とそのコメント情報を格納する。
 *
 * @auther kumagai
 * @since 15/03/07
 */
Table(name = ActivityDetail.TABLE)
public class ActivityDetail: Model(), Serializable {

    class object {
        val TABLE = "activity_detail"
        val ACTIVITY = "activity"
        val DATE = "date"
        val MEMO = "memo"
    }

    /** 活動日 */
    Column(name = DATE)
    public var date: Date = Date()
    /** 活動のメモ */
    Column(name = MEMO)
    public var memo: String? = null
    /** 親のアクティビティ */
    Column(name = ACTIVITY)
    public var activity: UserActivity? = null
}