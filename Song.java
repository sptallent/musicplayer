import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class Song implements Serializable {

    private long id;
    private String title;
    private String artist;
    private String duration;
    private long album_ID;
    private String album_art;
    private String dateAdded;

    private String mbid;
    private String album_mbid;
    private String artist_mbid;

    private String track_number;

    private Calendar calendar;
    private Date date;
    private Date createdTime;



    public Song(long songID, String songTitle, String songArtist, String songDuration, long alID) {
        id = songID;
        title = songTitle;
        artist = songArtist;
        duration = songDuration;
        album_ID = alID;
        calendar = Calendar.getInstance();
        createdTime = calendar.getTime();
        album_art = "";
    }

    public long getAlbum_ID() {
        return album_ID;
    }

    public long getId() {
        return id;
    }

    public String getMbid() {
        return mbid;
    }

    public String getArtist_mbid() {
        return artist_mbid;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public String getAlbum_mbid() {
        return album_mbid;
    }

    public Date getDate() {
        return date;
    }

    public String getTrack_number() {
        return track_number;
    }



    public long getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public long getAlbumID() { return album_ID; }

    public String getDateAdded() {
        return dateAdded;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public boolean isNull() {
        if(getDuration() == null)
            return true;
        return getDuration().isEmpty();
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setQueuedTime() {
        date = calendar.getTime();
    }

    public long getQueuedTime() {
        return date.getTime();
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setMbid(String mbid) {
        this.mbid = mbid;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum_ID(long album_ID) {
        this.album_ID = album_ID;
    }

    public void setArtist_mbid(String artist_mbid) {
        this.artist_mbid = artist_mbid;
    }

    public void setAlbum_mbid(String album_mbid) {
        this.album_mbid = album_mbid;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setTrack_number(String track_number) {
        this.track_number = track_number;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public long getCreatedTime() {
        return createdTime.getTime();
    }

}
