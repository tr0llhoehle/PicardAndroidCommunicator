package de.tr0llhoehle.android.picardcommunicator;

import com.MobileAnarchy.Android.Widgets.Joystick.DualJoystickView;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickMovedListener;
import com.MobileAnarchy.Android.Widgets.Joystick.JoystickView;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class DualJoystickActivity extends Activity {

	TextView txtX1, txtY1;
	TextView txtX2, txtY2;
	DualJoystickView joystick;
		
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
	            Toast.makeText(this, "settings pressed", Toast.LENGTH_SHORT).show();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
