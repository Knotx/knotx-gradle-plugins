import org.gradle.kotlin.dsl.register

plugins {
    id("io.knotx.release-base")
}

repositories {
    mavenLocal()
    jcenter()
}

tasks {
    register<io.knotx.release.common.ProjectVersionUpdateTask>("setKnotxVersion") {
        versionParamProperty = "version"
        propertyKeyNameInFile = "knotx.version"
    }
}