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
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            plugin("android-application", "com.android.application").version("8.2.0")
            plugin("kotlin-android", "org.jetbrains.kotlin.android").version("1.9.0")
            plugin("kotlin-compose", "org.jetbrains.compose").version("1.5.1")
            
            library("androidx-core-ktx", "androidx.core", "core-ktx").version("1.10.1")
            library("androidx-lifecycle-runtime-ktx", "androidx.lifecycle", "lifecycle-runtime-ktx").version("2.6.1")
            library("androidx-activity-compose", "androidx.activity", "activity-compose").version("1.7.2")
            library("androidx-compose-bom", "androidx.compose", "compose-bom").version("2023.08.00")
            library("androidx-ui", "androidx.compose.ui", "ui").withoutVersion()
            library("androidx-ui-graphics", "androidx.compose.ui", "ui-graphics").withoutVersion()
            library("androidx-ui-tooling", "androidx.compose.ui", "ui-tooling").withoutVersion()
            library("androidx-ui-tooling-preview", "androidx.compose.ui", "ui-tooling-preview").withoutVersion()
            library("androidx-ui-test-manifest", "androidx.compose.ui", "ui-test-manifest").withoutVersion()
            library("androidx-ui-test-junit4", "androidx.compose.ui", "ui-test-junit4").withoutVersion()
            library("androidx-material3", "androidx.compose.material3", "material3").version("1.1.1")
            library("androidx-navigation-compose", "androidx.navigation", "navigation-compose").version("2.7.4")
            library("junit", "junit", "junit").version("4.13.2")
            library("androidx-junit", "androidx.test.ext", "junit").version("1.1.5")
            library("androidx-espresso-core", "androidx.test.espresso", "espresso-core").version("3.5.1")
        }
    }
}

rootProject.name = "LibraryApp"
include(":app")
