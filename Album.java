import java.util.ArrayList;

public class Album {

    private String name;
    private String artist;
    private long id;
    ArrayList<Song> albumSongs;

    private String mbid;
    private String artist_mbid;
    private int track_count;
    private String album_art;

    public Album(String albumName, String albumArtist, long albumID) {
        name = albumName;
        artist = albumArtist;
        id = albumID;
        albumSongs = new ArrayList<>();
    }

    public String getAlbum_art() {
        return album_art;
    }

    public int getTrack_count() {
        return track_count;
    }

    public String getArtist_mbid() {
        return artist_mbid;
    }

    public String getMbid() {
        return mbid;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbumSongs(ArrayList<Song> albumSongs) {
        this.albumSongs = albumSongs;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public void setArtist_mbid(String artist_mbid) {
        this.artist_mbid = artist_mbid;
    }

    public void setTrack_count(int track_count) {
        this.track_count = track_count;
    }

    public long getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + artist + " " + String.valueOf(id);
    }

    public ArrayList<Song> getAlbumSongs() {
        return albumSongs;
    }

    public void addSong(Song s) {
        albumSongs.add(s);
    }

    public void removeSong(int index) {
        albumSongs.remove(index);
    }
}
