package rxjava2;

import io.reactivex.Observable;

public class Sample {
	public static void main(String[] args) {
		Observable<String> observable = Observable.just("Hello", "World");
		
		observable.subscribe(s -> System.out.println("Got " + s));
	}
}
