buildscript {
    val JAVA_HOME by extra("C:\\Program Files\\Java\\jdk-17")
    val agp_version by extra("8.1.0")
    val agp_version1 by extra("8.1.0")
    val agp_version2 by extra("8.1.0")
    val agp_version3 by extra("8.2.0-alpha15")
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0-alpha15" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.android.test") version "8.2.0-alpha15" apply false
    id("androidx.baselineprofile") version "1.2.0-beta01" apply false
}