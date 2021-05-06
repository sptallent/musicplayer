import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AcoustAPIHelper {

    final String API_KEY = "API_KEY";
    final private String acoust_base_url = "https://api.acoustid.org/v2/lookup";


    public AcoustAPIHelper() {

    }

    public void getRequest(String dur, String fp) {
        String temp_url = acoust_base_url + "?client=" + API_KEY + "&meta=recordingids&format=json&duration=" + dur + "&fingerprint=" + fp;
        new AcoustThread().execute(temp_url);
    }

}

class AcoustThread extends AsyncTask<String, String, MusicInfo> {
    MusicInfoGrabber musicInfoGrabber;

    @Override
    protected MusicInfo doInBackground(String... strings) {
        JSONObject jsonObject;
        musicInfoGrabber = new MusicInfoGrabber();
        MusicInfo mi = new MusicInfo();
        String mbid = "";
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.addRequestProperty("User-Agent", "Music Player(Android)");

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            jsonObject = new JSONObject(sb.toString());
            String mbid_map;

            mbid_map = jsonObject.getJSONArray("results").getJSONObject(0).getJSONArray("recordings").getString(0);
            mbid = (mbid_map.substring(mbid_map.indexOf("id")+5, mbid_map.indexOf("\"}")));
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }

        if(!mbid.isEmpty()) {
            System.out.println("mbid: " + mbid);
            try {
                mi = musicInfoGrabber.getMusicBrainzSongInfo(mbid);
            }catch(Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        System.out.print("Found info: " + mi.toString());
        return mi;
    }

    @Override
    protected void onPostExecute(MusicInfo mi) {
        super.onPostExecute(mi);

        /*if(mi.foundData())
            System.out.print("Found info: " + mi.toString());*/
    }
}


