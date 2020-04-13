package com.github.wzieba.versionname

import com.android.build.gradle.AppExtension
import com.github.wzieba.versionname.GetVersionNameTask.Companion.registerGetVariantAwareVersionNameTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class VariantAwareVersionNamePlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {

        pluginManager.withPlugin("com.android.application") {appliedPlugin ->
            afterEvaluate { project ->
                println("has android app plugin?: " + project.pluginManager.hasPlugin("com.android.application"))
                println("has extension?: " + extensions.findByType(AppExtension::class.java))
                val appExtension = extensions.getByType(AppExtension::class.java)
                registerGetVariantAwareVersionNameTask(
                    appExtension.applicationVariants
                )
            }
        }


    }
}
