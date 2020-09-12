import java.util.*

plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.google.cloud.tools.jib") version "2.5.0"
}

group = "Mpeix Backend"
version = "1.1.0"

application {
    mainClassName = "com.kekmech.MainKt"
}

repositories {
    mavenCentral()
    jcenter()
}

fun ktor(module: String, version: String? = null) = "io.ktor:ktor-$module${version?.let { ":$version" } ?: ""}"
fun jooq(module: String, version: String? = null) = "org.jooq:jooq$module${version?.let { ":$version" } ?: ""}"

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib"))

    implementation(platform(ktor("bom", "1.3.1")))
    implementation(ktor("server-core"))
    implementation(ktor("server-netty"))
    implementation(ktor("gson"))
    implementation(ktor("client-gson"))
    implementation(ktor("client-okhttp"))
    implementation(ktor("network-tls"))

    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.8.0")


    //implementation("org.ehcache:ehcache:3.8.1")
    implementation("com.github.ben-manes.caffeine:caffeine:2.8.5")

    implementation("org.koin:koin-core:2.1.6")

    implementation("org.postgresql:postgresql:42.2.14")

    implementation("org.jsoup:jsoup:1.13.1")

    implementation(jooq("", "3.13.4"))
    implementation(jooq("-meta", "3.13.4"))
    implementation(jooq("-codegen", "3.13.4"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
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
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    test {
        useJUnitPlatform()
    }
}
