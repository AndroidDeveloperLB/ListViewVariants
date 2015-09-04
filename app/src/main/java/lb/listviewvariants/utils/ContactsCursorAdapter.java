package lb.listviewvariants.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.Contacts;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import lb.library.cursor.SearchablePinnedHeaderCursorListViewAdapter;
import lb.listviewvariants.R;

public class ContactsCursorAdapter extends SearchablePinnedHeaderCursorListViewAdapter {

	public ContactsCursorAdapter(Context context) {
		super(context, getQuery(context), false);
	}

	private static Cursor getQuery(Context context) {
		return context.getContentResolver().query(Contacts.CONTENT_URI, new String[]{"_id", "display_name"}, null, null, "display_name asc");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		final View inflated = LayoutInflater.from(context).inflate(R.layout.listview_item, null);
		final ViewHolder holder = new ViewHolder();
		holder.name = (TextView) inflated.findViewById(R.id.listview_item__friendNameTextView);
		holder.headerView = (TextView) inflated.findViewById(R.id.header_text);
		inflated.setTag(holder);
		return inflated;
	}

	@Override
	protected TextView findHeaderView(View itemView) {
		return ((ViewHolder) itemView.getTag()).headerView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		super.bindView(view, context, cursor);
		final String name = cursor.getString(1);
		((ViewHolder) view.getTag()).name.setText(name);
	}

	@Override
	protected Cursor getFilterCursor(CharSequence constraint) {
		return null;
	}

	private static class ViewHolder {
		public TextView name;
		public TextView headerView;
	}
}
