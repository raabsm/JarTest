//this is a test comment
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
public class SimpleClassTransformer implements ClassFileTransformer {
    HashMap<String, String> methodMap = new HashMap<String, String>();
    String nameOfClass;
    String allMockCalls = "";
	
    private String output = "";
    
    
    public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		if(output!=null) {
			this.output = output;
			System.out.println("output set to " + output);
		}
	}

	@Override
    public byte[] transform( 
            final ClassLoader loader, 
            final String className,
            final Class<?> classBeingRedefined, 
            final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer ) throws IllegalClassFormatException {
        if ("sam/SampleClass".equals(className) || className.startsWith("ibi") || className.startsWith("com/ibi") || className.startsWith("com\\ibi")) {
            try {
            	nameOfClass = className;
                final ClassPool classPool = ClassPool.getDefault();
//                classPool.importPackage("java.lang.*");
                classPool.importPackage("org.apache.commons.io.FileUtils");
                classPool.importPackage("java.io.File");
                classPool.importPackage("java.io.IOException");
                classPool.importPackage("java.util.Arrays");
                classPool.importPackage("java.util.ArrayList");
                classPool.importPackage("java.util.LinkedList");
                classPool.importPackage("java.lang.reflect.Field");
                
                classPool.appendClassPath(new LoaderClassPath(loader));
                String classNameWithDots= className.replace("/", ".");
                
                final CtClass clazz = classPool.get(classNameWithDots); 
                String firstMethod = "public static String[] inspectObject(Object obj){"
									+ "\n 	Class c = obj.getClass();"
									+ "\n	Field[] fields = c.getDeclaredFields();"
									+ "\n	String[] list = new String[fields.length];"
									+ "\n	for(int i =0; i<fields.length; i++){"
									+ "\n		Object[] fieldValue = new Object[1];"
									+ "\n		fields[i].setAccessible(true);"
									+ "\n		fieldValue[0] = fields[i].get(obj);"
									+ "\n		String value = Arrays.deepToString(fieldValue);" 	
									+ "\n		list[i] = \"Type: \" + fields[i].getType().getCanonicalName() + \", Name: \" + fields[i].getName() + \", Value: \" + value.substring(1,value.length()-1);"
									+ "\n	}"
									+ "\n	return list;"
									+ "\n }";
                clazz.addMethod(CtNewMethod.make(firstMethod, clazz)); 
                
                String newMethod = "public static void printMethodToFile(String methodname, Object[] params, String[] paramCasts, String classname, String returnCast, String serFile, String serFileReturn, String numParams){"
                		+ "\n String printLine = \"class name: \" + classname + \"| methodName: \" + methodname + \"| params: \" + Arrays.deepToString(params);"
                		+ "\n printLine += \"|param casts: \" + Arrays.toString(paramCasts) + \"|numParams: \" + numParams;"
                		+ "\n printLine += \"|serFile \" + serFile + \"|serFileReturn: \" + serFileReturn  + \"|returnCast: \" + returnCast;"
                		+ "\n System.out.println(printLine);"
                		+ "\n File myfile = new File(\"" + output + className + ".txt\");" 
                		+ "\n FileUtils.write(myfile,\"\\n\" + printLine, \"UTF8\", true);"
                		+ "\n }";
                clazz.addMethod(CtNewMethod.make(newMethod, clazz)); 
                
                //CtMethod mainMethod = clazz.getDeclaredMethod("main");
                int counter = 0;
                for (final CtMethod method: clazz.getDeclaredMethods()) {
                	//System.out.println("injecting code into: " + method.getMethodInfo().getName());
                	String nameOfClass = clazz.getName();
                	String nameOfMethod = method.getMethodInfo().getName();	

                	if(!"printMethodToFile".equals(nameOfMethod)  && !"main".equals(nameOfMethod)  && !"inspectObject".equals(nameOfMethod)){
                    	String paramFile =  nameOfMethod+ ".txt";
                    	
                    	boolean ifReturnsArray = method.getReturnType().isArray();
   	                    String inStatement;
   	                    if(ifReturnsArray){
   	                    	inStatement = "true";
   	                    }
   	                    else{
   	                    	inStatement = "false";
   	                    }
   	                    if(methodMap.containsKey(paramFile)){
	                 		paramFile = counter++ + paramFile;
                 	}
   	                    String returnFile = "return" + paramFile;
   	                    methodMap.put(paramFile, returnFile);
   	                   
                    	
                    	String returnWrapper = "";
             			returnWrapper = method.getReturnType().getName();
             			returnWrapper = returnWrapper.substring(returnWrapper.lastIndexOf(".")+1);
             			returnWrapper = "(" + returnWrapper + ")";
                		
                		ArrayList<String> paramWrappers = new ArrayList<>();
                		CtClass[] paramTypeWrappers = method.getParameterTypes();
                		for(CtClass c: paramTypeWrappers){
                			String paramWrapper = c.getName();
                 			paramWrapper = paramWrapper.substring(paramWrapper.lastIndexOf(".")+1);
                 			paramWrapper = "(" + paramWrapper + ")";
                 			paramWrappers.add(paramWrapper);
                		}
                		
                		StringBuilder sb = new StringBuilder("new String[] {");
                		for(int i = 0; i<paramWrappers.size()-1; i++){
                			sb.append("\"" + paramWrappers.get(i) + "\",");
                		}
                		sb.append("\"" + paramWrappers.get(paramWrappers.size()-1) + "\"}");
	                    method.insertBefore("{ String nameofCurrMethod = new Exception().getStackTrace()[0].getMethodName(); "
	                             		+ "\n Object[] o = $args;"
	                             		+ "\n for(int i = 0; i<o.length; i++){"
	                             		+ "\n 	Class c = o[i].getClass();"
	                             		+ "\n	if(!c.getCanonicalName().contains(\"java\")){"
	                             		+ "\n		String[] inspection = inspectObject(o[i]);"
	                             		+ "\n		for(int x=0;x<inspection.length;x++) System.out.println(inspection[x]);"
	                             		+ "\n	}"
	                             		+ "\n }"
	                             		+ "\n inspectObject($args);"
	                             		+ "\n printMethodToFile(nameofCurrMethod, o, " + sb.toString() + ", \"" + nameOfClass + "\", \"" + returnWrapper + "\", \"" + paramFile + "\", \"" + returnFile + "\", \"" + paramWrappers.size() + "\"); "
	                             		+ "\n }");
	                    
	                    String insertAfter = "{ System.out.print(\"returned from \" + new Exception().getStackTrace()[0].getMethodName() + \":\");"
	                    		+ "\n File myfile = new File(\"" + output + className + ".txt\");" 
	                    		+ "\n if(" + inStatement + "){" 
	                    		+ "\n   System.out.print(Arrays.toString($_));"
	                    		+ "\n 	FileUtils.write(myfile, \"|return val: \" + Arrays.toString($_) , \"UTF8\", true);}"
	                    		+ "\n else{"
	                    		+ "\n   System.out.print($_);"
	                    		+ "\n 	FileUtils.write(myfile, \"|return val: \" + $_.toString() , \"UTF8\", true);}"
	                    		+ "\n}";
	                    method.insertAfter(insertAfter);
                	}
                }
                byte[] byteCode = clazz.toBytecode();
                clazz.detach();
                //testSerlizedParamaters();
                //seeIfReadFile();
                return byteCode;
            } catch (final NotFoundException | CannotCompileException | IOException ex) {
                ex.printStackTrace();
            } 
        }
        
        return null;
    }

}