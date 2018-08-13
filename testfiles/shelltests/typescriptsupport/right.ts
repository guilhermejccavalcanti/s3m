class Test {

	factorial(int n): int {
		if(n <= 1)
			return 1;
		return n * factorial(n - 1);
	}
	
	sub(int a, int b): int {
		return a - b;
	}	
}
