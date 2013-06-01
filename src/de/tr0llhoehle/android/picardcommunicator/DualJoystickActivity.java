package de.tr0llhoehle.android.picardcommunicator;

import java.io.IOException;

import org.json.JSONException;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DualJoystickActivity extends Activity {

	TextView txtX1, txtY1;
	TextView txtX2, txtY2;
	DualJoystickView joystick;
	
	private Communicator comm;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dualjoystick);

		txtX1 = (TextView)findViewById(R.id.TextViewX1);
        txtY1 = (TextView)findViewById(R.id.TextViewY1);
        
		txtX2 = (TextView)findViewById(R.id.TextViewX2);
        txtY2 = (TextView)findViewById(R.id.TextViewY2);

        joystick = (DualJoystickView)findViewById(R.id.dualjoystickView);
        
        joystick.setOnJostickMovedListener(_listenerLeft, _listenerRight);
        
        this.comm = new Communicator(this);
	}

    private JoystickMovedListener _listenerLeft = new JoystickMovedListener() {

		@Override
		public void OnMoved(int pan, int tilt) {
			txtX1.setText(Integer.toString(pan));
			txtY1.setText(Integer.toString(tilt));
		}

		@Override
		public void OnReleased() {
			txtX1.setText("released");
			txtY1.setText("released");
		}
		
		public void OnReturnedToCenter() {
			txtX1.setText("stopped");
			txtY1.setText("stopped");
			try {
				comm.authenticate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	}; 

    private JoystickMovedListener _listenerRight = new JoystickMovedListener() {

		@Override
		public void OnMoved(int pan, int tilt) {
			txtX2.setText(Integer.toString(pan));
			txtY2.setText(Integer.toString(tilt));
		}

		@Override
		public void OnReleased() {
			txtX2.setText("released");
			txtY2.setText("released");
		}
		
		public void OnReturnedToCenter() {
			txtX2.setText("stopped");
			txtY2.setText("stopped");
		};
	}; 
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.dual_joystick, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	//SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	        	//String bla = sharedPref.getString("username", "");
	            //Toast.makeText(this, bla, Toast.LENGTH_SHORT).show();
	            Intent myIntent = new Intent(this, SettingsActivity.class);
	            startActivity(myIntent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
