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

plugins {
    jacoco
}

val jacocoTestReport = tasks.getByName("jacocoTestReport")
val test = tasks.getByName("test")

jacocoTestReport.dependsOn(test);
test.finalizedBy(jacocoTestReport);

tasks.getByName<JacocoReport>("jacocoTestReport") {
    reports {
        sourceDirectories.from(fileTree("src/main/java"))
        classDirectories.from(fileTree("build/classes") {
            exclude("**/*Options*")
        })
        xml.outputLocation.set(File("$buildDir/reports/jacoco/report.xml"))
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}