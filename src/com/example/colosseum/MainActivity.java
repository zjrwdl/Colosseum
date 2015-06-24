package com.example.colosseum;


import com.example.colosseum.GlowPadView.OnTriggerListener;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends ActionBarActivity implements OnTriggerListener {
	GlowPadView mGlowPadView;
	ImageView mImageView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGlowPadView = (GlowPadView)this.findViewById(R.id.incomingCallWidget);
		mGlowPadView.setAlpha(1);
		mGlowPadView.setVisibility(View.VISIBLE);
		
		
//		mImageView = (ImageView)this.findViewById(R.id.image);
//		
//		Animation animation = AnimationUtils
//				.loadAnimation(this, R.anim.glow);
//		mImageView.startAnimation(animation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onGrabbed(View v, int handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReleased(View v, int handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTrigger(View v, int target) {
		// TODO Auto-generated method stub
		Log.i("TEST", "target ="+target);
	}

	@Override
	public void onGrabbedStateChange(View v, int handle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinishFinalAnimation() {
		// TODO Auto-generated method stub
		
	}
}
