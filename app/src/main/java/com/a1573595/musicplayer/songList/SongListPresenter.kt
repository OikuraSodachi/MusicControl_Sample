package com.a1573595.musicplayer.songList

import android.util.SparseArray
import com.a1573595.musicplayer.BasePresenter
import com.a1573595.musicplayer.model.Song
import com.a1573595.musicplayer.player.PlayerService
import kotlinx.coroutines.*

class SongListPresenter constructor(view: SongListView) : BasePresenter<SongListView>(view) {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())

    private lateinit var player: PlayerService

    private lateinit var adapter: SongListAdapter
    private val filteredSongList: SparseArray<Song> = SparseArray()

    fun setPlayerManager(player: PlayerService) {
        this.player = player

        loadSongList()
    }

    fun setAdapter(adapter: SongListAdapter) {
        this.adapter = adapter
    }

    fun fetchSongState() {
        player.getSong()?.let {
            view.updateSongState(it, player.isPlaying())
        }
    }


    fun getItemCount() = filteredSongList.size()

    fun getItem(position: Int): Song = filteredSongList.valueAt(position)

    fun onSongClick(index: Int) {
        view.onSongClick()

        val position = filteredSongList.keyAt(index)
        playSong(position)
    }

    private fun loadSongList() {
        scope.launch {
            view.showLoading()

            player.readSong()
            player.getSongList().forEachIndexed { index, song -> filteredSongList.put(index, song) }

            withContext(Dispatchers.Main) {
                view.stopLoading()
                adapter.notifyDataSetChanged()
                fetchSongState()
            }
        }
    }

    private fun playSong(position: Int) {
        player.play(position)
    }
}