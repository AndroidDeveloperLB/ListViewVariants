package lb.library;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * A ListView that maintains a header pinned at the top of the list. The pinned header can be pushed up and dissolved as
 * needed.
 */
public class PinnedHeaderListView extends ListView
  {
  private boolean mEnableHeaderTransparencyChanges=true;

  /**
   * Adapter interface. The list adapter must implement this interface.
   */
  public interface PinnedHeaderAdapter
    {
    /**
     * Pinned header state: don't show the header.
     */
    public static final int PINNED_HEADER_GONE=0;
    /**
     * Pinned header state: show the header at the top of the list.
     */
    public static final int PINNED_HEADER_VISIBLE=1;
    /**
     * Pinned header state: show the header. If the header extends beyond the bottom of the first shown element,
     * push it up and clip.
     */
    public static final int PINNED_HEADER_PUSHED_UP=2;

    /**
     * Computes the desired state of the pinned header for the given position of the first visible list item.
     * Allowed return values are {@link #PINNED_HEADER_GONE}, {@link #PINNED_HEADER_VISIBLE} or
     * {@link #PINNED_HEADER_PUSHED_UP}.
     */
    int getPinnedHeaderState(int position);

    /**
     * Configures the pinned header view to match the first visible list item.
     *
     * @param header   pinned header view.
     * @param position position of the first visible list item.
     * @param alpha    fading of the header view, between 0 and 255.
     */
    void configurePinnedHeader(View header,int position,int alpha);
    }

  private static final int MAX_ALPHA=255;
  private PinnedHeaderAdapter mAdapter;
  private View mHeaderView;
  private boolean mHeaderViewVisible;
  private int mHeaderViewWidth;
  private int mHeaderViewHeight;

  public PinnedHeaderListView(final Context context)
    {
    super(context);
    }

  public PinnedHeaderListView(final Context context,final AttributeSet attrs)
    {
    super(context,attrs);
    }

  public PinnedHeaderListView(final Context context,final AttributeSet attrs,final int defStyle)
    {
    super(context,attrs,defStyle);
    }

  public void setPinnedHeaderView(final View view)
    {
    mHeaderView=view;
    // Disable vertical fading when the pinned header is present
    // TODO change ListView to allow separate measures for top and bottom fading edge;
    // in this particular case we would like to disable the top, but not the bottom edge.
    if(mHeaderView!=null)
      setFadingEdgeLength(0);
    requestLayout();
    }

  @Override
  public void setAdapter(final ListAdapter adapter)
    {
    super.setAdapter(adapter);
    if(!isInEditMode())
      mAdapter=(PinnedHeaderAdapter)adapter;
    }

  @Override
  protected void onMeasure(final int widthMeasureSpec,final int heightMeasureSpec)
    {
    super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    if(mHeaderView!=null)
      {
      measureChild(mHeaderView,widthMeasureSpec,heightMeasureSpec);
      mHeaderViewWidth=mHeaderView.getMeasuredWidth();
      mHeaderViewHeight=mHeaderView.getMeasuredHeight();
      }
    }

  @Override
  protected void onLayout(final boolean changed,final int left,final int top,final int right,final int bottom)
    {
    super.onLayout(changed,left,top,right,bottom);
    if(mHeaderView!=null)
      {
      mHeaderView.layout(0,0,mHeaderViewWidth,mHeaderViewHeight);
      configureHeaderView(getFirstVisiblePosition());
      }
    }

  public void configureHeaderView(final int position)
    {
    try
      {
      if(mHeaderView==null||mAdapter==null)
        return;
      final int state=mAdapter.getPinnedHeaderState(position);
      switch(state)
        {
        case PinnedHeaderAdapter.PINNED_HEADER_GONE:
        {
        mHeaderViewVisible=false;
        break;
        }
        case PinnedHeaderAdapter.PINNED_HEADER_VISIBLE:
        {
        mAdapter.configurePinnedHeader(mHeaderView,position,MAX_ALPHA);
        if(mHeaderView.getTop()!=0)
          mHeaderView.layout(0,0,mHeaderViewWidth,mHeaderViewHeight);
        mHeaderViewVisible=true;
        break;
        }
        case PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP:
        {
        final View firstView=getChildAt(0);
        if(firstView==null)
          break;
        final int bottom=firstView.getBottom();
        // final int itemHeight=firstView.getHeight();
        final int headerHeight=mHeaderView.getHeight();
        int y, alpha;
        if(bottom<headerHeight)
          {
          y=bottom-headerHeight;
          alpha=MAX_ALPHA*(headerHeight+y)/headerHeight;
          }
        else
          {
          y=0;
          alpha=MAX_ALPHA;
          }
        mAdapter.configurePinnedHeader(mHeaderView,position,mEnableHeaderTransparencyChanges?alpha:MAX_ALPHA);
        if(mHeaderView.getTop()!=y)
          mHeaderView.layout(0,y,mHeaderViewWidth,mHeaderViewHeight+y);
        mHeaderViewVisible=true;
        break;
        }
        }
      }
    catch(Exception ex)
      {
      }
    }

  public void setEnableHeaderTransparencyChanges(boolean mEnableHeaderTransparencyChanges)
    {
    this.mEnableHeaderTransparencyChanges=mEnableHeaderTransparencyChanges;
    }

  @Override
  protected void dispatchDraw(final Canvas canvas)
    {
    super.dispatchDraw(canvas);
    if(mHeaderViewVisible)
      drawChild(canvas,mHeaderView,getDrawingTime());
    }
  }
