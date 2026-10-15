plugins {
    alias(libs.plugins.android.library)
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "ru.vik.spindel.feature.chart"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

}

dependencies {
    implementation(project(":core:navigation"))

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}