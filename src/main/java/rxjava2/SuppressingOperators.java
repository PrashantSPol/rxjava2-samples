package rxjava2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import rxjava2.common.Logger;

public class SuppressingOperators {
	public static void main(String[] args) throws InterruptedException {
		//takeDemo();
		//takeWhile();
		//skipDemo();
		//skipWhile();
		//distinct();
		//distinctUntilChanged();
		//delayDemo();
		//intervalDemo();
		//scanDemo();
		//reduceDemo();
		//collectDemo();
		//deferDemo();
		elementAt();
	}
	
	/*
	 * take restricts number of events to be received
	 * Source may have capacity to create infinite events but restriction can be put on it depending on the subscriber
	 * after certain number of events it makes source observable to call onComplete() method
	 */
	static void takeDemo() throws InterruptedException {
		Logger.log("Start takeDemo");
		Flowable.range(0,  100)
		.doOnNext(val -> Logger.log(" Emitted: " + val))
		.take(5)
		.subscribe(
				val -> Logger.log(" Received " + val),     // value emitted form onNext()
				ex -> Logger.log(" Got error "+ ex),       // in case of any error
				() -> {																		// its completed
					Logger.log(" Completed");
					   });
		
		System.out.println("=========================================================");
	}
	
	/*
	 * takeWhile() will pass emissions unless given condition is true
	 */
	static void takeWhile() {
		Logger.log("Start takeWhileDemo");
		Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4, 5 , 4, 3, 1).takeWhile(i -> i < 5);
		subscribe(flowable, null);
	}
	
	/*
	 * Similar to take(), but it skips given number of emissions and then start considering further emissions
	 */
	static void skipDemo() {
		Logger.log("Start skipDemo");
		Flowable<Integer> flowable = Flowable.range(1, 100)
			.skip(90);
		
		subscribe(flowable, null);
		System.out.println("=========================================================");
	}
	
	/*
	 * skipWhile() will skip emissions unless given condition is true
	 */
	static void skipWhile() {
		Logger.log("Start skipWhileDemo");
		Flowable<Integer> flowable = Flowable.just(1, 2, 3, 4, 5 , 4, 3, 1).skipWhile(i -> i < 5);
		subscribe(flowable, null);
	}
	
	/*
	 * distinct() passes unique emissions based on key passed through the funciton argument
	 */
	static void distinct() {
		Logger.log("Start distinctDemo");
		Flowable.just("One", "Two", "Three", "Four", "Five")
				.distinct(String::length)
				.subscribe(s -> Logger.log("Received %s", s));
		
		System.out.println("=========================================================");
	}
	
	/*
	 * it won't emit if same value keeps on repeating
	 */
	static void distinctUntilChanged() {
		Logger.log("Start distinctUntilChangedDemo");
		Flowable.just(1, 1, 2, 2, 3, 3, 3, 2, 1)
				.distinctUntilChanged()
				.subscribe(s -> Logger.log("Received %s", s));

		/*
		 *  2018:18:44 14:32 Thread[main,5,main] Start distinctUntilChangedDemo 
			2018:18:44 14:32 Thread[main,5,main] Received 1 
			2018:18:44 14:32 Thread[main,5,main] Received 2 
			2018:18:44 14:32 Thread[main,5,main] Received 3 
			2018:18:44 14:32 Thread[main,5,main] Received 2 
			2018:18:44 14:32 Thread[main,5,main] Received 1 
		 */
	}
	
	/*
	 * delay make subscriber to wait for some time and starts dispatching events after given time
	 * To simulate this behavior CountDownLatch is used which will make current thread to wait for sometime
	 */
	static void delayDemo() throws InterruptedException {
		Logger.log("Start delayDemo");
		CountDownLatch countDownLatch = new CountDownLatch(1);
		Flowable.range(0,  2)
		.doOnNext(val -> Logger.log(" Emitted: " + val))
		.delay(5, TimeUnit.SECONDS)
		.subscribe(
				val -> Logger.log(" Received " + val),     // value emitted form onNext()
				ex -> Logger.log(" Got error "+ ex),       // in case of any error
				() -> {																		// its completed
					   Logger.log(" Completed");
					   countDownLatch.countDown();
					   });
		
		countDownLatch.await();
		
		System.out.println("=========================================================");
	}
	
	/*
	 * interval is the function used to generate events after a certain interval
	 * This is the delay between two events generating from source
	 */
	static void intervalDemo() throws InterruptedException {
		Logger.log("Start intervalDemo");
		CountDownLatch countDownLatch = new CountDownLatch(1);
		Flowable.interval(1, TimeUnit.SECONDS)
		.doOnNext(val -> Logger.log(" Emitted: " + val))
		.take(5)
		.subscribe(
				val -> Logger.log(" Received " + val),     // value emitted form onNext()
				ex -> Logger.log(" Got error "+ ex),       // in case of any error
				() -> {																		// its completed
					Logger.log(" Completed");
					countDownLatch.countDown();
					   });
		countDownLatch.await();
		System.out.println("=========================================================");
	}
	
	/*
	 * scan method is used to accumulate results of entire stream
	 * where in each pass we will get 2 parameters, 
	 * first one is result of last iteration 
	 * and second one is current value from base stream
	 * 
	 * we need to return result value in function so that it will be act as first value for next iteration
	 * For first iteration initial value needs to be given
	 */
	static void scanDemo() throws InterruptedException {
		Logger.log("Start scanDemo");
		Flowable<Integer> flowable = Flowable.just(1, 5, -3, 10)
			.doOnNext(val -> Logger.log(" Emitted: " + val))
			.scan(0, (oldValue, currentValue) -> {
				Logger.log(" oldValue=%s currentValue=%s", oldValue, currentValue);
				return oldValue + currentValue;
			});
		
		flowable.subscribe();
		flowable.doOnComplete(() -> Logger.log(" Completed"));
		
		System.out.println("=========================================================");
	}
	
	/*
	 * reduce method is similar as scan method except it passes the result at the end to subscriber 
	 * where in each pass we will get 2 parameters, 
	 * first one is result of last iteration 
	 * and second one is current value from base stream
	 * 
	 * we need to return result value in function so that it will be act as first value for next iteration
	 * For first iteration initial value needs to be given
	 */
	static void reduceDemo() throws InterruptedException {
		Logger.log("Start reduceDemo");
		Single<Integer> flowable = Flowable.just(1, 5, -3, 10)
				.doOnNext(val -> Logger.log(" Emitted: " + val))
				.reduce(0, (oldValue, currentValue) -> {
					Logger.log(" oldValue=%s currentValue=%s", oldValue, currentValue);
					return oldValue + currentValue;
				});
		
		flowable.subscribe(val -> Logger.log("Received %s", val));
		System.out.println("=========================================================");
	}
	
	/*
	 * collect method is similar to reduce method but here we pass mutable container
	 * which keeps on collecting actual stream values and at the end provide it to subscribers
	 */
	static void collectDemo() {
		Logger.log("Start reduceDemo");
		Single<List<Integer>> single = Flowable.just(10, 20, 30, 40)
				.doOnNext(val -> Logger.log(" Emitted: " + val))
				.collect(ArrayList::new, (container, value) -> {
					container.add(value);
				});
		
		single.subscribe(val -> Logger.log("Received %s", val));
		System.out.println("=========================================================");
	}
	
	
	static void deferDemo() {
		Logger.log("Start deferDemo");
		// Its Not good to do blocking operations directly in Flowable#just or something similar method
		// better to separate it with defer method
		Logger.log("Going to Create first observable");
		Flowable<Integer> flowable = Flowable.just(blockingOperation());  // control will be blocked here itself
		Logger.log("After creating first observable");
		subscribe(flowable, null);
		
		/*
		 * 	2018:50:33 16:44 Thread[main,5,main] Going to Create first observable 
		 * 	2018:50:33 16:49 Thread[main,5,main] After creating first observable 
		 * 	2018:50:33 16:49 Thread[main,5,main]  Received 1 
		 * 	2018:50:33 16:49 Thread[main,5,main]  Completed 
		 */
		
		
		Logger.log("Going to Create second observable");
		flowable = Flowable.defer(() -> Flowable.just(blockingOperation())); // control won't be blocked here 
		Logger.log("After creating second observable");
		subscribe(flowable, null);
		
		/*
		 * 2018:50:33 16:49 Thread[main,5,main] Going to Create second observable 
		 * 2018:50:33 16:49 Thread[main,5,main] After creating second observable 
		 * 2018:50:33 16:54 Thread[main,5,main]  Received 1 
		 * 2018:50:33 16:54 Thread[main,5,main]  Completed 
		 */
	}
	
	/*
	 * returns element at particular location
	 */
	static void elementAt() {
		Flowable.just("One", "Two", "Three")
		.elementAt(1)
		.subscribe(s -> Logger.log("Received %s", s));
		/*
		 * 2018:50:44 14:22 Thread[main,5,main] Received Two 
		 */
	}
	
	/*
	 * method to display subscribed content
	 */
	static void subscribe(Flowable<Integer> flowable, CountDownLatch countDownLatch) {
		flowable.subscribe(
				val -> Logger.log(" Received " + val),     // value emitted form onNext()
				ex -> Logger.log(" Got error "+ ex),       // in case of any error
				() -> {									   // its completed
					   Logger.log(" Completed");
					   if(countDownLatch != null) countDownLatch.countDown();
					 });
	}
	
	
	static Integer blockingOperation() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 1;
	}
}
