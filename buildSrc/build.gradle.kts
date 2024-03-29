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
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
    implementation("com.gradle.plugin-publish:com.gradle.plugin-publish.gradle.plugin:0.10.1")
    implementation("org.nosphere.apache:creadur-rat-gradle:0.7.0")
}

sourceSets.main {
    java.srcDirs("../src/main/release-base")
}

gradlePlugin {
    plugins {
        create("knotx-plugins-release-base-tmp") {
            id = "knotx-plugins-release-base-tmp"
            implementationClass = "io.knotx.release.KnotxReleaseBasePlugin"
        }
    }
}