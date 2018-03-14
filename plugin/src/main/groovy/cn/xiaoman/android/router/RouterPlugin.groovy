package cn.xiaoman.android.router

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy

/**
 * Created by zhangqijun on 2017/5/16.
 */

public class RouterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        def hasApp = project.plugins.hasPlugin(AppPlugin)
        def hasLib = project.plugins.hasPlugin(LibraryPlugin)
        def hasApt = project.plugins.hasPlugin('com.neenbedankt.android-apt')
        def hasKotlin = project.plugins.hasPlugin('kotlin-android')
        def hasKapt = project.plugins.hasPlugin('kotlin-kapt')


        if (hasApp || hasLib) {

            if (hasKotlin && !hasKapt) {
                project.pluginManager.apply('kotlin-kapt')
            }

            project.dependencies {
                compile 'cn.xiaoman.android.router:router:0.13-SNAPSHOT'

                if (hasKotlin) {
                    kapt 'cn.xiaoman.android.router:compiler:0.13-SNAPSHOT'
                } else if (hasApt) {
                    apt 'cn.xiaoman.android.router:compiler:0.13-SNAPSHOT'
                } else {
                    annotationProcessor 'cn.xiaoman.android.router:compiler:0.13-SNAPSHOT'
                }

            }
            if (hasApp) {
                project.afterEvaluate(new Action<Project>() {
                    @Override
                    void execute(Project project1) {
                        project.android.applicationVariants.each { variant ->
                            def variantNameCapitalized = variant.name.capitalize()
                            def copyRouterInf = project.tasks.create("copyRouterInf$variantNameCapitalized", Copy) {
                                from project.fileTree(variant.javaCompile.destinationDir)
                                include "assets/**"

                                StringBuilder pathResult = new StringBuilder()

                                if (variant.productFlavors.size() > 0) {
                                    StringBuilder stringBuilder = new StringBuilder();
                                    variant.productFlavors.name.each {
                                        stringBuilder.append(it.capitalize())
                                    }
                                    if (stringBuilder.length() > 0) {
                                        pathResult.append(stringBuilder.uncapitalize())
                                    }
                                }
                                if (pathResult.length() > 0) {
                                    pathResult.append(File.separator)
                                }
                                pathResult.append(variant.buildType.name)

                                into "build/intermediates/sourceFolderJavaResources/$pathResult"

                            }
                            copyRouterInf.dependsOn(project.tasks.findByName("compile${variantNameCapitalized}JavaWithJavac"))
                            project.tasks.findByName("transformResourcesWithMergeJavaResFor$variantNameCapitalized").dependsOn(copyRouterInf)
                        }
                    }
                })
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