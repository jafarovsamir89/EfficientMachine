buildscript {
    dependencies {
        // AGP 9 has built-in Kotlin. Pin a newer KGP because the Compose compiler plugin
        // must match the Kotlin compiler version used by this project.
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.21")
    }
}

plugins {
    id("com.android.application") version "9.3.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.3.21" apply false
}
