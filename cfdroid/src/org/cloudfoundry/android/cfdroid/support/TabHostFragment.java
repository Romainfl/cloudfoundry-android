package org.cloudfoundry.android.cfdroid.support;

import java.util.HashMap;
import java.util.Map;

import org.cloudfoundry.android.cfdroid.R;
import org.cloudfoundry.android.cfdroid.applications.ApplicationControlFragment;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;

import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;

/**
 * A fragment that can display a {@link TabHost} whose contents are themselves
 * fragments.
 * 
 * @author ebottard
 * 
 */
public class TabHostFragment extends RoboSherlockFragment implements TabHost.OnTabChangeListener{

	@InjectView(android.R.id.tabhost)
	private TabHost tabHost;
	
	private TabContentFactory dummy = new TabContentFactory() {
		
		@Override
		public View createTabContent(String tag) {
			View view = new View(getActivity());
			view.setMinimumWidth(0);
			view.setMinimumHeight(0);
			return view;
		}
	};
	
	private static class TabInfo<F extends Fragment> {
		public TabInfo(Class<F> klass) {
			this.klass = klass;
		}
		private Class<? > klass;
		private F fragment;
	}
	
	private Map<String, TabInfo<?>> infos = new HashMap<String, TabHostFragment.TabInfo<?>>();
	
	private TabInfo<?> currentTabInfo;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tabs, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		tabHost.setup();
		tabHost.setOnTabChangedListener(this);
	}
	
	public <F extends Fragment> void addTab(String tag, Class<F> klass) {
		TabSpec tabSpec = tabHost.newTabSpec(tag);
		tabSpec.setContent(dummy);
		tabSpec.setIndicator(tag);
		TabInfo<F> info = new TabInfo<F>(klass);
		infos.put(tag, info);
		tabHost.addTab(tabSpec);
	}

	@Override
	public void onTabChanged(String tabId) {
		TabInfo info = infos.get(tabId);
		if (info != currentTabInfo) {
			FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
			if (currentTabInfo != null && currentTabInfo.fragment != null) {
				tx.detach(currentTabInfo.fragment);
			}

			if (info.fragment == null) {
				info.fragment = Fragment.instantiate(getActivity(), info.klass.getName());
				tx.add(R.id.realtabcontent, info.fragment, tabId);
			} else {
				tx.attach(info.fragment);
			}

			currentTabInfo = info;
			tx.commit();
			//getActivity().getSupportFragmentManager().executePendingTransactions();
		}
	}

}
