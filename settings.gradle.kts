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
        maven { url = uri("https://jitpack.io")  }
//        maven { url = uri("https://artifact.bytedance.com/repository/pangle")  }
//        maven {
//            name = "IronSourceAdsRepo"
//            url = uri("https://android-sdk.is.com/")
//            content { includeGroup("com.ironsource.sdk") }
//        }
//        maven {
//            name = "MintegralAdsRepo"
//             url = uri("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
//            content { includeGroup("com.mbridge.msdk.oversea") }
//        }
//        maven {
//            name = "PangleAdsRepo"
//             url = uri("https://artifact.bytedance.com/repository/pangle")
//            content { includeGroup("com.pangle.global") }
//        }
//        maven {
//            name = "SuperAwesomeAdsRepo"
//             url = uri("https://aa-sdk.s3-eu-west-1.amazonaws.com/android_repo")
//            content { includeGroup("tv.superawesome.sdk.publisher") }
//        }
//        maven {
//            name = "ChartboostAdsRepo"
//             url = uri("https://cboost.jfrog.io/artifactory/chartboost-ads/")
//            content { includeGroup("com.chartboost") }
//        }
//        maven {
//            name = "SmaatoAdsRepo"
//             url = uri("https://s3.amazonaws.com/smaato-sdk-releases/")
//            content { includeGroup("com.smaato.android.sdk") }
//        }
//        maven {
//            name = "MadexAdsRepo"
//             url = uri("https://sdkpkg.sspnet.tech")
//            content {
//                includeGroup("sspnet.tech")
//                includeGroup("sspnet.tech.adapters")
//            }
//        }
    }
}

rootProject.name = "Heart Zipper Screen Lock"
include(":app")
 