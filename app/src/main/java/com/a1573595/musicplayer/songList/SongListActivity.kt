package com.a1573595.musicplayer.songList

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.a1573595.musicplayer.BaseSongActivity
import com.a1573595.musicplayer.databinding.ActivitySongListBinding
import com.a1573595.musicplayer.model.Song
import com.a1573595.musicplayer.playSong.PlaySongActivity
import com.a1573595.musicplayer.player.PlayerManager
import com.a1573595.musicplayer.player.PlayerService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.util.*

class SongListActivity : BaseSongActivity<SongListPresenter>(), SongListView {
    private lateinit var viewBinding: ActivitySongListBinding

    private var loadingDialog: AlertDialog? = null

    private val backHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initRecyclerView()

        viewBinding.tvName.isSelected = true
    }

    override fun onDestroy() {
        loadingDialog?.dismiss()
        loadingDialog = null

        super.onDestroy()
    }

    override fun onBackPressed() {
        if (backHandler.hasMessages(0)) {
            super.onBackPressed()
        } else {
            backHandler.removeCallbacksAndMessages(null)
            backHandler.postDelayed({}, 2000)
        }
    }

    override fun playerBound(player: PlayerService) {
        presenter.setPlayerManager(player)

        setListen()
    }

    override fun updateState() {
        presenter.fetchSongState()
    }

    override fun createPresenter(): SongListPresenter = SongListPresenter(this)

    override fun showLoading() {
        lifecycleScope.launch {


            loadingDialog = MaterialAlertDialogBuilder(context()).create().apply {
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                setCancelable(false)

                show()
            }

        }
    }

    override fun stopLoading() {
        lifecycleScope.launch {
            loadingDialog?.dismiss()
            loadingDialog = null

            viewBinding.recyclerView.scheduleLayoutAnimation()
        }
    }

    override fun updateSongState(song: Song, isPlaying: Boolean) {
        lifecycleScope.launch {
            viewBinding.tvName.text = song.name
            viewBinding.tvArtist.text = song.author

        }
    }

    override fun update(o: Observable?, any: Any?) {
        when (any) {
            PlayerManager.ACTION_PLAY, PlayerManager.ACTION_PAUSE -> {
                presenter.fetchSongState()
            }
            PlayerService.ACTION_FIND_NEW_SONG, PlayerService.ACTION_NOT_SONG_FOUND -> {
            }
        }
    }

    override fun onSongClick() {
        viewBinding.bottomAppBar.performShow()
    }




    private fun initRecyclerView() {
        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = SongListAdapter(presenter)
        viewBinding.recyclerView.adapter = adapter
        presenter.setAdapter(adapter)

    }

    private fun setListen() {


        viewBinding.bottomAppBar.setOnClickListener {
            if (viewBinding.tvName.text.isNotEmpty() || viewBinding.tvArtist.text.isNotEmpty()) {
                val p1: Pair<View, String> =
                    Pair.create(viewBinding.imgDisc, viewBinding.imgDisc.transitionName)
                val p2: Pair<View, String> =
                    Pair.create(viewBinding.tvName, viewBinding.tvName.transitionName)

                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2)

                startActivity(Intent(this, PlaySongActivity::class.java), options.toBundle())
            }
        }
    }


}