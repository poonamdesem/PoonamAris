package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reg2 {

	public static void main(String[] arg) {
		// String s = "for(int i = 0; i >= 10; i++){";
		String s = "while(x < a.length);";

		Pattern pat1 = Pattern.compile("(while|for)\\s*\\((.+)\\)\\s*\\{?");
		Matcher mat1 = pat1.matcher(s.trim());

		Pattern pat2 = Pattern.compile("\\}?\\s*while\\s*\\((.+)\\)\\s*;+");
		Matcher mat2 = pat2.matcher(s.trim());

		// System.out.println(matc.matches());

		boolean doWhile = true;

		if (mat1.matches() && mat1.find(0)) {
			doWhile = false;
		} else if (mat2.matches() && mat2.find(0)) {
			doWhile = true;
			System.out.println("MATCHES");
		}

		String g = "";
		if (!doWhile) {
			g = mat1.group(2);
		} else {
			g = mat2.group(1);
		}

		System.out.println("G ==> " + g);

		String cond = "";

		if (s.startsWith("for")) {
			cond = g.split(";")[1];
		} else if (s.startsWith("while")) {
			cond = g;
		}

		String lhs = "", rhs = "";
		boolean lt = false, gt = false;

		if (!cond.contains("&&") && !cond.contains("||")) {
			if (cond.contains("<=")) {
				String[] temp = cond.split("<=");
				lhs = temp[0].trim();
				rhs = temp[1].trim();
				lt = true;
			} else if (cond.contains("<")) {
				String[] temp = cond.split("<");
				lhs = temp[0].trim();
				rhs = temp[1].trim();
				lt = true;
			} else if (cond.contains(">=")) {
				String[] temp = cond.split(">=");
				lhs = temp[0].trim();
				rhs = temp[1].trim();
				gt = true;
			} else if (cond.contains(">")) {
				String[] temp = cond.split(">");
				lhs = temp[0].trim();
				rhs = temp[1].trim();
				gt = true;
			}

			if (lt) {
				System.out.println("//@ (* decreasing " + rhs + " - " + lhs
						+ " *);");
			} else {
				System.out.println("//@ (* decreasing " + lhs + " - " + rhs
						+ " *);");
			}
		}

		// }
	}
}
