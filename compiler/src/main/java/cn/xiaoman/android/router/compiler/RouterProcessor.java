package cn.xiaoman.android.router.compiler;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import cn.xiaoman.android.router.annotation.RouterMap;

public class RouterProcessor extends AbstractProcessor {
    private Types types;
    private Filer filer;

    private HashMap<String, String> providers = new HashMap<>();

    public static final String ASSET_PATH = "assets/router";
    public static final String FILE_SUFFIX = ".json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
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

        try {
            return processImpl(annotations, roundEnv);
        } catch (Exception e) {
            // We don't allow exceptions of any kind to propagate to the compiler
            StringWriter writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            fatalError(writer.toString());
            return true;
        }
    }

    private boolean processImpl(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            generateConfigFiles();
        } else {
            processAnnotations(annotations, roundEnv);
        }

        return true;
    }

    private void processAnnotations(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations == null || annotations.isEmpty()) {
            System.out.println(">>> annotations is null... <<<");
        } else {
            JSONObject jsonObject = new JSONObject();
            HashMap<String, String> map = new HashMap<>();
            for (TypeElement annotation : annotations) {
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    RouterMap uri = element.getAnnotation(RouterMap.class);
                    TypeElement typeElement = (TypeElement) element;

                    String[] value = uri.value();
                    String clazzName = typeElement.getQualifiedName().toString();
                    for (String key : value) {
                        //避免Key是空的情况
                        if (key.length() == 0) {
                            break;
                        }
                        jsonObject.put(key, clazzName);
                    }

                }
            }
            String content = jsonObject.toString();
            //打印出内容
            System.out.println(">>> content:... <<<   " + content);
            providers.put(types.hashCode() + FILE_SUFFIX, content);

        }
    }

    private void generateConfigFiles() {
        for (String providerInterface : providers.keySet()) {
            String resourceFile = ASSET_PATH + File.separator + providerInterface;
            log("Working on resource file: " + resourceFile);
            try {
                FileObject existingFile = filer
                        .createResource(StandardLocation.CLASS_OUTPUT, "", resourceFile);

                Writer writer = existingFile.openWriter();
                writer.write(providers.get(providerInterface));
                writer.close();
                log("Looking for existing resource file at " + existingFile.toUri());
            } catch (IOException e) {
                fatalError("Unable to create " + resourceFile + ", " + e);
                return;
            }
        }
    }

    /**
     * 获取Resource地址
     *
     * @return
     * @throws IOException
     */
    private FileObject createResource() throws IOException {
        String string = types.toString();
        //使用HashCode作为文件名字，避免冲突
        int hashCode = types.hashCode();
//        System.out.println("typename:  " + string + "   hashCode: " + hashCode);

        String path = ASSET_PATH + hashCode + FILE_SUFFIX;
//        String path = ASSET_JSON;
//        String path =METADATA_PATH;
        FileObject resource = filer
                .createResource(StandardLocation.CLASS_OUTPUT, "", path);
        return resource;
    }

    private void fatalError(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "FATAL ERROR: " + msg);
    }

    private void log(String msg) {
        if (processingEnv.getOptions().containsKey("debug")) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, msg);
        }
    }


}
