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
