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
        jcenter()
        mavenCentral()
        maven { url = uri("https://jitpack.io")  }
        maven { url = uri("https://artifact.bytedance.com/repository/pangle")  }
    }
}

rootProject.name = "Heart Zipper Screen Lock"
include(":app")
 