package com.github.kei2100.misc;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Test {
	
	public static void main(String[] args) throws Exception {
		List<Class<?>> classes = getClasses(new File("."), "com.github.kei2100");
		System.out.println(classes);
	}
	
    /**
     * ディレクトリに存在する、特定パッケージのClassオブジェクトを、サブパッケージ含め、再帰的に取得
     */
    public static List<Class<?>> getClasses(File dir, String dotSeparatedPackage) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<Class<?>>();
        dotSeparatedPackage = 
            (dotSeparatedPackage.endsWith(".")) ? dotSeparatedPackage : dotSeparatedPackage + ".";

        String slashSeparatedPackage = dotSeparatedPackage.replaceAll("\\.", "/");

        for (File resource : dir.listFiles()) {
            if (resource.isDirectory()) {
                List<Class<?>> subDirClasses = getClasses(resource, dotSeparatedPackage);
                classes.addAll(subDirClasses);

            } else {
                if (isClassFile(resource)) {
                    Class<?> clazz = getClass(resource, slashSeparatedPackage);
                    if (clazz != null) {
                        classes.add(clazz);
                    }
                }
            }
        }
        return new ArrayList<Class<?>>(classes);
    }

    private static boolean isClassFile(File resource) {
        return (resource.isFile()) && 
                    resource.getName().endsWith(".class") ?  true : false; 
    }

    private static Class<?> getClass(File file, String slashSeparatedPackage) throws ClassNotFoundException {
        String fqcn = changeFqcn(file, slashSeparatedPackage);

        if (fqcn != null) {
            return Class.forName(fqcn);
        } else {            
            return null;
        }
    }

    /*
     * Fileが指定パッケージのクラスだったらFQCNに変換
     */
    private static String changeFqcn(File classFile, String slashSeparatedPackage) {
        String uri = classFile.toURI().toString();
        int beginIndex = uri.indexOf(slashSeparatedPackage);
        int endIndex = uri.lastIndexOf(".class");

        if (beginIndex < 0 || endIndex < 0) {
            return null;

        } else {
            String uriSubString = uri.substring(beginIndex, endIndex);
            String fqcn = uriSubString.replaceAll("/", ".");
            return fqcn;
        }
    }
}
