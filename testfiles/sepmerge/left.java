public class Test {
	String toString(List<T> l){
		int a = 20;
		int b = 10;
		int c = 20;

		for(int i = 1; i < 10; i++){
			System.out.println(i);
		}

		if( l==null || l.isEmpty() ){ return ""; }
		return String.join(",",l);
	}
}