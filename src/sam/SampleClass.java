package sam;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
public class SampleClass {
    public static void main( String[] xyz ) throws IOException {
        //fetch("http://www.google.com");
        //fetch("http://www.yahoo.com");
        SampleClass.method1("yo", 70);
        SampleClass.method1(new ExampleClass[] {new ExampleClass()});
        int[] x = {7,9};
        SampleClass.method2(x, 0);
    }
    
    public static String method1(String s, int y){ return "5";
    }
    public static ExampleClass method1(ExampleClass[] y){
    	return new ExampleClass();
    }
    public static int[] method2(int[] o, int i){
    	if(i == 1){
    		int[] y = {5,6,7,8};
    		return y;
    	}
    	else{
    		int[] x = {1,2,3,4};
    		return x;
    	}
    }
    private static void fetch(final String address) 
            throws MalformedURLException, IOException {
 
        final URL url = new URL(address);                
        final URLConnection connection = url.openConnection();
         
        try( final BufferedReader in = new BufferedReader(
                new InputStreamReader( connection.getInputStream() ) ) ) {
             
            String inputLine = null;
            final StringBuffer sb = new StringBuffer();
            while ( ( inputLine = in.readLine() ) != null) {
                sb.append(inputLine);
            }       
             
            System.out.println("Content size: " + sb.length());
        }
    }
}