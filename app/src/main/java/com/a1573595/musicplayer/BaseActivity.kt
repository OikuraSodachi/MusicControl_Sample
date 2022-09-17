package com.a1573595.musicplayer

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle

abstract class BaseActivity<P : BasePresenter<*>> : AppCompatActivity(), BaseView {
    protected lateinit var presenter: P

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
    }

    override fun isActive(): Boolean = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

    override fun context(): Context = this

    protected fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED

    protected fun requestPermission(requestCode: Int, vararg permissions: String) {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    protected abstract fun createPresenter(): P
}