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
    `maven-publish`
    signing
    id("org.nosphere.apache.rat") version "0.4.0"
}

tasks.rat {
    excludes.addAll(
            "README.md", "CODE_OF_CONDUCT.md",
            ".gradletasknamecache", "gradle/wrapper/**", "gradlew*", "build/**", // Gradle
            ".nb-gradle/**", "*.iml", "*.ipr", "*.iws", "*.idea/**", // IDEs
            ".travis.yml" // Tools
    )
}

tasks.check { dependsOn(tasks.rat) }

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    classifier = "javadoc"
    from(tasks.named<Javadoc>("javadoc"))
}
tasks.named<Javadoc>("javadoc") {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

fun configure(publication: MavenPublication) {
    publication.pom {
        if (publication.name == "io.knotx.codegenPluginMarkerMaven") {
            withXml {
                asNode().appendNode("name", "Knot.x Gradle Codegen Plugin")
                asNode().appendNode("description", "Vert.x Codegen dependencies setup.")
            }
        }
        if (publication.name == "io.knotx.codegen-testPluginMarkerMaven") {
            name.set("Knot.x Gradle Codegen Test Plugin")
            description.set("Vert.x Codegen dependencies for Unit tests.")
        }
        if (publication.name == "io.knotx.jacocoPluginMarkerMaven") {
            name.set("Knot.x Gradle Jacoco Plugin")
            description.set("Jacoco plugin setup to measure tests coverage.")
        }
        if (publication.name == "io.knotx.java-libraryPluginMarkerMaven") {
            name.set("Knot.x Gradle Java Library Plugin")
            description.set("Base java settings for Knot.x modules.")
        }
        if (publication.name == "io.knotx.maven-publishPluginMarkerMaven") {
            name.set("Knot.x Gradle Maven Publish Plugin")
            description.set("Defaults for maven-publish plugin in Knot.x modules.")
        }
        if (publication.name == "io.knotx.publish-all-compositePluginMarkerMaven") {
            name.set("Knot.x Gradle Composite Plugin")
            description.set("Publish all support for Knot.x Aggregator.")
        }
        if (publication.name == "io.knotx.unit-testPluginMarkerMaven") {
            name.set("Knot.x Gradle Unit Test Plugin")
            description.set("JUnit 5 tests support.")
        }
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
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "knotx-gradle-plugins"
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(javadocJar.get())
            pom {
                name.set("Knot.x Gradle Plugins")
                description.set("Knot.x Gradle Plugins minimize Knot.x modules Gradle configuration.")
            }
        }
        configure {
            withType(MavenPublication::class) {
                configure(this)
            }
        }
        repositories {
            maven {
                val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
                url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
                credentials {
                    username = if (project.hasProperty("ossrhUsername")) project.property("ossrhUsername")?.toString() else "UNKNOWN"
                    password = if (project.hasProperty("ossrhPassword")) project.property("ossrhPassword")?.toString() else "UNKNOWN"
                    println("Connecting with user: ${username}")
                }
            }
        }
    }
}

signing {
    setRequired {
        gradle.taskGraph.hasTask(":publish") ||
                gradle.taskGraph.hasTask(":publishMavenJavaPublicationToMavenRepository")
    }

    sign(publishing.publications)
}

repositories {
    jcenter()
    maven { url = uri("https://plugins.gradle.org/m2/") }
}
