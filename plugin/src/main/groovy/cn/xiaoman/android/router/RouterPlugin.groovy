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
        def usesAndroidAspectJxPlugin = project.plugins.hasPlugin('com.archinamon.aspectj') || project.plugins.hasPlugin('android-aspectjx')


        if (hasApp || hasLib) {

            if (hasApp && !usesAndroidAspectJxPlugin) {
//                project.pluginManager.apply(com.hujiang.gradle.plugin.android.aspectjx.AndroidAspectJXPlugin);
                project.pluginManager.apply('android-aspectjx')

//                project.buildscript.repositories.maven { url "https://jitpack.io" }
//                project.pluginManager.apply('com.archinamon.aspectj')
            }
            if (hasKotlin && !hasKapt) {
                project.pluginManager.apply('kotlin-kapt')
            }

            project.dependencies {
                compile 'org.aspectj:aspectjrt:1.8.9'
                compile 'cn.xiaoman.android.router:router:0.4-SNAPSHOT'

                if (hasKotlin) {
                    kapt 'cn.xiaoman.android.router:compiler:0.4-SNAPSHOT'
                } else if (hasApt) {
                    apt 'cn.xiaoman.android.router:compiler:0.4-SNAPSHOT'
                } else {
                    annotationProcessor 'cn.xiaoman.android.router:compiler:0.4-SNAPSHOT'
                }

            }

            if (hasKapt) {
                project.kapt {
                    arguments {
                        arg("moduleName", project.getName().replace("-", "_"))
                    }
                }
            } else {
                project.android.defaultConfig.javaCompileOptions {
                    annotationProcessorOptions {
                        arguments = [moduleName: project.getName().replace("-", "_")]
                    }
                }
            }

        }
        project.configurations.all {
            resolutionStrategy.cacheChangingModulesFor 0, 'seconds'
        }
    }

}