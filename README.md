# How To Use

import Xiaoman Android Repositroy

```
allprojects {
    repositories {
        maven {
            url "http://120.24.219.64:8808/nexus/content/groups/public"
            credentials {
                username 'android'
                password 'android@xiaoman.cn'
            }
            authentication {
                basic(BasicAuthentication)
//                digest(DigestAuthentication)
            }
        }
    }
}

buildscript {
    repositories {
        jcenter()
        maven {
            url "http://120.24.219.64:8808/nexus/content/groups/public"
            credentials {
                username 'android'
                password 'android@xiaoman.cn'
            }
            authentication {
                basic(BasicAuthentication)
//                digest(DigestAuthentication)
            }
        }
    }
    dependencies {
        classpath 'cn.xiaoman.android.router:plugin:0.4-SNAPSHOT'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

## use in library & application

```
apply plugin: 'android.router'
```
