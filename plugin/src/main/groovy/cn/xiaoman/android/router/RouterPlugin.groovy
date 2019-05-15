package cn.xiaoman.android.router

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by zhangqijun on 2017/5/16.
 */

class RouterPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def hasApp = project.plugins.hasPlugin("com.android.application")
        def hasLib = project.plugins.hasPlugin("com.android.library")
        def hasApt = project.plugins.hasPlugin("com.neenbedankt.android-apt")
        def hasKotlin = project.plugins.hasPlugin("kotlin-android")
        def hasKapt = project.plugins.hasPlugin("kotlin-kapt")

        if (hasApp || hasLib) {

            if (hasKotlin && !hasKapt) {
                project.pluginManager.apply("kotlin-kapt")
            }
            def inputStream = getClass().classLoader.getResourceAsStream("version.properties")
            Properties prop = new Properties()
            prop.load(inputStream)
            def version = prop.getProperty("version")

            project.dependencies {
                implementation "cn.xiaoman.android.router:annotation:${version}"
                implementation "cn.xiaoman.android.router:router:${version}"

                if (hasKotlin) {
                    kapt "cn.xiaoman.android.router:compiler:${version}"
                } else if (hasApt) {
                    apt "cn.xiaoman.android.router:compiler:${version}"
                } else {
                    annotationProcessor "cn.xiaoman.android.router:compiler:${version}"
                }

            }
        }
    }

}