package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reg {

	public static void main(String[] arg){
//		(\forall int i,j; 0 <= i && i < a.length && i <= j && j < a.length; a[i] <= a[j])
//		GROUP1 ==> int i in (0: a.Length), int j in (i: a.Length); a[i] <= a[j]
//				==> int i in (0: a.Length), int j in (i: a.Length)
//				==> a[i] <= a[j]
//				GROUP1 ==> int i in (0: a.Length); a[i] != key
//				==> int i in (0: a.Length)
//				==> a[i] != key
//				GROUP1 ==> int i in (0: low); a[i] != key
//				==> int i in (0: low)
//				==> a[i] != key
//				GROUP1 ==> int i in (high+1: a.Length); a[i] != key
//				==> int i in (high+1: a.Length)
//				==> a[i] != key
		String infunc = "int i in (high+1: a.Length); a[i] != key";
		String k = "forall";
		String[] parts = infunc.split(";");
		String[] init = parts[0].split(",");
		String type = "";
		String[] clauses = new String[init.length];
		String stats = "";
//		for(String s : init){		
		for(int i = 0; i < init.length; i++){
			clauses[i] = "";
			String s = init[i];
			System.out.println("==> " + s.trim());
			
			String[] ins = s.trim().split(" ");
			
			Pattern patt = Pattern
					.compile("in\\s*\\((.+)\\)\\s*");
			Matcher matc = patt.matcher(s.trim());			
			
			if(type.equals(""))
				type = ins[0];			
			
			stats += ins[1] + ",";					
			
			if (matc.find()) {
				String temp = matc.group(1);
				
				if(temp.contains(":")){
					String[] lr = temp.split(":");
					clauses[i] += lr[0] + " <= " + ins[1] + " && " + ins[1] + " < " + lr[1];
				}else{
					String[] lr = temp.split("..");
					clauses[i] += lr[0] + " <= " + ins[1] + " && " + ins[1] + " <= " + lr[1];
				}
				
				clauses[i] += matc.group(1) + " <= " + ins[1] + " && ";

				if (matc.group(2).equals(":")) {
					clauses[i] += ins[1] + " < " + matc.group(3);
				} else {
					clauses[i] += ins[1] + " <= " + matc.group(3);
				}

//				clauses[i] += matc.group(4);
//				clauses[i] = "(\\\\" + s + " " + clauses[i] + ");";
				
				System.out.println("CLAUSES[I] => " + clauses[i]);
			}
//			spec = spec.replaceFirst(s + "\\{.*\\};", clause);
		}
		
		stats = stats.substring(0, stats.length()-1);
		stats = type + " " + stats + "; ";
		
		if(init.length > 1){
			for(int i = 0; i < init.length; i++){
				stats += clauses[i] + " && ";
			}
			
			stats = stats.substring(0, stats.length() - 4) + ";";
		}else{
			stats += " " + clauses[0] + ";";
		}
				
		stats += " " + parts[1].trim();
		
		stats = "(\\\\" + k + " " + stats + ");";
		String spec = "ensures result < 0 ==> forall{int i in (0: a.Length); a[i] != key};";
		spec = spec.replaceFirst(k + "\\{.*\\};", stats);
		
		System.out.println("STATS => " + spec);
		

//
//		String clause = "", idx = "";

//		==> int i in (0: a.Length)
//		==> int j in (i: a.Length)

		
		
	}
}
