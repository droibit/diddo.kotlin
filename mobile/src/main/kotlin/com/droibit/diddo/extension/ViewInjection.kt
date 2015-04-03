package com.droibit.diddo.extension

import android.preference.Preference
import android.preference.PreferenceActivity
import android.support.v4.app.Fragment
import android.view.View

/**
 * ViewInjectionするためのインターフェース
 */
public trait ViewById<T> {
    fun get(thisRef: Any, prop: PropertyMetadata): T
}

/**
 * ViewInjectionするためのインターフェース
 */
public trait ViewByKey<T> {
    fun get(thisRef: Any, prop: PropertyMetadata): T
}

/**
 * [Fragment]クラスでViewInjectionするための拡張メソッド
 */
public fun <T : View>Fragment.bindView(resId: Int): ViewById<T> = object: ViewById<T> {

    private var view: T? = null

    [suppress("UNCHECKED_CAST")]
    override fun get(thisRef: Any, prop: PropertyMetadata): T {
        if (view == null) {
            view = getView().findViewById(resId) as T
        }
        return view!!
    }
}

/**
 * [Fragment]クラスでViewInjectionするための拡張メソッド
 */
public fun <T : View>View.bindView(resId: Int): ViewById<T> = object: ViewById<T> {

    private var view: T? = null

    [suppress("UNCHECKED_CAST")]
    override fun get(thisRef: Any, prop: PropertyMetadata): T {
        if (view == null) {
            view = findViewById(resId) as T
        }
        return view!!
    }
}

/**
 * [Preference] をViewInjectionするための拡張メソッド
 */
public fun <T: Preference>PreferenceActivity.bindView(resId: Int): ViewByKey<T> = object: ViewByKey<T> {

    private var pref: T? = null

    [suppress("UNCHECKED_CAST")]
    override fun get(thisRef: Any, prop: PropertyMetadata): T {
        if (pref == null) {
            pref = findPreference(getString(resId)) as T
        }
        return pref!!
    }
}