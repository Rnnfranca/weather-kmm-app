buildscript {
    val kotlin_version by extra("1.5.0")
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }

    val kotlinVersion = "1.4.0"
    val sqlDelightVersion: String by project

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.32")
        classpath("com.android.tools.build:gradle:4.0.1")
        // serialization
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        // sqldelight
        classpath("com.squareup.sqldelight:gradle-plugin:$sqlDelightVersion")
        // safeargs
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.3.5")
    }
}

group = "me.user"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

