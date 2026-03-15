package com.core.navigation

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.core.BaseApp
import com.core.R

inline fun <reified FRAGMENT : Fragment> Fragment.presentFragment(
    animate: Boolean = true,
    animationType: AnimationType = AnimationType.LEFT_TO_RIGHT,
    backStack: Boolean = true,
    container: Int = BaseApp.context.getConfig().mainContainer,
    openType: OpenType = OpenType.ADD,
    vararg arguments: Pair<String, Any?>
) {
    activity?.let {
        initFragment<FRAGMENT>(
            it, it.supportFragmentManager, animate, animationType, backStack,
            container, openType, arguments = arguments
        )
    }
}

inline fun <reified FRAGMENT : Fragment> AppCompatActivity.presentFragment(
    animate: Boolean = true,
    animationType: AnimationType = AnimationType.LEFT_TO_RIGHT,
    backStack: Boolean = true,
    container: Int = BaseApp.context.getConfig().mainContainer,
    openType: OpenType = OpenType.ADD,
    vararg arguments: Pair<String, Any?>
) {
    initFragment<FRAGMENT>(
        this, supportFragmentManager, animate, animationType, backStack,
        container, openType, arguments = arguments
    )
}

inline fun <reified FRAGMENT> initFragment(
    context: Context,
    manager: FragmentManager,
    animate: Boolean,
    animationType: AnimationType = AnimationType.LEFT_TO_RIGHT,
    backStack: Boolean,
    container: Int,
    openType: OpenType = OpenType.ADD,
    vararg arguments: Pair<String, Any?>
) {
    val tag = FRAGMENT::class.java.name
    val fragment = manager.fragmentFactory.instantiate(context.classLoader, tag)

    fragment.arguments = bundleOf(*arguments)
    inTransaction(manager, animate, animationType) {
        if (openType == OpenType.ADD) {
            add(container, fragment, tag)
        } else {
            replace(container, fragment, tag)
        }
        if (backStack) {
            addToBackStack(tag)
        }
    }
}

inline fun inTransaction(
    fragmentManager: FragmentManager,
    animate: Boolean,
    animationType: AnimationType,
    transaction: FragmentTransaction.() -> Unit
) {
    fragmentManager.commit {
        setReorderingAllowed(true)
        if (animate) {
            when (animationType) {
                AnimationType.LEFT_TO_RIGHT -> {
                    setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                }
                AnimationType.BOTTOM_TO_TOP -> {
                    setCustomAnimations(
                        R.anim.slide_in_bottom,
                        R.anim.no_animation,
                        R.anim.no_animation,
                        R.anim.slide_out_bottom
                    )
                }
            }
        }
        transaction()
    }
}
