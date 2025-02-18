import com.google.protobuf.gradle.*
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.32"
    id("org.jetbrains.kotlin.kapt") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.micronaut.application") version "1.5.0"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.4.32"
    id("org.jetbrains.kotlin.plugin.jpa") version "1.4.32"
    id("com.google.protobuf") version "0.8.15"
}

version = "0.1"
group = "com.zup.keymanager"

val kotlinVersion=project.properties.get("kotlinVersion")
repositories {
    mavenCentral()
}

micronaut {
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("com.zup.keymanager.*")
    }
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-inject-java:2.5.3")
    implementation("io.micronaut:micronaut-aop:2.5.3")
    kapt("io.micronaut.data:micronaut-data-processor")

    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.grpc:micronaut-grpc-runtime")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("javax.annotation:javax.annotation-api")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("mysql:mysql-connector-java")

    testImplementation("com.h2database:h2:1.4.200")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.mockito:mockito-core")

    implementation("io.micronaut.xml:micronaut-jackson-xml")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.micronaut:micronaut-http-client:2.5.2")

    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut.beanvalidation:micronaut-hibernate-validator:3.0.0")
    implementation("org.hibernate:hibernate-validator:6.1.6.Final")

//    implementation("com.michael-bull.kotlin-result:kotlin-result:1.1.11")

    //ByteBuddy
    implementation("net.bytebuddy:byte-buddy-agent:1.11.0")
    implementation("net.bytebuddy:byte-buddy:1.11.0")
}


application {
    mainClass.set("com.zup.keymanager.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
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


}
sourceSets {
    main {
        java {
            srcDirs("build/generated/source/proto/main/grpc")
            srcDirs("build/generated/source/proto/main/java")
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.14.0"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.33.1"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without options.
                id("grpc")
            }
        }
    }
}
