package lb.library;

import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.TextView;

public abstract class IndexedPinnedHeaderListViewAdapter extends BasePinnedHeaderListViewAdapter
  {
  private int _pinnedHeaderBackgroundColor;
  private int _pinnedHeaderTextColor;

  public void setPinnedHeaderBackgroundColor(final int pinnedHeaderBackgroundColor)
    {
    _pinnedHeaderBackgroundColor=pinnedHeaderBackgroundColor;
    }

  public void setPinnedHeaderTextColor(final int pinnedHeaderTextColor)
    {
    _pinnedHeaderTextColor=pinnedHeaderTextColor;
    }

  @Override
  public CharSequence getSectionTitle(final int sectionIndex)
    {
    return getSections()[sectionIndex].toString();
    }

  @Override
  public void configurePinnedHeader(final View v,final int position,final int alpha)
    {
    final TextView header=(TextView)v;
    final int sectionIndex=getSectionForPosition(position);
    final Object[] sections=getSections();
    if(sections!=null&&sections.length!=0)
      {
      final CharSequence title=getSectionTitle(sectionIndex);
      header.setText(title);
      }
    if(VERSION.SDK_INT<VERSION_CODES.HONEYCOMB)
      if(alpha==255)
        {
        header.setBackgroundColor(_pinnedHeaderBackgroundColor);
        header.setTextColor(_pinnedHeaderTextColor);
        }
      else
        {
        header.setBackgroundColor(Color.argb(alpha,Color.red(_pinnedHeaderBackgroundColor),
            Color.green(_pinnedHeaderBackgroundColor),Color.blue(_pinnedHeaderBackgroundColor)));
        header.setTextColor(Color.argb(alpha,Color.red(_pinnedHeaderTextColor),
            Color.green(_pinnedHeaderTextColor),Color.blue(_pinnedHeaderTextColor)));
        }
    else
      {
      header.setBackgroundColor(_pinnedHeaderBackgroundColor);
      header.setTextColor(_pinnedHeaderTextColor);
      header.setAlpha(alpha/255.0f);
      }
    }

  }
