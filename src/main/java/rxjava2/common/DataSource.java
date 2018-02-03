package rxjava2.common;

import java.util.ArrayList;
import java.util.List;

public class DataSource {
	public static List<Integer> listOfNumbers(int start, int end) {
		List<Integer> list = new ArrayList<>();
		for(int i = start; i <= end; i++) {
			list.add(i);
		}
		return list;
	}
	
	public static Integer[] arrayOfNumbers() {
		Integer[] arr = new Integer[10];
		for(int i = 0; i < 10; i++) {
			arr[i] = i + 1;
		}
		return arr;
	} 
}
