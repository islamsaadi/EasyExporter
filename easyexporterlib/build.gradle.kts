plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.maven.publish)
}


android {
    namespace = "com.islamsaadi.easyexporterlib"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

group = "com.github.islamsaadi"
version = "1.0.0"

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // JSON
    implementation(libs.gson)
    // CSV
    implementation(libs.opencsv)
    // PDF: Android PdfDocument is built-in; for iText:
    implementation(libs.itextpdf)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = project.group.toString()
                artifactId = "easyexporter"
                version = project.version.toString()

                artifact(tasks.getByName("bundleReleaseAar"))

                pom {
                    withXml {
                        val depsNode = asNode().appendNode("dependencies")
                        configurations["api"].dependencies.forEach { dep ->
                            val dependencyNode = depsNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", dep.group)
                            dependencyNode.appendNode("artifactId", dep.name)
                            dependencyNode.appendNode("version", dep.version)
                            dependencyNode.appendNode("scope", "compile")
                        }
                        configurations["implementation"].dependencies.forEach { dep ->
                            val dependencyNode = depsNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", dep.group)
                            dependencyNode.appendNode("artifactId", dep.name)
                            dependencyNode.appendNode("version", dep.version)
                            dependencyNode.appendNode("scope", "runtime")
                        }
                    }
                }
            }
        }

        repositories {
            mavenLocal()
        }
    }
}