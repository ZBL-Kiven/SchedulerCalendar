package com.zj.schedule

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.zj.cf.fragments.ConstrainFragment
import com.zj.cf.startFragment
import com.zj.schedule.utl.Config
import com.zj.schedule.utl.Utl

class NavigationToConstrainFragment private constructor(val config: Config) {

    companion object {

        fun create(config: Config, context: Context): NavigationToConstrainFragment {
            Utl.setConfig(config)
            Utl.init(context)
            return NavigationToConstrainFragment(config)
        }
    }

    fun <T : ConstrainFragment> start(activity: FragmentActivity, viewGroup: ViewGroup, cls: Class<T>, bundle: Bundle? = null, clearWhenEmptyStack: () -> Boolean = { true }) {
        Utl.frgManager = activity.startFragment(cls, viewGroup, bundle, clearWhenEmptyStack)
    }
}