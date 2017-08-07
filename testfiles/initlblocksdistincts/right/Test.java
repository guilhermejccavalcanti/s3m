public class Test {
	
	static String _name;
	
	static {
	    try {
	        Class.forName("Right");
	    } catch (ClassNotFoundException e) {
	    	e.printStackTrace();
	    }
	}
}