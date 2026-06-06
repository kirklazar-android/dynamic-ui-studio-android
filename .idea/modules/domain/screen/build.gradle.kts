plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.dynamic_ui_android.domain.screen"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}
