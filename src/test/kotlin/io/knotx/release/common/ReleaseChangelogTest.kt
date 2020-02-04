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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException

internal class ReleaseChangelogTest {

    @Test
    internal fun `Changelog updated when file exists and has proper structure`() {
        // given
        val changelogFile = "CHANGELOG.md".asResourceFile()
        // when
        val actual = releaseChangelog(changelogFile, "2.0.0")

        // then
        assertTrue(actual.exists())
        val expected = "CHANGELOG-expected.md".fileContentAsString()
        assertEquals(expected, actual.readText())
    }

    @Test
    internal fun `GradleException thrown when file with invalid structure`() {
        // given
        val changelogFile = createTempFile()

        // then
        assertThrows(GradleException::class.java) { releaseChangelog(changelogFile, "2.0.0") }
    }

    @Test
    internal fun `FileNotFoundException thrown when changelog file does not exist`() {
        // given
        val changelogFile = File("not-existing-file")

        // then
        assertThrows(FileNotFoundException::class.java) { releaseChangelog(changelogFile, "2.0.0") }
    }

    private fun String.asResourceFile() =
            File(ReleaseChangelogTest::javaClass.javaClass.classLoader.getResource("releaseChangelog/$this")!!.file)

    private fun String.fileContentAsString() =
            ReleaseChangelogTest::javaClass.javaClass.classLoader.getResource("releaseChangelog/$this")!!.readText()

}