package rxjava2;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import rxjava2.common.Logger;

/*
 * ConnectableObservable doesn't open new connection for each subscriber
 * All subscribers will be added to a common set,
 * when connect method will be called, theses subscribers will start receiving events 
 * but no subscriber is subscribed to main stream process
 */
public class ConnectableObservableSample {
	public static void main(String[] args) {
		Logger.log("ConnectableObservable demo started");
		ConnectableObservable<Integer> connectableObservable = Observable.<Integer>create(subscriber -> {
			Logger.log("Observable started");
			
			subscriber.setCancellable(() -> Logger.log("Observable cancelled"));
			
			Logger.log("push 1");
			subscriber.onNext(1);
			
			Logger.log("push 2");
			subscriber.onNext(2);
		}).publish();
		
		Logger.log("subscribing first");
		connectableObservable.subscribe(val -> Logger.log("first received %s", val));
		
		Logger.log("subscribing second");
		connectableObservable.subscribe(val -> Logger.log("second received %s", val));
		
		Logger.log("connecting");
		connectableObservable.connect();
		
		/*    ******************************  Sample Output  ******************************
		 * 
		 *  2018:58:36 11:47 Thread[main,5,main] ConnectableObservable demo started 
			2018:58:36 11:47 Thread[main,5,main] subscribing first 
			2018:58:36 11:47 Thread[main,5,main] subscribing second 
			2018:58:36 11:47 Thread[main,5,main] connecting 
			2018:58:36 11:47 Thread[main,5,main] Observable started 
			2018:58:36 11:47 Thread[main,5,main] push 1 
			2018:58:36 11:47 Thread[main,5,main] first received 1 
			2018:58:36 11:47 Thread[main,5,main] second received 1 
			2018:58:36 11:47 Thread[main,5,main] push 2 
			2018:58:36 11:47 Thread[main,5,main] first received 2 
			2018:58:36 11:47 Thread[main,5,main] second received 2 

		 */
	}
}
