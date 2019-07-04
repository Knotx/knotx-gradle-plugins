package io.knotx

import com.jfrog.bintray.gradle.BintrayExtension
import org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention
import org.jfrog.gradle.plugin.artifactory.dsl.DoubleDelegateWrapper
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig

plugins {
    `maven-publish`
    signing
    id("com.jfrog.bintray")
    id("com.jfrog.artifactory")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("${project.findProperty("publication.name")}")
                description.set("${project.findProperty("publication.description")}")
                url.set("http://knotx.io")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("marcinczeczko")
                        name.set("Marcin Czeczko")
                        email.set("https://github.com/marcinczeczko")
                    }
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
                    connection.set("${project.findProperty("publication.scm")}")
                    developerConnection.set("${project.findProperty("publication.scm")}")
                    url.set("http://knotx.io")
                }
            }
        }
    }
}

val subProjectPath = this.path
val bintrayUser = (project.findProperty("bintray.user") ?: System.getenv("BINTRAY_USER"))?.toString()
val bintrayKey = (project.findProperty("bintray.key") ?: System.getenv("BINTRAY_KEY"))?.toString()

configure<ArtifactoryPluginConvention> {
    artifactory {
        setContextUrl("https://oss.jfrog.org")

        resolve(delegateClosureOf<ResolverConfig> {
            // is: ResolverConfig.Repository
            repository(delegateClosureOf<DoubleDelegateWrapper> {
                invokeMethod("setRepoKey", "libs-release")
            })
        })

        publish(delegateClosureOf<PublisherConfig> {
            // is: PublisherConfig.Repository
            repository(delegateClosureOf<DoubleDelegateWrapper> {
                if (project.version.toString().endsWith("-SNAPSHOT")) {
                    invokeMethod("setRepoKey", "oss-snapshot-local")
                } else {
                    invokeMethod("setRepoKey", "oss-release-local")
                }

                invokeMethod("setUsername", bintrayUser)
                invokeMethod("setPassword", bintrayKey)
            })

            // FIXME is this even required?
            // old was:
            // properties = [ "bintray.repo": "knotx/maven", "bintray.package": "knotx:knotx", "bintray.version": project.version.toString() ]
            // defaultsClosure = delegateClosureOf<ArtifactoryTask> {
            //     publications("mavenJava")
            //     properties(delegateClosureOf<PropertiesConfig> {
            //
            //     })
            // }
        })
    }
}

configure<BintrayExtension> {
    user = bintrayUser
    key = bintrayKey

    setPublications("mavenJava")

    publish = (project.findProperty("bintray.publish") ?: "true").toString().toBoolean()
    override = (project.findProperty("bintray.override") ?: "false").toString().toBoolean()

    with(pkg) {
        repo = "testing"
        name = project.findProperty("artifactId").toString()
        desc = project.findProperty("publication.description").toString()
        userOrg = "tmaxx" // FIXME should be knotx
        vcsUrl = project.findProperty("publication.scm").toString()
        websiteUrl = "https://knotx.io/"

        setLicenses("Apache-2.0")
        setLabels("knot.x")

        with(version) {
            name = project.version.toString()
            desc = "${project.findProperty("publication.name")} ${project.version}"
            vcsTag = project.version.toString()
            // FIXME
            // released = LocalDate.now().toString()

            with(gpg) {
                sign = gradle.taskGraph.hasTask("$subProjectPath:publish") ||
                    gradle.taskGraph.hasTask("$subProjectPath:bintrayUpload")
            }

            with(mavenCentralSync) {
                sync = true
                user = (project.findProperty("ossrhUsername") ?: "UNKNOWN").toString()
                password = (project.findProperty("ossrhPassword") ?: "UNKNOWN").toString()
            }
        }
    }
}
