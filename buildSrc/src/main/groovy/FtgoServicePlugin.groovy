import org.gradle.api.Plugin
import org.gradle.api.Project

class FtgoServicePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        project.apply(plugin: 'org.springframework.boot')
    	project.apply(plugin: "io.spring.dependency-management")

        // The Spring Boot Gradle plugin (2.5.x) auto-imports its own BOM,
        // which would override transitive dependency versions. Pin the BOM
        // to the runtime springBootVersion to keep the classpath consistent.
        project.dependencyManagement {
            imports {
                mavenBom "org.springframework.boot:spring-boot-dependencies:${project.property('springBootVersion')}"
            }
        }
    }
}
