import com.sun.org.apache.xpath.internal.operations.Mod;
import reflection.api.Investigator;

import javax.naming.Name;
import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class MyInvestigator implements Investigator{

    Class clazz;
    Object instance;
    @Override
    public void load(Object anInstanceOfSomething) {
        clazz = anInstanceOfSomething.getClass();
        instance = anInstanceOfSomething;
    }

    @Override 
    public int getTotalNumberOfMethods() {
            Method methods[] = clazz.getDeclaredMethods();
            return methods.length;
    }

    @Override
    public int getTotalNumberOfConstructors() {
        Constructor[] constructors = clazz.getDeclaredConstructors();
        return constructors.length;
    }

    @Override 
    public int getTotalNumberOfFields() {
        Field[] fields = clazz.getDeclaredFields();
        return fields.length;
    }

    @Override
    public Set<String> getAllImplementedInterfaces() {
        Class[] interfaces = clazz.getInterfaces();
        Set<String> interfacesSet= new HashSet<>();
        for (Class interfaceObject: interfaces)
        {
            interfacesSet.add(interfaceObject.getSimpleName());
        }
        return interfacesSet;
    }

    @Override
    public int getCountOfConstantFields() {
        int counter=0;
        Field[] fields = clazz.getDeclaredFields();
        for(Field fieldObject: fields)
        {
            if(Modifier.isFinal(fieldObject.getModifiers()))
                counter++;
        }
        return counter;
    }

    @Override 
    public int getCountOfStaticMethods() {
        int counter = 0;
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method: methods) {
            if(Modifier.isStatic(method.getModifiers()))
                counter++;
        }
        return counter;
    }

    @Override
    public boolean isExtending() {
        return (clazz.getSuperclass().getSimpleName() != Object.class.getSimpleName() &&
                clazz.getSuperclass().getSimpleName() != clazz.getSimpleName());
    }

    @Override 
    public String getParentClassSimpleName() {
        return clazz.getSuperclass().getSimpleName();
    }

    @Override 
    public boolean isParentClassAbstract() {
        return Modifier.isAbstract(clazz.getSuperclass().getModifiers());
    }

    @Override
    public Set<String> getNamesOfAllFieldsIncludingInheritanceChain() {
        Class tempClass= clazz;
        Set<String> Names = new HashSet<>();
        while(tempClass != Object.class) {
            Field fields[] = tempClass.getDeclaredFields();
            for (Field field : fields) {
                Names.add(field.getName());
            }
            tempClass = tempClass.getSuperclass();
        }
        return Names;
    }

    @Override
    public int invokeMethodThatReturnsInt(String methodName, Object... args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method method = clazz.getMethod(methodName);
        return (int)method.invoke(instance,args);
    }

    @Override 
    public Object createInstance(int numberOfArgs, Object... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor constructors [] = clazz.getConstructors();
        for(Constructor ctor: constructors)
            if (ctor.getParameterCount() == numberOfArgs) {
                Object instance = ctor.newInstance(args);
                return instance;
            }
        return null;
    }

    @Override
    public Object elevateMethodAndInvoke(String name, Class<?>[] parametersTypes, Object... args) throws NoSuchMethodException {
        Method method = clazz.getDeclaredMethod(name, parametersTypes);
        if (!Modifier.isPublic(method.getModifiers()))
            method.setAccessible(true);
        try {
            return method.invoke(instance,args);
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            return "fuck";
        }
    }
    @Override
    public String getInheritanceChain(String delimiter) {
        String finalChain="";
        Class tempClass = clazz;
        Stack<String > chain = new Stack<>();
        while(tempClass != Object.class.getSuperclass())
        {
            chain.push(tempClass.getSimpleName());
            chain.push(delimiter);
            tempClass= tempClass.getSuperclass();
        }
        chain.pop();
        while(!chain.isEmpty()) {
            finalChain = finalChain.concat(chain.pop());
        }
        return finalChain;
    }
}
