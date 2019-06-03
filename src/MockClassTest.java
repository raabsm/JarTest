
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.junit.*;
public class MockClassTest {

	int x;
	TestClass t;
	@Before
	public void setUp(){
		t = mock(TestClass.class);
		x = 5;
	}
	
	@Test
	public void testPerform(){
		when(t.add(9, 8)).thenReturn(6);
		assertEquals(t.add(80, 8), 1);
	}
	
	
}
