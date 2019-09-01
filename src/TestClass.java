import java.util.LinkedList;

import sam.ReflectionTest;

public class TestClass {
	
	public static void main(String[] args){
		String[] x = {"hello", "bye"};
		LinkedList<String> list = ReflectionTest.inspectObject(new int[] {9, 11});
		for (String s: list){
			System.out.println(s);
		}

	}
}
