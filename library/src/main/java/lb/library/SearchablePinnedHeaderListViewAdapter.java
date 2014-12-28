package lb.library;

import android.text.TextUtils;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;


/**
 * This gives the ability of searching in a pinnedHead list view
 */
public abstract class SearchablePinnedHeaderListViewAdapter<T> extends IndexedPinnedHeaderListViewAdapter implements
		Filterable {
	private ArrayList<T> mFilterListCopy;
	private final Filter mFilter;

	public SearchablePinnedHeaderListViewAdapter() {
		mFilter = new Filter() {
			CharSequence lastConstraint = null;

			@Override
			protected FilterResults performFiltering(final CharSequence constraint) {
				if (constraint == null || constraint.length() == 0)
					return null;
				final ArrayList<T> newFilterArray = new ArrayList<T>();
				final FilterResults results = new FilterResults();
				for (final T item : getOriginalList())
					if (doFilter(item, constraint))
						newFilterArray.add(item);
				results.values = newFilterArray;
				results.count = newFilterArray.size();
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(final CharSequence constraint, final FilterResults results) {
				mFilterListCopy = results == null ? null : (ArrayList<T>) results.values;
				final boolean needRefresh = !TextUtils.equals(constraint,lastConstraint);
				lastConstraint = constraint == null ? null : constraint;
				if (needRefresh)
					notifyDataSetChanged();
			}
		};
	}

	@Override
	public Filter getFilter() {
		return mFilter;
	}

	/** returns true iff the item can "pass" the filtering process and should be shown */
	public abstract boolean doFilter(T item, CharSequence constraint);

	public abstract ArrayList<T> getOriginalList();

	@Override
	public T getItem(final int position) {
		if (position < 0)
			return null;
		final ArrayList<T> listCopy = getFilterListCopy();
		if (listCopy != null) {
			if (position < listCopy.size())
				return listCopy.get(position);
			else
				return null;
		} else {
			final ArrayList<T> originalList = getOriginalList();
			if (position < originalList.size())
				return originalList.get(position);
			else
				return null;
		}
	}

	@Override
	public int getCount() {
		final ArrayList<T> listCopy = getFilterListCopy();
		if (listCopy != null)
			return listCopy.size();
		else
			return getOriginalList().size();
	}

	private ArrayList<T> getFilterListCopy() {
		return mFilterListCopy;
	}

}
