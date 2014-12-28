package lb.library;

import android.widget.BaseAdapter;
import android.widget.SectionIndexer;

/**
 * a base adapter that allows having multiple sections. each section has its own items. items don't have to be of the
 * same type
 */
public abstract class BaseSectionedAdapter extends BaseAdapter implements SectionIndexer {
	private SectionedSectionIndexer mSectionIndexer;

	public void setSectionIndexer(final SectionedSectionIndexer sectionIndexer) {
		mSectionIndexer = sectionIndexer;
	}

	public SectionIndexer getSectionIndexer() {
		return this.mSectionIndexer;
	}

	@Override
	public int getPositionForSection(final int sectionIndex) {
		if (mSectionIndexer == null)
			return -1;
		return mSectionIndexer.getPositionForSection(sectionIndex);
	}

	@Override
	public int getSectionForPosition(final int position) {
		if (mSectionIndexer == null)
			return -1;
		return mSectionIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		if (mSectionIndexer == null)
			return new String[] { " " };
		return mSectionIndexer.getSections();
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mSectionIndexer.getItemsCount();
	}

	@Override
	public Object getItem(final int position) {
		return mSectionIndexer.getItem(position);
	}

	@Override
	public int getViewTypeCount() {
		return mSectionIndexer.getSections().length;
	}

	@Override
	public int getItemViewType(final int position) {
		return mSectionIndexer.getSectionForPosition(position);
	}
}
