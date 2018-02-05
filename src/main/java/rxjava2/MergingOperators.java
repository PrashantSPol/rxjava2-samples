package rxjava2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import rxjava2.common.Logger;

public class MergingOperators {
	public static void main(String[] args) throws InterruptedException {
		//zipDemo();
		//mergeDemo();
		concatDemo();
	}
	
	/*
	 * zip function is used to combine result of two or more streams together
	 * Here, zip function waits for all streams to return data and then only it will process it to combine results
	 * This function will be useful to wait to get data from multiple network calls, combine results and provide it to subscriber
	 */
	static void zipDemo() {
		Logger.log("start zipDemo");
		Single<String> dataStream = Single.fromFuture(CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Logger.log("Emitting Data Stream");
			return "Data";
		}));
		
		Logger.log("Data stream created");
		
		Single<Integer> valueStream = Single.fromFuture(CompletableFuture.supplyAsync(() -> {
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Logger.log("Emitting Value Stream");
			return 10;
		}));
		
		Logger.log("Value stream created");
		Single<String> resultStream = Single.zip(dataStream, valueStream, (data, value) -> {
				Logger.log("Received values in zip");
				return data + value;
			});
		
		resultStream.subscribe(result -> {
			Logger.log("Received in subscriber as " + result);
		});
		
		/*
		 *  ********************** Sample Output **************************
		 *  2018:50:36 09:04 Thread[main,5,main] start zipDemo 
			2018:50:36 09:05 Thread[main,5,main] Data stream created 
			2018:50:36 09:05 Thread[main,5,main] Value stream created 
			2018:50:36 09:09 Thread[ForkJoinPool.commonPool-worker-1,5,main] Emitting Data Stream 
			2018:50:36 09:11 Thread[ForkJoinPool.commonPool-worker-2,5,main] Emitting Value Stream 
			2018:50:36 09:11 Thread[main,5,main] Received values in zip 
			2018:50:36 09:11 Thread[main,5,main] Received in subscriber as Data10 
		 */
	}
	
	/*
	 * Unlike zip function, merge function doesn't wait for the all values of the stream to be available,
	 * it just keeps forwarding values which it gets from any of the stream.
	 */
	static void mergeDemo() throws InterruptedException {
		Logger.log("start mergeDemo");
		Flowable<String> colorStream = periodicEmitter(2, TimeUnit.SECONDS, "red", "green", "blue", "yellow");
		
		Flowable<Long> numberStream = Flowable.interval(1, TimeUnit.SECONDS).take(8).delay(1, TimeUnit.SECONDS);
		
		Flowable resultStream = Flowable.merge(colorStream, numberStream);
		CountDownLatch countDownLatch = new CountDownLatch(1);
		subscribe(resultStream, countDownLatch);
		countDownLatch.await();
		
		/**  ******************************  Sample Output  ***************************************
		 *  2018:16:36 10:17 Thread[main,5,main] start mergeDemo 
			2018:16:36 10:19 Thread[RxComputationThreadPool-1,5,main]  Received red 
			2018:16:36 10:19 Thread[RxComputationThreadPool-3,5,main]  Received 0 
			2018:16:36 10:20 Thread[RxComputationThreadPool-3,5,main]  Received 1 
			2018:16:36 10:21 Thread[RxComputationThreadPool-1,5,main]  Received green 
			2018:16:36 10:21 Thread[RxComputationThreadPool-3,5,main]  Received 2 
			2018:16:36 10:22 Thread[RxComputationThreadPool-3,5,main]  Received 3 
			2018:16:36 10:23 Thread[RxComputationThreadPool-1,5,main]  Received blue 
			2018:16:36 10:23 Thread[RxComputationThreadPool-3,5,main]  Received 4 
			2018:16:36 10:24 Thread[RxComputationThreadPool-3,5,main]  Received 5 
			2018:16:36 10:25 Thread[RxComputationThreadPool-1,5,main]  Received yellow 
			2018:16:36 10:25 Thread[RxComputationThreadPool-3,5,main]  Received 6 
			2018:16:36 10:26 Thread[RxComputationThreadPool-3,5,main]  Received 7 
			2018:16:36 10:26 Thread[RxComputationThreadPool-3,5,main]  Completed 

		 */
	}
	
	/*
	 * concat method helps to append data of different streams in front of one another.
	 * This concatenation is being done in the same sequence as we provide for concat function.
	 * This will wait for first stream to complete then starts for second, 
	 * even if first stream has delay then also it won't start for second one
	 */
	static void concatDemo() throws InterruptedException {
		Logger.log("start concatDemo");
		Flowable<String> dataStream = periodicEmitter(2, TimeUnit.SECONDS, "red", "green", "blue", "yellow");
		Flowable<Long> intervalStream = Flowable.interval(1, TimeUnit.SECONDS).take(4);
		
		CountDownLatch countDownLatch = new CountDownLatch(1);
		
		Flowable<Object> resultStream = Flowable.concat(dataStream, intervalStream);
		subscribe(resultStream, countDownLatch);
		
		countDownLatch.await();
		/*
		 * *****************************  Sample Output  ************************************
		 *  2018:25:36 10:15 Thread[main,5,main] start concatDemo 
			2018:25:36 10:17 Thread[RxComputationThreadPool-1,5,main]  Received red 
			2018:25:36 10:19 Thread[RxComputationThreadPool-1,5,main]  Received green 
			2018:25:36 10:21 Thread[RxComputationThreadPool-1,5,main]  Received blue 
			2018:25:36 10:23 Thread[RxComputationThreadPool-1,5,main]  Received yellow 
			2018:25:36 10:24 Thread[RxComputationThreadPool-2,5,main]  Received 0 
			2018:25:36 10:25 Thread[RxComputationThreadPool-2,5,main]  Received 1 
			2018:25:36 10:26 Thread[RxComputationThreadPool-2,5,main]  Received 2 
			2018:25:36 10:27 Thread[RxComputationThreadPool-2,5,main]  Received 3 
			2018:25:36 10:27 Thread[RxComputationThreadPool-2,5,main]  Completed 

		 */
	}
	
	/*
	 * method to display subscribed content
	 */
	static void subscribe(Flowable<Object> flowable, CountDownLatch countDownLatch) {
		flowable.subscribe(
				val -> Logger.log(" Received " + val),     // value emitted form onNext()
				ex -> {Logger.log(" Got error "+ ex);ex.printStackTrace();},       // in case of any error
				() -> {									   // its completed
					   Logger.log(" Completed");
					   if(countDownLatch != null) countDownLatch.countDown();
					 });
	}
	
	/*
	 * method which emits given values after a mentioned period of time
	 */
	static <T> Flowable<T> periodicEmitter(int duration, TimeUnit timeUnit, T... arr) {
		Flowable<T> dataStream = Flowable.fromArray(arr);
		Flowable<Long> intervalStream = Flowable.interval(duration, timeUnit);
		return Flowable.zip(dataStream, intervalStream, (data, interval) -> data);
	}
}
