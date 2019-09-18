import java.lang.instrument.Instrumentation;

public class SimpleAgent {
    public static void premain(String agentArgs, Instrumentation inst) {

    	String output = null;
    	System.out.println(agentArgs);
    	String[]  classes = {};
    	if(agentArgs!=null){
	    	String[] parms = agentArgs.split(";");
	        for(String s: parms){
	        	String[] pair = s.split("=");
	        	if("output".equals(pair[0]))
	        		output = pair[1];
	        		classes = pair[2].split(",");
	        }
        }
    	final SimpleClassTransformer transformer = new SimpleClassTransformer();
    	transformer.setOutput(output);
    	transformer.setClasses(classes);
        inst.addTransformer(transformer);
    }
}
