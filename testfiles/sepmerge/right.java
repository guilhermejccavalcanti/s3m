public class Test {
	String toString(List<T> l){
		int a = 10;
		int b = 20;
		int c = 30;

		for(int i = 0; i < 11; i++){
			System.out.println(i);
		}

		if( l.size() == 0 ){ return EMPTY; }
		return String.join(",",l);
	}
}