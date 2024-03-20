buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://packages.amplify.aws/sdk/android") }
    }
}
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
   // id("amplify.android.ui.component")
    kotlin("plugin.serialization") version "1.5.31"

   // id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.pensioniim"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pensioniim"
        minSdk = 23
        targetSdk = 34 // Update targetSdkVersion to 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("boolean", "SHOW_DEBUG_UI", "false")

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
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/versions/9/previous-compilation-data.bin"

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
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.amplifyframework:core:2.14.10")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    implementation("com.amplifyframework.ui:liveness:1.0.3")
    implementation("com.amplifyframework:aws-auth-cognito:2.14.5")
    implementation("androidx.compose.material3:material3:1.1.2")
    implementation("androidx.navigation:navigation-compose:2.4.0-alpha10")
    implementation("com.amplifyframework:core-kotlin:2.14.11")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
    implementation ("com.google.accompanist:accompanist-permissions:0.28.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
    implementation ("androidx.appcompat:appcompat:1.6.1")

    // Add any other dependencies you have here
}
