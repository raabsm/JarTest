package sam;
import java.io.IOException;

public class SampleClass {
    public static void main( String[] xyz ) throws IOException {
        SampleClass.method1("yo", new ExampleClass[] {new ExampleClass(), new ExampleClass()});

        int[] x = {7,9};
        SampleClass.method2(new String[] {"hello", "byw"}, 0);
    }
    
    public static String method1(String s, ExampleClass[] e){ return "5";}

    
    public static ExampleClass method2(String[] s, int i){
    		return new ExampleClass();
    }
    
}