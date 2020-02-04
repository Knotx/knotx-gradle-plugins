import groovy.util.Node

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
    id("maven-publish")
    id("signing")
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")
    id("org.nosphere.apache.rat")
}

repositories {
    jcenter()
    maven { url = uri("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.3.61"))

    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
    testCompile(gradleTestKit())
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {}
gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations.getByName("functionalTestImplementation").extendsFrom(configurations.getByName("testImplementation"))

tasks {
    named<Javadoc>("javadoc") {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }

    withType<Test>().configureEach {
        testLogging.showStandardStreams = true
        useJUnitPlatform()
    }

    register<Test>("functionalTest") {
        group = "verification"
        testClassesDirs = functionalTestSourceSet.output.classesDirs
        classpath = functionalTestSourceSet.runtimeClasspath

        useJUnitPlatform()
        mustRunAfter("test")
        dependsOn("jar")
        outputs.upToDateWhen { false }
    }

    named<Task>("build") {
        dependsOn("functionalTest")
    }

    val ratTask = named<org.nosphere.apache.rat.RatTask>("rat") {
        excludes.addAll(listOf(
                "*.md", // docs
                ".gradletasknamecache", "gradle/wrapper/**", "gradlew*", "**/build/**", // Gradle
                "src/test/resources/**", "src/functionalTest/resources/**", // tests resources
                ".nb-gradle/**", "*.iml", "*.ipr", "*.iws", "*.idea/**", // IDEs
                "azure-pipelines.yml" // Tools
        ))
    }

    named("check") {
        dependsOn(ratTask)
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    classifier = "javadoc"
    from(tasks.named<Javadoc>("javadoc"))
}

fun configure(publication: MavenPublication) {
    publication.pom {
        url.set("http://knotx.io")
        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("skejven")
                name.set("Maciej Laskowski")
                email.set("https://github.com/Skejven")
            }
            developer {
                id.set("tomaszmichalak")
                name.set("Tomasz Michalak")
                email.set("https://github.com/tomaszmichalak")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/Knotx/knotx-gradle-plugins.git")
            developerConnection.set("scm:git:ssh://github.com:Knotx/knotx-gradle-plugins.git")
            url.set("http://knotx.io")
        }
        withXml {
            setNameAndDescription(asNode(), publication.name)
        }
    }
}

fun setNameAndDescription(node: groovy.util.Node, publicationName: String) {
    when (publicationName) {
        "io.knotx.codegenPluginMarkerMaven" -> {
            node.appendNode("name", "Knot.x Gradle Codegen Plugin")
            node.appendNode("description", "Vert.x Codegen dependencies setup.")
        }
        "io.knotx.codegen-testPluginMarkerMaven" -> {
            node.appendNode("name", "Knot.x Gradle Codegen Test Plugin")
            node.appendNode("description", "Vert.x Codegen dependencies for Unit tests.")
        }
        "io.knotx.jacocoPluginMarkerMaven" -> {
            node.appendNode("name", "Knot.x Gradle Jacoco Plugin")
            node.appendNode("description", "Jacoco plugin setup to measure tests coverage.")
        }
        "io.knotx.java-libraryPluginMarkerMaven" -> {
            node.appendNode("name", "Knot.x Gradle Java Library Plugin")
            node.appendNode("description", "Base java settings for Knot.x modules.")
        }
        "io.knotx.maven-publishPluginMarkerMaven" -> {
            node.appendNode("name", "Knot.x Gradle Maven Publish Plugin")
            node.appendNode("description", "Defaults for maven-publish plugin in Knot.x modules.")
        }
        "io.knotx.publish-all-compositePluginMarkerMaven" -> {
            node.appendNode("name", "Knot.x Gradle Composite Plugin")
            node.appendNode("description", "Publish all support for Knot.x Aggregator.")
        }
        "io.knotx.unit-testPluginMarkerMaven" -> {
            node.appendNode("name", "Knot.x Gradle Unit Test Plugin")
            node.appendNode("description", "JUnit 5 tests support.")
        }
        "io.knotx.distributionPluginMarkerMaven" -> {
            node.appendNode("name", "Knot.x Gradle Distribution Plugin")
            node.appendNode("description", "A set of tasks that allow you to customize the Knot.x Stack / distribution")
        }
        "pluginMaven" -> {
            node.appendNode("name", "Knot.x Gradle Plugins")
            node.appendNode("description", "Knot.x Gradle Plugins minimize Knot.x modules Gradle configuration.")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "knotx-gradle-plugins"
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
        }
        configure<PublishingExtension> {
            withType(MavenPublication::class) {
                configure(this)
            }
        }
        repositories {
            maven {
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = if (project.hasProperty("ossrhUsername")) project.property("ossrhUsername")?.toString() else "UNKNOWN"
                    password = if (project.hasProperty("ossrhPassword")) project.property("ossrhPassword")?.toString() else "UNKNOWN"
                }
            }
        }
    }
}

extra["isReleaseVersion"] = !version.toString().endsWith("SNAPSHOT")
tasks.withType<Sign>().configureEach {
    onlyIf { project.extra["isReleaseVersion"] as Boolean }
}

gradlePlugin {
    plugins {
        create("io.knotx.release-base") {
            id = "io.knotx.release-base"
            implementationClass = "io.knotx.release.KnotxReleaseBasePlugin"
        }
        create("io.knotx.release-java") {
            id = "io.knotx.release-java"
            implementationClass = "io.knotx.release.KnotxReleaseJavaPlugin"
        }
    }
}

