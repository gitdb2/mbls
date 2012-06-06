package com.datamyne.mobile.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PruebajsonxmlActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		TitlesFragment titles = new TitlesFragment();
		
		Bundle args = new Bundle();
		args.putString("target", "dole");
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

	public JSONObject abrirAsset(String fileName) {
		try {
			AssetManager am = getAssets();
			InputStream is =  am.open(fileName);
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];

			Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			int n;
			while ((n = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, n);
			}
			is.close();
			String jsonString = writer.toString();
			JSONObject jso = new JSONObject(jsonString);
			return jso;

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JSONObject searchLocal(String target, String fileName){
		JSONObject jso = abrirAsset(fileName);

		try {
			JSONArray arr = jso.getJSONArray("list");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject item = arr.getJSONObject(i);

				System.out.println(item.getString("second") + ", "+item.getString("third"));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jso;
	}

	private void search(String target, String type){
		HttpURLConnection con = null;

		try {
			if(Thread.interrupted())
				throw new InterruptedException();

			String q 	= URLEncoder.encode(target, "UTF-8");
						URL url 	= new URL("http://192.168.122.114:8080/system/rest/autocomplete?" +
								"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);

//			URL url 	= new URL("http://192.168.0.16/system/rest/autocomplete?" +
//					"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);
			/*
			{"list": [{
			    "second": "6795958",
			    "third": "DOLE FOOD COMPANY INC (CA)",
			    "first": 402
			  }, {
			    "second": "6719778",
			    "third": "DOLE FOOD COMPANY INC (DE)",
			    "first": 402
			  }]}
			 */

			con =(HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream(), "UTF-8"));

			String payload = reader.readLine();
			reader.close();
			con.disconnect();
			con = null;

			if(!payload.trim().isEmpty()){
				JSONObject result = new JSONObject(payload);

				JSONArray arr = result.getJSONArray("list");
				for (int i = 0; i < arr.length(); i++) {
					JSONObject item = arr.getJSONObject(i);

					System.out.println(item.getString("second") + ", "+item.getString("third"));

					if(i==1){

						String data = getFullProfileJson(type, item.getString("second"));
						if(!data.isEmpty()){
							saveToSD(data, type, item.getString("second"));
						}else{
							System.err.println("DATA es blanck");
						}
					}


				}

			}

			if(Thread.interrupted())
				throw new InterruptedException();
		} catch (Exception e) {
			e.printStackTrace();

		}finally{
			if(con != null)
				con.disconnect();
		}
	}



	private String getFullProfileJson(String type, String id){
		String payload = "";
		    	String urlStr = "http://192.168.122.114:8080/system/rest/fullTradeprofile/"+type+"/"+id;
//		String urlStr = "http://200.40.197.173:8082/system/rest/fullTradeprofile/"+type+"/"+id;

		HttpURLConnection con = null;

		try {
			if(Thread.interrupted())
				throw new InterruptedException();

			URL url 	= new URL(urlStr);

			con =(HttpURLConnection) url.openConnection();
			con.setReadTimeout(10000);
			con.setConnectTimeout(15000);
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setDoInput(true);
			con.connect();

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

			payload = reader.readLine();
			reader.close();

			if(Thread.interrupted())
				throw new InterruptedException();
		} catch (Exception e) {
			e.printStackTrace();

		}finally{
			if(con != null)
				con.disconnect();
		}

		return payload;

	}


	private void saveToSD(String payload, String type, String id) {
		// Create a path where we will place our private file on external
		// storage.
		File root = new File(getExternalFilesDir(null), type);
		updateExternalStorageState();
		try {

			if(mExternalStorageWriteable){
				if (!root.exists()) {
					root.mkdirs();
				}
				File gpxfile = new File(root, id+".json");
				if(!gpxfile.exists()){
					FileWriter writer = new FileWriter(gpxfile);
					writer.append(payload);
					writer.flush();
					writer.close();
					Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(this, "Already Saved", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this, "No se puede escribir en la SD", Toast.LENGTH_SHORT).show();
			}

		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.w("ExternalStorage", "Error writing " + root, e);
			Toast.makeText(this, "No se pudo escribir a la sd no montada posiblemente", Toast.LENGTH_SHORT).show();
		}
	}

	public JSONObject loadFromSD(String type, String id) {
		// Create a path where we will place our private file on external
		// storage.
		File root = new File(getExternalFilesDir(null), type + File.separatorChar + id+".json");
		JSONObject result = null;
		updateExternalStorageState();
		try {

			if(mExternalStorageAvailable){
				String payload = "";
				if (!root.exists()) {
					payload = getFullProfileJson(type, id);
					saveToSD(payload, type, id);
				}else{
					InputStreamReader isReader = new FileReader(root);
					BufferedReader reader = new BufferedReader(isReader);

					payload = reader.readLine();
					reader.close();
				}

				if(payload!= null && !payload.trim().isEmpty()){
					try {
						result = new JSONObject(payload);
						Toast.makeText(this, "Archivo leido ok", Toast.LENGTH_SHORT).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(this, "No se pudo parsear", Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(this, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show();
				}
			}else{
				Toast.makeText(this, "La SD no esta disponible", Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			// Unable to create file, likely because external storage is
			// not currently mounted.
			Log.w("ExternalStorage", "Error reading " + root, e);
			Toast.makeText(this, "No se pudo leer a la sd, no montada posiblemente", Toast.LENGTH_SHORT).show();
		}
		return result;
	}

	boolean mExternalStorageAvailable = false;
	boolean mExternalStorageWriteable = false;

	void updateExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
	}

















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
		
		private JSONObject search(String target, String type){
			HttpURLConnection con = null;
			JSONObject result = null;
			try {
				if(Thread.interrupted())
					throw new InterruptedException();

				String q 	= URLEncoder.encode(target, "UTF-8");
							URL url 	= new URL("http://192.168.122.114:8080/system/rest/autocomplete?" +
									"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);

//				URL url 	= new URL("http://192.168.0.16/system/rest/autocomplete?" +
//						"Base=usa_mid12&idComponent=402&compositeid=402&targetTerm="+q);
				/*
				{"list": [{
				    "second": "6795958",
				    "third": "DOLE FOOD COMPANY INC (CA)",
				    "first": 402
				  }, {
				    "second": "6719778",
				    "third": "DOLE FOOD COMPANY INC (DE)",
				    "first": 402
				  }]}
				 */

				con =(HttpURLConnection) url.openConnection();
				con.setReadTimeout(10000);
				con.setConnectTimeout(15000);
				con.setRequestMethod("GET");
				con.setDoInput(true);
				con.connect();

				BufferedReader reader = new BufferedReader(new InputStreamReader(
						con.getInputStream(), "UTF-8"));

				String payload = reader.readLine();
				reader.close();
				con.disconnect();
				con = null;

				if(!payload.trim().isEmpty()){
					result = new JSONObject(payload);
				}

				if(Thread.interrupted())
					throw new InterruptedException();
			} catch (Exception e) {
				e.printStackTrace();

			}finally{
				if(con != null)
					con.disconnect();
			}
			return result;
		}
		
		
		
		
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);

			if (savedInstanceState != null) {
				// Restore last state for checked position.
				mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);
				itemList= savedInstanceState.getParcelableArrayList("itemList");
			}else{
				JSONObject searched =  search(getArguments().getString("target"), "consignee");
				
				itemList = new ArrayList<Item>();
				try {
					JSONArray arr = searched.getJSONArray("list");
					for (int i = 0; i < arr.length(); i++) {
						JSONObject item = arr.getJSONObject(i);
			
					//	System.out.println(item.getString("second") + ", "+item.getString("third"));
			
						itemList.add(new Item(item.getString("second"), item.getString("third")));
				
					}	
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
				DetailsFragment details = (DetailsFragment)
						getFragmentManager().findFragmentById(R.id.details);
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

			ScrollView scroller = new ScrollView(getActivity());
			TextView text = new TextView(getActivity());
			int padding = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					4, getActivity().getResources().getDisplayMetrics());
			text.setPadding(padding, padding, padding, padding);
			scroller.addView(text);
			
			text.setText("levanta de sd el archhivo "+getBundledType()+"/"+getBundledId()+".json");
			return scroller;
		}
	}


	public static class Shakespeare2 {
		/**
		 * Our data, part 1.
		 */
		public static final String[] TITLES = 
			{
			"Henry IV (1)",   
			"Henry V",
			"Henry VIII",       
			"Richard II",
			"Richard III",
			"Merchant of Venice",  
			"Othello",
			"King Lear"
			};

		/**
		 * Our data, part 2.
		 */
		public static final String[] DIALOGUE = 
			{
			"So shaken as we are, so wan with care," +
					"Find we a time for frighted peace to pant," +
					"And breathe short-winded accents of new broils" +
					"To be commenced in strands afar remote." +
					"No more the thirsty entrance of this soil" +
					"Shall daub her lips with her own children's blood;" +
					"Nor more shall trenching war channel her fields," +
					"Nor bruise her flowerets with the armed hoofs" +
					"Of hostile paces: those opposed eyes," +
					"Which, like the meteors of a troubled heaven," +
					"All of one nature, of one substance bred," +
					"Did lately meet in the intestine shock" +
					"And furious close of civil butchery" +
					"Shall now, in mutual well-beseeming ranks," +
					"March all one way and be no more opposed" +
					"Against acquaintance, kindred and allies:" +
					"The edge of war, like an ill-sheathed knife," +
					"No more shall cut his master. Therefore, friends," +
					"As far as to the sepulchre of Christ," +
					"Whose soldier now, under whose blessed cross" +
					"We are impressed and engaged to fight," +
					"Forthwith a power of English shall we levy;" +
					"Whose arms were moulded in their mothers' womb" +
					"To chase these pagans in those holy fields" +
					"Over whose acres walk'd those blessed feet" +
					"Which fourteen hundred years ago were nail'd" +
					"For our advantage on the bitter cross." +
					"But this our purpose now is twelve month old," +
					"And bootless 'tis to tell you we will go:" +
					"Therefore we meet not now. Then let me hear" +
					"Of you, my gentle cousin Westmoreland," +
					"What yesternight our council did decree" +
					"In forwarding this dear expedience.",

					"Hear him but reason in divinity," + 
							"And all-admiring with an inward wish" + 
							"You would desire the king were made a prelate:" + 
							"Hear him debate of commonwealth affairs," + 
							"You would say it hath been all in all his study:" + 
							"List his discourse of war, and you shall hear" + 
							"A fearful battle render'd you in music:" + 
							"Turn him to any cause of policy," + 
							"The Gordian knot of it he will unloose," + 
							"Familiar as his garter: that, when he speaks," + 
							"The air, a charter'd libertine, is still," + 
							"And the mute wonder lurketh in men's ears," + 
							"To steal his sweet and honey'd sentences;" + 
							"So that the art and practic part of life" + 
							"Must be the mistress to this theoric:" + 
							"Which is a wonder how his grace should glean it," + 
							"Since his addiction was to courses vain," + 
							"His companies unletter'd, rude and shallow," + 
							"His hours fill'd up with riots, banquets, sports," + 
							"And never noted in him any study," + 
							"Any retirement, any sequestration" + 
							"From open haunts and popularity.",

							"I come no more to make you laugh: things now," +
									"That bear a weighty and a serious brow," +
									"Sad, high, and working, full of state and woe," +
									"Such noble scenes as draw the eye to flow," +
									"We now present. Those that can pity, here" +
									"May, if they think it well, let fall a tear;" +
									"The subject will deserve it. Such as give" +
									"Their money out of hope they may believe," +
									"May here find truth too. Those that come to see" +
									"Only a show or two, and so agree" +
									"The play may pass, if they be still and willing," +
									"I'll undertake may see away their shilling" +
									"Richly in two short hours. Only they" +
									"That come to hear a merry bawdy play," +
									"A noise of targets, or to see a fellow" +
									"In a long motley coat guarded with yellow," +
									"Will be deceived; for, gentle hearers, know," +
									"To rank our chosen truth with such a show" +
									"As fool and fight is, beside forfeiting" +
									"Our own brains, and the opinion that we bring," +
									"To make that only true we now intend," +
									"Will leave us never an understanding friend." +
									"Therefore, for goodness' sake, and as you are known" +
									"The first and happiest hearers of the town," +
									"Be sad, as we would make ye: think ye see" +
									"The very persons of our noble story" +
									"As they were living; think you see them great," +
									"And follow'd with the general throng and sweat" +
									"Of thousand friends; then in a moment, see" +
									"How soon this mightiness meets misery:" +
									"And, if you can be merry then, I'll say" +
									"A man may weep upon his wedding-day.",

									"First, heaven be the record to my speech!" + 
											"In the devotion of a subject's love," + 
											"Tendering the precious safety of my prince," + 
											"And free from other misbegotten hate," + 
											"Come I appellant to this princely presence." + 
											"Now, Thomas Mowbray, do I turn to thee," + 
											"And mark my greeting well; for what I speak" + 
											"My body shall make good upon this earth," + 
											"Or my divine soul answer it in heaven." + 
											"Thou art a traitor and a miscreant," + 
											"Too good to be so and too bad to live," + 
											"Since the more fair and crystal is the sky," + 
											"The uglier seem the clouds that in it fly." + 
											"Once more, the more to aggravate the note," + 
											"With a foul traitor's name stuff I thy throat;" + 
											"And wish, so please my sovereign, ere I move," + 
											"What my tongue speaks my right drawn sword may prove.",

											"Now is the winter of our discontent" + 
													"Made glorious summer by this sun of York;" + 
													"And all the clouds that lour'd upon our house" + 
													"In the deep bosom of the ocean buried." + 
													"Now are our brows bound with victorious wreaths;" + 
													"Our bruised arms hung up for monuments;" + 
													"Our stern alarums changed to merry meetings," + 
													"Our dreadful marches to delightful measures." + 
													"Grim-visaged war hath smooth'd his wrinkled front;" + 
													"And now, instead of mounting barded steeds" + 
													"To fright the souls of fearful adversaries," + 
													"He capers nimbly in a lady's chamber" + 
													"To the lascivious pleasing of a lute." + 
													"But I, that am not shaped for sportive tricks," + 
													"Nor made to court an amorous looking-glass;" + 
													"I, that am rudely stamp'd, and want love's majesty" + 
													"To strut before a wanton ambling nymph;" + 
													"I, that am curtail'd of this fair proportion," + 
													"Cheated of feature by dissembling nature," + 
													"Deformed, unfinish'd, sent before my time" + 
													"Into this breathing world, scarce half made up," + 
													"And that so lamely and unfashionable" + 
													"That dogs bark at me as I halt by them;" + 
													"Why, I, in this weak piping time of peace," + 
													"Have no delight to pass away the time," + 
													"Unless to spy my shadow in the sun" + 
													"And descant on mine own deformity:" + 
													"And therefore, since I cannot prove a lover," + 
													"To entertain these fair well-spoken days," + 
													"I am determined to prove a villain" + 
													"And hate the idle pleasures of these days." + 
													"Plots have I laid, inductions dangerous," + 
													"By drunken prophecies, libels and dreams," + 
													"To set my brother Clarence and the king" + 
													"In deadly hate the one against the other:" + 
													"And if King Edward be as true and just" + 
													"As I am subtle, false and treacherous," + 
													"This day should Clarence closely be mew'd up," + 
													"About a prophecy, which says that 'G'" + 
													"Of Edward's heirs the murderer shall be." + 
													"Dive, thoughts, down to my soul: here" + 
													"Clarence comes.",

													"To bait fish withal: if it will feed nothing else," + 
															"it will feed my revenge. He hath disgraced me, and" + 
															"hindered me half a million; laughed at my losses," + 
															"mocked at my gains, scorned my nation, thwarted my" + 
															"bargains, cooled my friends, heated mine" + 
															"enemies; and what's his reason? I am a Jew. Hath" + 
															"not a Jew eyes? hath not a Jew hands, organs," + 
															"dimensions, senses, affections, passions? fed with" + 
															"the same food, hurt with the same weapons, subject" + 
															"to the same diseases, healed by the same means," + 
															"warmed and cooled by the same winter and summer, as" + 
															"a Christian is? If you prick us, do we not bleed?" + 
															"if you tickle us, do we not laugh? if you poison" + 
															"us, do we not die? and if you wrong us, shall we not" + 
															"revenge? If we are like you in the rest, we will" + 
															"resemble you in that. If a Jew wrong a Christian," + 
															"what is his humility? Revenge. If a Christian" + 
															"wrong a Jew, what should his sufferance be by" + 
															"Christian example? Why, revenge. The villany you" + 
															"teach me, I will execute, and it shall go hard but I" + 
															"will better the instruction.",

															"Virtue! a fig! 'tis in ourselves that we are thus" + 
																	"or thus. Our bodies are our gardens, to the which" + 
																	"our wills are gardeners: so that if we will plant" + 
																	"nettles, or sow lettuce, set hyssop and weed up" + 
																	"thyme, supply it with one gender of herbs, or" + 
																	"distract it with many, either to have it sterile" + 
																	"with idleness, or manured with industry, why, the" + 
																	"power and corrigible authority of this lies in our" + 
																	"wills. If the balance of our lives had not one" + 
																	"scale of reason to poise another of sensuality, the" + 
																	"blood and baseness of our natures would conduct us" + 
																	"to most preposterous conclusions: but we have" + 
																	"reason to cool our raging motions, our carnal" + 
																	"stings, our unbitted lusts, whereof I take this that" + 
																	"you call love to be a sect or scion.",

																	"Blow, winds, and crack your cheeks! rage! blow!" + 
																			"You cataracts and hurricanoes, spout" + 
																			"Till you have drench'd our steeples, drown'd the cocks!" + 
																			"You sulphurous and thought-executing fires," + 
																			"Vaunt-couriers to oak-cleaving thunderbolts," + 
																			"Singe my white head! And thou, all-shaking thunder," + 
																			"Smite flat the thick rotundity o' the world!" + 
																			"Crack nature's moulds, an germens spill at once," + 
																			"That make ingrateful man!"
			};
	}
}