import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import sam.SampleClass;
public class MockClassTest {

	int x;
	TestClass t;
	SampleClass cls;
	@Before
	public void setUp(){
		t = mock(TestClass.class);
		x = 5;
		cls = mock(SampleClass.class);
	}
	
	@Test
	public void testPerform(){
		//when(cls.method2(new String[] {"hello", "j", "k"}, 5, new ExampleClass())).thenReturn(new int[]{3,4,5});
		//when().thenReturn();
//      TestClass test = mock(TestClass.class);             
//      when(test.add(9, 8)).thenReturn(5);
//      System.out.println(test.add(9,8));
//      File myfile = new File("src/sam/SampleClass.txt"); 
//      FileUtils.write(myfile,"this is from JarTest", "UTF8", true); 
		assertEquals(t.add(80, 8), 1);
	}
	
}
