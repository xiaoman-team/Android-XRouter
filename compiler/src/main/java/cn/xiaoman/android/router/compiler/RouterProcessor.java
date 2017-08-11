package cn.xiaoman.android.router.compiler;


import com.google.auto.service.AutoService;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import cn.xiaoman.android.router.annotation.RouterMap;

@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {
    private Types types;
    private Messager messager;
    private Filer filer;

    public static final String ASSET_PATH = "assets/router/";
    public static final String FILE_SUFFIX = ".json";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
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

            if (annotations == null || annotations.isEmpty()) {
                System.out.println(">>> annotations is null... <<<");
                return true;
            }

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
                        map.put(key, clazzName);
                    }

                }
            }
            //生成Java代码
            createJava(map);

        } catch (FilerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            error(e.getMessage());
        }

        return true;
    }

    /**
     * javapoet 🔚介绍
     * <p>
     * http://www.jianshu.com/p/95f12f72f69a
     * http://www.jianshu.com/p/76e9e3a8ec0f
     * http://blog.csdn.net/crazy1235/article/details/51876192
     * http://blog.csdn.net/qq_26376637/article/details/52374063
     *
     * @param map
     * @throws Exception
     */
    private void createJava(HashMap<String, String> map) throws Exception {
        String content = new Gson().toJson(map);
        //打印出内容
        System.out.println(">>> content:... <<<   " + content);
        writeFile(content);
    }

    /**
     * 生成JSON文件保存到Assets里面
     *
     * @param content
     * @throws Exception
     */
    private void writeFile(String content) throws Exception {
        FileObject fileObject = createResource();
//        FileObject fileObject = createSourcePath();

        Writer writer = fileObject.openWriter();
        writer.write(content);
        writer.close();
//        System.out.println("Done");
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


    private void error(String error) {
        messager.printMessage(Diagnostic.Kind.ERROR, error);
    }


}
