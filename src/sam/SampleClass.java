package sam;
import java.io.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
public class SampleClass {
    public static void main( String[] xyz ) throws IOException {
        //fetch("http://www.google.com");
        //fetch("http://www.yahoo.com");
        SampleClass.method1("yowhatsup", 5);
        String[] x = {"hello","goodbye"};
        SampleClass.method2(x, 0);
    }
    
    public static void method1(String s, int i){
    }
    public static int[] method2(String[] o, int i){
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