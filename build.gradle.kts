import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
  kotlin("jvm") version "2.1.10"
  kotlin("plugin.serialization") version "2.1.10"
}

allprojects {
  group = "com.kSerialization2Graphql"
  repositories {
    mavenCentral()
  }
}

subprojects {
  apply { plugin("kotlin") }
  dependencies {
    // reflection
    implementation(kotlin("reflect"))

    // test
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
  }

  tasks {
    test {
      useJUnitPlatform()
      testLogging {
        events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.PASSED)
        showStandardStreams = true
        exceptionFormat = TestExceptionFormat.FULL
      }
    }
  }

  kotlin {
    jvmToolchain(21)
  }
}