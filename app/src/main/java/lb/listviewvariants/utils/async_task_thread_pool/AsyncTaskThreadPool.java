package lb.listviewvariants.utils.async_task_thread_pool;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * a thread pool that contains tasks to run like on asyncTask. <br/>
 * also has the ability to use a stack instead of a queue for the order of tasks
 */
public class AsyncTaskThreadPool
  {
  // classes used for the threadPool were created because of an issue with customized parameters of
  // ThreadPoolExecutor:
  // https://github.com/kimchy/kimchy.github.com/blob/master/_posts/2008-11-23-juc-executorservice-gotcha.textile
  private static final int              TIME_TO_KEEP_ALIVE_IN_SECONDS =10;
  private static final int              CORE_POOL_SIZE                =1;
  private static final int              MAXIMUM_POOL_SIZE             =Math.max(CORE_POOL_SIZE,Runtime.getRuntime().availableProcessors()-1);
  private final ThreadPoolExecutor      mExecutor;
  // private final Executor mExecutor = Executors.newSingleThreadExecutor();
  // private final Executor mExecutor = Executors.newCachedThreadPool();
  // private final Executor mExecutor = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE);
  // private final Executor mExecutor = Executors.newScheduledThreadPool(corePoolSize, threadFactory)
  private final Set<AsyncTaskEx<?,?,?>> mTasks                        =new HashSet<>();

  public AsyncTaskThreadPool()
    {
    this(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,TIME_TO_KEEP_ALIVE_IN_SECONDS);
    }

  public AsyncTaskThreadPool(final int minNumberOfThread,final int maxNumberOfThread,final int keepAliveTimeInSeconds)
    {
    final ScalingQueue<Runnable> poolWorkQueue=new ScalingQueue<>();
    final ThreadFactory threadFactory=new ThreadFactory()
      {
        private final AtomicInteger mCount =new AtomicInteger(1);

        @Override
        public Thread newThread(final Runnable r)
          {
          final Thread thread=new Thread(r,"thread #"+mCount.getAndIncrement());
          thread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
          return thread;
          }
      };
    // needed because normal ThreadPoolExecutor always uses a single thread.
    mExecutor=new ScalingThreadPoolExecutor(minNumberOfThread,maxNumberOfThread,keepAliveTimeInSeconds,TimeUnit.SECONDS,poolWorkQueue,threadFactory);
    mExecutor.setRejectedExecutionHandler(new ForceQueuePolicy());
    poolWorkQueue.setThreadPoolExecutor(mExecutor);
    }

  /** runs the task and remembers it till it finishes. */
  public <Params,Progress,Result> void executeAsyncTask(final AsyncTaskEx<Params,Progress,Result> task,@SuppressWarnings("unchecked") final Params... params)
    {
    task.addOnFinishedListener(new AsyncTaskEx.IOnFinishedListener()
      {
        @Override
        public void onFinished()
          {
          mTasks.remove(task);
          task.removeOnFinishedListener(this);
          }
      });
    mTasks.add(task);
    task.executeOnExecutor(mExecutor,params);
    }

  public <Params,Progress,Result> void executeAsyncTask(final AsyncTaskEx<Params,Progress,Result> task)
    {
    //noinspection unchecked
    executeAsyncTask(task,(Params[])null);
    }

  public void cancelAllTasks(final boolean alsoInterrupt)
    {
    for(final AsyncTaskEx<?,?,?> task : mTasks)
      task.cancel(alsoInterrupt);
    mTasks.clear();
    }
  }
