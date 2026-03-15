package com.core.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<B : ViewBinding> : Fragment(), FragmentResultCallback {

    private var _binding: B? = null
    protected val binding: B
        get() = _binding ?: throw IllegalStateException("Binding accessed outside fragment lifecycle")

    open fun createBinding(): Class<B> {
        return (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0] as Class<B>
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val method = createBinding().getMethod("inflate", LayoutInflater::class.java)
        _binding = method.invoke(null, inflater) as B
        _binding?.let {
            it.root.isClickable = true
            it.root.isFocusable = true
        }
        return _binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitView()
        onInitObservers()
    }

    protected open fun onInitView() {}
    protected open fun onInitObservers() {}

    override fun onFragmentResult(key: String, result: Bundle) {}

    protected fun Int.dpToPx(): Int {
        val density = resources.displayMetrics.density
        return (this * density).toInt()
    }
}
