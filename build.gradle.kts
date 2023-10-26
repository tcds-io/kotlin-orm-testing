import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jUnitVersion: String by project
val mockkVersion: String by project
val ormVersion: String by project

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

    object Project {
        const val name = "Kotlin Simple ORM Testing Tools"
        const val description = "Testing utilities for Kotlin Simple ORM"
        const val repository = "https://github.com/tcds-io/kotlin-orm-testing"
        const val scm = "scm:git:git://github.com:tcds-io/kotlin-orm-testing.git"

        const val organization = "tcds-io"
        const val developer = "Thiago Cordeiro"
        const val email = "thiago@tcds.io"
    }
}

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

    implementation("io.tcds.orm:orm:$ormVersion")
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

    publications {
        listOf("defaultMavenJava", "pluginMaven").forEach { publication ->
            create<MavenPublication>(publication) {
                artifact(sourcesJar)
                artifact(javadocJar)

                pom {
                    name.set(Publication.Project.name)
                    description.set(Publication.Project.description)
                    url.set(Publication.Project.repository)
                    packaging = "jar"

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("${Publication.Project.repository}/blob/main/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            id.set(Publication.Project.organization)
                            name.set(Publication.Project.developer)
                            email.set(Publication.Project.email)
                        }
                    }
                    scm {
                        connection.set(Publication.Project.scm)
                        url.set(Publication.Project.repository)
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
