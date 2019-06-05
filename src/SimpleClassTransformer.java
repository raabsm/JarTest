import java.util.HashMap;
import java.util.List;
import java.io.File;
import java.io.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//this is a test comment
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Modifier;
import java.security.KeyStore.Entry;
import java.security.ProtectionDomain;
import org.apache.commons.io.FileUtils;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.CtPrimitiveType;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class SimpleClassTransformer implements ClassFileTransformer {
    HashMap<String, String> methodMap = new HashMap<String, String>();
	
	@Override
    public byte[] transform( 
            final ClassLoader loader, 
            final String className,
            final Class<?> classBeingRedefined, 
            final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer ) throws IllegalClassFormatException {
        if ("sam/SampleClass".equals(className)) {
            try {
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
                String newMethod = "public static void printMethod(String name, Object[] params, String classname){"
                		+ "\n String printLine = \"class name: \" + classname + \"| methodName: \" + name + \"| params: \";"
                		+ "\n System.out.print(printLine);"
                		+ "\n String paramsToString = Arrays.deepToString(params);"
                		+ "\n System.out.print(paramsToString);"
                		+ "\n File myfile = new File(\"src/sam/SampleClass.txt\");" 
                		+ "\n FileUtils.write(myfile,\"\\n\" + printLine + paramsToString, \"UTF8\", true);"
                		+ "\n }";
           //     String newMethod = "public static void printMethod(String name, Object param, String classname, boolean isArray{";
                clazz.addMethod(CtNewMethod.make(newMethod, clazz)); 
                
                //CtMethod mainMethod = clazz.getDeclaredMethod("main");
                int counter = 0;
                for (final CtMethod method: clazz.getDeclaredMethods()) {
                	//System.out.println("injecting code into: " + method.getMethodInfo().getName());
                	String nameOfClass = clazz.getName();
                	String nameOfMethod = method.getMethodInfo().getName();
                	if(methodMap.containsKey(nameOfMethod)){
                		nameOfMethod = nameOfMethod + counter++;
                	}
                	String paramFile =  nameOfMethod+ ".txt"; 

                	if(!"printMethod".equals(method.getMethodInfo().getName())  && !"main".equals(method.getMethodInfo().getName())){
                		
	                    method.insertBefore("{ String nameofCurrMethod = new Exception().getStackTrace()[0].getMethodName(); "
	                             		+ "\n Object[] o = $args;"
	                             		+ "\n FileOutputStream fileOutputStream = new FileOutputStream(\"" + paramFile + "\");"
	                             		+ "\n ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);"
	                             		+ "\n objectOutputStream.writeObject(o);"
	                             		+ "\n objectOutputStream.flush();"
	                             		+ "\n objectOutputStream.close();"
	                             		+ "\n String[] paramTypes = new String[o.length];"
	                             		+ "\n for(int i=0; i<o.length; i++){ "
	                             		+ "\n 	String paramType = o[i].getClass().getName();"
	                             		+ "\n	paramType = paramType.replace(\"[L\",\"Array \").replace(\";\",\"\");" 
	                             		+ "\n   paramTypes[i] = paramType;"
	                             		+ "\n   if(paramTypes[i].contains(\"String\") && !paramTypes[i].contains(\"Array\"))"
	                             		+ "\n   	o[i] = \"\\\"\" + o[i] + \"\\\"\"; }"
	                             		+ "\n printMethod(nameofCurrMethod, o, \"" + nameOfClass + "\"); "
	                             		+ "\n System.out.println(Arrays.toString(paramTypes));"
	                             		+ "\n File myfile = new File(\"src/sam/SampleClass.txt\");" 
	                             		+ "\n FileUtils.write(myfile,\"\\t\" + \"paramtypes\" + Arrays.toString(paramTypes), \"UTF8\", true);}");
	                    
	                    boolean ifReturnsArray = method.getReturnType().isArray();
	                    String inStatement;
	                    if(ifReturnsArray){
	                    	inStatement = "true";
	                    }
	                    else{
	                    	inStatement = "false";
	                    }
	                    String returnFile = nameOfMethod + "return.txt";
	                    String wrapper = "";
	                    System.out.println(returnFile);
	                    methodMap.put(paramFile, returnFile);
//	                    if(method.getReturnType() instanceof CtPrimitiveType){
//                			CtPrimitiveType type = (CtPrimitiveType) method.getReturnType();
//                			if(!type.getWrapperName().equals("java.lang.Void")){
//                			System.out.println(nameOfMethod + " primitive test " + type.getWrapperName());
//                			wrapper = "(" + type.getWrapperName().replace("java.lang.", "") + ")";
//                			//wrapper = "(Object)";
//                			}
//                		}
//                		else{
//                			System.out.println(nameOfMethod + " class test " +  method.getReturnType().getName());
//                		}
	                    String string = "{ System.out.print(\"returned from \" + new Exception().getStackTrace()[0].getMethodName() + \":\");"
	                    	//	+ "\n if($_!=null) System.out.println($_.getClass().getName());"
	                    		+ "\n if(" + inStatement + "){" 
	                    		+ "\n   System.out.print(Arrays.toString($_));}"
	                    		+ "\n else{"
	                    		+ "\n   System.out.print($_);}"
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
                testSerlizedParamaters();
                return byteCode;
            } catch (final NotFoundException | CannotCompileException | IOException ex) {
                ex.printStackTrace();
            } 
        }
        
        return null;
    }
    /**
     * Reads through the map of method files that contain serialized parameters and prints those parameters
     */
    
    public void testSerlizedParamaters(){
    	for (String key: methodMap.keySet()) {
    		System.out.println(key);
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
    	boolean ifArray = false;
    	File myfile = new File("src/sam/SampleClass.txt"); 
    	try{
    	List<String> lines = FileUtils.readLines(myfile, "UTF8");
    	for(String line: lines){
    		if(line.length()>0){
    			
    			
    			
    			
    		}
    	}
    	
    	}
    	catch(IOException e){
    		
    	}
    	return mockCall;
    	
    }
}
