package lb.listviewvariants.utils.async_task_thread_pool;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ScalingThreadPoolExecutor extends ThreadPoolExecutor
  {
  /**
   * number of threads that are actively executing tasks
   */
  private final AtomicInteger activeCount =new AtomicInteger();

  @SuppressWarnings("unchecked")
  public ScalingThreadPoolExecutor(final int corePoolSize,final int maximumPoolSize,final long keepAliveTime,final TimeUnit unit,@SuppressWarnings("rawtypes") final BlockingQueue workQueue,final ThreadFactory threadFactory)
    {
    super(corePoolSize,maximumPoolSize,keepAliveTime,unit,workQueue,threadFactory);
    }

  @Override
  public int getActiveCount()
    {
    return activeCount.get();
    }

  @Override
  protected void beforeExecute(final Thread t,final Runnable r)
    {
    activeCount.incrementAndGet();
    }

  @Override
  protected void afterExecute(final Runnable r,final Throwable t)
    {
    activeCount.decrementAndGet();
    }
  }
