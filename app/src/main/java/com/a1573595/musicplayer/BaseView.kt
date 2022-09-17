package com.a1573595.musicplayer

import android.content.Context

interface BaseView {
    fun isActive(): Boolean

    fun context(): Context
}