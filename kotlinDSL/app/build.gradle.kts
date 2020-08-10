plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.example.plugin")
}
android {
    compileSdkVersion(Deps.Android.compileSdkVersion)
    buildToolsVersion(Deps.Android.buildToolsVersion)

    defaultConfig {
        applicationId = "com.example.kotlinDSL"
        minSdkVersion(Deps.Android.minSdkVersion)
        targetSdkVersion(Deps.Android.targetSdkVersion)
        versionCode = Deps.Android.versionCode
        versionName = Deps.Android.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(Deps.Libs.appcompt)
    implementation(Deps.Libs.constraintlayout)
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")

}