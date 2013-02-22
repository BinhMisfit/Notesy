package com.example.notesy;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Write extends Activity {
    /** Called when the activity is first created. */
	EditText myText;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myText =(EditText)findViewById(R.id.myText);
        Button  createButton=(Button)findViewById(R.id.btnCreate);
        Button readButton=(Button)findViewById(R.id.btnRead);
        createButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				createFile(myText.getText().toString());
				myText.setText("");
			}
		});

        readButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				readFile();

			}
		});
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

}
