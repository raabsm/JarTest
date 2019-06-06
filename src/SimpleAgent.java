import java.lang.instrument.Instrumentation;

public class SimpleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
//        $ java -javaagent:”your_jarfile.jar=output=c:\junit_mock_data\;”
//        
//        
//        
//
//In your premain(String agentArgs, Instrumentation inst)
//
//
//
//To parse the agentArgs String,
//
//split the string on ‘;’
//
//Then split each parameter on ‘=’
//
//Then look for the parameter name in your string array.
    	String output = null;
    	if(agentArgs!=null){
	    	String[] parms = agentArgs.split(";");
	        for(String s: parms){
	        	String[] pair = s.split("=");
	        	if("output".equals(pair[0]))
	        		output = pair[1];
	        }
        }
    	final SimpleClassTransformer transformer = new SimpleClassTransformer();
    	transformer.setOutput(output);
        inst.addTransformer(transformer);
    }
}
