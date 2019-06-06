import java.io.File;
import java.io.FileInputStream;
//this is a test comment
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
public class SimpleClassTransformer implements ClassFileTransformer {
    HashMap<String, String> methodMap = new HashMap<String, String>();
    String nameOfClass;
	@Override
    public byte[] transform( 
            final ClassLoader loader, 
            final String className,
            final Class<?> classBeingRedefined, 
            final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer ) throws IllegalClassFormatException {
        if ("sam/SampleClass".equals(className)) {
            try {
            	nameOfClass = className;
                final ClassPool classPool = ClassPool.getDefault();
                classPool.importPackage("java.lang.*");
                classPool.importPackage("org.apache.commons.io.FileUtils");
                classPool.importPackage("java.io.Serializable");
                classPool.importPackage("java.io.FileOutputStream");
                classPool.importPackage("java.io.ObjectOutputStream");
                classPool.importPackage("java.io.File");
                classPool.importPackage("java.io.IOException");
                classPool.importPackage("java.util.Arrays");
                classPool.importPackage("java.util.ArrayList");
                
                final CtClass clazz = classPool.get("sam.SampleClass");
//                TestClass test = mock(TestClass.class);             
//                when(test.add(9, 8)).thenReturn(5);
//                System.out.println(test.add(9,8));
//                File myfile = new File("src/sam/SampleClass.txt"); 
//                FileUtils.write(myfile,"this is from JarTest", "UTF8", true);  
                String newMethod = "public static void printMethodToFile(String methodname, Object[] params, String[] paramCasts, String classname, String returnCast, String serFile, String serFileReturn, String numParams){"
                		+ "\n String printLine = \"class name: \" + classname + \"| methodName: \" + methodname + \"| params: \" + Arrays.deepToString(params);"
                		+ "\n printLine += \"|param casts: \" + Arrays.toString(paramCasts) + \"|numParams: \" + numParams;"
                		+ "\n printLine += \"|serFile \" + serFile + \"|serFileReturn: \" + serFileReturn  + \"|returnCast: \" + returnCast;"
                		+ "\n System.out.println(printLine);"
                		+ "\n File myfile = new File(\"src/sam/SampleClass.txt\");" 
                		+ "\n FileUtils.write(myfile,\"\\n\" + printLine, \"UTF8\", true);"
                		+ "\n }";
                clazz.addMethod(CtNewMethod.make(newMethod, clazz)); 
                
                //CtMethod mainMethod = clazz.getDeclaredMethod("main");
                int counter = 0;
                for (final CtMethod method: clazz.getDeclaredMethods()) {
                	//System.out.println("injecting code into: " + method.getMethodInfo().getName());
                	String nameOfClass = clazz.getName();
                	String nameOfMethod = method.getMethodInfo().getName();
 

                	if(!"printMethodToFile".equals(method.getMethodInfo().getName())  && !"main".equals(method.getMethodInfo().getName())){
                    	
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
   	                    String wrapper = "";
   	                    methodMap.put(paramFile, returnFile);
   	                   
                    	
                    	String returnWrapper = "";
//                		if(method.getReturnType() instanceof CtPrimitiveType){
//                 			CtPrimitiveType type = (CtPrimitiveType) method.getReturnType();
//                 			if(!type.getWrapperName().equals("java.lang.Void")){
//                 				returnWrapper = "(" + type.getWrapperName().replace("java.lang.", "") + ")";
//                 			}
//                 		}
                 	//	else{
                 			//System.out.println(nameOfMethod + " class test " +  method.getReturnType().getName());
                 		//}
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
	                             		+ "\n FileOutputStream fileOutputStream = new FileOutputStream(\"" + paramFile + "\");"
	                             		+ "\n ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);"
	                             		+ "\n objectOutputStream.writeObject(o);"
	                             		+ "\n objectOutputStream.flush();"
	                             		+ "\n objectOutputStream.close();"
	                             		+ "\n printMethodToFile(nameofCurrMethod, o, " + sb.toString() + ", \"" + nameOfClass + "\", \"" + returnWrapper + "\", \"" + paramFile + "\", \"" + returnFile + "\", \"" + paramWrappers.size() + "\"); "
	                             		+ "\n }");
	                             		//+ "\n }System.out.println(\"paramTypes form method\" + Arrays.toString(paramTypes));"
	                    
	                    String string = "{ System.out.print(\"returned from \" + new Exception().getStackTrace()[0].getMethodName() + \":\");"
	                    		+ "\n File myfile = new File(\"src/sam/SampleClass.txt\");" 
	                    		+ "\n if(" + inStatement + "){" 
	                    		+ "\n   System.out.print(Arrays.toString($_));"
	                    		+ "\n 	FileUtils.write(myfile, \"|return val: \" + Arrays.toString($_) , \"UTF8\", true);}"
	                    		+ "\n else{"
	                    		+ "\n   System.out.print($_);"
	                    		+ "\n 	FileUtils.write(myfile, \"|return val: \" + $_.toString() , \"UTF8\", true);}"
	                    		+ "\n Object[] argsWithReturn = new Object[$args.length+1];"
	                    		+ "\n for(int i =0; i<$args.length; i++) argsWithReturn[i] = $args[i];"
	                    		+ "\n argsWithReturn[$args.length] = ($w)$_;"
	                    		+ "\n FileOutputStream fos = new FileOutputStream(\"" + returnFile + "\");"
	                            + "\n ObjectOutputStream oos = new ObjectOutputStream(fos);"
	                            + "\n oos.writeObject(argsWithReturn);"
	                            + "\n oos.flush();"
	                            + "\n oos.close();}";
	                    method.insertAfter(string);
	                    wrapper = "";
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
	
	public void seeIfReadFile(){
		File inputFile = new File("src/sam/SampleClass.txt"); 
    	try{
    		String test = FileUtils.readFileToString(inputFile, "UTF8");	
    		System.out.println("reading file: " + test);
    	}
    	catch(IOException e){
    		
    	}
	}
	
	
	
    /**
     * Reads through the map of method files that contain serialized parameters and prints those parameters
     */
    
    public void testSerlizedParamaters(){
    	for (String key: methodMap.keySet()) {
    		System.out.println("name of class: " + nameOfClass);
    		File f = new File(key);
    		if(f.length() == 0)
    			continue;
	    	try{
	    	FileInputStream fileInputStream= new FileInputStream(key);
		    ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
		    Object[] input = (Object[]) objectInputStream.readObject();
		    for(Object obj: input){
		    	System.out.println("printing serialized params from " + key + ":" + obj.toString());
		    }
		    objectInputStream.close(); 
		    System.out.println(methodMap.get(key));
		    FileInputStream fis = new FileInputStream(methodMap.get(key));
		    ObjectInputStream ois= new ObjectInputStream(fis);
		    Object[] paramsAndReturn = (Object[]) ois.readObject();
		    System.out.println(paramsAndReturn.length);
		    for(Object obj: paramsAndReturn){
		    	if(obj == null)
		    		System.out.println("null");
		    	else
		    		System.out.println("params and return " + methodMap.get(key) + ":" + obj.toString());
		    }
		    ois.close();
	    	}
	    	catch(IOException | ClassNotFoundException  c){
				c.printStackTrace();
	    		System.out.println("failed to read file");
			}
    	}
    }
    /**
     * reads class.txt file and initializes mock calls
     * @return mock call 
     */
    public String insertMockCalls(){
    	String mockCall = "";
    	String mockClass = "";
    	ArrayList<String> paramTypes = new ArrayList<>();
    	String returnType = "";
    	boolean ifArray = false;
    	File inputFile = new File("src/MockClasses.txt"); 
    	try{
    		mockClass = FileUtils.readFileToString(inputFile, "UTF8");	
    		mockClass.replace("{import_statement}", nameOfClass.replace("/", "."));
    		mockClass.replace("{classname}", nameOfClass.substring(nameOfClass.lastIndexOf(".")));
    	}
    	catch(IOException e){
    		
    	}
    	return mockCall;
    	
    }
}
