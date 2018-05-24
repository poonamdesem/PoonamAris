package test;

import java.lang.reflect.*;

import core.Pair;

public class Reflection {

	public static void main(String[] args) {
		Class aClass = Reflection.class;
		Method[] meths = aClass.getDeclaredMethods();
		
		for(Method m : meths){
			System.out.println(m.toGenericString());
			Parameter[] params = m.getParameters();
	        for (int i = 0; i < params.length; i++) {
	            printParameter(params[i]);
	            System.out.println();
	        }
		}
		
	}

	public static void printParameter(Parameter p) {
		System.out.println(p.getType().toString().startsWith("class"));
		System.out.println("Parameter class: " + p.getType());
		System.out.println("Parameter name: " + p.getName());
		System.out.println("Modifiers: " + p.getModifiers());
		System.out.println("Is implicit: " + p.isImplicit());
		System.out.println("Is name present: " + p.isNamePresent());
		System.out.println("Is synthetic: " + p.isSynthetic());
    }
	
	public void tes(int anInt, Pair aPair) {

	}
	
	public boolean simpleMethod(String stringParam, int intParam) {
        System.out.println("String: " + stringParam + ", integer: " + intParam); 
	    return true;
    }
}
