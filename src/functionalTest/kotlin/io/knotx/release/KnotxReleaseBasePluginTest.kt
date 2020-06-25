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
package io.knotx.release

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.File


internal class KnotxReleaseBasePluginTest {

    @Test
    @DisplayName("Expect updated CHANGELOG.md with the current project version")
    fun `updateChangelog with the current project version`() = GradleRunner.create().run {
        // given
        val taskToTest = "updateChangelog"
        given("$taskToTest/project", taskToTest, listOf("-Pversion=3.0.0"))

        // when
        val result = build()

        // then
        val actual = File("$FUNCTIONAL_TESTS_DIR/$taskToTest/CHANGELOG.md")
        assertEquals("SUCCESS", result.task(":$taskToTest")!!.outcome.toString())
        assertNotNull(result)
        assertTrue(actual.exists())
        val expected = "$taskToTest/results/CHANGELOG.md".fileContentAsString()
        assertEqualsTrimIdent(expected, actual.readText())
    }

    @Test
    @DisplayName("Expect updated gradle.properties with the given project version")
    fun `updateVersion with the current project version`() = GradleRunner.create().run {
        // given
        val taskToTest = "setVersion"
        given("$taskToTest/project", taskToTest, listOf("-Pversion=3.0.0"))

        // when
        val result = build()

        // then
        val actual = File("$FUNCTIONAL_TESTS_DIR/$taskToTest/gradle.properties")
        assertEquals("SUCCESS", result.task(":$taskToTest")!!.outcome.toString())
        assertNotNull(result)
        assertTrue(actual.exists())
        val expected = "$taskToTest/results/gradle.properties".fileContentAsString()
        assertEqualsTrimIdent(expected, actual.readText())
    }

    @Test
    @DisplayName("Expect updated gradle.properties with the given custom version property")
    fun `update custom version property with the current project version`() = GradleRunner.create().run {
        // given
        val taskToTest = "setKnotxVersion"
        given("$taskToTest/project", taskToTest, listOf("-Pversion=3.0.0"))

        // when
        val result = build()

        // then
        val actual = File("$FUNCTIONAL_TESTS_DIR/$taskToTest/gradle.properties")
        assertEquals("SUCCESS", result.task(":$taskToTest")!!.outcome.toString())
        assertNotNull(result)
        assertTrue(actual.exists())
        val expected = "$taskToTest/results/gradle.properties".fileContentAsString()
        assertEqualsTrimIdent(expected, actual.readText())
    }

    private fun GradleRunner.given(testResourcesRoot: String, task: String, args: List<String> = emptyList()) {
        forwardOutput()
        withPluginClasspath()
        val projectDir = File("$FUNCTIONAL_TESTS_DIR/$task").apply {
            deleteRecursively()
            mkdirs()
            testResourcesRoot.asProjectDir().copyRecursively(this)
        }
        withProjectDir(projectDir)
        val arguments = mutableListOf(task)
        arguments.addAll(args)
        withArguments(arguments)
    }

    private fun String.asProjectDir() =
            File(KnotxReleaseBasePluginTest::javaClass.javaClass.classLoader.getResource(this)!!.file)

    private fun String.fileContentAsString() =
            KnotxReleaseBasePluginTest::javaClass.javaClass.classLoader.getResource(this)!!.readText()

    private fun assertEqualsTrimIdent(expected: String, actual: String) {
        assertEquals(expected.trimIndent(), actual.trimIndent())
    }

    companion object {
        const val FUNCTIONAL_TESTS_DIR = "build/functionalTests"
    }

}