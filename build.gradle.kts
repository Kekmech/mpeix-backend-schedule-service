import java.util.*

plugins {
    application
    kotlin("jvm") version "1.3.72"
    id("com.google.cloud.tools.jib")
}

group = "Mpeix Backend"
version = "2020.0.0"

application {
    mainClass.set("com.kekmech.schedule.MainKt")
}

repositories {
    mavenCentral()
    jcenter()
}

fun ktor(module: String, version: String? = "_") = "io.ktor:ktor-$module${version?.let { ":$version" } ?: ""}"
fun jooq(module: String = "", version: String? = "_") = "org.jooq:jooq${if(module.isNotBlank()) "-$module" else ""}${version?.let { ":$version" } ?: ""}"

dependencies {
    implementation(platform(kotlin("bom")))
    implementation(kotlin("stdlib-jdk8"))

    implementation(platform(ktor("bom")))
    implementation(ktor("server-netty"))
    implementation(ktor("gson"))
    implementation(ktor("locations"))
    implementation(ktor("metrics-micrometer"))
    implementation(ktor("client-gson"))
    implementation(ktor("client-okhttp"))
    implementation(ktor("network-tls"))

    implementation("ch.qos.logback:logback-classic:_")
    implementation("com.squareup.okhttp3:logging-interceptor:_")

    implementation("io.github.config4k:config4k:_")

    implementation("com.github.ben-manes.caffeine:caffeine:_")

    implementation("org.koin:koin-core:_")
    implementation("org.koin:koin-ktor:_")

    implementation("org.postgresql:postgresql:_")
    implementation("com.zaxxer:HikariCP:_")

    implementation("org.jsoup:jsoup:_")

    implementation(jooq())
    implementation(jooq("meta"))
    implementation(jooq("codegen"))

    testImplementation("org.junit.jupiter:junit-jupiter:_")
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

fun getImageVersion() = buildString {
    append(version)
    val buildVersion = System.getenv("BUILD_VERSION")
    if(buildVersion != null) {
        append("-")
        append(buildVersion)
    }
}

jib {
    to {
        image = "manager.kekmech.com:5000/mpeix-schedule:${getImageVersion()}"
        auth {
            val creds = getJibCredsFromFromEnv() ?: getJibCredsFromSecretFile()
            if(creds != null) {
                username = creds.username
                password = creds.password
            }
        }
        container {
            mainClass = "com.kekmech.schedule.MainKt"
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
            freeCompilerArgs += "-Xopt-in=io.ktor.locations.KtorExperimentalLocationsAPI"
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
