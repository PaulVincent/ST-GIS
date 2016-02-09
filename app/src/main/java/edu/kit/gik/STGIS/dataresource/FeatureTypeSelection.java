package edu.kit.gik.STGIS.dataresource;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import edu.kit.gik.STGIS.view3d.ARActivity;
import edu.kit.gik.STGIS.view3d.InteractiveActivity;

public class FeatureTypeSelection extends ListActivity {

	private static String[] intentData;
	private static SimpleAdapter adapter;
	private static String chosenTypeName;
	private static String describeURL;
	private static String getURL;

	private static final String ROW_ID_1 = "NAME";
	private static final String ROW_ID_2 = "TITLE";
	private static final String ROW_ID_3 = "SRS";
	private static final String SEARCH_TAG_GETFEATURE = "gml:posList";
	private static final String SEARCH_TAG_DESCRIBE = "element";
//	private static int crs = 0;
	public static String serviceResponse;
	
	public static ProgressBar progressBar;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		intentData = getDataFromIntent();
		Log.d("er",Integer.toString(intentData.length));
		for ( int i = 0; i < intentData.length; i++) {
			Log.d("i",intentData[i]);
		}
		List<HashMap<String, String>> fillMaps = prepareData4List();

		adapter = new SimpleAdapter(this, fillMaps,
				android.R.layout.simple_list_item_2, new String[] { ROW_ID_1,
				 ROW_ID_3 }, new int[] { android.R.id.text1,
				android.R.id.text2 });

		setListAdapter(adapter);
		
		
		setContentView(R.layout.listfeaturetypes);
		progressBar = (ProgressBar)findViewById(R.id.marker_progress);
		progressBar.setVisibility(View.INVISIBLE);

//	    progressBar.setProgress(0);
		
//		setContentView(R.layout.listfeaturetypes);
		// getListView().setOnItemClickListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		HashMap<String, String> o = (HashMap<String, String>) getListAdapter()
				.getItem(position);
		chosenTypeName = (String) o.get(ROW_ID_1);
		describeURL = WFSSelection.baseURL + getString(R.string.Describe) + "&"
				+ getString(R.string.Version110) + "&" + getString(R.string.Typename)
				+ chosenTypeName;

		getURL = WFSSelection.baseURL + getString(R.string.Feature) + "&"
				+ getString(R.string.Version110) + "&" + getString(R.string.Typename)
				+ chosenTypeName + "&OUTPUTFORMAT=GOCAD";
		Log.d("describeurl", describeURL);
		Log.d("geturl", getURL);
		readWebpage(getListView());
	}

	private List<HashMap<String, String>> prepareData4List() {
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < intentData.length; i = i + 2) {
			HashMap<String, String> map = new HashMap<String, String>();
//			Log.d("epsg", intentData[i]);
			if (!intentData[i].equalsIgnoreCase("wfs")) {
			map.put(ROW_ID_1, intentData[i]);
			//map.put(ROW_ID_2, intentData[i].split(":")[1]);
			try {
			ARActivity.epsg = Integer.parseInt(intentData[i+1].split("EPSG:")[1]);
			map.put(ROW_ID_3, intentData[i+1].split("crs:")[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				Log.w("EPSG"," Kein EPSG bestimmber!");
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
					.getStringArray(WFSSelection.FEATURE_TYPE_INFOS);
			return intentData;
		} else {
			return null;
		}
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
				Intent intent = new Intent(FeatureTypeSelection.this,
						InteractiveActivity.class);
//				intent.putExtra(getString(R.string.TSObject), result);
//				intent.putExtra("ResourceType", "WFS");
				startActivity(intent);
				progressBar.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				Toast.makeText(FeatureTypeSelection.this, R.string.NOGOCAD,
						Toast.LENGTH_LONG).show();
			}

		}
	
		protected void onPreExecute() {
			   progressBar.setVisibility(View.VISIBLE);
			  }
	}

	public void readWebpage(View view) {
		DownloadWebPageTask task = new DownloadWebPageTask();
		task.execute(new String[] { describeURL, getURL });
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
		    while(result != -1) {
		      byte b = (byte)result;
		      buf.write(b);
		      result = bis.read();
		    }
		    return buf.toString();
		}
}
