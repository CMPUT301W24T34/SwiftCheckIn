plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")

}

android {
    namespace = "com.example.swiftcheckin"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.swiftcheckin"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation ("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.android.gms:play-services-tasks:18.1.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.mlkit:barcode-scanning-common:17.0.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.3.0-alpha03")
    // CameraX core library
    implementation ("androidx.camera:camera-core:1.3.0-alpha06")
    implementation ("androidx.camera:camera-camera2:1.3.0-alpha06")
    implementation ("androidx.camera:camera-lifecycle:1.3.0-alpha06")
    implementation ("androidx.camera:camera-view:1.3.0-alpha06")

    implementation ("com.google.mlkit:barcode-scanning:17.0.3")
    implementation ("com.google.android.gms:play-services-mlkit-barcode-scanning:18.3.0")
    implementation ("com.google.firebase:firebase-messaging:23.0.0")
    implementation("com.google.zxing:core:3.4.1")
    implementation ("com.android.volley:volley:1.2.1")
    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}