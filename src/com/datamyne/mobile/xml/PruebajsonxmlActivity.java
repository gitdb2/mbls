package com.datamyne.mobile.xml;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
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
		
		
		String target = getIntent().getExtras().getString("target");
		
		
		Bundle args = new Bundle();
		args.putString("target", target);
		args.putString("baseDir", baseDir);
		
		titles.setArguments(args);
		
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.titles, titles);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();

	}
	
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
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			if (savedInstanceState != null) {
				// Restore last state for checked position.
				mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
				itemList= savedInstanceState.getParcelableArrayList("itemList");
			}else{
				new HttpClientTask(this).execute(getArguments().getString("target"), "consignee");
			}
		
		}

		public void displayData(){
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
		
//		private IProfileProvider profileProvider 	= new ProfileProvider();
			
		
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
			
			
			
			ScrollView scroller = new ScrollView(getActivity());
			TextView text = new TextView(getActivity());
			int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					4, getActivity().getResources().getDisplayMetrics());
			text.setPadding(padding, padding, padding, padding);
			scroller.addView(text);
			
			new HttpClientTask2(this.getActivity(), text).execute(localBasePath, getBundledType(), getBundledId());
			
			return scroller;
		}
	}

	
	public static class HttpClientTask2 extends AsyncTask<String, Float, String> {

		//private IRestTradeProfileClient client 		= new RestTradeProfileClient();
		private IProfileProvider profileProvider 	= new ProfileProvider();
		private ProgressDialog dialog;
		private View view;
		
		public HttpClientTask2(Context context, TextView view) {
			super();
			this.view = view;
			dialog = new ProgressDialog(context);
	        dialog.setMessage("Descargando...");
	        dialog.setTitle("Progreso");
	        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        dialog.setCancelable(false);
		}

		@Override
		protected String doInBackground(String... params) {
			
			
			String tmp = profileProvider.loadFullProfile(params[0],params[1],params[2]);
					
//			
			
			
			return tmp;
		}

		protected void onPreExecute() {
			dialog.setProgress(0);
			dialog.setMax(100);
			dialog.show(); // Mostramos el diálogo antes de comenzar
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.dismiss();
			((TextView)view).setText(result);	
			//titlesFragment.displayData();
		}

	}
	
	
	/**
	 * Realiza la busqueda de empresas
	 * @author rodrigo
	 *
	 */
	public static class HttpClientTask extends AsyncTask<String, Float, ArrayList<Item>> {

		private IRestTradeProfileClient client 		= new RestTradeProfileClient();
		private ProgressDialog dialog;
		private TitlesFragment titlesFragment;
		
		public HttpClientTask(TitlesFragment titlesFragment) {
			super();
			this.titlesFragment = titlesFragment;
			dialog = new ProgressDialog(titlesFragment.getActivity());
	        dialog.setMessage("Descargando...");
	        dialog.setTitle("Progreso");
	        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        dialog.setCancelable(false);
		}

		@Override
		protected ArrayList<Item> doInBackground(String... params) {
			
			JSONObject searched =  client.searchRemote(params[0], params[1]);
			ArrayList<Item> itemList = new ArrayList<Item>();
			try {
				JSONArray arr = searched.getJSONArray("list");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject item = arr.getJSONObject(i);
					itemList.add(new Item(item.getString("second"), item.getString("third")));
				}	
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return itemList;
		}

		protected void onPreExecute() {
			dialog.setProgress(0);
			dialog.setMax(100);
			dialog.show(); // Mostramos el diálogo antes de comenzar
		}
		
		@Override
		protected void onPostExecute(ArrayList<Item> result) {
			super.onPostExecute(result);
			dialog.dismiss();
			titlesFragment.itemList = result;	
			titlesFragment.displayData();
		}

	}
	
	
	
}