

import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.io.FileInputStream
import java.util.Properties


// Load properties from local.properties file
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
val openAiApiKey = localProperties.getProperty("OPENAI_API_KEY")


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildkonfig)
}

// in your-shared-module/build.gradle.kts

// in your-shared-module/build.gradle.kts

buildkonfig {
    packageName = "org.example.dailyquotesaver"

    defaultConfigs {
        buildConfigField(
            // This is the fix. We use FieldSpec.Type.STRING
            // instead of the raw string "String".
            type = FieldSpec.Type.STRING,
            name = "OPENAI_API_KEY",
            value = localProperties.getProperty("OPENAI_API_KEY", "")
        )
    }
}

kotlin {



    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop") { // <<< This is the desktop target
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21) // <<< Set this for desktop compilation!
        }
    }





    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.ktor.client.okhttp)


        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)



            api(libs.kotlinx.serialization.json)

            //implementation(libs.compose.materialIconsExtended)
            implementation(compose.materialIconsExtended)

            implementation("androidx.datastore:datastore:1.1.7")
            implementation("androidx.datastore:datastore-preferences:1.1.7")

            implementation(libs.ktor.client.logging)

            implementation(libs.bundles.ktor.common)


        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        nativeMain.dependencies {
            implementation(libs.ktor.client.darwin)

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)

            implementation(libs.ktor.client.cio)

        }
    }


}

android {
    namespace = "org.example.dailyquotesaver"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.example.dailyquotesaver"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

// ... (rest of your build.gradle.kts)

compose.desktop {
    application {
        mainClass = "org.example.dailyquotesaver.MainKt"
        javaHome = "C:/Program Files/Microsoft/jdk-21.0.8.9-hotspot" // Keep this line
        //jvmTarget.set(JvmTarget.JVM_21) // Keep this line for compilation

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb, TargetFormat.Exe)
            packageName = "org.example.dailyquotesaver"
            packageVersion = "1.0.0"
            // *** ADD THIS LINE ***
            modules("jdk.unsupported") // Include the module that contains sun.misc.Unsafe
            // *** END OF ADDED LINE ***
        }
    }
}


