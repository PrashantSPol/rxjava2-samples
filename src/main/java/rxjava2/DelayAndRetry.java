package rxjava2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import rxjava2.common.Logger;

public class DelayAndRetry {
	public static void main(String[] args){
		//timeout();
		retry();
	}
	
	/*
	 * timeout function will wait for specified time 
	 * and if event doesn't come by that period then it will generate error
	 */
	static void timeout() {
		Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4, 3, 1)
				.map(value -> {
					Logger.log("Delaying for %s seconds", value);
					Thread.sleep(value * 1000);
					return value;
				});
		
		// specify timeout by 3 seconds
		flowable = flowable.timeout(3, TimeUnit.SECONDS)
				.onErrorResumeNext(Flowable.just(0));
		
		subscribe(flowable, null);
		/*  **************************  Sample Output  *********************************
		 * 
		 *  2018:20:37 17:53 Thread[main,5,main] Delaying for 1 seconds 
			2018:20:37 17:54 Thread[main,5,main]  Received 1 
			2018:20:37 17:54 Thread[main,5,main] Delaying for 2 seconds 
			2018:20:37 17:56 Thread[main,5,main]  Received 2 
			2018:20:37 17:56 Thread[main,5,main] Delaying for 3 seconds 
			2018:20:37 17:59 Thread[main,5,main]  Received 3 
			2018:20:37 17:59 Thread[main,5,main] Delaying for 4 seconds 
			2018:21:37 17:02 Thread[RxComputationThreadPool-1,5,main]  Received 0 
			2018:21:37 17:02 Thread[RxComputationThreadPool-1,5,main]  Completed 
		 */
	}
	
	/*
	 * This method makes current stream to retry for given number of attempts or else throw error
	 */
	static void retry() {
		Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4, 3, 1)
				.flatMap(value -> {
					Flowable<Integer> subFlowable = Flowable.create(emitter -> {
						Logger.log("Delaying for %s seconds", value);
						Thread.sleep(value * 1000);
						emitter.onNext(value);
						emitter.onComplete();
					}, BackpressureStrategy.MISSING);
					
					// specify timeout and retry functions
					subFlowable = subFlowable.timeout(3, TimeUnit.SECONDS)
							.retry(2)
							.onErrorResumeNext(throwable -> {
								Logger.log("Retry finished.. returning 0");
								return Flowable.just(0);
							});
					
					return subFlowable;
				}, 1);
		
		subscribe(flowable, null);
		
		/*  *******************************  Sample output  ******************************************
		 * 
		 *  2018:32:37 17:57 Thread[main,5,main] Delaying for 1 seconds 
			2018:32:37 17:58 Thread[main,5,main]  Received 1 
			2018:32:37 17:58 Thread[main,5,main] Delaying for 2 seconds 
			2018:33:37 17:00 Thread[main,5,main]  Received 2 
			2018:33:37 17:00 Thread[main,5,main] Delaying for 3 seconds 
			2018:33:37 17:03 Thread[main,5,main]  Received 3 
			2018:33:37 17:03 Thread[main,5,main] Delaying for 4 seconds 
			2018:33:37 17:07 Thread[main,5,main] Delaying for 4 seconds 
			2018:33:37 17:11 Thread[main,5,main] Delaying for 4 seconds 
			2018:33:37 17:14 Thread[RxComputationThreadPool-2,5,main] Retry finished.. returning 0 
			2018:33:37 17:14 Thread[RxComputationThreadPool-2,5,main]  Received 0 
			2018:33:37 17:15 Thread[main,5,main] Delaying for 3 seconds 
			2018:33:37 17:18 Thread[main,5,main]  Received 3 
			2018:33:37 17:18 Thread[main,5,main] Delaying for 1 seconds 
			2018:33:37 17:19 Thread[main,5,main]  Received 1 
			2018:33:37 17:19 Thread[main,5,main]  Completed 
		 */
	}
	
	
	/*
	 * method to display subscribed content
	 */
	static void subscribe(Flowable<Integer> flowable, CountDownLatch countDownLatch) {
		flowable.subscribe(
				val -> Logger.log(" Received " + val),     // value emitted form onNext()
				ex -> {Logger.log(" Got error : "+ ex);},       // in case of any error
				() -> {									   // its completed
					Logger.log(" Completed");
					if(countDownLatch != null) countDownLatch.countDown();
				});
	}
}
