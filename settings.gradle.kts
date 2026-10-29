pluginManagement {
    repositories {
        google()
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
}

rootProject.name = "Kanzan Touch Recorder"

include(":app")

// Core modules
include(":core:designsystem")
include(":core:ui")
include(":core:domain")
include(":core:data")
include(":core:service")
include(":core:ads")

// Feature modules
include(":feature:macro")
include(":feature:autoclicker")
include(":feature:settings")
include(":feature:onboarding")
