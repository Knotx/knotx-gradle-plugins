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
    testAnnotationProcessor(group = "io.vertx", name = "vertx-codegen", classifier = "processor")
    testAnnotationProcessor(group = "io.vertx", name = "vertx-service-proxy", classifier = "processor")
    testAnnotationProcessor(group = "io.vertx", name = "vertx-rx-java2-gen")
    testAnnotationProcessor(group = "io.vertx", name = "vertx-web-api-service")
}

tasks.named<JavaCompile>("compileTestJava") {
    options.generatedSourceOutputDirectory.set(file("src/test/generated"))
}

sourceSets.named("test") {
    java.srcDir("src/test/generated")
}

tasks.named<Delete>("clean") {
    delete.add("src/test/generated")
}