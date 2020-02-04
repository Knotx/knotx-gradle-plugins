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

open class ChangelogUpdateTask : DefaultTask() {

    @Internal
    open var versionProperty: String = "newVersion"

    @get:Input
    val version: String by lazy {
        if (!project.hasProperty(versionProperty)) {
            throw GradleException("Missing version property `$versionProperty` in project properties!")
        }
        project.property(versionProperty).toString()
    }

    @get:OutputFile
    val changelogFile: File by lazy {
        val file = project.file(CHANGELOG_FILE_NAME)
        if (!file.exists()) {
            throw GradleException("Missing changelog file at `${project.projectDir}/$CHANGELOG_FILE_NAME`!")
        }
        file
    }

    @TaskAction
    fun execute() {
        val updatedChangelog = releaseChangelog(changelogFile, version)
        check(changelogFile.delete() && updatedChangelog.renameTo(changelogFile)) { "failed to replace file" }
    }

    companion object {
        const val CHANGELOG_FILE_NAME = "CHANGELOG.md"
    }
}
