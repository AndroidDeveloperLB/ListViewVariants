package lb.library;

import android.widget.SectionIndexer;

public class SectionedSectionIndexer implements SectionIndexer {
	private final SimpleSection[] mSectionArray;

	public SectionedSectionIndexer(final SimpleSection[] sections) {
		mSectionArray = sections;
		//
		int previousIndex = 0;
		for (int i = 0; i < mSectionArray.length; ++i) {
			mSectionArray[i].startIndex = previousIndex;
			previousIndex += mSectionArray[i].getItemsCount();
			mSectionArray[i].endIndex = previousIndex - 1;
		}
	}

	@Override
	public int getPositionForSection(final int section) {
		final int result = section < 0 || section >= mSectionArray.length ? -1 : mSectionArray[section].startIndex;
		return result;
	}

	/** given a flat position, returns the position within the section */
	public int getPositionInSection(final int flatPos) {
		final int sectionForPosition = getSectionForPosition(flatPos);
		final SimpleSection simpleSection = mSectionArray[sectionForPosition];
		return flatPos - simpleSection.startIndex;
	}

	@Override
	public int getSectionForPosition(final int flatPos) {
		if (flatPos < 0)
			return -1;
		int start = 0, end = mSectionArray.length - 1;
		int piv = (start + end) / 2;
		while (true) {
			final SimpleSection section = mSectionArray[piv];
			if (flatPos >= section.startIndex && flatPos <= section.endIndex)
				return piv;
			if (piv == start && start == end)
				return -1;
			if (flatPos < section.startIndex)
				end = piv - 1;
			else
				start = piv + 1;
			piv = (start + end) / 2;
		}
	}

	@Override
	public SimpleSection[] getSections() {
		return mSectionArray;
	}

	public Object getItem(final int flatPos) {
		final int sectionIndex = getSectionForPosition(flatPos);
		final SimpleSection section = mSectionArray[sectionIndex];
		final Object result = section.getItem(flatPos - section.startIndex);
		return result;
	}

	public Object getItem(final int sectionIndex, final int positionInSection) {
		final SimpleSection section = mSectionArray[sectionIndex];
		final Object result = section.getItem(positionInSection);
		return result;
	}

	public int getRawPosition(final int sectionIndex, final int positionInSection) {
		final SimpleSection section = mSectionArray[sectionIndex];
		return section.startIndex + positionInSection;
	}

	public int getItemsCount() {
		if (mSectionArray.length == 0)
			return 0;
		return mSectionArray[mSectionArray.length - 1].endIndex + 1;
	}

	// /////////////////////////////////////////////
	// Section //
	// //////////
	public static abstract class SimpleSection {
		private String name;
		private int startIndex, endIndex;

		public SimpleSection() {
		}

		public SimpleSection(final String sectionName) {
			this.name = sectionName;
		}

		public String getName() {
			return name;
		}

		public void setName(final String name) {
			this.name = name;
		}

		public abstract int getItemsCount();

		public abstract Object getItem(int posInSection);

  @Override
  public String toString()
    {
    return name;
    }
  }

}
