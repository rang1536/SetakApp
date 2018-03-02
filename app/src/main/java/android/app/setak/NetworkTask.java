package android.app.setak;

import android.content.ContentValues;
import android.os.AsyncTask;

/**
 * Created by Administrator on 2018-02-18.
 */

public class NetworkTask extends AsyncTask<Void, Void, String> {
    private String url;
    private ContentValues values;

    public NetworkTask(String url, ContentValues values) {
        this.url = url;
        this.values = values;
    }

    @Override
    protected String doInBackground(Void... params) {
        String result; // 요청 결과를 저장할 변수.
        RequestHttpConnection requestHttpURLConnection = new RequestHttpConnection();
        result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}


