package rxjava2;


import java.io.File;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import io.reactivex.Completable;
import io.reactivex.Observable;

/* 
 * There are different ways to create observable
 * Those are listed in this class
*/
public class ObservableSample {
	public static void main(String[] args) {
		
		// if we have a set of values which can be used as a source of observable
		Observable.just(1, 2, 3, 4, 5)
				.subscribe(s -> System.out.print(" just " + s));
		System.out.println("\n==========================\n");
		
		// if we have array of elements which can be used as a source of observable
		Observable.fromArray(new int[]{1, 2, 3, 4})
					.subscribe(s -> System.out.print(" fromArray " + s));
		
		System.out.println("\n==========================\n");
		
		// Any iterable can be used, it might be List, Set etc
		Observable.fromIterable(Arrays.asList(new int[]{1, 2, 3, 4}))
					.subscribe(s -> System.out.print(" fromIterable " + s));
		
		System.out.println("\n==========================\n");
		
		// Any source which can return results, it can be file download and pass it to subscriber 
		Observable.fromCallable(() -> downloadFile())
					.subscribe(s -> System.out.print("fromCallable " + s.getAbsolutePath()));
		System.out.println("\n==========================\n");
		
		// Any future which does long running async task
		Observable.fromFuture(fetchDataFromNetwork())
					.subscribe(s -> System.out.print("fromFuture " + s));
		System.out.println("\n==========================\n");
		
		// Create Stream
		Observable.create(subscriber -> {
			for(int i = 1; i <= 10; i++) {
				subscriber.onNext(i);
			}
			subscriber.onComplete();
		}).subscribe(data -> System.out.print(" Stream " + data));
		
	}
	
	static File downloadFile() {
		// steps to download file and return its instance
		return new File("");
	}
	
	static Future<String> fetchDataFromNetwork() {
		return CompletableFuture.supplyAsync(() -> {
			try {

				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return "Data from Network";
		});
		
	}
	
}
