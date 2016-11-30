package com.dmplayer.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.dmplayer.asynctask.AbstractMultiPlaylistTask;
import com.dmplayer.asynctask.TaskStateListener;
import com.dmplayer.helperservises.VkMusicHelper;
import com.dmplayer.models.AsyncTaskResult;
import com.dmplayer.models.PlaylistItem;
import com.dmplayer.models.playlisitems.VkPlaylistCategorySeveral;

import java.io.IOException;
import java.util.List;

public final class VkMultiPlaylistTaskFactory {
    private TaskStateListener<List<? extends PlaylistItem>> listener;

    private VkMusicHelper musicLoader;

    public VkMultiPlaylistTaskFactory(Context context,
                                      TaskStateListener<List<? extends PlaylistItem>> listener) {
        musicLoader = new VkMusicHelper.Builder(context).build();

        this.listener = listener;
    }

    public AsyncTask<Void, Void, AsyncTaskResult<List<? extends PlaylistItem>>>
    getTask(final VkPlaylistCategorySeveral category) {
        switch (category) {
            case MY_ALBUMS:
                return new AbstractMultiPlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<List<? extends PlaylistItem>> doInBackground(Void... params) {
                        try {
                            return new AsyncTaskResult<List<? extends PlaylistItem>>(musicLoader.loadUserAlbums());
                        } catch (IOException | NullPointerException e) {
                            return new AsyncTaskResult<>(e);
                        }
                    }
                };
            case POPULAR:
                return new AbstractMultiPlaylistTask(listener) {
                    @Override
                    protected AsyncTaskResult<List<? extends PlaylistItem>> doInBackground(Void... params) {
                        return new AsyncTaskResult<List<? extends PlaylistItem>>(musicLoader.loadPopularGenres());
                    }
                };
            default:
                throw new IllegalArgumentException("Default case reached!");
        }
    }
}
