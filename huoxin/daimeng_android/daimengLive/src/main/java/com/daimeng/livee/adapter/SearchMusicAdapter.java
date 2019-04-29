package com.daimeng.livee.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bese.CircleProgressBar;
import com.daimeng.livee.AppConfig;
import com.daimeng.livee.AppContext;
import com.daimeng.livee.R;
import com.daimeng.livee.bean.MusicBean;
import com.daimeng.livee.fragment.SearchMusicDialogFragment;
import com.daimeng.livee.utils.DBManager;
import com.daimeng.livee.widget.BlackTextView;
//import com.daimeng.livee.AppConfig;
//import com.daimeng.livee.bean.MusicBean;
//import com.daimeng.livee.fragment.SearchMusicDialogFragment;
//import com.daimeng.livee.utils.DBManager;
//import com.daimeng.livee.AppContext;
//import com.daimeng.livee.R;
//import com.daimeng.livee.widget.BlackTextView;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 搜索音乐列表
 */
public class SearchMusicAdapter extends BaseAdapter {

    private List<MusicBean> mMusicList;
    private SearchMusicDialogFragment mFragment;
    private DBManager mDbManager;

    public SearchMusicAdapter(List<MusicBean> MusicList, SearchMusicDialogFragment fragment, DBManager dbManager){
        this.mMusicList =  MusicList;
        this.mFragment = fragment;
        this.mDbManager = dbManager;
    }
    public void notifyDataSetChangedMusicList(List<MusicBean> MusicList){
        this.mMusicList =  MusicList;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        convertView = View.inflate(AppContext.getInstance(), R.layout.item_search_music,null);
        viewHolder.mMusicName = (BlackTextView) convertView.findViewById(R.id.item_tv_search_music_name);
        viewHolder.mMusicAuthor = (BlackTextView) convertView.findViewById(R.id.item_tv_search_music_author);
        viewHolder.cpProgress = convertView.findViewById(R.id.cp_progress);

//        viewHolder.mBtnDownload = (CircularProgressButton) convertView.findViewById(R.id.item_btn_search_music_download);
        final MusicBean music = mMusicList.get(position);
        viewHolder.mMusicName.setText(music.audio_name);
        viewHolder.mMusicAuthor.setText(music.artist_name);
        final File file = new File(AppConfig.DEFAULT_SAVE_MUSIC_PATH + music.audio_name + ".mp3");
        //判断该音乐是否存在
        if(mDbManager.queryFromEncryptedSongId(music.audio_id).getCount() != 0){
//            viewHolder.mBtnDownload.setText(R.string.select);
        }
        //点击下载或播放



//        viewHolder.mBtnDownload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        viewHolder.cpProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CircleProgressBar.Status status = viewHolder.cpProgress.getStatus();
                switch (status) {
                    case Waiting:
                        viewHolder.cpProgress.setStatus(CircleProgressBar.Status.Loading);
                        viewHolder.timer = new Timer();
                        viewHolder.task = new TimerTask() {
                            @Override
                            public void run() {
                                final int progress = viewHolder.cpProgress.getProgress();
                                if (progress >= 100) {
                                    viewHolder.timer.cancel();
                                    viewHolder.cpProgress.setStatus(CircleProgressBar.Status.Finish);
                                } else {
                                    viewHolder.cpProgress.setProgress(progress);
                                }
                            }
                        };
                        viewHolder.timer.schedule(viewHolder.task, 0, 100);
                        break;
                    case Loading:
                        viewHolder.timer.cancel();
                        viewHolder.cpProgress.setStatus(CircleProgressBar.Status.Pause);
                        break;
                    case Pause:
                        viewHolder.cpProgress.setStatus(CircleProgressBar.Status.Loading);
                        viewHolder.timer = new Timer();
                        viewHolder.task = new TimerTask() {
                            @Override
                            public void run() {
                                final int progress = viewHolder.cpProgress.getProgress();
                                if (progress >= 100) {
                                    viewHolder.timer.cancel();
                                    viewHolder.cpProgress.setStatus(CircleProgressBar.Status.Finish);
                                } else {
                                    viewHolder.cpProgress.setProgress(progress);
                                }
                            }
                        };
                        viewHolder.timer.schedule(viewHolder.task, 0, 100);
                        break;
                    case Error:
                        viewHolder.cpProgress.setStatus(CircleProgressBar.Status.Loading);
                        viewHolder.timer = new Timer();
                        viewHolder.task = new TimerTask() {
                            @Override
                            public void run() {
                                final int progress = viewHolder.cpProgress.getProgress();
                                if (progress >= 100) {
                                    viewHolder.timer.cancel();
                                    viewHolder.cpProgress.setStatus(CircleProgressBar.Status.Finish);
                                } else {
                                    viewHolder.cpProgress.setProgress(progress);
                                }
                            }
                        };
                        viewHolder.timer.schedule(viewHolder.task, 0, 100);
                        break;
                    case Finish:
                        viewHolder.cpProgress.setProgress(0);
                        viewHolder.cpProgress.setStatus(CircleProgressBar.Status.Waiting);
                        break;
                }
            }
        });
        return convertView;
    }

    class ViewHolder{
        CircleProgressBar cpProgress;
        Timer timer;
        BlackTextView mMusicName,mMusicAuthor;
        TimerTask task;
//        CircularProgressButton mBtnDownload;
    }
}
