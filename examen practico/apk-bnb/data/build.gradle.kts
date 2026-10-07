plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.bille.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.core)
    implementation(libs.hilt.android)
    implementation(libs.room.runtime)
    annotationProcessor(libs.hilt.compiler)
    annotationProcessor(libs.room.compiler)
    coreLibraryDesugaring(libs.desugar)
    testImplementation(libs.junit)
}
