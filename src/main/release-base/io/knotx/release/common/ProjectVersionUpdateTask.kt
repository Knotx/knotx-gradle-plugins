/*
 * Copyright (C) 2019 Knot.x Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.knotx.release.common

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

open class ProjectVersionUpdateTask : DefaultTask() {

    @Internal
    open var versionParamProperty: String = VERSION_PROJECT_PROPERTY

    @Input
    open var propertyKeyNameInFile: String = VERSION_PROJECT_PROPERTY

    @get:Input
    val version: String by lazy {
        if (!project.hasProperty(versionParamProperty)) {
            throw GradleException("Missing project property `$versionParamProperty`!")
        }
        project.property(versionParamProperty).toString()
    }

    @get:OutputFile
    val propertiesFile: File by lazy {
        val file = project.file(PROPERTIES_FILE_NAME)
        if (!file.exists()) {
            throw GradleException("Missing properties file at `${project.projectDir}/$PROPERTIES_FILE_NAME`!")
        }
        file
    }

    @TaskAction
    fun execute() {
        logger.lifecycle("Executing UpdateVersionTask")
        val updatedProperties = updateProjectVersion(propertiesFile, version, propertyKeyNameInFile)

        check(propertiesFile.delete() && updatedProperties.renameTo(propertiesFile)) { "Failed to update properties!" }
        project.setProperty(versionParamProperty, version)
    }

    companion object {
        const val PROPERTIES_FILE_NAME = "gradle.properties"
        const val VERSION_PROJECT_PROPERTY = "version"
    }
}