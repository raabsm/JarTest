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
import javassist.Modifier;
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
                classPool.importPackage("java.lang.reflect.Field");
                classPool.importPackage("java.util.Arrays");
                classPool.importPackage("java.util.List");
                classPool.importPackage("org.json.simple.JSONObject");
                classPool.importPackage("org.json.simple.JSONArray");
                classPool.importPackage("org.json.simple.parser.JSONParser");
                classPool.importPackage("java.util.Set");

                classPool.appendClassPath(new LoaderClassPath(loader));
                String classNameWithDots= className.replace("/", ".");
                
                final CtClass clazz = classPool.get(classNameWithDots); 
                String storeObject = "{"
									+ "\n JSONObject jsonObj = new JSONObject();"
									+ "\n Class c = $1.getClass();"
									+ "\n Field[] fields = c.getDeclaredFields();"
									+ "\n for(int i =0; i<fields.length; i++){"
									+ "\n	Object[] fieldValue = new Object[1];"
									+ "\n	fields[i].setAccessible(true);"
									+ "\n	fieldValue[0] = fields[i].get($1);"
									+ "\n 	String paramType =  fields[i].getType().getCanonicalName() + \"--\" + fields[i].getName();"
									+ "\n 	jsonObj.put(paramType, storeParam(fieldValue,paramType).get(paramType));"
									+ "\n }"
									+ "\n return jsonObj;"
									+ "\n }";
                
               
                String storeObjects = "{"
                					+ "\n JSONArray jArr = new JSONArray();"
                					+ "\n if($1[0].getClass().getComponentType()!=null){"
                					+ "\n 	 jArr.add(storeObjectArray((Object[])$1[0]));"
                					+ "\n }"
                					+ "\n else{"
                					+ "\n 	for(int i =0; i<$1.length; i++){" 
                					+ "\n 		jArr.add(storeObject($1[i]));"
                					+ "\n 	}"
                					+ "\n }"
                					+ "\n return jArr;"
                					+ "\n }";
                
                
                
                String ifArrMethod = "{"
								    + "\n 	Class c = $1.getClass();"
								   	+ "\n	return c.getCanonicalName().contains(\"[]\");}";
                
                String ifPrimitive = "public static boolean ifPrimitive(String type){"
                					+ "\n return (type.contains(\"int\") || "
                					+ "type.contains(\"int\") ||"
                					+ " type.contains(\"byte\") ||"
                					+ " type.contains(\"long\") ||"
                					+ " type.contains(\"short\") ||"
                					+ " type.contains(\"java\") ||"
                					+ " type.contains(\"boolean\") ||"
                					+ " type.contains(\"char\") ||"
                					+ " type.contains(\"float\") ||"
                					+ " type.contains(\"double\"));}";

                String storeParam = "{"
                					+ "\n Object paramObj = $1[0];"
                					+ "\n JSONObject jsonParam = new JSONObject();"
                					+ "\n String paramCanonicalName = paramObj.getClass().getCanonicalName();"
                					+ "\n if(ifPrimitive(paramCanonicalName)){"
                					+ "\n 	String value = Arrays.deepToString($1);"
                					+ "\n 	jsonParam.put($2, value.substring(1,value.length()-1));"
                					+ "\n }"
                					+ "\n else{"
                					+ "\n 	if(paramCanonicalName.contains(\"[]\")){"
                					+ "\n 		jsonParam.put($2, storeObjectArray((Object[])paramObj));"
                					+ "\n 	}"
                					+ "\n 	else{"
                					+ "\n 		jsonParam.put($2, storeObject(paramObj));"
                					+ "\n 	}"	
                					+ "\n }"
                					+ "\n return jsonParam;"
                					+ "\n }";
               
                
                String storeParams = "{"
                		+ "\n JSONArray paramArr = new JSONArray();"
                		+ "\n JSONObject paramJsonObj;"
                		+ "\n Object[] paramWrapperObject = new Object[1];"
                		+ "\n int num = $3;"
                		+ "\n for(int i = 0; i<num; i++){"
                		+ "\n 	paramJsonObj  = new JSONObject();"
                		+ "\n 	paramWrapperObject[0] = $1[i];"
                		+ "\n 	paramJsonObj.put($2[i], storeParam(paramWrapperObject, $2[i]).get($2[i]));"
                		+ "\n 	paramArr.add(paramJsonObj);}"
                		+ "\n return paramArr;"
                	    + "\n }";
                
                String storeParamsOrWriteFile = "public static void storeParamsOrWriteFile(String methodSignature, Object[] params, String[] paramCasts, int numParams){"
                		+ "\n String fileName = \"" + className + ".json\";"
                		+ "\n File myfile = new File(fileName);" //took out output for now
                		+ "\n JSONParser parser = new JSONParser();"
                		+ "\n try{"
                		+ "\n 	String json = FileUtils.readFileToString(myfile, \"UTF8\");"
                		+ "\n 	JSONObject readJsonObj = (JSONObject)parser.parse(json);"
                		+ "\n 	JSONObject methods = (JSONObject)readJsonObj.get(\"methods\");"
                		+ "\n 	JSONObject method_info = new JSONObject();"
                		+ "\n 	method_info.put(\"params_before\", storeParams(params, paramCasts, numParams));"
                		+ "\n 	methods.put(methodSignature, method_info);"
                		+ "\n 	FileUtils.write(myfile, readJsonObj.toJSONString(), \"UTF8\", false);"
                		+ "\n }"
                		+ "\n catch(IOException e){"
                		+ "\n 	JSONObject classObject = new JSONObject();"
                		+ "\n  	classObject.put(\"class_name\", \"" + className + "\");"
                		+ "\n 	JSONObject methods = new JSONObject();"
                		+ "\n 	JSONObject method_info = new JSONObject();"
                		+ "\n 	method_info.put(\"params_before\", storeParams(params, paramCasts, numParams));"
                		+ "\n 	methods.put(methodSignature, method_info);"
                		+ "\n 	classObject.put(\"methods\", methods);"
                		+ "\n 	FileUtils.write(myfile, classObject.toJSONString(), \"UTF8\", false);"
                		+ "\n 	System.out.println(classObject.toJSONString());"
                		+ "\n }"
                		+ "\n }";
                
                String storeObjectAbstract = "public static abstract JSONObject storeObject(Object obj);";
                String storeObjectsAbstract = "public static abstract JSONArray storeObjectArray(Object[] obj);";
                String ifArrAbstract = "public static abstract boolean ifArray(Object obj);";
                String storeParamAbstract = "public static abstract JSONObject storeParam(Object[] param, String paramCast);";
                String storeParamsAbstract = "public static abstract JSONArray storeParams(Object[] params, String[] paramCasts, int numParams);";
                
                CtMethod method1 = CtNewMethod.make(ifArrAbstract, clazz);
                CtMethod method2 = CtNewMethod.make(storeObjectAbstract, clazz);
                CtMethod method3 = CtNewMethod.make(storeObjectsAbstract, clazz);
                CtMethod method4 = CtNewMethod.make(storeParamAbstract, clazz);
                CtMethod method5 = CtNewMethod.make(storeParamsAbstract, clazz);
                clazz.addMethod(CtNewMethod.make(ifPrimitive, clazz));

                clazz.addMethod(method1);
                clazz.addMethod(method2);
                clazz.addMethod(method3);
                clazz.addMethod(method4);
                clazz.addMethod(method5);
                method1.setBody(ifArrMethod);
                method2.setBody(storeObject);
                method3.setBody(storeObjects);
                method4.setBody(storeParam);
                method5.setBody(storeParams);
                clazz.setModifiers(clazz.getModifiers() & ~Modifier.ABSTRACT);
                clazz.addMethod(CtNewMethod.make(storeParamsOrWriteFile, clazz));

                //CtMethod mainMethod = clazz.getDeclaredMethod("main");
                int counter = 0;
                for (final CtMethod method: clazz.getDeclaredMethods()) {
                	//System.out.println("injecting code into: " + method.getMethodInfo().getName());
                	String nameOfClass = clazz.getName();
                	String nameOfMethod = method.getMethodInfo().getName();	
                	if(!"storeParamsOrWriteFile".equals(nameOfMethod)  && !"main".equals(nameOfMethod)  && !"storeObject".equals(nameOfMethod) && !"storeParams".equals(nameOfMethod) && !"ifArray".equals(nameOfMethod) && !"storeParam".equals(nameOfMethod) && !"storeObjectArray".equals(nameOfMethod)&& !"ifPrimitive".equals(nameOfMethod)){
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
                 			paramWrappers.add(paramWrapper);
                		}
                		StringBuilder paramCasts = new StringBuilder("new String[] {");
                		StringBuilder methodSignature = new StringBuilder(nameOfMethod + "(");
                		
                		for(int i = 0; i<paramWrappers.size()-1; i++){
                			paramCasts.append("\"" + paramWrappers.get(i) + "\",");
                			methodSignature.append(paramWrappers.get(i) + ",");
                		}
                    	
                		paramCasts.append("\"" + paramWrappers.get(paramWrappers.size()-1) + "\"}");
                		methodSignature.append(paramWrappers.get(paramWrappers.size()-1) + ")");
                		System.out.println(methodSignature.toString());
	                    method.insertBefore("{ String nameOfCurrMethod = new Exception().getStackTrace()[0].getMethodName(); "
	                             		+ "\n Object[] o = $args;"
	                             		+ "\n storeParamsOrWriteFile(\"" + methodSignature.toString() + "\", o, " + paramCasts.toString() + ", " + paramWrappers.size() + ");"
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