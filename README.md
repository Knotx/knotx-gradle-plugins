[![Build Status](https://dev.azure.com/knotx/Knotx/_apis/build/status/Knotx.knotx-gradle-plugins?branchName=master)](https://dev.azure.com/knotx/Knotx/_build/latest?definitionId=14&branchName=master)

# knotx-gradle-plugins
Gradle plugins that help manage Knot.x modules builds.

## Plugins

### Distribution Plugin
Distribution Plugin identifies good practices on how to create a custom `Knot.x` distribution. This is a set of tasks that you can use when starting a new project, regardless of whereter it is Docker-based or not.

#### How to use
Declare the `plugins` section in your build script

```kotlin
plugins {
    id("io.knotx.distribution")
}
```
Then point to the root directory with the `Knot.x` configuration, setting the `knotx.conf` property in `gradle.properties`. E.g:

```
knotx.conf=src/main/packaging
```

#### Available tasks
 - `overwriteCustomFiles` - copies/replaces files from the directory specified in the `knotx.conf` property to `build/out/knotx`. In addition, all dependencies form `dist` are copied to  `build/out/lib`.  
 - `downloadBaseDistribution` - downloads the [Knot.x Stack](https://github.com/Knotx/knotx-stack) and extracts it into the `build/out` directory
 - `assembleCustomDistribution` - combines the two above tasks and creates a full `Knot.x` distribution with all your custom dependencies. Distribution available in the `build/distribution` directory 

#### Examples

##### ZIPed distribution 
Add the following lines to your `build.gradle.kts`:
```
plugins {
    id("io.knotx.distribution") version "CURRENT_VERSION"
}
tasks.named("build") { finalizedBy("assembleCustomDistribution") }
tasks.named("clean") { dependsOn("cleanDistribution") }
```
or check the [Getting Started with Knot.x Stack](https://github.com/Knotx/knotx-example-project/tree/master/getting-started) example project.

##### Docker distribution
Check [Knot.x Starter Kit](https://github.com/Knotx/knotx-starter-kit) as a reference.
 
 ## Local development and testing

In order to properly develop and test new version of this plugin, you will need a working project on which tests will be carried out.

When you have it prepared, add the following snippet at the beginning of the target project's `settings.gradle.kts` file:

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}
```

After that, you can push the new version of `knotx-gradle-plugins` to your local Maven repository (via Gradle's `publishToMavenLocal` task),
and verify that the added functionality works as expected.
