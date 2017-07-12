package cn.xiaoman.android.router.compiler;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import cn.xiaoman.android.router.annotation.RouterMap;
import cn.xiaoman.android.router.compiler.exception.TargetErrorException;

@SupportedOptions({"moduleName"})
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
    private Messager mMessager;
    private Filer mFiler;
    private Elements elementUtils;
    private Map<String, String> options;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        options = processingEnv.getOptions();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(RouterMap.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(RouterMap.class);

        try {
            TypeSpec type = getRouterTableInitializer(elements);
            if (type != null) {
                JavaFile.builder("cn.xiaoman.android.router.router.router", type)
                        .build().writeTo(mFiler);
            }
        } catch (FilerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            error(e.getMessage());
        }

        return true;
    }

    private TypeSpec getRouterTableInitializer(Set<? extends Element> elements) throws ClassNotFoundException, TargetErrorException {
        if (elements == null || elements.size() == 0) {
            return null;
        }
        TypeElement activityType = elementUtils.getTypeElement("android.app.Activity");

        ParameterizedTypeName mapTypeName = ParameterizedTypeName
                .get(ClassName.get(Map.class), ClassName.get(String.class), ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(ClassName.get(activityType))));
        ParameterSpec mapParameterSpec = ParameterSpec.builder(mapTypeName, "router")
                .build();
        MethodSpec.Builder routerInitBuilder = MethodSpec.methodBuilder("initRouterTable")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(mapParameterSpec);
        for (Element element : elements) {
            if (element.getKind() != ElementKind.CLASS) {
                throw new TargetErrorException();
            }
            RouterMap router = element.getAnnotation(RouterMap.class);
            String[] routerUrls = router.value();
            for (String routerUrl : routerUrls) {
                routerInitBuilder.addStatement("router.put($S, $T.class)", routerUrl, ClassName.get((TypeElement) element));
            }
        }
        MethodSpec routerInitMethod = routerInitBuilder.build();
        TypeElement routerInitializerType = elementUtils.getTypeElement("cn.xiaoman.android.router.router.IActivityRouteTableInitializer");
        String className = "AnnotatedRouterTableInitializer";
        if (options.containsKey("moduleName")) {
            className = className + "_" + options.get("moduleName");
        }


        ClassName namedBoards = ClassName.get("cn.xiaoman.android.router.router", "ActivityRouter");

        ClassName name = ClassName.get("cn.xiaoman.android.router.router.router", className);

        MethodSpec.Builder aopBuild = MethodSpec.methodBuilder("initRouter")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(AnnotationSpec.builder(Around.class)
                        .addMember("value", "\"execution(* cn.xiaoman.android.router.router.ActivityRouter.init(android.content.Context))\"")
                        .build())
                .addParameter(ParameterizedTypeName.get(ProceedingJoinPoint.class), "proceedingJoinPoint")
                .addStatement("$T activityRouter = ($T) proceedingJoinPoint.getTarget()", namedBoards, namedBoards)
                .addStatement("activityRouter.initActivityRouterTable(new $T())", name)
                .addStatement("proceedingJoinPoint.proceed()")
                .addException(Throwable.class);

        return TypeSpec.classBuilder(className)
                .addSuperinterface(ClassName.get(routerInitializerType))
                .addModifiers(Modifier.PUBLIC)
                .addMethod(routerInitMethod)
                .addMethod(aopBuild.build())
                .addAnnotation(Aspect.class)
                .build();
    }


    private void error(String error) {
        mMessager.printMessage(Diagnostic.Kind.ERROR, error);
    }


}
