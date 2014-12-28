package lb.listviewvariants.utils.async_task_thread_pool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class ScalingQueue<T> extends LinkedBlockingQueue<T>
  {
  private static final long  serialVersionUID =2868771663367097439L;
  /**
   * The executor this Queue belongs to
   */
  private ThreadPoolExecutor executor;

  /**
   * Creates a TaskQueue with a capacity of {@link Integer#MAX_VALUE}.
   */
  public ScalingQueue()
    {
    super();
    }

  /**
   * Creates a TaskQueue with the given (fixed) capacity.
   *
   * @param capacity
   * the capacity of this queue.
   */
  public ScalingQueue(final int capacity)
    {
    super(capacity);
    }

  /**
   * Sets the executor this queue belongs to.
   */
  public void setThreadPoolExecutor(final ThreadPoolExecutor executor)
    {
    this.executor=executor;
    }

  /**
   * Inserts the specified element at the tail of this queue if there is at least one available thread to run the
   * current task. If all pool threads are actively busy, it rejects the offer.
   *
   * @param o
   * the element to add.
   * @return true if it was possible to add the element to this queue, else false
   * @see java.util.concurrent.ThreadPoolExecutor#execute(Runnable)
   */
  @Override
  public boolean offer(final T o)
    {
    final int allWorkingThreads=executor.getActiveCount()+super.size();
    return allWorkingThreads<executor.getPoolSize()&&super.offer(o);
    }
  }
