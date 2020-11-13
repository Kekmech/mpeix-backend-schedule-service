import java.util.*

plugins {
    application
    kotlin("jvm") version "1.4.10"
    id("com.google.cloud.tools.jib") version "2.5.0"
}

group = "Mpeix Backend"
version = "1.1.3"

application {
    mainClass.set("com.kekmech.MainKt")
}

repositories {
    mavenCentral()
    jcenter()
}

fun ktor(module: String, version: String? = "_") = "io.ktor:ktor-$module${version?.let { ":$version" } ?: ""}"
fun jooq(module: String = "", version: String? = "_") = "org.jooq:jooq${if(module.isNotBlank()) "-$module" else ""}${version?.let { ":$version" } ?: ""}"

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib"))

    implementation(platform(ktor("bom")))
    implementation(ktor("server-core"))
    implementation(ktor("server-netty"))
    implementation(ktor("gson"))
    implementation(ktor("client-gson"))
    implementation(ktor("client-okhttp"))
    implementation(ktor("network-tls"))

    implementation("ch.qos.logback:logback-classic:_")
    implementation("com.squareup.okhttp3:logging-interceptor:_")

    implementation("com.github.ben-manes.caffeine:caffeine:_")

    implementation("org.koin:koin-core:_")

    implementation("org.postgresql:postgresql:_")
    implementation("com.zaxxer:HikariCP:_")

    implementation("org.jsoup:jsoup:_")

    implementation(jooq())
    implementation(jooq("meta"))
    implementation(jooq("codegen"))

    testImplementation("org.junit.jupiter:junit-jupiter:_")
}

jib {
    to {
        image = "tonykolomeytsev/mpeix-backend-schedule-service:latest"
        auth {
            val secretsFile = File("${System.getProperty("user.home")}/mpeix/docker.secret")
            if (secretsFile.exists()) {
                val secrets = Properties()
                secrets.load(secretsFile.inputStream())
                username = secrets.getProperty("username")
                password = secrets.getProperty("password")
            }
        }
        container {
            mainClass = "com.kekmech.MainKt"
            ports = listOf("8080", "2000-2003/udp", "80")
            volumes = listOf("/etc/ehcache/schedule")
        }
    }
    from {
        image = "gcr.io/distroless/java:11"
    }
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    test {
        useJUnitPlatform()
    }
}
