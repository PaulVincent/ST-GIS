package edu.kit.gik.STGIS.dataresource;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import edu.kit.gik.STGIS.view3d.ARActivity;
import edu.kit.gik.STGIS.view3d.InteractiveActivity;

public class QueryActivity extends ListActivity {

    private static String[] intentData;
    private static SimpleAdapter adapter;
    private static String chosenTypeName;
    private static String describeURL;
    private static String getURL;

    private static String time;

    private static final String ROW_ID_1 = "NAME";
    private static final String ROW_ID_2 = "TITLE";
    private static final String ROW_ID_3 = "SRS";
    private static final String SEARCH_TAG_GETFEATURE = "gml:posList";
    private static final String SEARCH_TAG_DESCRIBE = "element";
    //	private static int crs = 0;
    public static String serviceResponse;

    public static ProgressBar progressBar;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intentData = getDataFromIntent();
        Log.d("er", Integer.toString(intentData.length));
        for (int i = 0; i < intentData.length; i++) {
            Log.d("i", intentData[i]);
        }

        List<HashMap<String, String>> fillMaps = prepareData4List();

        adapter = new SimpleAdapter(this, fillMaps,
                android.R.layout.simple_list_item_2, new String[]{ROW_ID_1,
                ROW_ID_3}, new int[]{android.R.id.text1,
                android.R.id.text2});

        setListAdapter(adapter);


        setContentView(R.layout.listfeaturetypes);
        progressBar = (ProgressBar) findViewById(R.id.marker_progress);
        progressBar.setVisibility(View.INVISIBLE);

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        registerForContextMenu(getListView());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        final HashMap<String, String> o = (HashMap<String, String>) getListAdapter()
                .getItem(position);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                Calendar myCalendar = Calendar.getInstance();

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-MM-dd'T'HH:mm:ss"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

                time = sdf.format(myCalendar.getTime());

                chosenTypeName = (String) o.get(ROW_ID_1);
                describeURL = WFSSelectionActivity.baseURL +
                        getString(R.string.Describe)
                        + "&"
                        +
                        getString(R.string.Version110)
                        + "&" +
                        getString(R.string.Typename)
                        + chosenTypeName;

                getURL = WFSSelectionActivity.baseURL +
                        getString(R.string.Feature)
                        + "&"
                        +
                        getString(R.string.Version110)
                        + "&" +
                        getString(R.string.Typename)
                        + chosenTypeName + "&OUTPUTFORMAT=GOCAD"
                        + "&" +
                        "TIME=" + time;

                Log.d("describeurl", describeURL);
                Log.d("geturl", getURL);

                readWebpage(getListView());
            }
        };

        new DatePickerDialog(this, date, 1990, 0,
                1).show();
    }

    private List<HashMap<String, String>> prepareData4List() {
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
        for (int i = 0; i < intentData.length; i = i + 2) {
            HashMap<String, String> map = new HashMap<String, String>();
            if (!intentData[i].equalsIgnoreCase("wfs")) {
                map.put(ROW_ID_1, intentData[i]);
                try {
                    ARActivity.epsg = Integer.parseInt(intentData[i + 1].split("EPSG:")[1]);
                    map.put(ROW_ID_3, intentData[i + 1].split("crs:")[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.w("EPSG", " Kein EPSG bestimmbar!");
                }

                fillMaps.add(map);
            }
        }
        return fillMaps;
    }

    private String[] getDataFromIntent() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            intentData = extras
                    .getStringArray(WFSSelectionActivity.FEATURE_TYPE_INFOS);
            return intentData;
        } else {
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "QueryActivity Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.kit.gik.STGIS.dataresource/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "QueryActivity Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.kit.gik.STGIS.dataresource/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public void readWebpage(View view) {
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{describeURL, getURL});
    }

    public String getSearchTag(String url) {
        // TODO Auto-generated method stub
        if (url.contains(getString(R.string.Describe))) {
            return SEARCH_TAG_DESCRIBE;
        } else {
            return SEARCH_TAG_GETFEATURE;
        }
    }

    public static String readInputStreamAsString(InputStream in)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        return buf.toString();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // Kontextmenue entfalten
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wfs_wps_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.WFS_menu_WPS_query:
                performWPSquery(item);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void performWPSquery(MenuItem item) {

        // create query
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                .getMenuInfo();

        final HashMap<String, String> o = (HashMap<String, String>) getListAdapter()
                .getItem(item.getOrder());

        chosenTypeName = o.get(ROW_ID_1);
        String[] objectAndSpace = chosenTypeName.split(":");

        String wpsBaseURL = WFSSelectionActivity.baseURL.toUpperCase().replace("WFS", "WPS");

        String wpsURL = wpsBaseURL +
                getString(R.string.Version100)
                + "&"
                + getString(R.string.TypeWPS)
                + "&"
                + getString(R.string.Execute)
                + "&"
                + getString(R.string.AverageSpeed)
                + "&"
                + getString(R.string.Dateinputs)
                + getString(R.string.Spacename)
                + objectAndSpace[0]
                + ";"
                + getString(R.string.Objectname)
                + objectAndSpace[1]
                + "]";

        Log.d("WPSurl", wpsURL);

        WPSTask task = new WPSTask();
        task.execute(wpsURL);
    }

    private class DownloadWebPageTask extends
            AsyncTask<String, Void, Boolean> {
//		int myProgress;

        @Override
        protected Boolean doInBackground(String... urls) {
            Boolean response = null;
            for (String url : urls) {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse execute;
                try {
                    execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();
                    String res = readInputStreamAsString(content);
                    if (!res.startsWith("<?xml")) {
                        BufferedReader br;
                        br = new BufferedReader(new StringReader(res));
                        response = InteractiveActivity.setTSObject(br);
                    } else {
                        response = false;
                    }
                } catch (ClientProtocolException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return response;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                Intent intent = new Intent(QueryActivity.this,
                        InteractiveActivity.class);

                startActivity(intent);
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(QueryActivity.this, R.string.NOGOCAD,
                        Toast.LENGTH_LONG).show();
            }

        }

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private class WPSTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {

            // query
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(urls[0]);
            String result = null;
            HttpResponse execute;

            try {
                execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();
                result = readInputStreamAsString(content);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return result;
        }

        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        }
    }
}
