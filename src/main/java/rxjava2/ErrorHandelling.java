package rxjava2;

import java.util.concurrent.CountDownLatch;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import rxjava2.common.Logger;

public class ErrorHandelling {
	public static void main(String[] args) {
		//errorCheck();
		//onErrorReturn();
		//onErrorReturnWithFlatmap_1();
		//onErrorReturnWithFlatmap_2();
		onErrorResumeNext();
	}

	/*
	 * Once exception will be occurred all upstreams will be unsubscribed 
	 * and error will be delivered to onError() method
	 */
	static void errorCheck() {
		Flowable<String> flowable = Flowable.just("First", "Second", "Third", "fourth").map(val -> {
			if(val.equals("Third")) {
				throw new RuntimeException("Exception for " + val);
			}
			return val + "**";
		});

		subscribe(flowable, null);

		/*  ************************************  Sample Output  ************************************
		 *  2018:28:37 15:35 Thread[main,5,main]  Received First** 
			2018:28:37 15:35 Thread[main,5,main]  Received Second** 
			2018:28:37 15:35 Thread[main,5,main]  Got error : Exception for Third 
		 */
	}

	/*
	 * onErrorReturn makes the subscriber to get mentioned value
	 * This won't cause call to onError() method but some dummy default value will be pushed using onNext() method
	 */
	static void onErrorReturn() {
		Flowable<String> flowable = Flowable.just("First", "Second", "Third", "fourth").map(val -> {
			if(val.equals("Third")) {
				throw new RuntimeException("Exception for " + val);
			}
			return val + "**";
		}).onErrorReturn((throwable) -> "Zero");

		subscribe(flowable, null);

		/*  ************************************  Sample Output  ************************************
		 *  
		 *  2018:33:37 15:31 Thread[main,5,main]  Received First** 
			2018:33:37 15:31 Thread[main,5,main]  Received Second** 
			2018:33:37 15:31 Thread[main,5,main]  Received Zero 
			2018:33:37 15:31 Thread[main,5,main]  Completed 

		 */
	}

	/*
	 * Even if something happened at sub stream level and handled at main stream level 
	 * then all subscriptions till the main upstream will be unsubscribed 
	 */
	static void onErrorReturnWithFlatmap_1() {
		Flowable<String> flowable = Flowable.just("First", "Second", "Third", "fourth")
				// Create multiple streams 
				.flatMap(value -> {

					// generate stream for each value
					return Flowable.<String>create(emitter -> {

						// generate error for one of the value of main stream
						if(value.equals("Third")) {
							throw new RuntimeException("Exception for " + value);
						}

						emitter.onNext(value + "_1");
						emitter.onNext(value + "_2");
					}, BackpressureStrategy.MISSING);
				})
				// append some value to know working of stream
				.map(val -> {
					return val + "_mapped";
				}).onErrorReturn((throwable) -> "Zero");

		subscribe(flowable, null);
		/*  **************************  Sample Output  *****************************
		 * 
		 *  2018:42:37 15:54 Thread[main,5,main]  Received First_1_mapped 
			2018:42:37 15:54 Thread[main,5,main]  Received First_2_mapped 
			2018:42:37 15:54 Thread[main,5,main]  Received Second_1_mapped 
			2018:42:37 15:54 Thread[main,5,main]  Received Second_2_mapped 
			2018:42:37 15:54 Thread[main,5,main]  Received Zero 
			2018:42:37 15:54 Thread[main,5,main]  Completed 
		 */
	}

	/*
	 * when exception occurred at sub stream level and its been handled at substream level
	 * then it won't affect processing of top level 
	 */
	static void onErrorReturnWithFlatmap_2() {
		Flowable<String> flowable = Flowable.just("First", "Second", "Third", "fourth")
				// Create multiple streams 
				.flatMap(value -> {

			    // generate stream for each value
				Flowable<String> subFlowable = Flowable.<String>create(emitter -> {

				// generate error for one of the value of main stream
				if(value.equals("Third")) {
					throw new RuntimeException("Exception for " + value);
				}

				emitter.onNext(value + "_1");
				emitter.onNext(value + "_2");

				emitter.onComplete();
			}, BackpressureStrategy.MISSING);

			// handle error at the substream level
			subFlowable = subFlowable.onErrorReturn((throwable) -> "Zero");

			return subFlowable;
		});
		
				// append some value to know working of stream
		flowable = flowable.map(val -> {
			return val + "_mapped";
		});

		subscribe(flowable, null);
		/*  **************************  Sample Output  *****************************
		 * 
		 *  2018:54:37 15:31 Thread[main,5,main]  Received First_1_mapped 
			2018:54:37 15:31 Thread[main,5,main]  Received First_2_mapped 
			2018:54:37 15:31 Thread[main,5,main]  Received Second_1_mapped 
			2018:54:37 15:31 Thread[main,5,main]  Received Second_2_mapped 
			2018:54:37 15:31 Thread[main,5,main]  Received Zero_mapped 
			2018:54:37 15:31 Thread[main,5,main]  Received fourth_1_mapped 
			2018:54:37 15:31 Thread[main,5,main]  Received fourth_2_mapped 
			2018:54:37 15:31 Thread[main,5,main]  Completed  
		 */
	}

	/*
	 * onErrorResumeNext() method provides chance to provide failback mechanism
	 * we may check type of exception to provide failback stream or to return stream for another exception
	 */
	static void onErrorResumeNext() {
		Flowable<String> flowable = Flowable.just("one", "two", "three", "four")
				.map(value -> {
					if(value.equals("two")) {
						throw new ArrayIndexOutOfBoundsException("Exception for two");
					}
					
					if(value.equals("four")) {
						throw new IllegalArgumentException("Exception for four");
					}
					
					return value;
				}).onErrorResumeNext(throwable -> {
					if(throwable instanceof IllegalArgumentException) {
						return Flowable.error(new RuntimeException(throwable.getMessage()));
					}
					
					return Flowable.just("Failback stream");
				});
		
		subscribe(flowable, null);
		
		/*  ******************************  Sample Output  *******************************
		 * 
		 *  2018:05:37 17:58 Thread[main,5,main]  Received one 
			2018:05:37 17:58 Thread[main,5,main]  Received Failback stream 
			2018:05:37 17:58 Thread[main,5,main]  Completed 
		 */
	}
	
	/*
	 * method to display subscribed content
	 */
	static void subscribe(Flowable<String> flowable, CountDownLatch countDownLatch) {
		flowable.subscribe(
				val -> Logger.log(" Received " + val),     // value emitted form onNext()
				ex -> {Logger.log(" Got error : "+ ex.getMessage());},       // in case of any error
				() -> {									   // its completed
					Logger.log(" Completed");
					if(countDownLatch != null) countDownLatch.countDown();
				});
	}
}
