/*
 * Test class for Java * grammar.
 */

/*
 * COMMENTED STUFF MEANS NOT WORKING
 */

import rx.util.functions.Action1;

public @interface Test {
    String lastModifiedBy() default  "none";
}

@Incubating
public enum DuplicatesStrategy implements A, B {
    INCLUDE,
    EXCLUDE;
    
    public int sum(int a, int b, int c){
    	return a + b + c;
    }
    
    public static DuplicatesStrategy fromString(String str) {
        return valueOf(str.toUpperCase());
    }
}


@Retention(RUNTIME) @Target(PARAMETER)
public @interface Header {
	String value();
}

public enum KeyEventSource { // no instances
	;
	public static Observable<KeyEvent> fromKeyEventsOf(final Component component) {}
}

public final class Test {

	public void m(){
		String nonBmpString = "AB\uD840\uDC00C";

		final class MapResultCallback implements ResultCallback {
			private Object key;
		}
	}

	public Test(){
		super();
		if (OSerializationSetThreadLocal.check((ODocument) sourceRecord)) {
			iter = new HashSet<Entry<OIdentifiable, Object>>(ORecordLazySet.super.map.entrySet()).iterator();
		} else {
			iter = ORecordLazySet.super.map.entrySet().iterator();
		}
		final int index = (hashCode >>> shift) & mask;
	}

	/*
	 * JAVA 7 TEST CODE:BEGIN
	 */
	private testDiamondOperator(){
		Map<String, List<Trade>> trades = new TreeMap<String, List<Trade>> ();
		Map<String, List<Trade>> trades = new TreeMap <> ();
	}

	public testAutomaticresourcemanagement() {
		try(FileOutputStream fos = newFileOutputStream("movies.txt");
				DataOutputStream dos = newDataOutputStream(fos)) {
			dos.writeUTF("Java 7 Block Buster");
		} catch(IOException e) {
		}
	}


	void testNumericliteralswithunderscores(){
		int million  =  1_000_000;
		for(WatchEvent<?> event : key.pollEvents()) {
			Kind<?> kind = event.kind();
			System.out.println("Event on "+ event.context().toString() + " is "+ kind);
		}
	}

	public voidnewMultiMultiCatch() {
		try{
			methodThatThrowsThreeExceptions();
		} catch(ExceptionOne e) {
		} catch(ExceptionTwo | ExceptionThree e) {
		}
	}

	private testUsingstringsinswitchstatements(Trade t){
		String status = t.getStatus();
		if(status.equalsIgnoreCase(NEW)) {
			newTrade(t);
		} else if(status.equalsIgnoreCase(EXECUTE)) {
			executeTrade(t);
		} else if(status.equalsIgnoreCase(PENDING)) {
			pendingTrade(t);
		}
		tring status = t.getStatus();
		/*		switch(status) {

		caseNEW:
			newTrade(t);
		break;
		caseEXECUTE:
			executeTrade(t);
		break;
		casePENDING:
			pendingTrade(t);
		break;
		default:
			break;
		}*/
	}
	/*
	 * JAVA 7 TEST CODE:END
	 */

	/*
	 * ######################
	 */

	/*
	 * JAVA 8 TEST CODE:BEGIN
	 */
	private interface Formula {
		double calculate(int a);

		default double sqrt(int a) {
			return Math.sqrt(a);
		}
	}

	void testDefaultMethodsforInterfaces(){
		Formula formula = new Formula() {
			@Override
			public double calculate(int a) {
				return sqrt(a * 100);
			}
		};

		formula.calculate(100);     // 100.0
		formula.sqrt(16);           // 4.0
	}

	void testLambdaExpressions(){
		List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

		Collections.sort(names, new Comparator<String>() {
			@Override
			public int compare(String a, String b) {
				return b.compareTo(a);
			}
		});

		Collections.sort(names, (String a, String b) -> {
			return b.compareTo(a);
		});

		Collections.sort(names, (String a, String b) -> b.compareTo(a));

		names.sort((a, b) -> b.compareTo(a));
	}

	@FunctionalInterface
	interface Converter<F, T> {
		T convert(F from);
	}

	void testFunctionalInterface(){
		Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
		Integer converted = converter.convert("123");
		System.out.println(converted);    // 123
	}

	private class Something {
		String startsWith(String s) {
			return String.valueOf(s.charAt(0));
		}
	}

	private class Person {
		String firstName;
		String lastName;

		Person() {}

		Person(String firstName, String lastName) {
			this.firstName = firstName;
			this.lastName = lastName;
		}
	}

	private interface PersonFactory<P extends Person> {
		P create(String firstName, String lastName);
	}

	void testMethodandConstructorReferences(){
		Converter<String, Integer> converter = Integer::valueOf;
		Integer converted = converter.convert("123");
		System.out.println(converted);   // 123

		Something something = new Something();
		Converter<String, String> converter = something::startsWith;
		String converted = converter.convert("Java");
		System.out.println(converted);    // "J"

		PersonFactory<Person> personFactory = Person::new;
		Person person = personFactory.create("Peter", "Parker");
	}

	void testingLambdaAccessinglocalvariables(){
		int a;
		a = 10;

		final int num = 1;
		Converter<Integer, String> stringConverter = (from) -> String.valueOf(from + num);
		stringConverter.convert(2);     // 3

		int num = 1;
		Converter<Integer, String> stringConverter = (from) -> String.valueOf(from + num);
		stringConverter.convert(2);     // 3
	}


	private class testAccessingfieldsandstaticvariables {
		static int outerStaticNum;
		int outerNum;

		void testScopes() {
			Converter<Integer, String> stringConverter1 = (from) -> {
				outerNum = 23;
				return String.valueOf(from);
			};

			Converter<Integer, String> stringConverter2 = (from) -> {
				outerStaticNum = 72;
				return String.valueOf(from);
			};
		}
	}


	class TestAnnotations {

		@interface Hints {
			Hint[] value();
		}

		@Repeatable(Hints.class)
		@interface Hint {
			String value();
		}

		@Hints({@Hint("hint1"), @Hint("hint2")})
		class Person {}

		@Hints({@Hint("hint1"), @Hint("hint2")})
		class Person {}

		@Target({ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
		@interface MyAnnotation {}

		void m(){

			Hint hint = Person.class.getAnnotation(Hint.class);
			System.out.println(hint);                   // null

			Hints hints1 = Person.class.getAnnotation(Hints.class);
			System.out.println(hints1.value().length);  // 2

			Hint[] hints2 = Person.class.getAnnotationsByType(Hint.class);
			System.out.println(hints2.length);          // 2
		}
	}
	/*
	 * JAVA 8 TEST CODE:FINISH
	 */
}