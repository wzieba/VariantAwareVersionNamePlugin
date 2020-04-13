package com.github.wzieba.versionname

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.api.AndroidBasePlugin
import org.assertj.core.api.Assertions.assertThat
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import org.junit.jupiter.api.io.TempDir

class GetVersionNameTaskTest {

    @TempDir
    lateinit var rootDirectory: File

    lateinit var rootBuildScript: File
    lateinit var moduleAppRoot: File

    @BeforeEach
    fun setUp() {
        rootDirectory.apply {
            resolve("settings.gradle").writeText("""include ":app" """)

            @Language("groovy")
            val config =
                """buildscript {
    repositories {
        google()
        gradlePluginPortal()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.6.1'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71"
    }
}

                    """.trimIndent()

            resolve("build.gradle").writeText(config)

            moduleAppRoot = resolve("app") {
                @Language("groovy")
                val buildScript =
                    """
                    plugins {
                        id('com.android.application')
                        id('kotlin-android')
                        id('com.github.wzieba.versionname')
                    }
                    
                    android {
                        compileSdkVersion 29
                        buildTypes {
                            debug { }
                            release { }
                        }
                        flavorDimensions "version"
                        productFlavors {
                            demo { }
                            full { }
                        }
                    }
                    
                    """.trimIndent()
                resolve("build.gradle") {
                    writeText(buildScript)
                }
                resolve("src/main/AndroidManifest.xml") {
                    writeText(
                        """
                        <manifest package="com.example.module1" />
                        
                        """.trimIndent()
                    )
                }
            }
        }
    }

    @Test
    fun `task passes configuration phase`() {
        runTask("help")
    }

    @Test
    fun `should generate task for every variant`() {
        val tasks = runTask("tasks").tasks

        assertThat(tasks.map { it.outcome.name }).contains("versionNameDebugDemo")
    }

    private fun runTask(vararg taskName: String) =
        GradleRunner.create().apply {
            forwardOutput()
            withPluginClasspath()
            withArguments(*taskName)
            withProjectDir(rootDirectory)
        }.run {
            build()
        }

    protected fun File.resolve(relative: String, receiver: File.() -> Unit): File =
        resolve(relative).apply {
            parentFile.mkdirs()
            receiver()
        }
}