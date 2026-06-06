pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "dynamic_ui_android"
include(":app")
include(":core:common")
include(":core:designsystem")
include(":core:dynamicui")
include(":core:network")
include(":core:database")
include(":core:testing")

include(":domain:screen")

include(":data:screen")
include(":data:analytics")

include(":feature:renderer")
 