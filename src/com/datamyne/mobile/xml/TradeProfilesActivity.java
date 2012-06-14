package com.datamyne.mobile.xml;

import java.io.File;
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
import com.datamyne.mobile.profile.utils.HoneycombCompatibility;
import com.datamyne.mobile.profile.utils.Item;
import com.datamyne.mobile.providers.IProfileProvider;
import com.datamyne.mobile.providers.IRestTradeProfileClient;
import com.datamyne.mobile.providers.ProfileProvider;
import com.datamyne.mobile.providers.ProfilesSQLiteHelper;
import com.datamyne.mobile.providers.RestTradeProfileClient2;

/**
 * Clase que resuelve los Trade Profiles en modo online, se accede a ella desde el Dashboard
 */
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
        actionBar.setTitle("Trade Profiles (Online mode)");

        HoneycombCompatibility.actionBarSetLogo(actionBar, R.drawable.title_home_default);
		
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
	/**
	 * Adapter para generar los fragments de las paginas del profiel en el pagerView
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
	 * Actividad que va a mostrar los datos de un profile (en caso que sea celular o tablet vertical)
	 * @author rodrigo
	 *
	 */
	public static class DetailsActivity extends FragmentActivity{
		
		String id;
		String name;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			/**
			 * Si la orientacion es landscape y es pantalla grande (tablet), no se crea la activity DetailsActivity , ya que se va a usar un fragment
			 */
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
	        HoneycombCompatibility.actionBarSetLogo(actionBar, R.drawable.title_home_default);
	        
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
	 * Fragmento que muestra los resultados de la busqueda 
	 * @author rodrigo
	 *
	 */
	public static class TitlesFragment extends ListFragment {
		/**
		 * Indica si se esta usando dos paneles (2 fragmentos)
		 */
		boolean mDualPane;
		int mCurCheckPosition = 0;
		ArrayList<Item> itemList;
		boolean viewing = false;
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			if (savedInstanceState != null) {
				// se carga el estado anterior
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
			try {//se pone entre try y catch para cuando se va al home de android y cuando se resume la aplciacion no de problema al recargar la vista e intentar agregarla de nuevo
				displayData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	
		/**
		 * Muestra los datos del resultado
		 */
		public void displayData(){
			if(itemList!= null){
				setListAdapter(new ArrayAdapter<Item>(getActivity(), android.R.layout.simple_list_item_activated_1, itemList));
				View detailsFrame 	= getActivity().findViewById(R.id.details);
				mDualPane 			= detailsFrame != null && detailsFrame.getVisibility() == View.VISIBLE;
	
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
					if (mDualPane) { //si se esta mostrando dos paneles, entonces se selecciona el item en la listView, se obtiene el contenedor del pager
						//y se le asigna el adapter correspondiente, pasandole los parametros necesarios para ejecutar la consulta y armar las paginas del profile 
						getListView().setItemChecked(index, true);
						ViewPager details = (ViewPager) getActivity().findViewById(R.id.viewPager);
						
						if (details != null){
							MyFragmentPagerAdapter mMyFragmentPagerAdapter = 
									new MyFragmentPagerAdapter(getFragmentManager(), item.getCode(), item.getName(), "consignee", index);  
							details.setAdapter(mMyFragmentPagerAdapter);  
						}

					} else {
						//En caso que sea vista simple (celular o tablet vertical)
						//se dispara una DetailsActivity
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
	 * Fragmento que representa una pagina del trade profile desplegado en el pagerView
	 * @author rodrigo
	 *
	 */
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

			/**
			 * Si no hay contenedor, entonces no es landscape, entonces no se debe crear esta vista, no hay pagerView
			 */
			if (container == null) {
				return null;
			}

			IProfileProvider profileProvider = new ProfileProvider();
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
	 * Task asincrona que Realiza la busqueda de empresas
	 * @author rodrigo
	 *
	 */
	public static class HttpClientTask extends AsyncTask<String, Integer, ArrayList<Item>> {

		private IRestTradeProfileClient client 		= new RestTradeProfileClient2();
		private ProgressDialog dialog;
		private TitlesFragment titlesFragment;
		int errorCode = 0;
		
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
			//para que se pueda buscar de nuevo al hacer enter
			((TradeProfilesActivity) titlesFragment.getActivity()).lastQuery = null;
			
			titlesFragment.itemList = result;	
			titlesFragment.displayData();
	
		}
		
		  @Override
          protected void onCancelled()
          {
             super.onCancelled();
             ((TradeProfilesActivity) titlesFragment.getActivity()).lastQuery = null;
 			
 			titlesFragment.itemList = new ArrayList<Item>();	
 			titlesFragment.displayData();
                    
          }

		/***
		 * Lo usamos para manejar las excepciones
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
			
			errorCode = values[0]; 
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
				dialog.setMessage("Communication error Could not reach server, try later");
				dialog.setCancelable(true);
				
				break;
			default:
				break;
			}
			
		}

	}
	
	public boolean onQueryTextChange(String newText) {
		return false;
	}

	/**
	 * variable usada para evitar el rebote al presionar enter con un teclado fisico en el searchView
	 */
	private String lastQuery = null;
	
	/**
	 * MEtodo ejecutado cuando se ejecuta un submit en el searchView
	 */
	public boolean onQueryTextSubmit(String query) {
		
		boolean trigger= lastQuery == null || !query.equals(lastQuery); 
		//Existe un caso en que cuando se usa teclado al presionar enter se produce un rebote (KeyUp y down), y este metodo se llama dos veces,
		//por lo que controlamos que lo escrito antes sea distinto a lo escrito ahora, para dispoarar la busqueda
		if(trigger){
			lastQuery = query;
			
			String baseDir = null; 

			//Chequea si existe SD cuando file es null
			File file = getExternalFilesDir(null);
			if(file != null){
				baseDir = file.getPath();
			}
			
			TitlesFragment titles = new TitlesFragment();
			
			Bundle args = new Bundle();
			args.putString("target", query);
			args.putString("baseDir", baseDir);
			
			titles.setArguments(args);
			
			//Se reemplaza con un fragmento el frameLayout titles, donde muestra el resultado de la busqueda
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.titles, titles);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commit();
		}
		return false;
	}
	
}
