package org.cloudfoundry.android.cfdroid.support.masterdetail;

import java.util.List;

/**
 * Used by {@link MasterDetailActivity} to make state available to the detail fragment.
 * 
 * @author ebottard
 *
 * @param <I> the kind of domain object we're working with
 */
public interface DataHolder<I> {
	
	List<I> getData();

}