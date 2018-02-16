package rxjava2;

import io.reactivex.Flowable;
import rxjava2.common.Logger;

public class TransformingOperators {
	public static void main(String[] args) {
		map();
		cast();
		startWith();
		defaultIfEmpty();
		switchIfEmpty();
	}
	
	/*
	 * it takes each value, transform it and then passes it in the chain
	 * Useful for one-to-one emission
	 */
	static void map() {
		Flowable.just(1, 2, 3, 4)
		.map(i -> i * i)
		.subscribe(i -> Logger.log("Received %s", i));
		/*
		 *  2018:34:44 15:10 Thread[main,5,main] Received 1 
			2018:34:44 15:10 Thread[main,5,main] Received 4 
			2018:34:44 15:10 Thread[main,5,main] Received 9 
			2018:34:44 15:10 Thread[main,5,main] Received 16 
		 */
	}
	
	/*
	 * for type casting
	 */
	static void cast() {
		Flowable<Integer> intFlowable = Flowable.just(1, 2, 3, 4, 5);
		Flowable<Object> objFlowable = intFlowable.cast(Object.class);
		objFlowable.subscribe(i -> Logger.log("Received %s", i));
		/*
		 *  2018:41:44 15:25 Thread[main,5,main] Received 1 
			2018:41:44 15:25 Thread[main,5,main] Received 2 
			2018:41:44 15:25 Thread[main,5,main] Received 3 
			2018:41:44 15:25 Thread[main,5,main] Received 4 
			2018:41:44 15:25 Thread[main,5,main] Received 5 
		 */
	}
	
	/*
	 * to insert some items at the start
	 */
	static void startWith() {
		Flowable.just("Tea", "Coffee", "Milk", "Juice")
			.startWith("Cafe Menu")
			.subscribe(i -> Logger.log("Received %s", i));
		
		/*
		 *  2018:02:13 15:22 Thread[main,5,main] Received Cafe Menu 
			2018:02:13 15:22 Thread[main,5,main] Received Tea 
			2018:02:13 15:22 Thread[main,5,main] Received Coffee 
			2018:02:13 15:22 Thread[main,5,main] Received Milk 
			2018:02:13 15:22 Thread[main,5,main] Received Juice 
		 */
		
		Flowable.just("Tea", "Coffee", "Milk", "Juice")
			.startWithArray("Cafe Menu", "------------------")
			.subscribe(i -> Logger.log("Received %s", i));
		/*
		 *  2018:02:13 15:22 Thread[main,5,main] Received Cafe Menu 
			2018:02:13 15:22 Thread[main,5,main] Received ------------------ 
			2018:02:13 15:22 Thread[main,5,main] Received Tea 
			2018:02:13 15:22 Thread[main,5,main] Received Coffee 
			2018:02:13 15:22 Thread[main,5,main] Received Milk 
			2018:02:13 15:22 Thread[main,5,main] Received Juice 

		 */
	}
	
	/*
	 * if there is no emission till onComplete() call, then default item will be emitted
	 */
	static void defaultIfEmpty() {
		Flowable.just("One", "Two", "Three", "Four")
			.filter(i -> i.length() > 5)
			.defaultIfEmpty("None")
			.subscribe(Logger::log);
		/*
		 * 2018:02:13 16:05 Thread[main,5,main] None 
		 */	
	}
	
	/*
	 * switch to new stream if earlier one is empty
	 */
	static void switchIfEmpty() {
		Flowable.just("One", "Two", "Three", "Four")
			.filter(i -> i.length() > 5)
			.switchIfEmpty(Flowable.just("Eleven", "Tweleve"))
			.subscribe(Logger::log);
		
		/*
		 *  2018:02:13 16:10 Thread[main,5,main] Eleven 
			2018:02:13 16:10 Thread[main,5,main] Tweleve 
		 */
	}
}
