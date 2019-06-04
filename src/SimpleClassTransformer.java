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
import java.security.ProtectionDomain;
import org.apache.commons.io.FileUtils;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
public class SimpleClassTransformer implements ClassFileTransformer {
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
                
                final CtClass clazz = classPool.get("sam.SampleClass");
//                TestClass test = mock(TestClass.class);             
//                when(test.add(9, 8)).thenReturn(5);
//                System.out.println(test.add(9,8));
//                File myfile = new File("src/sam/SampleClass.txt"); 
//                FileUtils.write(myfile,"this is from JarTest", "UTF8", true);  
               // CtClass hashClass = ClassPool.getDefault().get("java.util.HashMap");
               // CtField f = new CtField(hashClass, "methodMap", clazz);
               // clazz.addField(f);

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

                for (final CtMethod method: clazz.getDeclaredMethods()) {
                	//System.out.println("injecting code into: " + method.getMethodInfo().getName());
                	String nameOfClass = clazz.getName();
                	/*CodeAttribute ca = method.getMethodInfo().getCodeAttribute(); 
                	CodeIterator i = ca.iterator();
                	while(i.hasNext()){
                		System.out.println(i.next());
                	}*/
//                	CtClass[] types = method.getParameterTypes();
//                	StringBuilder sb = new StringBuilder();
//                	
//                	for(CtClass thisType: types){
//                		System.out.println("method: " + method.getName() + "  " + thisType.getClass());
////                		if(thisType.getClass() == javassist.CtPrimitiveType.class){
////                			System.out.println("primitive");
////                		}
//                	}
//                	MethodInfo methodInfo = method.getMethodInfo();
//                	LocalVariableAttribute table = methodInfo.getCodeAttribute().getAttribute(javassist.bytecode.LocalVariableAttribute.tag);
//                	
//                	
                	if(!"printMethod".equals(method.getMethodInfo().getName())  && !"main".equals(method.getMethodInfo().getName())){
	                    method.insertBefore("{ String nameofCurrMethod = new Exception().getStackTrace()[0].getMethodName(); "
	                             		+ "\n Object[] o = $args; "
	                             		+ "\n FileOutputStream fileOutputStream = new FileOutputStream(\"SerializeTest.txt\");"
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
	                    
	                    
	                    
	          /*          + "\n System.out.print(\"class name: \" + classname + \"| methodName: \" + name + \"| params: \");"
                		+ "\n for(int i = 0; i< params.length; i++){"
                		+ "\n int[] x = {1,2,3,4,5};"
                		+ "\n if(params[i].getClass().isArray()){"
                		+ "\n 	System.out.println(Arrays.toString(params[i]));}"
                		+ "\n else{System.out.print(\" \" + params[i].getClass().getName() + \" \" + params[i] + \",\");}"
                		+ "\n }"
                		+ "\n }"*/
	                    
	                    boolean ifReturnsArray = method.getReturnType().isArray();
	                    String inStatement;
	                    if(ifReturnsArray){
	                    	inStatement = "true";
	                    }
	                    else{
	                    	inStatement = "false";
	                    }
	                    
	                    method.insertAfter("{ System.out.println(\"returned from \" + new Exception().getStackTrace()[0].getMethodName() + \":\");"
	                    		+ "\n if(" + inStatement + "){" 
	                    		+ "\n   System.out.println(Arrays.toString($_));}"
	                    		+ "\n else{"
	                    		+ "\n   System.out.println($_);}}");
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
     * reads serialized object[] parameters from the last method call and prints them to the screen
     */
    
    public void testSerlizedParamaters(){
    	try{
    	FileInputStream fileInputStream= new FileInputStream("SerializeTest.txt");
	    ObjectInputStream objectInputStream= new ObjectInputStream(fileInputStream);
	    Object[] o = (Object[]) objectInputStream.readObject();
	    objectInputStream.close(); 
	    for(Object obj: o){
	    	System.out.println("printing serialized params + " + obj.toString());
	    }
    	}
    	catch(IOException | ClassNotFoundException  c){
			System.out.println("failed to read file");
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
    		
    		
    		
    	}
    	
    	}
    	catch(IOException e){
    		
    	}
    	return mockCall;
    	
    }
}
