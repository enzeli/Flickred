package li.enze.flickred;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;

import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
				
		// Select Contact button action
		// http://stackoverflow.com/questions/4993063/how-to-call-android-contacts-list
		// -and-select-one-phone-number-from-its-details-s
		Button pick_button = (Button) findViewById(R.id.pick_button);
		pick_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
	            Intent intent = new Intent(Intent.ACTION_PICK);
	            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
	            startActivityForResult(intent, 1); 
			}
		});
		
		
		// Search Button Action
		Button search_button = (Button) findViewById(R.id.search_button);
		search_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText search_box = (EditText) findViewById(R.id.contact_text);
				
				String tag = search_box.getText().toString();
				if (tag.matches("")){
					Toast.makeText(getApplicationContext(),"Please input search tag", Toast.LENGTH_LONG).show();
				} else {
					
				
					Intent intent=new Intent();
					intent.putExtra("tag", tag);
					intent.setClass(MainActivity.this, ListViewActivity.class);
					startActivityForResult(intent, 1); 
				}
			}
		});
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Uri uri = data.getData();
	        if (uri != null) {
	            Cursor c = null;
	            try {
	                c = getContentResolver().query(uri, 
	                		new String[]{ ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME },
	                        null, null, null);

	                if (c != null && c.moveToFirst()) {
	                    String name = c.getString(0);
	                    putNameInSearchBox(name);
	                }
	            } finally {
	                if (c != null) {
	                    c.close();
	                }
	            }
	        }
	    }
	}

	public void putNameInSearchBox(String name) {
	    EditText contactEditText = (EditText) findViewById(R.id.contact_text);
	    contactEditText.setText(name);
	}

}
