/*
 * This is the source code of DMPLayer for Android v. 1.0.0.
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright @Dibakar_Mistry, 2015.
 */
package com.dmplayer.childfragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.activities.DMPlayerBaseActivity;
import com.dmplayer.manager.MediaController;
import com.dmplayer.models.SongDetail;
import com.dmplayer.phonemedia.PhoneMediaControl;
import com.dmplayer.phonemedia.PhoneMediaControl.PhoneMediaControlInterface;
import com.dmplayer.utility.DMPlayerUtility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ChildFragmentAllSongs extends Fragment {

    private AllSongsListAdapter mAllSongsListAdapter;
    private List<SongDetail> songList = new ArrayList<>();

    public static ChildFragmentAllSongs newInstance(int position, Context mContext) {
        return new ChildFragmentAllSongs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_child_allsongs, null);
        setupInitialViews(v);
        loadAllSongs();
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setupInitialViews(View inflateView) {
        ListView recyclerSongsList = (ListView) inflateView.findViewById(R.id.listView_songs);
        mAllSongsListAdapter = new AllSongsListAdapter(getActivity());
        recyclerSongsList.setAdapter(mAllSongsListAdapter);
        recyclerSongsList.setFastScrollEnabled(true);
    }

    private void loadAllSongs() {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhoneMediaControlInterface(new PhoneMediaControlInterface() {

            @Override
            public void loadSongsComplete(List<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
            }
        });
        mPhoneMediaControl.loadMusicListAsync(getActivity(), -1, PhoneMediaControl.SongsLoadFor.ALL, "");
    }

    public class AllSongsListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public AllSongsListAdapter(Context mContext) {
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
            this.options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art)
                    .showImageOnFail(R.drawable.bg_default_album_art)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mViewHolder;
            if (convertView == null) {
                mViewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_song, null);
                mViewHolder.songName = (TextView) convertView.findViewById(R.id.song_name);
                mViewHolder.artistAndDuration = (TextView) convertView.findViewById(R.id.song_details);
                mViewHolder.songImage = (ImageView) convertView.findViewById(R.id.song_icon_art);
                mViewHolder.menuImage = (ImageView) convertView.findViewById(R.id.song_icon_option_more);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            SongDetail mDetail = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mViewHolder.artistAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
            mViewHolder.songName.setText(mDetail.getTitle());
            String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";

            imageLoader.displayImage(contentURI, mViewHolder.songImage, options);

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    SongDetail mDetail = songList.get(position);
                    mDetail.audioProgress = 0.0f;
                    mDetail.audioProgressSec = 0;
                    ((DMPlayerBaseActivity) getActivity()).loadSongsDetails(mDetail);

                    if (mDetail != null) {
                        if (MediaController.getInstance().isPlayingAudio(mDetail) && !MediaController.getInstance().isAudioPaused()) {
                            MediaController.getInstance().pauseAudio(mDetail);
                        } else {
                            MediaController.getInstance().setPlaylist(songList, mDetail, PhoneMediaControl.SongsLoadFor.ALL.ordinal(), -1);
                        }
                    }
                }
            });

            mViewHolder.menuImage.setColorFilter(Color.DKGRAY);
            mViewHolder.menuImage.setImageAlpha(255);

            mViewHolder.menuImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        PopupMenu popup = new PopupMenu(context, v);
                        popup.getMenuInflater().inflate(R.menu.list_item_option, popup.getMenu());
                        popup.show();
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {

                                switch (item.getItemId()) {
                                    case R.id.playnext:
                                        break;
                                    case R.id.addtoque:
                                        break;
                                    case R.id.addtoplaylist:
                                        break;
                                    case R.id.gotoartis:
                                        break;
                                    case R.id.gotoalbum:
                                        break;
                                    case R.id.delete:
                                        break;
                                    default:
                                        break;
                                }

                                return true;
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return convertView;
        }

        @Override
        public int getCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder {
            TextView songName;
            ImageView songImage;
            ImageView menuImage;
            TextView artistAndDuration;
        }
    }

}
