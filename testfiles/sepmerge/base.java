public class Test {
	String toString(List<T> l){
		int a = 10;
		int b = 10;
		int c = 10;

		for(int i = 0; i < 10; i++){
			System.out.println(i);
		}

		if( l.size() == 0 ){ return ""; }
		return String.join(",",l);
	}
}