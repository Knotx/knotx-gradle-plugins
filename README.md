# knotx-gradle-plugins
Gradle plugins that help manage Knot.x modules builds.


### Local development and testing

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

### Distribution plugin

Distribution plugin helps to produce a full `Knot.x` stack distribution,

#### How to use

Declare in section plugins your build script

```kotlin
plugins {
    id("io.knotx.distribution")
}
```

Point your root directory with `Knot.x` configuration by setting `knotx.conf` property in `gradle.properties`. For instance: 

```
knotx.conf=src/main/packaging
```

#### Available tasks

 - `overwriteCustomFiles` - copies and overwrites files from directory pointed in property `knotx.conf` to `build/out/knotx`. In addition, all dependencies  form `dist` are copied to  `build/out/lib`.  
 - `downloadBaseDistribution` - downloads `Knot.x` stack distribution and unpack it into `build/out` directory
 - `assembleCustomDistribution` - It combines two above tasks and produce full `Knot.x` stack distribution with all custom dependencies. Distribution available in `build/distribution` directory 

 #### Example
 
 Produce distribution on build phase
 ```
 tasks.named("build") { finalizedBy("assembleCustomDistribution") }
 ```