import com.android.build.gradle.internal.ide.kmp.KotlinAndroidSourceSetMarker.Companion.android

plugins {
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.build.konfig)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.mokkery)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
}

kotlin {

// Target declarations - add or remove as needed below. These define
// which platforms this KMP module supports.
// See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        namespace = "com.example.shared"
        compileSdk = 35
        minSdk = 24

        withHostTestBuilder {
        }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

// For iOS targets, this is also where you should
// configure native binary output. For more information, see:
// https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

// A step-by-step guide on how to include this library in an XCode
// project can be found here:
// https://developer.android.com/kotlin/multiplatform/migrate

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "WeatherCommon"
            isStatic = true
        }
    }

    targets.configureEach {
        compilations.configureEach {
            compileTaskProvider.get().compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
            }
        }
    }

    applyDefaultHierarchyTemplate()
// Source set declarations.
// Declaring a target automatically creates a source set with the same name. By default, the
// Kotlin Gradle Plugin creates additional source sets that depend on each other, since it is
// common to share sources between related targets.
// See: https://kotlinlang.org/docs/multiplatform-hierarchy.html
    sourceSets {
        commonMain {
            dependencies {
                implementation(project.dependencies.platform(libs.androidx.compose.bom))
                implementation(libs.bundles.common.ktor)

                implementation(libs.jetbrains.androidx.navigation.compose)
                implementation(libs.jetbrains.androidx.viewmodel.compose)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.view.model)
                implementation(libs.koin.core)
                implementation(libs.konnection)
                implementation(libs.jetbrains.kotlinx.coroutines.core)
                implementation(libs.jetbrains.kotlinx.serialization.json)
                implementation(libs.ktor.network)
                implementation(libs.ktor.network.tls)
                implementation(libs.ktor.client.auth)
                implementation(libs.multiplatform.settings)
                implementation(libs.multiplatform.settings.no.arg)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
                implementation(project.dependencies.platform(libs.androidx.compose.bom))
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.lifecycle.runtime.ktx)
                implementation(libs.koin.android)
                implementation(libs.ktor.client.okhttp)

                // Location Services
                implementation(libs.play.services.location)
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }
}

buildkonfig {
    packageName = "com.medik.app"
    exposeObjectWithName = "BuildKonfig"
    defaultConfigs { }
}