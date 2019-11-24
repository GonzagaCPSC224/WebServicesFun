/**
 * download JSON jar file from: https://mvnrepository.com/artifact/org.json/json/20190722
 */

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrAPI {
    // wrapper class for all of our work with the flickr API
    static final String BASE_URL = "https://api.flickr.com/services/rest";
    static final String API_KEY = "1e62fea963ae2caf0854ae1be8fee7fd"; // BAD PRACTICE!!!
    // don't put your API key in your code!!

    Controller controller; // for callbacks when our asynchronous code has results

    public FlickrAPI(Controller c) {
        controller = c;
    }

    public void fetchInterestingPhotos() {
        // construct a url for the request
        String url = createInterestingPhotosListURL();
        System.out.println("fetchInterestingPhotos: " + url);

        // start a background thread to carry out the request
        // we are going to define a subclass of AsyncTask
        // asynchronous means doesn't wait/block
        FetchInterestingPhotosThread fetchInterestingPhotosThread = new FetchInterestingPhotosThread(url);
        fetchInterestingPhotosThread.start();
    }

    public String createInterestingPhotosListURL() {
        String url = BASE_URL;
        url += "?method=flickr.interestingness.getList";
        url += "&api_key=" + API_KEY;
        url += "&format=json";
        url += "&nojsoncallback=1"; // raw json data
        url += "&extras=date_taken,url_h";

        return url;
    }

    // execute long running tasks on background threads
    // fetch the interesting photo information and parse it on a background thread
    // so it doesn't block the main GUI event thread (AKA event dispatch thread)
    // see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html
    class FetchInterestingPhotosThread extends Thread {
        String urlStr;

        public FetchInterestingPhotosThread(String urlStr) {
            this.urlStr = urlStr;
        }

        public void run() {
            // runs on a background thread
            // CANNOT update the UI
            List<InterestingPhoto> interestingPhotoList = new ArrayList<>();
            // goals
            // 1. open url request for JSON response
            // 2. download the JSON response
            // 3. parse the JSON to build a list of InterestingPhoto

            try {
                // 1. open url
                URL url = new URL(urlStr);
                // try to connect using the HTTP protocol to the url
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                // 2. download JSON as string
                // build character by character
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                String resultStr = "";
                int data = reader.read();
                while (data != -1) { // read() returns -1 when end of stream reached
                    resultStr += (char) data;
                    data = reader.read();
                }
                System.out.println("doInBackground: " + resultStr);

                // 3. parse the JSON
                JSONObject jsonObject = new JSONObject(resultStr);
                JSONObject photosObject = jsonObject.getJSONObject("photos"); // photos is the key
                JSONArray photoArray = photosObject.getJSONArray("photo"); // photo is the key
                for (int i = 0; i < photoArray.length(); i++) {
                    // get the JSONobject for the object at index i
                    JSONObject singlePhotoObject = photoArray.getJSONObject(i);
                    // try to parse out the id, title, datetaken, url_h
                    InterestingPhoto interestingPhoto = parseInterestingPhoto(singlePhotoObject);
                    if (interestingPhoto != null) {
                        interestingPhotoList.add(interestingPhoto);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            controller.receivedInterestingPhotos(interestingPhotoList);
        }

        public InterestingPhoto parseInterestingPhoto(JSONObject singlePhotoObject) {
            InterestingPhoto interestingPhoto = null;

            try {
                String id = singlePhotoObject.getString("id"); // id is the key
                // task: repeat for remaining 3 fields and create an InterestingPhoto
                String title = singlePhotoObject.getString("title");
                String dateTaken = singlePhotoObject.getString("datetaken");
                String photoURL = singlePhotoObject.getString("url_h");
                interestingPhoto = new InterestingPhoto(id, title, dateTaken, photoURL);
            } catch (JSONException e) {
                // do nothing
            }

            return interestingPhoto;
        }
    }

    public void fetchPhotoImage(String photoURL) {
        FetchImageThread fetchImageThread = new FetchImageThread(photoURL);
        fetchImageThread.start();
    }

    // execute long running tasks on background threads
    // fetch the image for an interesting photo on a background thread
    // so it doesn't block the main GUI event thread (AKA event dispatch thread)
    // see https://docs.oracle.com/javase/tutorial/uiswing/concurrency/dispatch.html
    class FetchImageThread extends Thread {
        String urlStr;

        public FetchImageThread(String urlStr) {
            this.urlStr = urlStr;
        }

        public void run() {
            // runs on a background thread
            // CANNOT update the UI
            List<InterestingPhoto> interestingPhotoList = new ArrayList<>();
            // goal: download image from photoURL

            try {
                URL url = new URL(urlStr);
                // https://www.java-tips.org/java-se-tips-100019/40-javax-imageio/1725-how-to-read-an-image-from-a-file-inputstream-or-url.html
                Image image = ImageIO.read(url);
                controller.receivedPhotoImage(image);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
