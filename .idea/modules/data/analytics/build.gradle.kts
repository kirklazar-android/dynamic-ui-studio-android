plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.dynamic_ui_android.data.analytics"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
    }
}
