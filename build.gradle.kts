import java.util.*

plugins {
    application
    kotlin("jvm") version "1.4.10"
    id("com.google.cloud.tools.jib") version "2.5.0"
}

group = "Mpeix Backend"
version = "1.1.2"

application {
    mainClass.set("com.kekmech.MainKt")
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

    implementation("com.github.ben-manes.caffeine:caffeine:2.8.5")

    implementation("org.koin:koin-core:2.1.6")

    implementation("org.postgresql:postgresql:42.2.14")
    implementation("com.zaxxer:HikariCP:3.4.5")

    implementation("org.jsoup:jsoup:1.13.1")

    implementation(jooq("", "3.13.4"))
    implementation(jooq("-meta", "3.13.4"))
    implementation(jooq("-codegen", "3.13.4"))

    testImplementation("org.junit.jupiter:junit-jupiter:5.6.2")
}

data class JibCreds(val username: String, val password: String)

fun getJibCredsFromSecretFile(): JibCreds? {
    val secretsFile = File("${System.getProperty("user.home")}/mpeix/docker.secret")
    if (!secretsFile.exists()) {
        return null
    }
    val secrets = Properties().apply {
        load(secretsFile.inputStream())
    }
    return JibCreds(secrets.getProperty("username"), secrets.getProperty("password"))
}

fun getJibCredsFromFromEnv(): JibCreds? {
    val username = System.getenv("REGISTRY_USER")
    val password = System.getenv("REGISTRY_PASSWORD")
    return if (username != null && password != null) {
        JibCreds(username, password)
    } else {
        null
    }
}

jib {
    to {
        image = "docker.pkg.github.com/kekmech/mpeix-schedule/application:${version}"
        auth {
            val creds = getJibCredsFromFromEnv() ?: getJibCredsFromSecretFile()
            if(creds != null) {
                username = creds.username
                password = creds.password
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
