import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MusicInfoGrabber {

    private final String base_url = "https://musicbrainz.org/ws/2/";
    private final String mb_entity = "recording/";
    private final String mb_params = "?inc=releases+artists+media&fmt=json";
    private final String ca_base_url = "http://coverartarchive.org/release/";

    public MusicInfoGrabber() {

    }

    public MusicInfo getMusicBrainzSongInfo(String song_mbid) {
        MusicInfo musicInfo = new MusicInfo();
        try {
            URL url = new URL(base_url+mb_entity+song_mbid+mb_params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.addRequestProperty("User-Agent", "Jolly Music Player(Android)");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            JSONObject mbJSONObj = new JSONObject(sb.toString());

            String song_title = mbJSONObj.getString("title");

            JSONArray jsonArtistArray = mbJSONObj.getJSONArray("artist-credit");
            JSONObject mbArtistObj = (JSONObject) jsonArtistArray.get(0);

            String artist_name = mbArtistObj.getString("name");
            String artist_mbid = mbArtistObj.getJSONObject("artist").getString("id");

            JSONArray jsonReleasesArray = mbJSONObj.getJSONArray("releases");
            JSONObject mbReleaseObj = (JSONObject) jsonReleasesArray.get(0);

            String album_title = mbReleaseObj.getString("title");
            String album_mbid = mbReleaseObj.getString("id");
            String track_number = ((JSONObject)(((JSONObject)mbReleaseObj.getJSONArray("media").get(0)).getJSONArray("tracks").get(0))).getString("number");
            int album_track_count = (((JSONObject)mbReleaseObj.getJSONArray("media").get(0)).getInt("track-count"));

            if(!song_mbid.isEmpty())
                musicInfo.setSong_mbid(song_mbid);
            if(!song_title.isEmpty())
                musicInfo.setSong_title(song_title);
            if(!artist_name.isEmpty())
                musicInfo.setArtist_name(artist_name);
            if(!artist_mbid.isEmpty())
                musicInfo.setArtist_mbid(artist_mbid);
            if(!album_title.isEmpty())
                musicInfo.setAlbum_title(album_title);
            if(!album_mbid.isEmpty())
                musicInfo.setAlbum_mbid(album_mbid);
            if(!track_number.isEmpty())
                musicInfo.setTrack_number(track_number);
            if(album_track_count>0)
                musicInfo.setAlbum_track_count(album_track_count);
            String album_art;
            if(!album_mbid.isEmpty()) {
                album_art = getAlbumCoverArt(album_mbid);
                if (!album_art.isEmpty())
                    musicInfo.setAlbum_art(album_art);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return musicInfo;
    }

    public String getAlbumCoverArt(String album_mbid) {
        String cover_art = "";
        try {
            URL ca_url = new URL(ca_base_url + album_mbid);
            HttpURLConnection ca_conn = (HttpURLConnection) ca_url.openConnection();
            ca_conn.setRequestMethod("GET");
            ca_conn.setInstanceFollowRedirects(true);
            ca_conn.setRequestProperty("Content-Type", "application/json; utf-8");

            BufferedReader ca_br = new BufferedReader(new InputStreamReader(ca_conn.getInputStream()));
            String ca_line;
            StringBuilder ca_sb = new StringBuilder();
            while ((ca_line = ca_br.readLine()) != null) {
                ca_sb.append(ca_line);
            }
            JSONObject caJSONObj = new JSONObject(ca_sb.toString());
            JSONObject caImageObj = (JSONObject) caJSONObj.getJSONArray("images").get(0);
            if (caImageObj.getBoolean("front")) {
                cover_art = caImageObj.getString("image");
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return cover_art;
    }
}
