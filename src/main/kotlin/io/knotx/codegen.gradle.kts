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
    java
}

dependencies {
    annotationProcessor(group = "io.vertx", name = "vertx-codegen", classifier = "processor")
    annotationProcessor(group = "io.vertx", name = "vertx-service-proxy", classifier = "processor")
    annotationProcessor(group = "io.vertx", name = "vertx-rx-java2-gen")
    annotationProcessor(group = "io.vertx", name = "vertx-web-api-service")

    implementation(group = "io.vertx", name = "vertx-codegen")
}

tasks.named<JavaCompile>("compileJava") {
    options.generatedSourceOutputDirectory.set(file("src/main/generated"))
    options.compilerArgs.addAll(listOf(
            "-processor", "io.vertx.codegen.CodeGenProcessor",
            "-Acodegen.output=${project.projectDir}/docs"))
}

sourceSets.named("main") {
    java.srcDir("src/main/generated")
}

tasks.named<Delete>("clean") {
    delete.add("src/main/generated")
}
