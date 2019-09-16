package sam;
import java.io.IOException;

public class SampleClass {
    public static void main( String[] xyz ) throws IOException {
        SampleClass.method1("yo", new String[] {"dfsaf", "fdsaf"});

        int[] x = {7,9};
        SampleClass.method2(new int[] {7, 9}, 0);
        SampleClass.method3(new ExampleClass[] {new ExampleClass("rach"), new ExampleClass("sam")});
    }
    
    public static String method1(String s, String[] x){ return "5";}

    
    public static ExampleClass method2(int[] s, int i){
    		return new ExampleClass("sdfadf");
    }
    
    public static String method3(ExampleClass[] e){return "yo";}
    
}