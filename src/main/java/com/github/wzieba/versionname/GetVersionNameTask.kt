package com.github.wzieba.versionname

import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.DefaultTask
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GetVersionNameTask : DefaultTask() {

    @Input
    lateinit var variant: ApplicationVariant

    init {
        group = "versioning"
    }

    companion object {

        const val TASK_NAME = "versionName"

        fun Project.registerGetVariantAwareVersionNameTask(applicationVariants: DomainObjectSet<ApplicationVariant>) {
            applicationVariants.all { variant ->
                tasks.register(
                    TASK_NAME + variant.name.capitalize(),
                    GetVersionNameTask::class.java
                ) { task ->
                    task.variant = variant
                    task.description = "Get version name for given variant ${variant.name}"
                }
            }
        }
    }

    @TaskAction
    fun doTaskAction() {
        println(variant.versionName)
    }
}
