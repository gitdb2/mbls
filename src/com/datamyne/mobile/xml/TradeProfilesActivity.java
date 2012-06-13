package com.datamyne.mobile.xml;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.datamyne.mobile.dashboard.HomeActivity;
import com.datamyne.mobile.profile.utils.DetailsAsyncTask;
import com.datamyne.mobile.profile.utils.Item;
import com.datamyne.mobile.providers.IProfileProvider;
import com.datamyne.mobile.providers.IRestTradeProfileClient;
import com.datamyne.mobile.providers.ProfileProvider;
import com.datamyne.mobile.providers.ProfilesSQLiteHelper;
import com.datamyne.mobile.providers.RestTradeProfileClient2;

public class TradeProfilesActivity extends FragmentActivity implements SearchView.OnQueryTextListener {

	private static final int NUMBER_OF_PAGES = 6;
	private SearchView searchView;
	private ProfilesSQLiteHelper dbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trade_profiles);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		dbHelper = new ProfilesSQLiteHelper(this);
		 
		ActionBar actionBar = getActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setLogo(R.drawable.title_home_default);
		
		searchView = (SearchView) findViewById(R.id.searchViewCompany);
		searchView.setIconifiedByDefault(false);
		searchView.setOnQueryTextListener(this);
	}
	
	public ProfilesSQLiteHelper getDBHelper() {
		return dbHelper;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                 Intent intent = new Intent(this, HomeActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
	
	private static class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {  

		String id;
		String type;
		String name;
		int selectedIndex;
		
//		public MyFragmentPagerAdapter(FragmentManager fm, String id, String type, int selectedIndex) {  
//			super(fm);
//			this.id = id;
//			this.type = type;
//			this.selectedIndex = selectedIndex;
//		}
		
		public MyFragmentPagerAdapter(FragmentManager fm, String id, String name, String type, int selectedIndex) {  
			super(fm);
			this.id = id;
			this.name = name;
			this.type = type;
			this.selectedIndex = selectedIndex;
		} 

		public Fragment getItem(int page) {  
			return PageFragment.newInstance(id, name, type, page, selectedIndex);
		}  

		@Override  
		public int getCount() {  
			return NUMBER_OF_PAGES;  
		}  
	}  	

	/**
	 * This is a secondary activity, to show what the user has selected
	 * when the screen is not large enough to show it all in one activity.
	 */

	public static class DetailsActivity extends FragmentActivity{
		
		String id;
		String name;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			if (getResources().getConfiguration().orientation
					== Configuration.ORIENTATION_LANDSCAPE 
					&& getResources().getConfiguration().isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE)) {
				// If the screen is now in landscape mode, we can show the
				// dialog in-line with the list so we don't need this activity.
				finish();
				return;
			}

			setContentView(R.layout.activity_trade_profiles_details);
			
			ActionBar actionBar = getActionBar();
	        actionBar.show();
	        actionBar.setDisplayHomeAsUpEnabled(true);
	        actionBar.setLogo(R.drawable.title_home_default);
			
			if (savedInstanceState == null) {
 
				id = getIntent().getExtras().getString("id");
				name = getIntent().getExtras().getString("name");
				
			}else{
				id= savedInstanceState.getString("id");
				name= savedInstanceState.getString("name");
			}
			
			ViewPager details = (ViewPager) findViewById(R.id.viewPager);
				MyFragmentPagerAdapter mMyFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), id, name, "consignee", 0);
				details.setAdapter(mMyFragmentPagerAdapter);  
			}
		
		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putString("id", id);
			outState.putString("name", name);
		}
		
		@Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case android.R.id.home:
