import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;

import musicplayer.MusicUtils.MusicUtils;
import musicplayer.MusicUtils.Song;

public class MusicService extends android.app.Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {

    private boolean shuffle=false;
    private boolean loop = false;
    private Random rand;

    private long songID = 0;
    private String songTitle= "";
    private String songArtist= "";
    private long albumID;
    private String songDur;

    long queuedTime;

    private static final int NOTIFY_ID=1;
    private MediaPlayer player;
    private MediaSessionManager mediaManager;
    private MediaSession mediaSession;
    private MediaController mediaController;
    private ArrayList<Song> songs;
    private ArrayList<Song> songsQueue;
    private ArrayList<Song> songsPrevious;
    MusicUtils musicUtils;
    private int songPosn;
    private final IBinder musicBind = new MusicBinder();
    private int myBufferPosition;
    boolean timerWait;

    final public static String ACTION_PLAY = "action_play";
    final public static String ACTION_PAUSE = "action_pause";
    final public static String ACTION_NEXT = "action_next";
    final public static String ACTION_PREV = "action_prev";
    final public static String ACTION_STOP = "action_stop";

    Intent playIntent;

    private BroadcastReceiver songViewDeletedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long song_id = intent.getLongExtra("song_id", -1);
            songsQueue = musicUtils.removeSongFromList((int)song_id, songsQueue);
            songs = musicUtils.removeSongFromList((int)song_id, songs);
            songsPrevious = musicUtils.removeSongFromList((int)song_id, songsPrevious);
        }
    };

    private BroadcastReceiver addPlayNextReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Song song_obj = (Song)intent.getSerializableExtra("song_obj");
            addQueue(song_obj);
        }
    };

    private BroadcastReceiver songCheckDeletedReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long song_id = intent.getLongExtra("song_id", -1);
            if(songPosn != -1) {
                if (song_id != -1 && songs.size() > 1 && (songs.get(songPosn).getID() == song_id)) {
                    playNext();
                }
            }
        }
    };

    public void onCreate() {
        super.onCreate();
        timerWait = true;
        songPosn = 0;
        player = new MediaPlayer();
        rand=new Random();
        musicUtils = new MusicUtils(getContentResolver());
        this.songsQueue = new ArrayList<>();
        this.songsPrevious = new ArrayList<>();
        this.queuedTime = -1;

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(songViewDeletedReciever, new IntentFilter("songViewDeleted"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(addPlayNextReciever, new IntentFilter("addPlayNext"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(songCheckDeletedReciever, new IntentFilter("checkDeletedSongPlaying"));

        playIntent = new Intent(getApplicationContext(), MusicService.class);
        initMusicPlayer();
    }

    private void handleIntent( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;

        String action = intent.getAction();

        if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            mediaController.getTransportControls().play();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            mediaController.getTransportControls().pause();
        } else if( action.equalsIgnoreCase( ACTION_PREV ) ) {
            mediaController.getTransportControls().skipToPrevious();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            mediaController.getTransportControls().skipToNext();
        } else if( action.equalsIgnoreCase( ACTION_STOP ) ) {
            mediaController.getTransportControls().stop();
        }
    }

    private Notification.Action generateAction(int icon, String title, String intentAction) {
        Intent intent = new Intent( getApplicationContext(), MusicService.class );
        intent.setAction(intentAction);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new Notification.Action.Builder( icon, title, pendingIntent ).build();
    }

    private void buildNotification(Notification.Action action) {
        Notification.MediaStyle style = new Notification.MediaStyle();

        Intent intent = new Intent(getApplicationContext(), MusicService.class);
        intent.setAction(ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(songTitle)
                .setContentText(songArtist)
                //.setLargeIcon(musicUtils.getAlbumArt(albumID))
                .setDeleteIntent(pendingIntent)
                .setStyle(style);

        builder.addAction(generateAction(R.drawable.prev, "Previous", ACTION_PREV));
        builder.addAction(action);
        builder.addAction(generateAction(R.drawable.next, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0,1,2,3,4);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mediaManager == null)
            initMediaSessions();

        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMediaSessions() {
        mediaSession = new MediaSession(getApplicationContext(), "simple player session");
        mediaController = new MediaController(getApplicationContext(), mediaSession.getSessionToken());

        mediaSession.setCallback(new MediaSession.Callback(){
                                 @Override
                                 public void onPlay() {
                                     super.onPlay();

                                     playSong();
                                     buildNotification(generateAction(R.drawable.pausebutton, "Pause", ACTION_PAUSE));

                                 }

                                 @Override
                                 public void onPause() {
                                     super.onPause();
                                     pausePlayer();
                                     buildNotification(generateAction(R.drawable.playbutton, "Play", ACTION_PLAY));
                                 }

                                 @Override
                                 public void onSkipToNext() {
                                     super.onSkipToNext();
                                     playNext();
                                     buildNotification(generateAction(R.drawable.pausebutton, "Pause", ACTION_PAUSE));
                                 }

                                 @Override
                                 public void onSkipToPrevious() {
                                     super.onSkipToPrevious();
                                     playPrev();
                                     buildNotification(generateAction(R.drawable.pausebutton, "Pause", ACTION_PAUSE));
                                 }

                                 @Override
                                 public void onStop() {
                                     super.onStop();
                                     pausePlayer();
                                     NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                     notificationManager.cancel(1);
                                     Intent intent = new Intent( getApplicationContext(), MusicService.class);
                                     stopService(intent);
                                 }

                                 @Override
                                 public void onSeekTo(long pos) {
                                     super.onSeekTo(pos);
                                 }

                             }
        );
    }

    public void sendLocalBroadcast(Intent intent) {
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.playbutton)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        //Notification not = builder.build();
        //startForeground(NOTIFY_ID, not);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        mediaSession.release();
        player.stop();
        player.release();

        return false;
    }

    public void playSong() {
        player.reset();

        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songID);

        try{
            player.setDataSource(getApplicationContext(), trackUri);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();
        updateCrap();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        setBufferPosition(percent * getDur() / 100);
    }

    public void setSong(Song s) {
        songID = s.getID();
        songTitle = s.getTitle();
        songArtist = s.getArtist();
        songDur = s.getDuration();
        albumID = s.getAlbumID();
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
        setSong(songs.get(songPosn));
    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev() {
        Song s;
        if ((getCurrentProgress() / 1000) < 5) {
            //less than 5 seconds so we have to change to prev song
            if (songsPrevious.isEmpty()) {
                songPosn--;
                if (songPosn < 0)
                    songPosn = songs.size() - 1;
                s = songs.get(songPosn);
            }else{
                s = songsPrevious.get(0);
                songsPrevious.remove(0);
            }
            setSong(s);
        }
        playSong();
    }

    public void playNext() {
        if (!loop) {
            songsPrevious.add(0, new Song(songID, songTitle, songArtist, songDur, albumID));
            if (!songsQueue.isEmpty()) {
                if (queuedTime != -1) {
                    for (int i = 0; i < songsQueue.size(); i++) {
                        if (songsQueue.get(i).getQueuedTime() == queuedTime) {
                            songsQueue.remove(i);
                            break;
                        }
                    }
                }
                if(!songsQueue.isEmpty()){
                        Song songQueued = songsQueue.get(0);
                        queuedTime = songQueued.getQueuedTime();
                        setSong(songQueued);
                        songsQueue.remove(0);
                    }
            }else if (shuffle) {
                int newSong = songPosn;
                while (newSong == songPosn) {
                    newSong = rand.nextInt(songs.size());
                }
                songPosn = newSong;
                setSong(songs.get(songPosn));
            } else {
                songPosn++;
                if (songPosn >= songs.size())
                    songPosn = 0;
                setSong(songs.get(songPosn));
            }
        }
        playSong();
    }

    public void toggleShuffle(boolean shuff){
        shuffle = shuff;
    }

    public void toggleLoop(boolean loo) {
        loop = loo;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public boolean isLoop() {
        return loop;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public int getCurrentProgress() {
        return player.getCurrentPosition();
    }

    protected void setBufferPosition(int progress) {
        myBufferPosition = progress;
    }

    protected int getBufferPosition() {
        return myBufferPosition;
    }

    public int getSongPosn() {
        return songPosn;
    }

    public void updateCrap() {
        Intent intent = new Intent("sendUpdate");
        intent.putExtra("title", songTitle);
        intent.putExtra("artist", songArtist);
        intent.putExtra("album_id", albumID);
        intent.putExtra("dur", String.valueOf(getBufferPosition()));

        sendLocalBroadcast(intent);
    }

    public ArrayList<Song> getSongList() {
        return songs;
    }
    public void setSongList(ArrayList<Song> s) {
        songs = s;
    }

    public Song getSongListNext(int offset) {
        /*if (offset <= 0)// this condition and result duplicates songs on the queue list
            return getSongNext();*/
        int temp = songPosn;
        while(offset>0) {
            if((temp+1) < songs.size()-1)
                temp++;
            else
                temp = 0;
            offset--;
        }
        if(temp < 0 || temp > (songs.size()-1))
            temp = songPosn;//0
        return songs.get(temp);
    }

    public Song getSongNext() {
        int nextPos;
        if(songsQueue.isEmpty()) {
            nextPos = songPosn + 1;
            if (nextPos > songs.size()) {
                nextPos = 0;
            }
        }else{
            return songsQueue.get(0);
        }
        return songs.get(nextPos);
    }

    public Song getSongPrev() {
        int prevPos;
        if(songsPrevious.isEmpty()) {
            prevPos = songPosn - 1;
            if (prevPos < 0)
                prevPos = songs.size() - 1;
        }else{
            return songsPrevious.get(0);
        }
        return songs.get(prevPos);
    }

    public void addQueue(Song s) {
        s.setQueuedTime();
        songsQueue.add(0, s);
    }

    public String getSongDur() {
        return songDur;
    }

    public ArrayList<Song> getSongsQueue() {
        return songsQueue;
    }

    public void setSongsQueue(ArrayList<Song> sq) {
        songsQueue = sq;
    }

    public void clearQueue() {
        songsQueue.clear();
    }

}
