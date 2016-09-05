package com.dmplayer.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dmplayer.R;
import com.dmplayer.models.SongDetail;
import com.dmplayer.phonemidea.DMPlayerUtility;
import com.dmplayer.phonemidea.PhoneMediaControl;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class MusicChooserActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private int color = 0xFFFFFF;
    private Context context;

    private ListView recycler_songslist;
    private AllSongsListAdapter mAllSongsListAdapter;
    private ArrayList<SongDetail> songList = new ArrayList<SongDetail>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set your theme first
        context = MusicChooserActivity.this;
        theme();

        //Set your Layout view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicchooser);

        initialize();
        loadAllSongs();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //Catch  theme changed from settings
    public void theme() {
        sharedPreferences = getSharedPreferences("VALUES", Context.MODE_PRIVATE);
        int theme = sharedPreferences.getInt("THEME", 0);

        DMPlayerUtility.settingTheme(context, theme);
    }

    private void initialize() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        recycler_songslist = (ListView) findViewById(R.id.listView_songs);
        mAllSongsListAdapter = new AllSongsListAdapter(context);
        recycler_songslist.setAdapter(mAllSongsListAdapter);
//        TypedValue primaryColor = new TypedValue();
//        context.getTheme().resolveAttribute(R.attr.colorPrimary, primaryColor,true);
//        recycler_songslist.setSelector(primaryColor.resourceId);
        recycler_songslist.setFastScrollEnabled(true);
        recycler_songslist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        recycler_songslist.setMultiChoiceModeListener(new MultiSongs(recycler_songslist));
    }

    private void loadAllSongs() {
        PhoneMediaControl mPhoneMediaControl = PhoneMediaControl.getInstance();
        PhoneMediaControl.setPhoneMediaControlInterface(new PhoneMediaControl.PhoneMediaControlInterface() {

            @Override
            public void loadSongsComplete(ArrayList<SongDetail> songsList_) {
                songList = songsList_;
                mAllSongsListAdapter.notifyDataSetChanged();
            }
        });
        mPhoneMediaControl.loadMusicList(context, -1, PhoneMediaControl.SongsLoadFor.All,"");
    }

    public class AllSongsListAdapter extends BaseAdapter {
        private Context context = null;
        private LayoutInflater layoutInflater;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = ImageLoader.getInstance();

        public AllSongsListAdapter(Context mContext) {
            this.context = mContext;
            this.layoutInflater = LayoutInflater.from(mContext);
            this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                    .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                    .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
        }

        @Override
        public Object getItem(int position) {
            return songList.get(position);
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
                convertView = layoutInflater.inflate(R.layout.inflate_allsongsitem, null);
                mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
                mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
                mViewHolder.textViewSongArtistNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
                mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);
                convertView.setTag(mViewHolder);
            } else {
                mViewHolder = (ViewHolder) convertView.getTag();
            }
            final SongDetail mDetail = songList.get(position);

            String audioDuration = "";
            try {
                audioDuration = DMPlayerUtility.getAudioDuration(Long.parseLong(mDetail.getDuration()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            mViewHolder.textViewSongArtistNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getArtist());
            mViewHolder.textViewSongName.setText(mDetail.getTitle());
            String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
            imageLoader.displayImage(contentURI, mViewHolder.imageSongThm, options);


//            convertView.setOnClickListener(new View.OnClickListener() {
////
//                @Override
//                public void onClick(View v) {
//
//
////
////                    setResult(RESULT_OK, (new Intent()).setData(Uri.parse(songList.get(position).getPath())));
//////                    setResult(RESULT_OK,(new Intent()).putExtra("songInfo",mDetail.getBytes()));
////                    overridePendingTransition(0, 0);
////                    finish();
//                }
//            });

            return convertView;
        }

        @Override
        public int getCount() {
            return (songList != null) ? songList.size() : 0;
        }

        class ViewHolder {
            TextView textViewSongName;
            ImageView imageSongThm;
            TextView textViewSongArtistNameAndDuration;
            LinearLayout song_row;
        }
    }

    public class MultiSongs implements AbsListView.MultiChoiceModeListener{
        private AbsListView listView;
        private ArrayList<SongDetail> arrayOut = new ArrayList<>();

        public MultiSongs(AbsListView listView){
            this.listView=listView;
        }

        @Override
        public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
            actionMode.setTitle(String.valueOf(listView.getCheckedItemCount()) + " selected");
        }

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.contextbar_multi_select,menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.cab_add){
                SparseBooleanArray selectedSongs = listView.getCheckedItemPositions();
                for(int i=0;i<selectedSongs.size();i++){
                    if(selectedSongs.valueAt(i)){
                        arrayOut.add((SongDetail) listView.getItemAtPosition(i));
                    }
                }
                Bundle result = new Bundle();
                result.putSerializable("songs", arrayOut);
                setResult(RESULT_OK,(new Intent()).putExtras(result));
                finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            finish();
        }
    }

}