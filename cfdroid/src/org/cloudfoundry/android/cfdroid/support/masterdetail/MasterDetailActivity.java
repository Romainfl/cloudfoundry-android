package org.cloudfoundry.android.cfdroid.support.masterdetail;

import javax.annotation.Nullable;

import org.cloudfoundry.android.cfdroid.R;

import roboguice.inject.InjectFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockFragmentActivity;

/**
 * Helper class for doing layout dependent master/detail activities. Subclasses
 * should set a content view that will either
 * <ul>
 * <li>have a single {@link FrameLayout} with id {@code fragment_container}</li>
 * <li>have a two-pane layout containing fragments with ids {@code left_pane}
 * and {@code right_pane}</li>
 * </ul>
 * 
 * 
 * @author ebottard
 * 
 * @param <I>
 *            the type of domain object this activity manages
 * @param <M>
 *            the actual type of the "master" fragment
 * @param <D>
 *            the actual type of the "detail" fragment
 */
public abstract class MasterDetailActivity<D extends Fragment & DetailPaneEventsCallback>
		extends RoboSherlockFragmentActivity implements
		MasterDetailEventsCallback {

	@Nullable
	@InjectFragment(R.id.right_pane)
	private D rightPane;

	private int position = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			position = savedInstanceState.getInt(
					MasterDetailEventsCallback.KEY_SELECTION, -1);
			if (rightPane != null) {
				// two-pane layout, direct update
				rightPane.selectionChanged(this.position);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(MasterDetailEventsCallback.KEY_SELECTION, position);
	}

	@Override
	public void onLeftPaneSelection(int position) {
		this.position = position;
		if (rightPane != null) {
			// two-pane layout, direct update
			rightPane.selectionChanged(this.position);
		} else {
			Intent intent = new Intent(this, RightPaneHoldingActivity.class);
			intent.putExtra(RIGHT_PANE_LAYOUT_ID, rightPaneLayout());
			intent.putExtra(KEY_SELECTION, position);
			
			startActivity(intent);
		}
	}

	protected abstract int rightPaneLayout();

}
