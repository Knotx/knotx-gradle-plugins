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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File
import java.io.FileNotFoundException

internal class UpdateProjectVersionTest {

    @ParameterizedTest(name = "{index} => version=''{0}'', propertyName=''{1}'', expected={2}")
    @CsvSource(
            "version=1.0.0, version, true",
            "version=1.0.0-SNAPSHOT, version, true",
            "version=0.0.0, version, true",
            "version=0.0.1, version, true",
            "version=1.0.0-RC1, version, true",
            "knotx.version=1.0.0, knotx.version, true",
            "anything=0.0.1, anything, true",
            "notmatching=1.0.0, something, false",
            "version=1.0, version, false",
            "1.0.0, version, false",
            "v=1.0.0, version, false"
    )
    internal fun `Version regex test`(version: String, propertyName: String, expected: Boolean) {
        assertEquals(expected, projectVersionRegex(propertyName).matches(version))
    }

    @Test
    internal fun `Version updated when file exists and has proper structure`() {
        // given
        val fileToTest = "gradle.1.0.0.properties".asResourceFile()

        // when
        val actual = updateProjectVersion(fileToTest, "2.0.0")

        // then
        assertTrue(actual.exists())
        val expected = "gradle-expected.properties".fileContentAsString()
        assertEqualsTrimIdent(expected, actual.readText())
    }

    @Test
    internal fun `Custom version updated when file exists and has proper structure`() {
        // given
        val fileToTest = "gradle.1.0.0.custom.properties".asResourceFile()

        // when
        val actual = updateProjectVersion(fileToTest, "2.0.0", "knotx.version")

        // then
        assertTrue(actual.exists())
        val expected = "gradle.custom-expected.properties".fileContentAsString()
        assertEqualsTrimIdent(expected, actual.readText())
    }

    @Test
    internal fun `Other properties not updated when file exists and has proper structure`() {
        // given
        val fileToTest = "gradle.long.properties".asResourceFile()

        // when
        val actual = updateProjectVersion(fileToTest, "2.0.0")

        // then
        assertTrue(actual.exists())
        val expected = "gradle.long-expected.properties".fileContentAsString()
        assertEqualsTrimIdent(expected, actual.readText())
    }

    @Test
    internal fun `GradleException thrown when file with invalid structure`() {
        // given
        val fileToTest = createTempFile()

        // then
        assertThrows(GradleException::class.java) { updateProjectVersion(fileToTest, "2.0.0") }
    }

    @Test
    internal fun `FileNotFoundException thrown when properties file does not exist`() {
        // given
        val fileToTest = File("not-existing-file")

        // then
        assertThrows(FileNotFoundException::class.java) { updateProjectVersion(fileToTest, "2.0.0") }
    }

    private fun assertEqualsIgnoreEndlines(expected: String, actual: String) {
        assertEquals(expected.trimIndent(), actual.trimIndent())
    }

    private fun String.asResourceFile() =
            File(UpdateProjectVersionTest::javaClass.javaClass.classLoader.getResource("updateProjectVersion/$this")!!.file)

    private fun String.fileContentAsString() =
            UpdateProjectVersionTest::javaClass.javaClass.classLoader.getResource("updateProjectVersion/$this")!!.readText()

}