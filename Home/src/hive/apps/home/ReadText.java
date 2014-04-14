package hive.apps.home;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class ReadText extends AsyncTask<String, Void, String> {
    private final HttpClient Client = new DefaultHttpClient();
    private String Content;
    private String Error = null;
    
    protected void onPreExecute() {
    }

    protected String doInBackground(String... urls) {
        try {
            HttpGet httpget = new HttpGet(urls[0]);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            //GETTING CONTENT FROM SERVER
            Content = Client.execute(httpget, responseHandler);
        } catch (ClientProtocolException e) {
            Error = e.getMessage();
            cancel(true);
        } catch (IOException e) {
            Error = e.getMessage();
            cancel(true);
        }
        //RETURNING IT SO WE CAN ACCESS IT VIA .get()
        return Content;
    }
    
    protected void onPostExecute(Void unused) {
        
    }
    
}