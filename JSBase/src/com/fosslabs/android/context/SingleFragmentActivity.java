package com.fosslabs.android.context;

import com.fosslabs.android.jsbase.R;
import com.fosslabs.android.utils.Funs;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public abstract class SingleFragmentActivity extends Activity {
	protected abstract Fragment createFragment();
	private boolean mReveseLandscape = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Funs.hiddeTitleBar(this, mReveseLandscape);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragment);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                .add(R.id.fragment, fragment)
                .commit();
        }
    }
    
    /*public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
	}*/
}
