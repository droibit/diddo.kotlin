package com.droibit.diddo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import com.droibit.diddo.dummy.DummyContent

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment: Fragment() {

    /**
     * The dummy content this fragment is presenting.
     */
    private var mItem: DummyContent.DummyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_item_detail, container, false)

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            (rootView.findViewById(R.id.item_detail) as TextView).setText(mItem!!.content)
        }

        return rootView
    }

    class object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        public val ARG_ITEM_ID: String = "item_id"
    }
}