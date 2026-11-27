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

rootProject.name = "Spindel"
include(":app")
include(":core:navigation")
include(":feature:brewing")
include(":feature:devices")
include(":core:metrics:api")
include(":core:metrics:impl")
include(":core:network:api")
include(":core:network:impl")
include(":core:network:fake")
include(":core:brews:api")
include(":core:brews:impl")
include(":core:devices:api")
include(":core:devices:impl")
include(":core:database:api")
include(":core:database:fake")
include(":core:designsystem")
include(":feature:rawdata")
include(":core:database:impl")