//	                 Intent intent = new Intent(this, HomeActivity.class);
//	                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	                 startActivity(intent);
	            	finish(); //VER QUE ES MEJOR SI BACK O HOME
	                return true;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
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
		boolean viewing = false;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			if (savedInstanceState != null) {
				// Restore last state for checked position.
				mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
				itemList= savedInstanceState.getParcelableArrayList("itemList");
				viewing =  savedInstanceState.getBoolean("viewing", false);
				if(viewing){ //si ese estaba mirando un detalle lo muestro
					showDetails(mCurCheckPosition);
				}
			}else{
				new HttpClientTask(this).execute(getArguments().getString("target"), "consignee");
			}
		
		}

		
		@Override
		public void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			try {
				displayData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	
		
		public void displayData(){
			if(itemList!= null){
				setListAdapter(new ArrayAdapter<Item>(getActivity(), android.R.layout.simple_list_item_activated_1, itemList));
				
				// Check to see if we have a frame in which to embed the details
				// fragment directly in the containing UI.
				View detailsFrame = getActivity().findViewById(R.id.details);
				mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
	
				if (mDualPane) {
					// In dual-pane mode, the list view highlights the selected item.
					getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
					// Make sure our UI is in the correct state.
					if(viewing){ //si ese estaba mirando un detalle lo muestro
						showDetails(mCurCheckPosition);
					}
				}
			}
		}
		
		@Override
		public void onSaveInstanceState(Bundle outState) {
			super.onSaveInstanceState(outState);
			outState.putInt("curChoice", mCurCheckPosition);
			outState.putBoolean("viewing", viewing);
			
			outState.putParcelableArrayList("itemList", itemList);
		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			//Item item = (Item)l.getAdapter().getItem(position);
			viewing = true;
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
			if(itemList != null && !itemList.isEmpty()){
				try {
					Item item = itemList.get(index);
					if (mDualPane) {
						// We can display everything in-place with fragments, so update
						// the list to highlight the selected item and show the data.
						getListView().setItemChecked(index, true);

						// Check what fragment is currently shown, replace if needed.
						ViewPager details = (ViewPager) getActivity().findViewById(R.id.viewPager);
				
						
						if (details != null){

							MyFragmentPagerAdapter mMyFragmentPagerAdapter = 
									new MyFragmentPagerAdapter(getFragmentManager(), item.getCode(), item.getName(), "consignee", index);  
							details.setAdapter(mMyFragmentPagerAdapter);  
						}

					} else {
						// Otherwise we need to launch a new activity to display
						// the dialog fragment with selected text.
						Intent intent = new Intent();
						intent.setClass(getActivity(), DetailsActivity.class);
						intent.putExtra("index", index);
						intent.putExtra("type", "consignee");
						intent.putExtra("id", item.getCode());
						intent.putExtra("name", item.getName());
						intent.putExtra("page", 0);
						startActivity(intent);
					}
				} catch (IndexOutOfBoundsException e) {

					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public static class PageFragment extends Fragment {

		
		public static PageFragment newInstance(String id, String name, String type,	int page, int index) {

			PageFragment pageFragment = new PageFragment();
			
			Bundle args = new Bundle();
			args.putString("type", type);
			args.putString("id", id);
			args.putString("name", name);
			args.putInt("index", index);
			args.putInt("page", page);
			
			pageFragment.setArguments(args);
			return pageFragment;
		}

		public int getBundledIndex() {
			return getArguments().getInt("index", 0);
		}

		public int getBundledPage() {
			return getArguments().getInt("page", 0);
		}

		public String getBundledId() {
			return getArguments().getString("id");
		}

		public String getBundledName() {
			return getArguments().getString("name");
		}

		public String getBundledType() {
			return getArguments().getString("type");
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			if (container == null) {
				// We have different layouts, and in one of them this
				// fragment's containing frame doesn't exist. The fragment
				// may still be created from its saved state, but there is
				// no reason to try to create its view hierarchy because it
				// won't be displayed. Note this is not needed -- we could
				// just run the code below, where we would create and return
				// the view hierarchy; it would just never be used.
				return null;
			}


			ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.fragment_trade_profile_detail_pager, null);
			String localBasePath = getActivity().getExternalFilesDir(null).getPath();
	
			IProfileProvider profileProvider = new ProfileProvider();
			boolean showDialog = !profileProvider.checkFileExists(localBasePath, getBundledType(), getBundledId());

			new DetailsAsyncTask(getActivity(), layout, showDialog, getBundledPage()).execute(localBasePath, getBundledType(), getBundledId());
			
			return layout;
		}  
	}
	
		
	/**
	 * Realiza la busqueda de empresas
	 * @author rodrigo
	 *
	 */
	public static class HttpClientTask extends AsyncTask<String, Integer, ArrayList<Item>> {

		private IRestTradeProfileClient client 		= new RestTradeProfileClient2();
		private ProgressDialog dialog;
		private TitlesFragment titlesFragment;
		
		public HttpClientTask(TitlesFragment titlesFragment) {
			super();
			this.titlesFragment = titlesFragment;
			dialog = new ProgressDialog(titlesFragment.getActivity());
			dialog.setTitle("Connecting to server");
		    dialog.setMessage("Fetching data...");
	        
	        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        dialog.setCancelable(false);
		}

		@Override
		protected ArrayList<Item> doInBackground(String... params) {
			
			ArrayList<Item> itemList = new ArrayList<Item>();
					
			try {
				JSONObject searched =  client.searchRemote(params[0], params[1]);
				JSONArray arr = searched.getJSONArray("list");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject item = arr.getJSONObject(i);
					itemList.add(new Item(item.getString("second"), item.getString("third")));
				}	
				
			} catch (JSONException e) {
				e.printStackTrace();
				publishProgress(-1);
				cancel(true);
			}
			catch (SocketTimeoutException e) {
				e.printStackTrace();
				publishProgress(-2);
				cancel(true);
			}
			catch (IOException e) {
				e.printStackTrace();
				publishProgress(-3);
				cancel(true);
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
			if(!isCancelled()){//si fue cancelado (tiro excepcion)
				dialog.dismiss();
				
			}
			titlesFragment.itemList = result;	
			titlesFragment.displayData();
		}
		
		  @Override
          protected void onCancelled()
          {
                  super.onCancelled();
          }
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			switch (values[0]) {
			case -1:
				dialog.setMessage("Unknown data");
				dialog.setCancelable(true);
				
				break;
			case -2:
				dialog.setMessage("Server unreachable, try later. Tap to close");
				dialog.setCancelable(true);
				
				break;
			case -3:
				dialog.setMessage("Other IO Exception, try later");
				dialog.setCancelable(true);
				
				break;
			default:
				break;
			}
			
		}

	}

	public boolean onQueryTextChange(String newText) {
		// TODO Auto-generated method stub
		return false;
	}

	private String lastQuery = null;
	public boolean onQueryTextSubmit(String query) {
		
		boolean trigger= lastQuery == null || !query.equals(lastQuery); 
		if(trigger){
			lastQuery = query;
			String baseDir = getExternalFilesDir(null).getPath();
	
			TitlesFragment titles = new TitlesFragment();
			
			Bundle args = new Bundle();
			args.putString("target", query);
			args.putString("baseDir", baseDir);
			
			titles.setArguments(args);
			
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.titles, titles);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}
		return false;
	}
	
}