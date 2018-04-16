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
	private void testDiamondOperator(){
		Map<String, List<Trade>> trades = new TreeMap<String, List<Trade>> ();
		Map<String, List<Trade>> trades = new TreeMap <> ();
	}

	public void testAutomaticresourcemanagement() {
		try(FileOutputStream fos = new FileOutputStream("movies.txt");
				DataOutputStream dos = new DataOutputStream(fos)) {
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

	public void newMultiMultiCatch() {
		try{
			methodThatThrowsThreeExceptions();
		} catch(ExceptionOne e) {
		} catch(ExceptionTwo | ExceptionThree e) {
		}
	}

	private void testUsingstringsinswitchstatements(Trade t){
		String status = t.getStatus();
		if(status.equalsIgnoreCase(NEW)) {
			newTrade(t);
		} else if(status.equalsIgnoreCase(EXECUTE)) {
			executeTrade(t);
		} else if(status.equalsIgnoreCase(PENDING)) {
			pendingTrade(t);
		} else {
			String status = t.getStatus();
		}
		
		switch(status){
			case NEW:
				newTrade(t);
				break;
			case EXECUTE:
				executeTrade(t);
				break;
			case PENDING:
				pendingTrade(t);
				break;
			default:
				break;
		}
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
	 
	class Test{
		void m(){
			ContaCorrente[] minhasContas;
			minhasContas = new ContaCorrente[10];
			minhasContas[0] = contaNova;
			int [] iniciaValores = { 12 , 32 , 54 , 6 , 8 , 89 , 64 , 64 , 6 };
		
		    for (int i = 0; i < 10; i++) {
		        System.out.println(idades[i]);
		    }
			
		    for (int x : array) {
		        System.out.println(x);
		    }
		}
	}
	
	class DoWhile {
		public static void main(String[] args) {
			boolean continuar=true;
			int opcao;
			Scanner entrada = new Scanner(System.in);
			do
			{
				System.out.println("\t\tMenu de opções do curso Java Progressivo:");
				System.out.println("\t1. Ver o menu");
				System.out.println("\t2. Ler o menu");
				System.out.println("\t3. Repetir o menu");
				System.out.println("\t4. Tudo de novo");
				System.out.println("\t5. Não li, pode repetir?");
				System.out.println("\t0. Sair");
				
				System.out.print("\nInsira sua opção: ");
				opcao = entrada.nextInt();
				
				if(opcao == 0){
					continuar = false;
					System.out.println("Programa finalizado.");
				}
				else{
					System.out.printf("\n\n\n\n\n\n");
				}
				
				Integer velocidadeParticula = retornarVelocidadeParticula(12, QUILOMETROS_POR_SEGUNDO);
				assert(velocidadeParticula < VELOCIDADE_LUZ):"Velocidade da particula não pode ser maior que a velocidade da luz";	
			} while( continuar );
		}
	}
	
	class Obstacles {
		private static final int BOX_LENGTH = 12;
		private ArrayList boxes;
		private WormChase wcTop;

		public Obstacles(WormChase wc) {
			boxes = new ArrayList();
			wcTop = wc;
		}

		public synchronized void add(int x, int y) {
			boxes.add(new Rectangle(x, y, BOX_LENGTH, BOX_LENGTH));
			wcTop.setBoxNumber(boxes.size()); 
		}

		synchronized public boolean hits(Point p, int size){
			Rectangle r = new Rectangle(p.x, p.y, size, size);
			Rectangle box;
			for (int i = 0; i < boxes.size(); i++) {
				box = (Rectangle) boxes.get(i);
				if (box.intersects(r))
					return true;
			}
			return false;
		}

		public void draw(Graphics g){
			Rectangle box;
			g.setColor(Color.blue);
			for (int i = 0; i < boxes.size(); i++) {
				box = (Rectangle) boxes.get(i);
				g.fillRect(box.x, box.y, box.width, box.height);
			}
		}

		public int getNumObstacles() {
			synchronized(this){
				return boxes.size();
			}
		}
	}
}


 

