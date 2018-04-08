package nl.smith.mathematics.development;

public class Tokens {

	public static void main(String[] args) {
		StringBuilder a = new StringBuilder("Mark");
		StringBuilder c = new StringBuilder("Smith");

		StringBuilder b = new StringBuilder(a);
		b.append(c);

		System.out.println(b);
		c.append("GMalsen");
		System.out.println(b);
	}

}
