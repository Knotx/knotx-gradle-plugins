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

import org.gradle.api.GradleException
import java.io.File
import java.nio.file.Files

const val UNRELEASED_NOTICE = "## Unreleased"
val UNRELEASED_NOTICE_REGEX = Regex(UNRELEASED_NOTICE)

private enum class ChangelogUpdateStatus {
    NOT_FOUND, UPDATED, FINISHED
}

/**
 * Scans `changelogFile` and updates the version
 */
fun releaseChangelog(changelogFile: File, version: String): File {
    val tempFilePath = Files.createTempFile("tmp", "release")
    val tempFile = File(tempFilePath.toUri())
    var versionUpdated = ChangelogUpdateStatus.NOT_FOUND

    tempFile.printWriter().use { writer ->
        changelogFile.forEachLine { line ->
            writer.println(when {
                UNRELEASED_NOTICE_REGEX.matches(line) -> {
                    versionUpdated = ChangelogUpdateStatus.UPDATED
                    """
                        $UNRELEASED_NOTICE
                        List of changes that are finished but not yet released in any final version.
                                        
                        """.trimIndent()
                }
                versionUpdated == ChangelogUpdateStatus.UPDATED -> {
                    versionUpdated = ChangelogUpdateStatus.FINISHED
                    "## $version"
                }
                else -> line
            })
        }
    }

    if (versionUpdated != ChangelogUpdateStatus.FINISHED) {
        throw GradleException("Failed to update CHANGELOG file: ${changelogFile.path}!")
    }
    return tempFile
}

fun updateProjectVersion(propertiesFile: File, version: String, versionPropertyName: String = "version"): File {
    val tempFilePath = Files.createTempFile("tmp", "release")
    val tempFile = File(tempFilePath.toUri())
    val versionRegex = projectVersionRegex(versionPropertyName)

    var updated = false
    tempFile.printWriter().use { writer ->
        propertiesFile.forEachLine { line ->
            writer.println(when {
                versionRegex.matches(line) -> {
                    updated = true
                    "$versionPropertyName=${version}"
                }
                else -> line
            })
        }
    }

    if (!updated) {
        throw GradleException("Failed to update gradle.properties file: ${propertiesFile.path}!")
    }
    return tempFile
}

fun projectVersionRegex(versionPropertyName: String) =
        Regex("""$versionPropertyName=\d+.\d+.\d+(\-\w+){0,1}""")