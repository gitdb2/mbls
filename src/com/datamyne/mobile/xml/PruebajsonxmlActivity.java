package com.datamyne.mobile.xml;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.datamyne.mobile.providers.IProfileProvider;
import com.datamyne.mobile.providers.IRestTradeProfileClient;
import com.datamyne.mobile.providers.ProfileProvider;
import com.datamyne.mobile.providers.RestTradeProfileClient;

public class PruebajsonxmlActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		String baseDir = getExternalFilesDir(null).getPath();
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		TitlesFragment titles = new TitlesFragment();
		
		Bundle args = new Bundle();
		args.putString("target", "dole");
		args.putString("baseDir", baseDir);
		
		titles.setArguments(args);
		
		
		
//		//android.R.id.content es el id del contenedor (root view)
//		getFragmentManager().beginTransaction().add(android.R.id.content, titles).commit();
//		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.titles, titles);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

/*
		JSONObject jso = abrirAsset("consignee/6795958.json");
		try {
			JSONObject jso2= jso.getJSONObject("tradeProfileContainer");
			System.out.println(jso2.getString("companyId"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//searchLocal("dole", "query.json");
		search("dole", "consignee");
*/
	}

//	public JSONObject abrirAsset(String fileName) {
//		try {
//			AssetManager am = getAssets();
//			InputStream is =  am.open(fileName);
//			Writer writer = new StringWriter();
//			char[] buffer = new char[1024];
//
//			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//			int n;
//			while ((n = reader.read(buffer)) != -1) {
//				writer.write(buffer, 0, n);
//			}
//			is.close();
//			String jsonString = writer.toString();
//			JSONObject jso = new JSONObject(jsonString);
//			return jso;
//
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

//	public JSONObject searchLocal(String target, String fileName){
//		JSONObject jso = abrirAsset(fileName);
//
//		try {
//			JSONArray arr = jso.getJSONArray("list");
//			for (int i = 0; i < arr.length(); i++) {
//				JSONObject item = arr.getJSONObject(i);
//
//				System.out.println(item.getString("second") + ", "+item.getString("third"));
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return jso;
//	}

