package edu.kit.gik.STGIS.dataresource;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import edu.kit.gik.STGIS.database.SQLiteOnSD;
import edu.kit.gik.STGIS.xml.XMLHandler;

public class WFSSelection extends ListActivity {

	/**
	 * Handler to access the SQLite database of the stored web feature services
	 */
	private static SQLiteOnSD openHandler;

	/**
	 * Adapter to fill the ListView with the entries of the SQLite database
	 */
	private static SimpleCursorAdapter mAdapter;

	/**
	 * Button to open the dialog to add an new web feature service
	 */
	private Button addWFS;

	/**
	 * String to hold the name of the intent data passed on to the called
	 * activity
	 */
	public static final String FEATURE_TYPE_INFOS = "FEATURETYPEINFOS";

	/**
	 * String to hold the base URL of the chosen web feature service
	 */
	public static String baseURL;

	/**
	 * String array to hold the provided feature types of the chosen web feature
	 * service
	 */
	public static String[] serviceResponse;
	
	public static ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		openHandler = new SQLiteOnSD(this);

		mAdapter = initAdapter();

		setListAdapter(mAdapter);

		setContentView(R.layout.listviewwithbutton);
		progressBar = (ProgressBar) findViewById(R.id.marker_progress1);
		progressBar.setVisibility(View.INVISIBLE);
		addWFS = (Button) findViewById(R.id.AddWFSButton);
		addWFS.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showDialog(R.string.DIALOG_ADDWFS);
			}
		});

		registerForContextMenu(getListView());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		openHandler.close();
	}
	
	private SimpleCursorAdapter initAdapter() {

		Cursor data = openHandler.query();

		return new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_2, data, new String[] {
						SQLiteOnSD.WFS_NAME, SQLiteOnSD.WFS_URL }, new int[] {
						android.R.id.text1, android.R.id.text2 }, 0);
	}

	private void deleteRow(long id) {
		if (openHandler.delete(id) != 0) {
			Toast.makeText(this, R.string.DELETED, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, R.string.FAILED, Toast.LENGTH_LONG).show();
		}
	}

	private void updateList() {
		// zun�chst Cursor, dann Liste aktualisieren
		mAdapter.getCursor().requery();
		mAdapter.notifyDataSetChanged();
	}
	
	public void onListItemClick(ListView l, View view, int position, long id) {

		Object o = getListAdapter().getItem(position);
		Cursor w = (Cursor) o;
		int columnIndex = w.getColumnIndex(SQLiteOnSD.WFS_URL);
		baseURL = w.getString(columnIndex);
		if (!baseURL.startsWith(getString(R.string.HTTP))) {
			baseURL = getString(R.string.HTTP) + baseURL;
		}
		Log.d("baseurl", baseURL);
		ConnectivityManager cm =
		        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		    NetworkInfo netInfo = cm.getActiveNetworkInfo();
//		    Log.d("test", Boolean.toString(netInfo.isConnectedOrConnecting()));
		    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
		    	readWebpage(l);
		    } else {
		    	Toast.makeText(this, R.string.NOWEB, Toast.LENGTH_LONG).show();
		    	startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		    }
		
	}

	protected Dialog onCreateDialog(int id) {

		switch (id) {
		case R.string.DIALOG_ADDWFS:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			View addWFSView = getLayoutInflater().inflate(
					R.layout.addwfsdialog, null);
			builder.setView(addWFSView);
			final EditText wfsName = (EditText) addWFSView
					.findViewById(R.id.WFSName);
			final EditText wfsBaseURL = (EditText) addWFSView
					.findViewById(R.id.WFSBaseURL);
			builder.setTitle(R.string.WFSDialogTitle);
			builder.setPositiveButton(R.string.AddWFSButtonText,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							openHandler.insert(wfsName.getText().toString(),
									wfsBaseURL.getText().toString());
							updateList();
						}
					});

			builder.setNegativeButton(R.string.cancelItem,
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});

			return builder.create();
		default:
			return super.onCreateDialog(id);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// Kontextmen� entfalten
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.wfs_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.WFS_menu_delete:
			deleteRow(info.id);
			updateList();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void readWebpage(View view) {
		DownloadWebPageTask task = new DownloadWebPageTask();
		String url = baseURL + getString(R.string.Type)
				+ getString(R.string.KVP_Separator)
				+ getString(R.string.Capabilities)
				+ getString(R.string.KVP_Separator)
				+ getString(R.string.Version110);
		Log.d("url",url);
		task.execute(new String[] { url });
	}

	private class DownloadWebPageTask extends
			AsyncTask<String, Void, ArrayList<String>> {
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<String> doInBackground(String... urls) {
			ArrayList<String> response = null;
//			HashMap<String, String> response = null;
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				try {
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();
					//
					XMLReader xr = sp.getXMLReader();
					//
					XMLHandler Handler = new XMLHandler(new String[]{
							getString(R.string.FEATURE_TYPE_TAG), getString(R.string.SRS_TAG)});
					xr.setContentHandler(Handler);
					HttpResponse execute = client.execute(httpGet);
					InputStream content = execute.getEntity().getContent();
					InputSource inSource = new InputSource(content);
					xr.parse(inSource);
					response = Handler.data;
//					response = Handler.data1;

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(ArrayList<String> result) {
			

			if (result != null && result.size() != 0) {
				serviceResponse = new String[result.size()];
				for (int i = 0; i < result.size(); i++) {
					serviceResponse[i] = result.get(i);
				}
				Intent intent = new Intent(WFSSelection.this,
						FeatureTypeSelection.class);
				intent.putExtra(FEATURE_TYPE_INFOS, serviceResponse);
				startActivity(intent);
				progressBar.setVisibility(View.INVISIBLE);
			} else {
				progressBar.setVisibility(View.INVISIBLE);
				Toast.makeText(WFSSelection.this, R.string.NOWFS, Toast.LENGTH_LONG).show();
			}
			
			
		}
	}
}
