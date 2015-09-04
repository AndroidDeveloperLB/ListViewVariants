package lb.library.cursor;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AlphabetIndexer;
import android.widget.CursorAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import lb.library.PinnedHeaderListView;

public abstract class BasePinnedHeaderCursorListViewAdapter extends CursorAdapter implements SectionIndexer, OnScrollListener, PinnedHeaderListView.PinnedHeaderAdapter {

	private SectionIndexer mSectionIndexer;
	private boolean mHeaderViewVisible = true;

	public BasePinnedHeaderCursorListViewAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		setSectionIndexer(new AlphabetIndexer(c, 1, "ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
	}

	public void setSectionIndexer(final SectionIndexer sectionIndexer) {
		mSectionIndexer = sectionIndexer;
	}

	public abstract CharSequence getSectionTitle(int sectionIndex);
	protected abstract TextView findHeaderView(View itemView);


	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		bindSectionHeader(findHeaderView(view), null, cursor.getPosition());
	}

	@Override
	public int getPinnedHeaderState(final int position) {
		if (mSectionIndexer == null || getCount() == 0 || !mHeaderViewVisible) {
			return PINNED_HEADER_GONE;
		}

		if (position < 0) {
			return PINNED_HEADER_GONE;
		}
		// The header should get pushed up if the top item shown
		// is the last item in a section for a particular letter.
		final int section = getSectionForPosition(position);
		final int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1 && position == nextSectionPosition - 1) {
			return PINNED_HEADER_PUSHED_UP;
		}
		return PINNED_HEADER_VISIBLE;
	}

	@Override
	public void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
	                     final int totalItemCount) {
		((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
	}

	@Override
	public void onScrollStateChanged(final AbsListView arg0, final int arg1) {
	}

	@Override
	public int getPositionForSection(final int sectionIndex) {
		if (mSectionIndexer == null) {
			return -1;
		}
		return mSectionIndexer.getPositionForSection(sectionIndex);
	}

	@Override
	public int getSectionForPosition(final int position) {
		if (mSectionIndexer == null) {
			return -1;
		}
		return mSectionIndexer.getSectionForPosition(position);
	}

	@Override
	public Object[] getSections() {
		if (mSectionIndexer == null) {
			return new String[]{" "};
		}
		return mSectionIndexer.getSections();
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	protected final SectionIndexer getSectionIndexer() {
		return mSectionIndexer;
	}

	protected void bindSectionHeader(final TextView headerView, final View dividerView, final int position) {
		final int sectionIndex = getSectionForPosition(position);
		if (getPositionForSection(sectionIndex) == position) {
			final CharSequence title = getSectionTitle(sectionIndex);
			headerView.setText(title);
			headerView.setVisibility(View.VISIBLE);
			if (dividerView != null) {
				dividerView.setVisibility(View.GONE);
			}
		} else {
			headerView.setVisibility(View.GONE);
			if (dividerView != null) {
				dividerView.setVisibility(View.VISIBLE);
			}
		}
		// move the divider for the last item in a section
		if (dividerView != null) {
			if (getPositionForSection(sectionIndex + 1) - 1 == position) {
				dividerView.setVisibility(View.GONE);
			} else {
				dividerView.setVisibility(View.VISIBLE);
			}
		}

		if (!mHeaderViewVisible) {
			headerView.setVisibility(View.GONE);
		}
	}
}
