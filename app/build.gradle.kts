plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.6.10"

}

android {
    namespace = "com.example.pensioniim"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pensioniim"
        minSdk = 24
        targetSdk = 34 // Update targetSdkVersion to 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("boolean", "SHOW_DEBUG_UI", "false")
        multiDexEnabled=true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"

    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.2"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"

        }
    }
    configurations.all {
        resolutionStrategy {
            force ("com.intellij:annotations:12.0")
            force ("org.jetbrains:annotations:23.0.0")
            force ("org.tensorflow:tensorflow-lite:2.7.0")
        }
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    annotationProcessor ("androidx.room:room-compiler:2.6.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.amplifyframework:core:2.14.11")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")


    implementation("com.amplifyframework:aws-auth-cognito:2.14.5")
    //implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha10")
    implementation("com.amplifyframework:core-kotlin:2.14.11")
    implementation ("com.amplifyframework:aws-api:2.14.9")
    implementation("com.amplifyframework.ui:liveness:1.2.1") {
        exclude(group="org.jetbrains", module= "annotations")}
//    }
//    implementation("com.intellij:annotations:12.0") {
//        exclude(group = "org.jetbrains", module = "annotations")
//    }
//
//    implementation("com.intellij:annotations:12.0") {
//        exclude(group = "org.intellij.lang", module = "annotations.Identifier")
//        exclude(group = "org.intellij.lang", module = "annotations.JdkConstants")
//        exclude(group = "org.intellij.lang", module = "annotations.Language")
//        exclude(group = "org.intellij.lang", module = "annotations.MagicConstant")
//        exclude(group = "org.intellij.lang", module = "annotations.Pattern")
//        exclude(group = "org.intellij.lang", module = "annotations.PrintFormat")
//    }


//
//    implementation("org.tensorflow:tensorflow-lite-api:2.13.0") {
//        exclude(group = "org.tensorflow", module = "tensorflow-lite")
//    }
   // implementation ("com.android.support:multidex:2.0.1")

    // Add any other dependencies you have here
}