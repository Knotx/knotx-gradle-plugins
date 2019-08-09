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

val downloadDir = file("${buildDir}/download")
val distributionDir = file("${buildDir}/out")
val stackName = "knotx"
val stackDistribution = "knotx-stack-${version}.zip"
val knotxVersion = project.property("knotx.version")
val isAssembleDistribution = project.hasProperty("knotx.assembleDistribution")
val configDir = project.property("knotx.conf")

configurations {
    register("dist")
    register("zipped")
}

dependencies {
    "zipped"(group = "io.knotx", name = "knotx-stack", version = "${knotxVersion}", ext = "zip")
}

val cleanDistribution = tasks.register<Delete>("cleanDistribution") {
    group = "distribution"
    delete(listOf(distributionDir))
}

val copyConfigs = tasks.register<Copy>("copyConfigs") {
    group = "distribution"

    from(file("$configDir"))
    into(file("${distributionDir}/${stackName}"))

    mustRunAfter("cleanDistribution")
}

val downloadDeps = tasks.register<Copy>("downloadDeps") {
    group = "distribution"

    from(configurations.named("dist"))
    into("${distributionDir}/${stackName}/lib")

    mustRunAfter("cleanDistribution")
}

val assembleDistribution = tasks.register<Zip>("assembleDistribution") {
    group = "distribution"

    archiveName = stackDistribution
    from(distributionDir)
}

val downloadStack = tasks.register<Copy>("downloadStack") {
    group = "distribution"

    from(configurations.named("zipped"))
    into("${downloadDir}")
}

val unzipStack = tasks.register<Copy>("unzipStack") {
    group = "distribution"

    val zipPath = "${buildDir}/download/knotx-stack-${knotxVersion}.zip"

    from(zipTree(zipPath))
    into("${distributionDir}")
}


val assembleBaseDistribution = tasks.register("assembleBaseDistribution"){
    group = "distribution"

    dependsOn(copyConfigs, downloadDeps)
}

assembleDistribution {
    dependsOn(assembleBaseDistribution)
}

if(isAssembleDistribution) {
    copyConfigs { dependsOn(downloadStack, unzipStack) }

    tasks.named("build") { finalizedBy(assembleDistribution) }
    tasks.named("clean") { dependsOn(cleanDistribution) }
}