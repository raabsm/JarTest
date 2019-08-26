import java.util.LinkedList;

import sam.ExampleClass;
import sam.ReflectionTest;

public class TestClass {
	
	public static void main(String[] args){
		ExampleClass e = new ExampleClass();
		LinkedList<String> list = ReflectionTest.inspectObject(e);
		for (String s: list){
			System.out.println(s);
		}
	}
}
