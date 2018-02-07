package rxjava2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import rxjava2.common.Logger;

public class FlatMapSample {
	public static void main(String[] args) throws InterruptedException {
		//flatMapDemo();
		flatMapWithConcurrencyOne();
	}
	
	/*
	 * flatMap is worked on Streams of Streams,
	 * It takes single stream and the create multiple streams for each object generated from event of original stream
	 * Then it flattens all these streams to make use at single subscribe method
	 */
	static void flatMapDemo() throws InterruptedException {
		Logger.log("Start flatMapDemo");
		Flowable<String> colorStream = Flowable.just("Mumbai", "Pune", "Delhi");
		
		colorStream = colorStream.flatMap((cityName) -> simulateNetworkCalls(cityName), 10);
		
		CountDownLatch countDownLatch = new CountDownLatch(1);
		colorStream.subscribe(val -> Logger.log("Received " + val),
				ex -> Logger.log("Error " + ex),
				() -> {
					Logger.log("OnCompleted");
					countDownLatch.countDown();
				});
		
		countDownLatch.await();
		
		/*  **********************  Sample Output  ****************************
		 *  2018:20:37 14:58 Thread[RxComputationThreadPool-1,5,main] Received Hi Mumbai 
			2018:20:37 14:58 Thread[RxComputationThreadPool-1,5,main] Received Hi Pune 
			2018:20:37 14:58 Thread[RxComputationThreadPool-1,5,main] Received Hi Delhi 
			2018:20:37 14:59 Thread[RxComputationThreadPool-3,5,main] Received Hello Delhi 
			2018:20:37 14:59 Thread[RxComputationThreadPool-3,5,main] Received Hello Mumbai 
			2018:20:37 14:59 Thread[RxComputationThreadPool-3,5,main] Received Hello Pune 
			2018:21:37 14:00 Thread[RxComputationThreadPool-1,5,main] Received Bye Mumbai 
			2018:21:37 14:00 Thread[RxComputationThreadPool-1,5,main] Received Bye Delhi 
			2018:21:37 14:00 Thread[RxComputationThreadPool-2,5,main] Received Bye Pune 
			2018:21:37 14:00 Thread[RxComputationThreadPool-2,5,main] OnCompleted 
		 * 
		 */
	}
	
	/*
	 * When concurrency is mentioned as 1, 
	 * all later threads will wait for processing until earlier threads finished their execution
	 */
	static void flatMapWithConcurrencyOne() throws InterruptedException {
		Logger.log("Start flatMapDemo");
		Flowable<String> colorStream = Flowable.just("Mumbai", "Pune", "Delhi");

		colorStream = colorStream.flatMap((cityName) -> simulateNetworkCalls(cityName), 1);

		CountDownLatch countDownLatch = new CountDownLatch(1);
		colorStream.subscribe(val -> Logger.log("Received " + val),
				ex -> Logger.log("Error " + ex),
				() -> {
					Logger.log("OnCompleted");
					countDownLatch.countDown();
				});

		countDownLatch.await();

		/*  **********************  Sample Output  ****************************
		 *  2018:49:37 14:51 Thread[main,5,main] Start flatMapDemo 
			2018:49:37 14:53 Thread[RxComputationThreadPool-1,5,main] Received Hi Mumbai 
			2018:49:37 14:54 Thread[RxComputationThreadPool-1,5,main] Received Hello Mumbai 
			2018:49:37 14:55 Thread[RxComputationThreadPool-1,5,main] Received Bye Mumbai 
			2018:49:37 14:56 Thread[RxComputationThreadPool-2,5,main] Received Hi Pune 
			2018:49:37 14:57 Thread[RxComputationThreadPool-2,5,main] Received Hello Pune 
			2018:49:37 14:58 Thread[RxComputationThreadPool-2,5,main] Received Bye Pune 
			2018:49:37 14:59 Thread[RxComputationThreadPool-3,5,main] Received Hi Delhi 
			2018:50:37 14:00 Thread[RxComputationThreadPool-3,5,main] Received Hello Delhi 
			2018:50:37 14:01 Thread[RxComputationThreadPool-3,5,main] Received Bye Delhi 
			2018:50:37 14:01 Thread[RxComputationThreadPool-3,5,main] OnCompleted 
		 * 
		 */
	}
	
	/*
	 * common method to simulate network call
	 */
	static Flowable<String> simulateNetworkCalls(String cityName) {
		// to generate events after every 1 second
		Flowable<Long> timer = Flowable.interval(1, TimeUnit.SECONDS);
		
		// event generator
		Flowable<String> flowable = Flowable.<String>create(emitter -> {
			emitter.onNext("Hi " + cityName);
			emitter.onNext("Hello " + cityName);
			emitter.onNext("Bye " + cityName);
			emitter.onComplete();
		}, BackpressureStrategy.MISSING);

		return flowable.zipWith(timer, (data, value) -> data);
	}
}
