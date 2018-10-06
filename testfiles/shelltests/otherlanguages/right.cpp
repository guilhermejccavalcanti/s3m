class Test {

public:
	int factorial(int n) {
		if(n <= 1)
			return 1;
		return n * factorial(n - 1);
	}

	int arrangement(int n, int k) {
		return factorial(n) / factorial(n - k);
	}
}