//	private void search(String target, String type){
//		HttpURLConnection con = null;
//
//		try {
//			if(Thread.interrupted())
//				throw new InterruptedException();
//
//			String q 	= URLEncoder.encode(target, "UTF-8");
//						URL url 	= new URL("http://192.168.122.114:8080/system/rest/autocomplete?" +
//								"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);
//
//						
////						http://200.40.197.173:8082/system/rest/autocomplete?Base=usa_mid12&idComponent=402&compositeid=402&targetTerm=dole
//						
////			URL url 	= new URL("http://192.168.0.16/system/rest/autocomplete?" +
////					"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);
//			/*
//			{"list": [{
//			    "second": "6795958",
//			    "third": "DOLE FOOD COMPANY INC (CA)",
//			    "first": 402
//			  }, {
//			    "second": "6719778",
//			    "third": "DOLE FOOD COMPANY INC (DE)",
//			    "first": 402
//			  }]}
//			 */
//
//			con =(HttpURLConnection) url.openConnection();
//			con.setReadTimeout(10000);
//			con.setConnectTimeout(15000);
//			con.setRequestMethod("GET");
//			con.setDoInput(true);
//			con.connect();
//
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					con.getInputStream(), "UTF-8"));
//
//			String payload = reader.readLine();
//			reader.close();
//			con.disconnect();
//			con = null;
//
//			if(!payload.trim().isEmpty()){
//				JSONObject result = new JSONObject(payload);
//
//				JSONArray arr = result.getJSONArray("list");
//				for (int i = 0; i < arr.length(); i++) {
//					JSONObject item = arr.getJSONObject(i);
//
//					System.out.println(item.getString("second") + ", "+item.getString("third"));
//
//					if(i==1){
//
//						String data = getFullProfileJson(type, item.getString("second"));
//						if(!data.isEmpty()){
//							saveToSD(data, type, item.getString("second"));
//						}else{
//							System.err.println("DATA es blanck");
//						}
//					}
//
//
//				}
//
//			}
//
//			if(Thread.interrupted())
//				throw new InterruptedException();
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}finally{
//			if(con != null)
//				con.disconnect();
//		}
//	}



//	private String getFullProfileJson(String type, String id){
//		String payload = "";
//		    	String urlStr = "http://192.168.122.114:8080/system/rest/fullTradeprofile/"+type+"/"+id;
////		String urlStr = "http://200.40.197.173:8082/system/rest/fullTradeprofile/"+type+"/"+id;
//
//		HttpURLConnection con = null;
//
//		try {
//			if(Thread.interrupted())
//				throw new InterruptedException();
//
//			URL url 	= new URL(urlStr);
//
//			con =(HttpURLConnection) url.openConnection();
//			con.setReadTimeout(10000);
//			con.setConnectTimeout(15000);
//			con.setRequestMethod("GET");
//			con.setRequestProperty("Accept", "application/json");
//			con.setDoInput(true);
//			con.connect();
//
//			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
//
//			payload = reader.readLine();
//			reader.close();
//
//			if(Thread.interrupted())
//				throw new InterruptedException();
//		} catch (Exception e) {
//			e.printStackTrace();
//
//		}finally{
//			if(con != null)
//				con.disconnect();
//		}
//
//		return payload;
//
//	}


//	private void saveToSD(String payload, String type, String id) {
//		// Create a path where we will place our private file on external
//		// storage.
//		File root = new File(getExternalFilesDir(null), type);
//		updateExternalStorageState();
//		try {
//
//			if(mExternalStorageWriteable){
//				if (!root.exists()) {
//					root.mkdirs();
//				}
//				File gpxfile = new File(root, id+".json");
//				if(!gpxfile.exists()){
//					FileWriter writer = new FileWriter(gpxfile);
//					writer.append(payload);
//					writer.flush();
//					writer.close();
//					Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
//				}else{
//					Toast.makeText(this, "Already Saved", Toast.LENGTH_SHORT).show();
//				}
//			}else{
//				Toast.makeText(this, "No se puede escribir en la SD", Toast.LENGTH_SHORT).show();
//			}
//
//		} catch (IOException e) {
//			// Unable to create file, likely because external storage is
//			// not currently mounted.
//			Log.w("ExternalStorage", "Error writing " + root, e);
//			Toast.makeText(this, "No se pudo escribir a la sd no montada posiblemente", Toast.LENGTH_SHORT).show();
//		}
//	}

//	public JSONObject loadFromSD(String type, String id) {
//		// Create a path where we will place our private file on external
//		// storage.
//		File root = new File(getExternalFilesDir(null), type + File.separatorChar + id+".json");
//		JSONObject result = null;
//		updateExternalStorageState();
//		try {
//
//			if(mExternalStorageAvailable){
//				String payload = "";
//				if (!root.exists()) {
//					payload = getFullProfileJson(type, id);
//					saveToSD(payload, type, id);
//				}else{
//					InputStreamReader isReader = new FileReader(root);
//					BufferedReader reader = new BufferedReader(isReader);
//
//					payload = reader.readLine();
//					reader.close();
//				}
//
//				if(payload!= null && !payload.trim().isEmpty()){
//					try {
//						result = new JSONObject(payload);
//						Toast.makeText(this, "Archivo leido ok", Toast.LENGTH_SHORT).show();
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						Toast.makeText(this, "No se pudo parsear", Toast.LENGTH_SHORT).show();
//					}
//				}else{
//					Toast.makeText(this, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show();
//				}
//			}else{
//				Toast.makeText(this, "La SD no esta disponible", Toast.LENGTH_SHORT).show();
//			}
//		} catch (IOException e) {
//			// Unable to create file, likely because external storage is
//			// not currently mounted.
//			Log.w("ExternalStorage", "Error reading " + root, e);
//			Toast.makeText(this, "No se pudo leer a la sd, no montada posiblemente", Toast.LENGTH_SHORT).show();
//		}
//		return result;
//	}

//	boolean mExternalStorageAvailable = false;
//	boolean mExternalStorageWriteable = false;
//
//	void updateExternalStorageState() {
//		String state = Environment.getExternalStorageState();
//		if (Environment.MEDIA_MOUNTED.equals(state)) {
//			mExternalStorageAvailable = mExternalStorageWriteable = true;
//		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//			mExternalStorageAvailable = true;
//			mExternalStorageWriteable = false;
//		} else {
//			mExternalStorageAvailable = mExternalStorageWriteable = false;
//		}
//	}

















	//http://192.168.122.114:8080/system/bills/autoCompleteController.spf3?ajaxRequest=1&Base=usa_mid12&idComponent=402&compositeid=402&targetTerm=dole


	/**
	 * This is a secondary activity, to show what the user has selected
	 * when the screen is not large enough to show it all in one activity.
	 */

	public static class DetailsActivity extends Activity {

		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			if (getResources().getConfiguration().orientation
					== Configuration.ORIENTATION_LANDSCAPE) {
				// If the screen is now in landscape mode, we can show the
				// dialog in-line with the list so we don't need this activity.
				finish();
				return;
			}

			if (savedInstanceState == null) {
				// During initial setup, plug in the details fragment.
				DetailsFragment details = new DetailsFragment();
				details.setArguments(getIntent().getExtras());
				getFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
			}
		}
	}


	
	public static class Item implements Parcelable{
		String code;
		String name;
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Item() {
			super();
			// TODO Auto-generated constructor stub
		}
		public Item(String code, String name) {
			super();
			this.code = code;
			this.name = name;
		}
		@Override
		public String toString() {
			return name;
		}
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(code);
			dest.writeString(name);
		}
	

	     public static final Parcelable.Creator<Item> CREATOR
	             = new Parcelable.Creator<Item>() {
	         public Item createFromParcel(Parcel in) {
	             return new Item(in);
	         }

	         public Item[] newArray(int size) {
	             return new Item[size];
	         }
	     };
	     
	     private Item(Parcel in) {
	        this.code = in.readString();
			this.name = in.readString();
	     }
		
	}
	
	
	/**
	 * This is the "top-level" fragment, showing a list of items that the
	 * user can pick.  Upon picking an item, it takes care of displaying the
	 * data to the user as appropriate based on the currrent UI layout.
	 */

	public static class TitlesFragment extends ListFragment {
		boolean mDualPane;
		int mCurCheckPosition = 0;
		ArrayList<Item> itemList;
		private IRestTradeProfileClient client 		= new RestTradeProfileClient();
	//	private IProfileProvider profileProvider 	= new ProfileProvider(client);
		
		
//		private JSONObject search(String target, String type){
//			HttpURLConnection con = null;
//			JSONObject result = null;
//			try {
//				if(Thread.interrupted())
//					throw new InterruptedException();
//
//				String q 	= URLEncoder.encode(target, "UTF-8");
//							URL url 	= new URL("http://192.168.122.114:8080/system/rest/autocomplete?" +
//									"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);
//
////				URL url 	= new URL("http://192.168.0.16/system/rest/autocomplete?" +
////						"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);
//				/*
//				{"list": [{
//				    "second": "6795958",
//				    "third": "DOLE FOOD COMPANY INC (CA)",
//				    "first": 402
//				  }, {
//				    "second": "6719778",
//				    "third": "DOLE FOOD COMPANY INC (DE)",
//				    "first": 402
//				  }]}
//				 */
//
//				con =(HttpURLConnection) url.openConnection();
//				con.setReadTimeout(10000);
//				con.setConnectTimeout(15000);
//				con.setRequestMethod("GET");
//				con.setDoInput(true);
//				con.connect();
//
//				BufferedReader reader = new BufferedReader(new InputStreamReader(
//						con.getInputStream(), "UTF-8"));
//
//				String payload = reader.readLine();
//				reader.close();
//				con.disconnect();
//				con = null;
//
//				if(!payload.trim().isEmpty()){
//					result = new JSONObject(payload);
//				}
//
//				if(Thread.interrupted())
//					throw new InterruptedException();
//			} catch (Exception e) {
//				e.printStackTrace();
//
//			}finally{
//				if(con != null)
//					con.disconnect();
//			}
//			return result;
//		}
		
		
		
		
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			if (savedInstanceState != null) {
				// Restore last state for checked position.
				mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
				itemList= savedInstanceState.getParcelableArrayList("itemList");
			}else{
			
				JSONObject searched =  client.searchRemote(getArguments().getString("target"), "consignee");
				itemList = new ArrayList<Item>();
				try {
					JSONArray arr = searched.getJSONArray("list");
					for (int i = 0; i < arr.length(); i++) {
						JSONObject item = arr.getJSONObject(i);
						itemList.add(new Item(item.getString("second"), item.getString("third")));
					}	
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
	
			
			// Populate list with our static array of titles.
			setListAdapter(new ArrayAdapter<Item>(getActivity(),
					android.R.layout.simple_list_item_activated_1, itemList));

			// Check to see if we have a frame in which to embed the details
			// fragment directly in the containing UI.
			View detailsFrame = getActivity().findViewById(R.id.details);
			mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;

		

			if (mDualPane) {
				// In dual-pane mode, the list view highlights the selected item.
				getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				// Make sure our UI is in the correct state.
				showDetails(mCurCheckPosition);
			}
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putInt("curChoice", mCurCheckPosition);
			outState.putParcelableArrayList("itemList", itemList);
			
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			//Item item = (Item)l.getAdapter().getItem(position);
			showDetails(position);
		}

		/**
		 * Helper function to show the details of a selected item, either by
		 * displaying a fragment in-place in the current UI, or starting a
		 * whole new activity in which it is displayed.
		 */
		void showDetails( int index) {
			//List<Item> itemList,
			mCurCheckPosition = index;
			Item item = itemList.get(index);
			if (mDualPane) {
				// We can display everything in-place with fragments, so update
				// the list to highlight the selected item and show the data.
				getListView().setItemChecked(index, true);

				// Check what fragment is currently shown, replace if needed.
				DetailsFragment details = (DetailsFragment) getFragmentManager().findFragmentById(R.id.details);
				
				if (details == null || details.getShownIndex() != index) {
					// Make new fragment to show this selection.
					details = DetailsFragment.newInstance(item.getCode(), "consignee", index);

					// Execute a transaction, replacing any existing fragment
					// with this one inside the frame.
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.replace(R.id.details, details);
					ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					ft.commit();
				}

			} else {
				// Otherwise we need to launch a new activity to display
				// the dialog fragment with selected text.
				Intent intent = new Intent();
				intent.setClass(getActivity(), DetailsActivity.class);
				intent.putExtra("index", index);
				intent.putExtra("type", "consignee");
				intent.putExtra("id", item.getCode());
				startActivity(intent);
			}
		}
	}


	/**
	 * This is the secondary fragment, displaying the details of a particular
	 * item.
	 */

	public static class DetailsFragment extends Fragment {
		
		private IProfileProvider profileProvider 	= new ProfileProvider();
			
		
		/**
		 * Create a new instance of DetailsFragment, initialized to
		 * show the text at 'index'.
		 */
		public static DetailsFragment newInstance(String id, String type, int index) {
			DetailsFragment f = new DetailsFragment();

			
			// Supply index input as an argument.
			Bundle args = new Bundle();
			args.putString("type", type);
			args.putString("id", id);
			args.putInt("index", index);
			f.setArguments(args);

			return f;
		}

        public int getShownIndex() {
            return getArguments().getInt("index", 0);
        }

		public String getBundledId(){
			return getArguments().getString("id");
		}
		public String getBundledType(){
			return getArguments().getString("type");
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			if (container == null) {
				// We have different layouts, and in one of them this
				// fragment's containing frame doesn't exist.  The fragment
				// may still be created from its saved state, but there is
				// no reason to try to create its view hierarchy because it
				// won't be displayed.  Note this is not needed -- we could
				// just run the code below, where we would create and return
				// the view hierarchy; it would just never be used.
				return null;
			}
			
			String localBasePath = getActivity().getExternalFilesDir(null).getPath();
			System.out.println(localBasePath);
			
			String tmp = profileProvider.loadFullProfile(localBasePath, getBundledType(), getBundledId());
			
			ScrollView scroller = new ScrollView(getActivity());
			TextView text = new TextView(getActivity());
			int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					4, getActivity().getResources().getDisplayMetrics());
			text.setPadding(padding, padding, padding, padding);
			scroller.addView(text);
			
			//text.setText("levanta de sd el archhivo "+getBundledType()+"/"+getBundledId()+".json");
			text.setText(tmp);
			return scroller;
		}
	}

}