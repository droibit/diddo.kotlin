package com.droibit.diddo.models.dummy

import com.droibit.diddo.models.ActivityDate
import com.droibit.diddo.models.UserActivity
import com.droibit.diddo.models.dummyActivity
import com.droibit.diddo.models.dummyActivityDate
import java.util.Date

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    companion object {

        /**
         * An array of sample (dummy) items.
         */
        public val ITEMS: MutableList<UserActivity> = arrayListOf(
                dummyActivity("Item 1", Date(1420038000000)),   // 2015/01/01
                dummyActivity("Item 3", Date(1420124400000)),   // 2015/02/01
                dummyActivity("Item 2", Date(1388502000000))    // 2014/01/01
        )

        public val DETAIL_ITEMS: MutableList<ActivityDate> = arrayListOf(
                dummyActivityDate(Date(1420038000000)),
                dummyActivityDate(Date(1420124400000)),
                dummyActivityDate(Date(1388502000000)),
                dummyActivityDate(Date(1389452400000))
        )

        /**
         * A map of sample (dummy) items, by ID.
         */
        public var ITEM_MAP: MutableMap<String, UserActivity> = hashMapOf(
                "1" to dummyActivity("Item 1", Date(1420038000000)),
                "2" to dummyActivity("Item 2", Date(1420124400000)),
                "3" to dummyActivity("Item 3", Date(1388502000000))
        )
    }
}
