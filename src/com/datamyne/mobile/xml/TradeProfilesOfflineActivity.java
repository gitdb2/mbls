package com.datamyne.mobile.xml;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.datamyne.mobile.dashboard.HomeActivity;
import com.datamyne.mobile.profile.utils.DetailsAsyncTask;
import com.datamyne.mobile.profile.utils.HoneycombCompatibility;
import com.datamyne.mobile.profile.utils.Item;
import com.datamyne.mobile.providers.DataBaseProfileProvider;
import com.datamyne.mobile.providers.IDatabaseProfileProvider;
import com.datamyne.mobile.providers.IProfileProvider;
import com.datamyne.mobile.providers.ProfileProvider;
import com.datamyne.mobile.providers.ProfilesSQLiteHelper;

/**
 * Clase que resuelve los Trade Profiles en modo offline, se accede a ella desde el Dashboard
 */
public class TradeProfilesOfflineActivity extends FragmentActivity {

	private static final int NUMBER_OF_PAGES = 6;
	private ProfilesSQLiteHelper dbHelper;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trade_profiles_offline);
		
		dbHelper = new ProfilesSQLiteHelper(this);
		 
		ActionBar actionBar = getActionBar();
        actionBar.show();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Trade Profiles (Offline mode)");
        HoneycombCompatibility.actionBarSetLogo(actionBar, R.drawable.title_home_default);
        populateTitles();
	}
	
	/**
	 * Carga los ultimas busquedas desde la sd
	 */
	private void populateTitles() {
		TitlesFragment titles = new TitlesFragment();
		
		Bundle args = new Bundle();
		args.putString("target", "");
		
		String baseDir = null; 
		//Chequea si existe SD cuando file es null
		File file = getExternalFilesDir(null);
		if(file != null){
			baseDir = file.getPath();
		}
		args.putString("baseDir", baseDir);
		
		titles.setArguments(args);
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.titles, titles);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.commit();
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
	
	/**
	 * Clase Adapter usada para generar los PageFragment del PagerView
	 * @author rodrigo
	 *
	 */
	private static class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {  

		String id;
		String type;
		String name;
		int selectedIndex;
		
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
	 * ver descripcion en TradeProfilesActivity
	 * @author rodrigo
	 *
	 */
	public static class DetailsActivity extends FragmentActivity {
		
		String id;
		String name;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			if (getResources().getConfiguration().orientation
					== Configuration.ORIENTATION_LANDSCAPE 
					&& getResources().getConfiguration().isLayoutSizeAtLeast(Configuration.SCREENLAYOUT_SIZE_LARGE)) {
				finish();
				return;
			}

			setContentView(R.layout.activity_trade_profiles_details);
			
			ActionBar actionBar = getActionBar();
	        actionBar.show();
	        actionBar.setDisplayHomeAsUpEnabled(true);

		    try {
				Method setLogo = ActionBar.class.getMethod("setLogo");//, new Class[]{int.class});
				setLogo.invoke(actionBar, new Object[] {R.drawable.title_home_default});
			} catch (Exception e) {
				Log.i("tradeProfilesActivity", e.getMessage());
			}
			
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
	                 Intent intent = new Intent(this, HomeActivity.class);
	                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                 startActivity(intent);
	                return true;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	    }
		
	}
	
	/**
	 * ver descripcion en TradeProfilesActivity
	 * @author rodrigo
	 *
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
			super.onResume();
			try {
				displayData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void displayData(){
			if(itemList!= null){
				setListAdapter(new ArrayAdapter<Item>(getActivity(), android.R.layout.simple_list_item_activated_1, itemList));
				
				View detailsFrame = getActivity().findViewById(R.id.details);
				mDualPane = detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
	
				if (mDualPane) {
					getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
			viewing = true;
			showDetails(position);
		}

		void showDetails( int index) {
			mCurCheckPosition = index;
			if(itemList != null && !itemList.isEmpty()){
				try {
					Item item = itemList.get(index);
					if (mDualPane) {
						getListView().setItemChecked(index, true);
						ViewPager details = (ViewPager) getActivity().findViewById(R.id.viewPager);
						
						if (details != null){

							MyFragmentPagerAdapter mMyFragmentPagerAdapter = 
									new MyFragmentPagerAdapter(getFragmentManager(), item.getCode(), item.getName(), "consignee", index);  
							details.setAdapter(mMyFragmentPagerAdapter);  
						}

					} else {
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
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * ver descripcion en TradeProfilesActivity
	 * @author rodrigo
	 *
	 */
	public static class PageFragment extends Fragment {
		
		public static PageFragment newInstance(String id, String name, String type, int page, int index) {

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
				return null;
			}
			
			
			SharedPreferences pref 	= getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
			String baseServer 		= pref.getString("baseServer", "");
			
			IProfileProvider profileProvider = new ProfileProvider(baseServer);
			
			ViewGroup layout = (ViewGroup)inflater.inflate(R.layout.fragment_trade_profile_detail_pager, null);
			String localBasePath = null; 
			//Chequea si existe SD cuando file es null
			if(profileProvider.isSdPresent()){
				File file = getActivity().getExternalFilesDir(null);
				if(file != null){
					localBasePath = file.getPath();
				}
			}
			
			boolean showDialog = !profileProvider.checkFileExists(localBasePath, getBundledType(), getBundledId());

			new DetailsAsyncTask(getActivity(), layout, showDialog, getBundledPage()).execute(localBasePath, getBundledType(), getBundledId(), getBundledName());
			
			return layout;
		}

	}
	
	/**
	 * Realiza la busqueda de empresas
	 * @author rodrigo
	 *
	 */
	public static class HttpClientTask extends AsyncTask<String, Integer, ArrayList<Item>> {

		private ProgressDialog dialog;
		private TitlesFragment titlesFragment;
		
		public HttpClientTask(TitlesFragment titlesFragment) {
			super();
			this.titlesFragment = titlesFragment;
			dialog = new ProgressDialog(titlesFragment.getActivity());
	        dialog.setMessage("Fetching data...");
	        dialog.setTitle("Progreso");
	        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        dialog.setCancelable(false);
		}

		@Override
		protected ArrayList<Item> doInBackground(String... params) {
			ProfilesSQLiteHelper dbHelper = new ProfilesSQLiteHelper(titlesFragment.getActivity());
			IDatabaseProfileProvider provider = new DataBaseProfileProvider();
			ArrayList<Item> itemList = provider.loadSavedProfiles(dbHelper);
			dbHelper.close();
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
	
}
