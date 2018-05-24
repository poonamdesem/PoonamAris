package test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reg3 {

	public static void main(String[] arg){
//		tes1();
		tes2();
	}
	
	public static void tes1(){
		String s = "assume blah";

		Pattern pat1 = Pattern.compile(".*\\bsum\\b.*");
		Matcher mat1 = pat1.matcher(s.trim());
		System.out.println(mat1.matches());
	}
	
	public static void tes2(){
		String s = "Block: If: x < y	Value: ";
		System.out.println(s.indexOf("Value:"));
		int vIdx = s.indexOf("Value:");
		if(vIdx != -1){
			System.out.println(s.substring(7, vIdx));
		}		
	}
}
