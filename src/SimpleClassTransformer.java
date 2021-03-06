//this is a test comment
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import javassist.NotFoundException;
public class SimpleClassTransformer implements ClassFileTransformer {
	private HashSet<String> classes;
    private String output = "";
    
    public String getOutput() {
		return output;
	}
    
    public void setClasses(String[] classes){
    	this.classes = new HashSet<String>(Arrays.asList(classes));
    	System.out.println(this.classes);
    }
    
	public void setOutput(String output) {
		if(output!=null) {
			this.output = output;
			System.out.println("output set to " + output);
		}
	}
	
	public boolean ifIbiClass(String className){
		for(String s: classes){
			if(className.startsWith(s)) return true;
		}
		return false;
	}

	@Override
    public byte[] transform(
            final ClassLoader loader, 
            final String className,
            final Class<?> classBeingRedefined, 
            final ProtectionDomain protectionDomain,
            final byte[] classfileBuffer ) throws IllegalClassFormatException {
        if (ifIbiClass(className)) {
            try {
                final ClassPool classPool = ClassPool.getDefault();
                classPool.importPackage("org.apache.commons.io.FileUtils");
                classPool.importPackage("java.io.File");
                classPool.importPackage("java.io.IOException");
                classPool.importPackage("java.lang.reflect.Field");
                classPool.importPackage("java.util.Arrays");
                classPool.importPackage("java.util.Collection");
                classPool.importPackage("java.util.Iterator");
                classPool.importPackage("org.json.simple.JSONObject");
                classPool.importPackage("org.json.simple.JSONArray");
                classPool.importPackage("org.json.simple.parser.JSONParser");
                classPool.importPackage("java.util.Set");
                classPool.importPackage("java.util.Map");

                classPool.appendClassPath(new LoaderClassPath(loader));
                String classNameWithDots= className.replace("/", ".");
                
                final CtClass clazz = classPool.get(classNameWithDots);
                
                String storeParamsOrWriteFile = "public static void storeParamsOrWriteFile(String methodSignature, Object[] params, String[] paramCasts, int numParams){"
                		+ "\n String fileName = \"" + output+ className + ".json\";"
                		+ "\n File myfile = new File(fileName);"
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
                		+ "\n  	classObject.put(\"class_name\", \"" + classNameWithDots + "\");"
                		+ "\n 	JSONObject methods = new JSONObject();"
                		+ "\n 	JSONObject method_info = new JSONObject();"
                		+ "\n 	method_info.put(\"params_before\", storeParams(params, paramCasts, numParams));"
                		+ "\n 	methods.put(methodSignature, method_info);"
                		+ "\n 	classObject.put(\"methods\", methods);"
                		+ "\n 	FileUtils.write(myfile, classObject.toJSONString(), \"UTF8\", false);"
                		+ "\n 	System.out.println(classObject.toJSONString());"
                		+ "\n }"
                		+ "\n }";
                
                String storeReturn = "public static void storeReturn(String methodSignature, Object[] params, String[] paramCasts, int numParams, Object[] returnParam, String returnParamCast){"
                		+ "\n String fileName = \"" + output + className + ".json\";"
                		+ "\n File myfile = new File(fileName);" //took out output for now
                		+ "\n JSONParser parser = new JSONParser();"
		                + "\n try{"
		        		+ "\n 	String json = FileUtils.readFileToString(myfile, \"UTF8\");"
		        		+ "\n 	JSONObject readJsonObj = (JSONObject)parser.parse(json);"
		        		+ "\n 	JSONObject methods = (JSONObject)readJsonObj.get(\"methods\");"
		        		+ "\n 	JSONObject method = (JSONObject)methods.get(methodSignature);"
		        		+ "\n 	method.put(\"params_after\", storeParams(params, paramCasts, numParams));"
		        		+ "\n 	method.put(\"returned\", storeParam(returnParam, returnParamCast));"
		        		+ "\n 	FileUtils.write(myfile, readJsonObj.toJSONString(), \"UTF8\", false);"
		        		+ "\n 	System.out.println(readJsonObj.toJSONString());"
		        		+ "\n }"
		        		+ "\n catch(IOException e){"
		        		+ "\n 	e.printStackTrace();"
		        		+ "\n }}";           		
                
                String ifPrimitive = "public static boolean ifPrimitive(String type){"
                					+ "\n return (type.contains(\"int\") || "
                					+ " type.contains(\"byte\") ||"
                					+ " type.contains(\"long\") ||"
                					+ " type.contains(\"short\") ||"
                					+ " type.contains(\"lang\") ||"
                					+ " type.contains(\"boolean\") ||"
                					+ " type.contains(\"char\") ||"
                					+ " type.contains(\"float\") ||"
                					+ " type.contains(\"double\"));}";
                
               /*-----group of mutually recursive methods, must be declared as abstract, and then added to class-----*/
                
                /*public static JSONArray storeParams(Object[] params ($1), String[] paramCasts ($2) , int numParams ($3) );*/ 
                
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
                
                /*public static JSONObject storeParam(Object[] param ($1) , String paramCast ($2) )*/
                
                String storeParam = "{"
    					+ "\n Object paramObj = $1[0];"
    					+ "\n JSONObject jsonParam = new JSONObject();"
    					+ "\n if($2.equals(\"void\")){"
    					+ "\n 	jsonParam.put(\"void\", null);"
    					+ "\n 	return jsonParam;"
    					+ "\n }"
    					+ "\n String paramCanonicalName = paramObj.getClass().getCanonicalName();"
    					+ "\n if(ifPrimitive(paramCanonicalName)){"
    					+ "\n 	String value = Arrays.deepToString($1);"
    					+ "\n 	jsonParam.put($2, value.substring(1,value.length()-1));"
    					+ "\n }"
    					+ "\n else{"
    					+ "\n 	if(paramCanonicalName.contains(\"Set\") || paramCanonicalName.contains(\"List\")){"
    					+ "\n 		jsonParam.put($2, storeList((Collection)paramObj));"
    					+ "\n 	}"
    					+ "\n 	else if(paramCanonicalName.contains(\"Map\")){"
    					+ "\n 		jsonParam.put($2, storeMap((Map)paramObj));"
    					+ "\n 	}"
    					+ "\n 	else if(paramCanonicalName.contains(\"[]\")){"
    					+ "\n 		jsonParam.put($2, storeObjectArray((Object[])paramObj));"
    					+ "\n 	}"
    					+ "\n 	else{"
    					+ "\n 		jsonParam.put($2, storeObject(paramObj));"
    					+ "\n 	}"	
    					+ "\n }"
    					+ "\n return jsonParam;"
    					+ "\n }";       
                
                /* public static JSONArray storeObjectArray(Object[] obj ($1) ) */
               
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
                
                /*public static JSONObject storeObject(Object obj ($1) );*/
                
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
                
                /*public static JSONArray storeMap(Map map ($1) )*/
                
                String storeMap = "{"
			    					+ "\n JSONArray jArr = new JSONArray();"
			    					+ "\n JSONObject jObj;"
			    					+ "\n Iterator itr = $1.keySet().iterator();"
			    					+ "\n Object[] keyObj = new Object[1];"
			    					+ "\n Object[] value = new Object[1];"
			    					+ "\n while(itr.hasNext()){"
			    					+ "\n 	jObj = new JSONObject();"
			    					+ "\n 	Object key = itr.next();"
			    					+ "\n 	keyObj[0] = key;"
			    					+ "\n 	value[0] = $1.get(key);"
			    					+ "\n 	jObj.put(\"key\", storeParam(keyObj, key.getClass().getSimpleName()));"
			    					+ "\n 	jObj.put(\"value\", storeParam(value, value[0].getClass().getSimpleName()));"
			    					+ "\n 	jArr.add(jObj);"
			    					+ "\n }"
			    					+ "\n return jArr;"
			    					+ "\n }"
			    					+ "\n ";
                
                /*public static JSONArray storeList(Collection list)*/

                String storeList = "{"
                					+ "\n JSONArray jArr = new JSONArray();"
                					+ "\n Iterator itr = $1.iterator();"
                					+ "\n Object[] param = new Object[1];"
                					+ "\n while(itr.hasNext()){"
                					+ "\n 	param[0] = itr.next();"
                					+ "\n 	jArr.add(storeParam(param, param[0].getClass().getSimpleName()));"
                					+ "\n }"
                					+ "\n return jArr;"
                					+ "\n }"
                					+ "\n ";

                /*public static abstract boolean ifArray(Object obj  ($1) )*/
                
                String ifArrMethod = "{"
								    + "\n 	Class c = $1.getClass();"
								   	+ "\n	return c.getCanonicalName().contains(\"[]\");}";
		
                String storeObjectAbstract = "public static abstract JSONObject storeObject(Object obj);";
                String storeObjectsAbstract = "public static abstract JSONArray storeObjectArray(Object[] obj);";
                String ifArrAbstract = "public static abstract boolean ifArray(Object obj);";
                String storeParamAbstract = "public static abstract JSONObject storeParam(Object[] param, String paramCast);";
                String storeParamsAbstract = "public static abstract JSONArray storeParams(Object[] params, String[] paramCasts, int numParams);";
                String storeListAbstract = "public static abstract JSONArray storeList(Collection list);";
                String storeMapAbstract = "public static abstract JSONArray storeMap(Map map);";

                CtMethod method1 = CtNewMethod.make(ifArrAbstract, clazz);
                CtMethod method2 = CtNewMethod.make(storeObjectAbstract, clazz);
                CtMethod method3 = CtNewMethod.make(storeObjectsAbstract, clazz);
                CtMethod method4 = CtNewMethod.make(storeParamAbstract, clazz);
                CtMethod method5 = CtNewMethod.make(storeParamsAbstract, clazz);
                CtMethod method6 = CtNewMethod.make(storeListAbstract, clazz);
                CtMethod method7 = CtNewMethod.make(storeMapAbstract, clazz);

                clazz.addMethod(CtNewMethod.make(ifPrimitive, clazz));

                clazz.addMethod(method1);
                clazz.addMethod(method2);
                clazz.addMethod(method3);
                clazz.addMethod(method4);
                clazz.addMethod(method5);
                clazz.addMethod(method6);
                clazz.addMethod(method7);

                method1.setBody(ifArrMethod);
                method2.setBody(storeObject);
                method3.setBody(storeObjects);
                method4.setBody(storeParam);
                method5.setBody(storeParams);
                method6.setBody(storeList);
                method7.setBody(storeMap);

                clazz.setModifiers(clazz.getModifiers() & ~Modifier.ABSTRACT);
                clazz.addMethod(CtNewMethod.make(storeParamsOrWriteFile, clazz));
                clazz.addMethod(CtNewMethod.make(storeReturn, clazz));

                //CtMethod mainMethod = clazz.getDeclaredMethod("main");
                int counter = 0;
                for (final CtMethod method: clazz.getDeclaredMethods()) {
                	String nameOfClass = clazz.getName();
                	String nameOfMethod = method.getMethodInfo().getName();	
                	if(!"storeMap".equals(nameOfMethod) && !"storeList".equals(nameOfMethod) && !"storeParamsOrWriteFile".equals(nameOfMethod)  && !"main".equals(nameOfMethod)  && !"storeReturn".equals(nameOfMethod)&& !"storeObject".equals(nameOfMethod) && !"storeParams".equals(nameOfMethod) && !"ifArray".equals(nameOfMethod) && !"storeParam".equals(nameOfMethod) && !"storeObjectArray".equals(nameOfMethod)&& !"ifPrimitive".equals(nameOfMethod)){
                    	String paramFile =  nameOfMethod+ ".txt";
                    	boolean ifReturnsArray = method.getReturnType().isArray();
   	                    String inStatement;
   	                    if(ifReturnsArray){
   	                    	inStatement = "true";
   	                    }
   	                    else{
   	                    	inStatement = "false";
   	                    }

                    	String returnWrapper = "";
             			returnWrapper = method.getReturnType().getName();
             			returnWrapper = returnWrapper.substring(returnWrapper.lastIndexOf(".")+1);
             			
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
                    	String lastWrapper = paramWrappers.size()>0 ? paramWrappers.get(paramWrappers.size()-1) : "";
                		
                		paramCasts.append("\"" + lastWrapper + "\"}");
                		methodSignature.append(lastWrapper + ")");
	                    method.insertBefore("{ String nameOfCurrMethod = new Exception().getStackTrace()[0].getMethodName(); "
	                             		+ "\n Object[] o = $args;"
	                             		+ "\n storeParamsOrWriteFile(\"" + methodSignature.toString() + "\", o, " + paramCasts.toString() + ", " + paramWrappers.size() + ");"
	                             		+ "\n }");
	                    
	                    String insertAfter = "{"
	                    		+ "\n Object[] returnVar = new Object[1];"
                         		+ "\n Object[] o = $args;"
	                    		+ "\n returnVar[0] = ($w)$_;"
	                    		+ "\n storeReturn(\"" + methodSignature.toString() + "\", o, " + paramCasts.toString() + ", " + paramWrappers.size() + ", returnVar, \"" + returnWrapper + "\");"
	                    		+ "\n}";
	                    method.insertAfter(insertAfter);

                	}
                }
                byte[] byteCode = clazz.toBytecode();
                clazz.detach();
                return byteCode;
            } catch (final NotFoundException | CannotCompileException | IOException ex) {
                ex.printStackTrace();
            } 
        }
        
        return null;
    }

}