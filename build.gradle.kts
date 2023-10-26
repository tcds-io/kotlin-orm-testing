import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jUnitVersion: String by project
val mockkVersion: String by project
val ormVersion: String by project

plugins {
    `kotlin-dsl`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version "1.3.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    gradleApi()
    api(kotlin("stdlib"))

    implementation("io.tcds:orm:$ormVersion")
    implementation("org.junit.jupiter:junit-jupiter:$jUnitVersion")
    implementation("io.mockk:mockk:$mockkVersion")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        languageVersion = "1.9"
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events("started", "skipped", "passed", "failed")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        showStandardStreams = true
    }
}

object Publication {
    const val group = "io.tcds.orm"
    val buildVersion: String = System.getenv("VERSION") ?: "dev"

    object Sonatype {
        val username: String? = System.getenv("OSS_USER")
        val password: String? = System.getenv("OSS_PASSWORD")
    }

    object Gpg {
        val signingKeyId: String? = System.getenv("GPG_KEY_ID")
        val signingKey: String? = System.getenv("GPG_KEY")
        val signingPassword: String? = System.getenv("GPG_KEY_PASSWORD")
    }
}

val sourcesJar by tasks.creating(Jar::class) { archiveClassifier.set("sources"); from(sourceSets.main.get().allSource) }
val javadocJar by tasks.creating(Jar::class) { archiveClassifier.set("javadoc"); from(tasks.javadoc) }

publishing {
    repositories {
        maven {
            name = "SonaType"
            group = Publication.group
            version = Publication.buildVersion
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")

            credentials {
                username = Publication.Sonatype.username
                password = Publication.Sonatype.password
            }
        }
    }

    /**
     * references:
     * - https://docs.gradle.org/current/userguide/publishing_maven.html
     * - https://github.com/LukasForst/ktor-plugins/blob/master/build.gradle.kts
     * - https://devanshuchandra.medium.com/maven-central-publishing-using-gradle-and-gpg-signing-47b216179dd6
     */
    publications {
        listOf("defaultMavenJava", "pluginMaven").forEach { publication ->
            create<MavenPublication>(publication) {
                // from(components["java"])
                artifact(sourcesJar)
                artifact(javadocJar)

                pom {
                    name.set("Kotlin Simple ORM Testing")
                    description.set("Testing utilities for Kotlin Simple ORM")
                    url.set("https://github.com/tcds-io/kotlin-orm-testing")
                    packaging = "jar"

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/tcds-io/kotlin-orm-testing/blob/main/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set("tcds-io")
                            name.set("Thiago Cordeiro")
                            email.set("thiago@tcds.io")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com:tcds-io/kotlin-orm-testing.git")
                        url.set("https://github.com/tcds-io/kotlin-orm-testing")
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        Publication.Gpg.signingKeyId,
        Publication.Gpg.signingKey,
        Publication.Gpg.signingPassword,
    )
    sign(publishing.publications["pluginMaven"])
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(Publication.Sonatype.username)
            password.set(Publication.Sonatype.password)
        }
    }
}
