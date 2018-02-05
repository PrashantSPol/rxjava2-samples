package rxjava2;

import java.util.concurrent.CountDownLatch;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import rxjava2.common.Logger;

public class SchedulerSample {
	public static void main(String[] args) throws InterruptedException {
		// here everything works on mainThread
		mainThreadExecDemo();
		
		// here work is done on cached io thread
		subscribeOnDemo();
		
		// here map function and subscription works on different threads
		observeOnDemo();
	}
	
	/*
	 * All operations will be done on main Thread
	 */
	static void mainThreadExecDemo() {
		Logger.log("Starting mainThreadExecDemo");
		Observable<Integer> observable = Observable.create(emitter -> {
			Logger.log("Starting observable");
			
			Logger.log("Pushing 1");
			emitter.onNext(1);
			
			blockingOperation();
			
			Logger.log("Pushing 2");
			emitter.onNext(2);
			
			emitter.onComplete();
		});
		
		observable = observable.map(val -> {
			Logger.log("Mapping %s to %s", val, val * 10);
			return val * 10;
		});
		
		subscribe(observable, null);
		
		Logger.log("=========================================================");
		
		/*   *********************** Sample Output *************************
		 *  2018:52:36 14:28 Thread[main,5,main] Starting mainThreadExecDemo 
			2018:52:36 14:29 Thread[main,5,main] Starting observable 
			2018:52:36 14:29 Thread[main,5,main] Pushing 1 
			2018:52:36 14:29 Thread[main,5,main] Mapping 1 to 10 
			2018:52:36 14:29 Thread[main,5,main]  Received 10 
			2018:52:36 14:31 Thread[main,5,main] Pushing 2 
			2018:52:36 14:31 Thread[main,5,main] Mapping 2 to 20 
			2018:52:36 14:31 Thread[main,5,main]  Received 20 
			2018:52:36 14:31 Thread[main,5,main]  Completed 
			2018:52:36 14:31 Thread[main,5,main] ========================================================= 
		 */
	}
	
	/*
	 * subscribeOn method allows us to specify thread on which Observable#create method will be called
	 * and events will be pushed
	 * even if we specify subscribeOn() multiple times, only first one will be considered.
	 */
	static void subscribeOnDemo() throws InterruptedException {
		Logger.log("Starting subscriberOnDemo");
		Observable<Integer> observable = Observable.create(emitter -> {
			Logger.log("Starting observable");
			
			Logger.log("Pushing 1");
			emitter.onNext(1);
			
			blockingOperation();
			
			Logger.log("Pushing 2");
			emitter.onNext(2);
			
			emitter.onComplete();
		});
		
		CountDownLatch countDownLatch = new CountDownLatch(1);
		observable = observable
				.subscribeOn(Schedulers.io())
				.map(val -> {
			Logger.log("Mapping %s to %s", val, val * 10);
			return val * 10;
		});
		
		subscribe(observable, countDownLatch);
		countDownLatch.await();
		Logger.log("=========================================================");
		
		/*  ******************************  Sample Output  ************************************
		 * 
		 *  2018:52:36 14:31 Thread[main,5,main] Starting subscriberOnDemo 
			2018:52:36 14:31 Thread[RxCachedThreadScheduler-1,5,main] Starting observable 
			2018:52:36 14:31 Thread[RxCachedThreadScheduler-1,5,main] Pushing 1 
			2018:52:36 14:31 Thread[RxCachedThreadScheduler-1,5,main] Mapping 1 to 10 
			2018:52:36 14:31 Thread[RxCachedThreadScheduler-1,5,main]  Received 10 
			2018:52:36 14:33 Thread[RxCachedThreadScheduler-1,5,main] Pushing 2 
			2018:52:36 14:33 Thread[RxCachedThreadScheduler-1,5,main] Mapping 2 to 20 
			2018:52:36 14:33 Thread[RxCachedThreadScheduler-1,5,main]  Received 20 
			2018:52:36 14:33 Thread[RxCachedThreadScheduler-1,5,main]  Completed 
			2018:52:36 14:33 Thread[main,5,main] ========================================================= 
		 */
	}
	
	/*
	 * observeOn makes next task to execute on the specified thread
	 * we can use observeOn to specify different task to be executed on different threads
	 */
	static void observeOnDemo() throws InterruptedException {
		Logger.log("Starting observeOnDemo");
		Observable<Integer> observable = Observable.create(emitter -> {
			Logger.log("Starting observable");
			
			Logger.log("Pushing 1");
			emitter.onNext(1);
			
			blockingOperation();
			
			Logger.log("Pushing 2");
			emitter.onNext(2);
			
			emitter.onComplete();
		});
		
		CountDownLatch countDownLatch = new CountDownLatch(1);
		observable = observable
				.subscribeOn(Schedulers.io())
				.observeOn(Schedulers.computation())
				.map(val -> {
			Logger.log("Mapping %s to %s", val, val * 10);
			return val * 10;
		}).observeOn(Schedulers.newThread());
		
		subscribe(observable, countDownLatch);
		countDownLatch.await();
		Logger.log("=========================================================");
		
		/*  ********************************  Sample Output  *****************************
		 * 
		 *  2018:52:36 14:33 Thread[main,5,main] Starting observeOnDemo 
			2018:52:36 14:33 Thread[RxCachedThreadScheduler-1,5,main] Starting observable 
			2018:52:36 14:33 Thread[RxCachedThreadScheduler-1,5,main] Pushing 1 
			2018:52:36 14:33 Thread[RxComputationThreadPool-1,5,main] Mapping 1 to 10 
			2018:52:36 14:33 Thread[RxNewThreadScheduler-1,5,main]  Received 10 
			2018:52:36 14:35 Thread[RxCachedThreadScheduler-1,5,main] Pushing 2 
			2018:52:36 14:35 Thread[RxComputationThreadPool-1,5,main] Mapping 2 to 20 
			2018:52:36 14:35 Thread[RxNewThreadScheduler-1,5,main]  Received 20 
			2018:52:36 14:35 Thread[RxNewThreadScheduler-1,5,main]  Completed 
			2018:52:36 14:35 Thread[main,5,main] ========================================================= 
		 */
	}
	
	/*
	 * method to display subscribed content
	 */
	static void subscribe(Observable<Integer> observable, CountDownLatch countDownLatch) {
		observable.subscribe(
				val -> Logger.log(" Received " + val),     // value emitted form onNext()
				ex -> Logger.log(" Got error "+ ex),       // in case of any error
				() -> {									   // its completed
					   Logger.log(" Completed");
					   if(countDownLatch != null) countDownLatch.countDown();
					 });
	}
	
	
	static Integer blockingOperation() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 1;
	}
}
