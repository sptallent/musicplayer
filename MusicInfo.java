public class MusicInfo {

    String song_mbid;
    String artist_mbid;
    String album_mbid;

    String song_title;
    String artist_name;
    String album_title;

    String track_number;
    int album_track_count;

    String album_art;

    public MusicInfo() {

    }

    @Override
    public String toString() {
        return "Song Title: " + song_title + "\nSong MBID: " + song_mbid + "\nTrack Number: " + track_number + "\nArtist Name: " + artist_name + "\nArtist MBID: " + artist_mbid + "\nAlbum Title : " + album_title +
                "\nAlbum MBID: " + album_mbid + "\nAlbum Track Count: " + album_track_count + "\nAlbum Art: " + album_art + "\n";
    }

    public String getSong_mbid() {
        return song_mbid;
    }

    public String getArtist_mbid() {
        return artist_mbid;
    }

    public String getAlbum_mbid() {
        return album_mbid;
    }

    public String getSong_title() {
        return song_title;
    }

    public String getArtist_name() {
        return artist_name;
    }

    public String getAlbum_title() {
        return album_title;
    }

    public String getTrack_number() {
        return track_number;
    }

    public int getAlbum_track_count() {
        return album_track_count;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setSong_mbid(String song_mbid) {
        this.song_mbid = song_mbid;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public void setArtist_mbid(String artist_mbid) {
        this.artist_mbid = artist_mbid;
    }

    public void setTrack_number(String track_number) {
        this.track_number = track_number;
    }

    public void setAlbum_mbid(String album_mbid) {
        this.album_mbid = album_mbid;
    }

    public void setAlbum_title(String album_title) {
        this.album_title = album_title;
    }

    public void setAlbum_track_count(int album_track_count) {
        this.album_track_count = album_track_count;
    }

    public void setArtist_name(String artist_name) {
        this.artist_name = artist_name;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public boolean foundData() {
        if(!getSong_mbid().isEmpty())
            return true;
        return false;
    }
}
