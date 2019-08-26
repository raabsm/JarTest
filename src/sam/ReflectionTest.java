package sam;
import java.lang.reflect.Field;
import java.util.Arrays;

public class ReflectionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Integer e = new Integer(5);
		Class c = e.getClass();
		Field[] fields = c.getDeclaredFields();
		for(int i =0; i< fields.length; i++){
			Object[] fieldValue = new Object[1];
			fields[i].setAccessible(true);
			try {
					fieldValue[0] = fields[i].get(e);
				} catch (IllegalArgumentException e1) {
					// TODO Auto-generated catch block
					fieldValue[0] = 0;
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					// TODO Auto-generated catch block
					fieldValue[0] = 0;
					e1.printStackTrace();
				}
			String value = Arrays.deepToString(fieldValue); 	
			System.out.println("Type: " + fields[i].getType().getCanonicalName() + ", Name: " + fields[i].getName() + ", Value: " + value.substring(1,value.length()-1));
		}
		Field i;
//		try {
//			i = c.getDeclaredField("i");
//			i.setAccessible(true);
//			i.set(e, 5543);
//		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
	}

}
