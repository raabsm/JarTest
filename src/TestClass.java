import java.util.LinkedList;

import sam.ReflectionTest;

public class TestClass {
	
	public static void test1(Object o){
		System.out.println("test1");
	}
	public static void test1(Object[] o){
		System.out.println("test2");
	}
	
	
	public static void main(String[] args){
		String[] x = {"hello", "bye"};
		LinkedList<String> list = ReflectionTest.inspectObject(new int[] {9, 11});
		for (String s: list){
			System.out.println(s);
		}
		Object[] obj = {"hello", 5};
		test1(new Object[] {1,2,3});

	}
}
