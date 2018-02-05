package rxjava2;

import io.reactivex.subjects.ReplaySubject;
import rxjava2.common.Logger;

public class Subjects {
	public static void main(String[] args) {
		replaySubjectDemo();
	}
	
	/*
	 * Subject is hot publisher which will keep on emitting events even if it doesn't have any subscriber,
	 * ReplaySubject caches events and when new subscriber subscribes to subject, it starts delivering cached events first
	 * and then delivers new events in real time
	 */
	static void replaySubjectDemo() {
		Logger.log("start replaySubject demo");
		ReplaySubject<Integer> subject = ReplaySubject.createWithSize(10);
		
		Logger.log("Push 1");
		subject.onNext(1);
		
		Logger.log("Push 2");
		subject.onNext(2);
		
		Logger.log("Subscribe with first subscriber");
		subscribe(subject, "First");
		

		Logger.log("Push 3");
		subject.onNext(3);
		
		Logger.log("Subscribe with second subscriber");
		subscribe(subject, "Second");
		
		Logger.log("Push 4");
		subject.onNext(4);
		
		Logger.log("Push 5");
		subject.onNext(5);

		Logger.log("Subscribe with third subscriber");
		subscribe(subject, "Third");
		
		Logger.log("Completing stream");
		subject.onComplete();
		
		/*  *****************************  Sample output  *********************************
		 * 
		 *  2018:56:36 10:58 Thread[main,5,main] start replaySubject demo 
			2018:56:36 10:58 Thread[main,5,main] Push 1 
			2018:56:36 10:58 Thread[main,5,main] Push 2 
			2018:56:36 10:58 Thread[main,5,main] Subscribe with first subscriber 
			2018:56:36 10:58 Thread[main,5,main] First Received 1 
			2018:56:36 10:58 Thread[main,5,main] First Received 2 
			2018:56:36 10:58 Thread[main,5,main] Push 3 
			2018:56:36 10:58 Thread[main,5,main] First Received 3 
			2018:56:36 10:58 Thread[main,5,main] Subscribe with second subscriber 
			2018:56:36 10:58 Thread[main,5,main] Second Received 1 
			2018:56:36 10:58 Thread[main,5,main] Second Received 2 
			2018:56:36 10:58 Thread[main,5,main] Second Received 3 
			2018:56:36 10:58 Thread[main,5,main] Push 4 
			2018:56:36 10:58 Thread[main,5,main] First Received 4 
			2018:56:36 10:58 Thread[main,5,main] Second Received 4 
			2018:56:36 10:58 Thread[main,5,main] Push 5 
			2018:56:36 10:58 Thread[main,5,main] First Received 5 
			2018:56:36 10:58 Thread[main,5,main] Second Received 5 
			2018:56:36 10:58 Thread[main,5,main] Subscribe with third subscriber 
			2018:56:36 10:58 Thread[main,5,main] Third Received 1 
			2018:56:36 10:58 Thread[main,5,main] Third Received 2 
			2018:56:36 10:58 Thread[main,5,main] Third Received 3 
			2018:56:36 10:58 Thread[main,5,main] Third Received 4 
			2018:56:36 10:58 Thread[main,5,main] Third Received 5 
			2018:56:36 10:58 Thread[main,5,main] Completing stream 
			2018:56:36 10:58 Thread[main,5,main] First OnCompleted 
			2018:56:36 10:58 Thread[main,5,main] Second OnCompleted 
			2018:56:36 10:58 Thread[main,5,main] Third OnCompleted 

		 * 
		 */
	}
	
	static void subscribe(ReplaySubject<Integer> replaySubject, String subscriberName) {
		replaySubject.subscribe(val -> Logger.log("%s Received %s", subscriberName, val),
				ex -> Logger.log(" OnError ", subscriberName, ex),
				() -> Logger.log("%s OnCompleted", subscriberName));
	}
}
