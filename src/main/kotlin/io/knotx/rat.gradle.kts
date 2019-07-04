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
package io.knotx

import org.nosphere.apache.rat.RatTask

plugins {
   id("org.nosphere.apache.rat") version "0.4.0"
}

tasks {
    named<RatTask>("rat") {
        excludes.addAll(
            // Build files
            "**/build/*", "**/out/*", "**/resources/*",
            // Markdown files, docs, GitHub templates
            "README.md", "CODE_OF_CONDUCT.md", "CHANGELOG.md", "CONTRIBUTING.md", "RELEASING.md",
            "docs/**", ".github/**",
            // Gradle
            ".gradletasknamecache", "gradle/wrapper/**", "gradlew*", "build/**",
            "gradle.properties",
            // IDEs
            ".nb-gradle/**", "*.iml", "*.ipr", "*.iws", "*.idea/**",
            // Tools
            ".travis.yml"
        )
    }
    getByName("build").dependsOn("rat")
}
