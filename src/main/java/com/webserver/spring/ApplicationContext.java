package com.webserver.spring;

import com.webserver.spring.AOP.BeanPostProcessor;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther wy
 * @create 2022/4/4 10:37
 */
public class ApplicationContext {
    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitiontHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();


    public ApplicationContext(Class configClass_){
        configClass = configClass_;

        //扫描配置文件
        if(configClass.isAnnotationPresent(CompoentScan.class)){
            CompoentScan compoentScanAnnotation = (CompoentScan) configClass.getDeclaredAnnotation(CompoentScan.class);
            String path = compoentScanAnnotation.value().replace('.','/');

            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File(resource.getFile());


            if(file.isDirectory()){
                File[] files = file.listFiles();

                for(File f : files){
                    String fileName = f.getAbsolutePath();
//                    System.out.println(fileName);

                    if (fileName.endsWith(".class")){
                        String className = fileName.substring(fileName.indexOf("com"),fileName.indexOf(".class")).replace("\\",".");
//                        System.out.println(className);
                        try {
                            Class<?> clazz = classLoader.loadClass(className);

                            // 这里会再有一个筛选条件，一般是根据类文件的元数据筛选
                            // 比如是不是具体类，是不是顶层类，是不是抽象类等
                            // 默认情况下只添加顶层的具体类，顶层的意思是可以独立实例化而不会依赖外部类
                            // 成员内部类需要外部类对象才能实例化，就不会通过

                            if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                                BeanPostProcessor instance =(BeanPostProcessor) clazz.newInstance();
                                beanPostProcessorList.add(instance);
                            }

                            if(!clazz.isAnnotationPresent(Compoent.class)){
                                System.out.println(className);
                                continue;
                            }

                            String beanName;
                            if (clazz.isAnnotationPresent(Compoent.class)){


                                //Beanname
                                Compoent compoent = clazz.getAnnotation(Compoent.class);
                                beanName = compoent.value();
                                if (beanName.equals("")){
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }
                            }else {
                                beanName = Introspector.decapitalize(clazz.getSimpleName());
                            }
                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setType(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)){
                                Scope scopeAnnotation = clazz.getAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            }else {
                                beanDefinition.setScope("singleton");
                            }


                            beanDefinitiontHashMap.put(beanName, beanDefinition);

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    
                }
            }

            //实例化单例Bean
            for(String beanName : beanDefinitiontHashMap.keySet()){
                BeanDefinition beanDefinition = beanDefinitiontHashMap.get(beanName);
                String scope = beanDefinition.getScope();
                if(scope.equals("singleton")){
                    Object bean = singletonObjects.get(beanName);
                    if (bean==null){
                        bean = createBean(beanName, beanDefinition);
                        singletonObjects.put(beanName, bean);
                    }
                }

            }

        }

    }
    public Object getBean(String beanName){
        BeanDefinition beanDefinition = beanDefinitiontHashMap.get(beanName);
        Object bean;
        if (beanDefinition==null){
            throw new NullPointerException();
        } else {
            String scope = beanDefinition.getScope();
            if (scope.equals("singleton")){
                bean = singletonObjects.get(beanName);
                if (bean==null){
                    Object o = createBean(beanName, beanDefinition);
                    singletonObjects.put(beanName, o);
                }
            }else{
               bean = createBean(beanName, beanDefinition);
            }
        }

        return bean;
    }
    private Object createBean(String beanName, BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getType();

        try {

            Object instance = clazz.getConstructor().newInstance();

            //依赖注入
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field f : clazz.getDeclaredFields()){
                if(f.isAnnotationPresent(Autowired.class)){
                    f.setAccessible(true);
                    f.set(instance, getBean(f.getName()));
                }
            }

            //Aware
            if (instance instanceof BeanNameAware){
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            //初始化前置方法
            for (BeanPostProcessor beanPostProcessor: beanPostProcessorList){
                instance = beanPostProcessor.postProcessorBeforeInitializingBean(beanName, instance);
            }

            //初始化
            if (instance instanceof InitializingBean){
                ((InitializingBean) instance).afterPropertiesSet();
            }
            //初始化后置方法
            for (BeanPostProcessor beanPostProcessor: beanPostProcessorList){
                instance = beanPostProcessor.postProcessorAfterInitializingBean(beanName, instance);
            }


            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


        return null;
    }
}
