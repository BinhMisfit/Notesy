package com.example.notesy;


import com.example.notesy.R;
import com.example.notesy.UploadNote;
import android.view.Menu;


import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;



import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class MainActivity extends Activity {
   
	// Class hien dang lap trinh se ke thua lop Activity.
		private static final String TAG = "Notesy";

	    ///////////////////////////////////////////////////////////////////////////
	    //                      Your app-specific settings.                      //
	    ///////////////////////////////////////////////////////////////////////////

	    // Replace this with your app key and secret assigned by Dropbox.
	    // Note that this is a really insecure way to do this, and you shouldn't
	    // ship code which contains your key & secret in such an obvious way.
	    // Obfuscation is good.
	    final static private String APP_KEY = "e7aokddab896xlt";
	    final static private String APP_SECRET = "hda6sr8v389praq";

	    // If you'd like to change the access type to the full Dropbox instead of
	    // an app folder, change this value.
	    final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	    ///////////////////////////////////////////////////////////////////////////
	    //                      End app-specific settings.                       //
	    ///////////////////////////////////////////////////////////////////////////

	    // You don't need to change these, leave them alone.
	    final static private String ACCOUNT_PREFS_NAME = "prefs";
	    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
	    private final String	NOTE_DIR="/Notes/";

	    DropboxAPI<AndroidAuthSession> mApi;

	    private boolean mLoggedIn;

	    // Android widgets
	    private Button mSubmit;   // Co them mot button Submit o day ne!


	
	    EditText myText;
	

   
   @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		// When starting the application, we have to do the following step:
        // We create a new AuthSession so that we can use the Dropbox API.
        // Step 1:
        AndroidAuthSession session = buildSession();
        mApi = new DropboxAPI<AndroidAuthSession>(session);

        // Basic Android widgets
		setContentView(R.layout.activity_main);

        checkAppKeySetup();
        mSubmit = (Button)findViewById(R.id.auth_button);
        
        mSubmit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // This logs you out if you're logged in, or vice versa
                if (mLoggedIn) {
                    logOut();
                } else {
                    // Start the remote authentication
                    mApi.getSession().startAuthentication(MainActivity.this);
                }
            }
        });


		// Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }
	    
	    // We will try to create a Text-file and save it into SDcard.
	    
	    myText =(EditText)findViewById(R.id.myText);
        Button  createButton=(Button)findViewById(R.id.btnCreate);
        Button readButton=(Button)findViewById(R.id.btnRead);
        createButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//createFile(myText.getText().toString());
				//myText.setText("");
				
				try {
		            File myFile = new File("/sdcard/mysdfile.txt");
		            
		            
		            
		            
		            
                    myFile.createNewFile();
		            
		            FileOutputStream fOut = new FileOutputStream(myFile);
		            OutputStreamWriter myOutWriter =new OutputStreamWriter(fOut);
		            myOutWriter.append(myText.getText());
		            myOutWriter.close();
		            fOut.close();
		            Toast.makeText(v.getContext(),"Done writing SD 'mysdfile.txt'", Toast.LENGTH_SHORT).show();
		            myText.setText("");// Sau khi luu lai thi xoa di bang ghi text tren dien thoai.
		            
		            

		            UploadNote upload = new UploadNote(MainActivity.this, mApi, NOTE_DIR, myFile);
                    upload.execute();
		            
		        } 
		        catch (Exception e) 
		        {
		            Toast.makeText(v.getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
		        }
			}
		});
        // If an vo button readText.
        readButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				//readFile();

				try {

		            File myFile = new File("/sdcard/mysdfile.txt");
		            FileInputStream fIn = new FileInputStream(myFile);
		            BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
		            String aDataRow = "";
		            String aBuffer = "";
		            while ((aDataRow = myReader.readLine()) != null) 
		            {
		                aBuffer += aDataRow ;
		                aBuffer+="\n"; // De xuong dong cho de dang hon. 
		            }
		            myText.setText(aBuffer);
		            myReader.close();
		            Toast.makeText(v.getContext(),"Done reading SD 'mysdfile.txt'",Toast.LENGTH_SHORT).show();
		        } 
		        catch (Exception e)
		        {
		            Toast.makeText(v.getContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
		        }
			}
		});
 
        // Upload this file
        
        Button UploadButton=(Button)findViewById(R.id.btnUpLoad);
        UploadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileInputStream inputStream = null;
				try {
				    File file = new File("/sdcard/mysdfile.txt");
				    inputStream = new FileInputStream(file);
				    Entry newEntry = mApi.putFile("/testing.txt", inputStream,
				            file.length(), null, null);
				    Log.i("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
				    Toast.makeText(v.getContext(),"Done uploading SD 'mysdfile.txt' to Dropbox!",Toast.LENGTH_SHORT).show();

				} catch (DropboxUnlinkedException e) {
				    // User has unlinked, ask them to link again here.
				    Log.e("DbExampleLog", "User has unlinked.");
				} catch (DropboxException e) {
				    Log.e("DbExampleLog", "Something went wrong while uploading.");
				} catch (FileNotFoundException e) {
				    Log.e("DbExampleLog", "File not found.");
				} finally {
				    if (inputStream != null) {
				        try {
				            inputStream.close();
				        } catch (IOException e) {}
				    }
				}
			}
		});
        
        
        //Download this file.
        
        Button DownloadButton = (Button)findViewById(R.id.btnDownLoad);
        DownloadButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				DownloadRandomNote download = new DownloadRandomNote(MainActivity.this, mApi, NOTE_DIR, myText); // Goi class DownloadRandomJava de thuc thi nhiem vu.
                download.execute(); // download the picture ve may tinh.
				
				
				/*FileOutputStream outputStream = null;
				try {
					
				    File file = new File("/sdcard/mysdfile1.txt");
				    outputStream = new FileOutputStream(file);
				    DropboxFileInfo info = mApi.getFile("/testing.txt", null, outputStream, null);
				    Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
				    Toast.makeText(v.getContext(),"Done downloading file 'text.txt' in Dropbox to SD 'mysdfile.txt'!",Toast.LENGTH_SHORT).show();

				    // /path/to/new/file.txt now has stuff in it.
				} catch (DropboxException e) {
				    Log.e("DbExampleLog", "Something went wrong while downloading.");
				} catch (FileNotFoundException e) {
				    Log.e("DbExampleLog", "File not found.");
				} finally {
				    if (outputStream != null) {
				        try {
				            outputStream.close();
				        } catch (IOException e) {}
				    }
				}*/
			}
		});
        // Display the proper UI state if logged in or not
       setLoggedIn(mApi.getSession().isLinked()); //tra lai man hinh chinh cua application.

	    
	}

	private void doMySearch(String query) {
		// TODO Auto-generated method stub
		
	}

    private void createFile(String Text){

    	FileOutputStream fos=null;
    	
    	
    	try {
			fos=openFileOutput("mynote.txt", MODE_PRIVATE);
			fos.write(Text.getBytes());
			Toast.makeText(getApplicationContext(), "File created succesfully", Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			 Log.e("CreateFile", e.getLocalizedMessage());
		}
		catch (IOException e) {
			 Log.e("CreateFile", e.getLocalizedMessage());
		}

		finally{
			if(fos!=null){
				try {
					// drain the stream
					fos.flush();
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }

    private void readFile(){

    	FileInputStream fis;

    	try {
			fis=openFileInput("mynote.txt");
			byte[] reader=new byte[fis.available()];
			while (fis.read(reader)!=-1) {

			}
		    myText.setText(new String(reader));
		    Toast.makeText(getApplicationContext(), "File read succesfully", Toast.LENGTH_SHORT).show();
		    if(fis!=null){
		    	fis.close();
		    }
		}
    	catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			Log.e("Read File", e.getLocalizedMessage());
		}

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.layout.activity_main, menu);
		return true;
	}
	
    @Override
    protected void onResume() {
        super.onResume();
        AndroidAuthSession session = mApi.getSession();

        // The next part must be inserted in the onResume() method of the
        // activity from which session.startAuthentication() was called, so
        // that Dropbox authentication completes properly.
        if (session.authenticationSuccessful()) {
            try {
                // Mandatory call to complete the auth
                session.finishAuthentication();

                // Store it locally in our app for later use
                TokenPair tokens = session.getAccessTokenPair();
                storeKeys(tokens.key, tokens.secret);
                setLoggedIn(true);
            } catch (IllegalStateException e) {
                showToast("Couldn't authenticate with Dropbox:" + e.getLocalizedMessage());
                Log.i(TAG, "Error authenticating", e);
            }
        }
    }


    private void checkAppKeySetup() {
        // Check to make sure that we have a valid app key
        if (APP_KEY.startsWith("CHANGE") ||
                APP_SECRET.startsWith("CHANGE")) {
            showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
            finish();
            return;
        }

        // Check if the app has set up its manifest properly.
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        String scheme = "db-" + APP_KEY;
        String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
        testIntent.setData(Uri.parse(uri));
        PackageManager pm = getPackageManager();
        if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
            showToast("URL scheme in your app's " +
                    "manifest is not set up correctly. You should have a " +
                    "com.dropbox.client2.android.AuthActivity with the " +
                    "scheme: " + scheme);
            finish();
        }
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        error.show();
    }


    /**
     * Convenience function to change UI state based on being logged in
     */
    private void logOut() {
        // Remove credentials from the session
        mApi.getSession().unlink();

        // Clear our stored keys
        clearKeys();
        // Change UI state to display logged out version
        setLoggedIn(false);
    }


    
    private void setLoggedIn(boolean loggedIn) {
    	mLoggedIn = loggedIn;
    	if (loggedIn) {
    		mSubmit.setText("Unlink from Dropbox");
           // mDisplay.setVisibility(View.VISIBLE);
    	} else {
    		mSubmit.setText("Link with Dropbox");
          //  mDisplay.setVisibility(View.GONE);
          //  mImage.setImageDrawable(null);
    	}
    }

	/**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
    private String[] getKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }

	
	
    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     */
    private void storeKeys(String key, String secret) {
        // Save the access key for later
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.putString(ACCESS_KEY_NAME, key);
        edit.putString(ACCESS_SECRET_NAME, secret);
        edit.commit();
    }

    private void clearKeys() {
        SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        Editor edit = prefs.edit();
        edit.clear();
        edit.commit();
    }

    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session;

        String[] stored = getKeys();
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }

}

