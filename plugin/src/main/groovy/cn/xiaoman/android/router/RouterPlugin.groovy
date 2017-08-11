package cn.xiaoman.android.router

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
/**
 * Created by zhangqijun on 2017/5/16.
 */

public class RouterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        def hasApp = project.plugins.withType(AppPlugin)
        def hasLib = project.plugins.withType(LibraryPlugin)
        def hasApt = project.plugins.hasPlugin('com.neenbedankt.android-apt')
        def hasKotlin = project.plugins.hasPlugin('kotlin-android')
        def hasKapt = project.plugins.hasPlugin('kotlin-kapt')


        if (hasApp || hasLib) {

            if (hasKotlin && !hasKapt) {
                project.pluginManager.apply('kotlin-kapt')
            }

            project.dependencies {
                compile 'cn.xiaoman.android.router:router:0.5-SNAPSHOT'

                if (hasKotlin) {
                    kapt 'cn.xiaoman.android.router:compiler:0.5-SNAPSHOT'
                } else if (hasApt) {
                    apt 'cn.xiaoman.android.router:compiler:0.5-SNAPSHOT'
                } else {
                    annotationProcessor 'cn.xiaoman.android.router:compiler:0.5-SNAPSHOT'
                }

            }

//            if (hasApp) {
//                //拷贝生成的 assets/目录到打包目录
//                project.afterEvaluate(new Action<Project>() {
//                    @Override
//                    void execute(Project project1) {
//                        project.android.applicationVariants { variant ->
//                            def variantName = variant.name
//                            def variantNameCapitalized = variantName.capitalize()
//                            def copyMetaInf = tasks.create "copyMetaInf$variantNameCapitalized", Copy
//                            copyMetaInf.from project.fileTree(javaCompile.destinationDir)
//                            copyMetaInf.include "assets/**"
//                            copyMetaInf.into "build/intermediates/sourceFolderJavaResources/$variantName"
//                            tasks.findByName("transformResourcesWithMergeJavaResFor$variantNameCapitalized").dependsOn copyMetaInf
//                        }
//                        project.android {
//                            lintOptions {
//                                abortOnError false
//                            }
//                        }
//                    }
//                })
//
//            }

        }
        project.configurations.all {
            resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
        }
    }

}