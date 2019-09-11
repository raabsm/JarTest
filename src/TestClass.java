import java.util.LinkedList;

import sam.ReflectionTest;

public class TestClass {
	
	public static void test1(Object o){
		System.out.println("test1" + o.toString());
	}
	public static void test1(Object[] o){
		Class c = o[0].getClass();
		System.out.println("test2" + c.getCanonicalName() + c.getComponentType());
		if(c.getSimpleName().contains("[]")){
			test1((Object[])o[0]);
		}
		else{
			test1(o[0]);
		}
	}
	
	
	public static void main(String[] args){
		String[] x = {"hello", "bye"};
		LinkedList<String> l = new LinkedList();
		l.add("hello");
		LinkedList<String> list = ReflectionTest.inspectObject(l);
		for (String s: list){
			System.out.println(s);
		}
		Object[] obj = {"hello", 5};
		test1(new String[][][] {new String[][] {new String[] {"hello"}}});
		System.out.println("________");
		char u = '6';
		test1(u);
		System.out.println("hello".contains("h"));

	}
}
