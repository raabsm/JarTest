package sam;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;

public class ReflectionTest{

		// TODO Auto-generated method stub
		
		public static <T> LinkedList<String> inspectObject(T obj){
			Class c = obj.getClass();
			LinkedList<String> list = new LinkedList<String>();
			Field[] fields = c.getDeclaredFields();
			for(int i =0; i< fields.length; i++){
				Object[] fieldValue = new Object[1];
				fields[i].setAccessible(true);
				try {
						fieldValue[0] = fields[i].get(obj);
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
				list.add("Type: " + fields[i].getType().getCanonicalName() + ", Name: " + fields[i].getName() + ", Value: " + value.substring(1,value.length()-1));
			}
			return list;
	 }

}

