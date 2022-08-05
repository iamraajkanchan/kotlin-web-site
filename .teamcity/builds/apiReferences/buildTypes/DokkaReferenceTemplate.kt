package builds.apiReferences.buildTypes

import jetbrains.buildServer.configs.kotlin.Template
import jetbrains.buildServer.configs.kotlin.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.buildSteps.script


object DokkaReferenceTemplate : Template({
  name = "Dokka Reference Template"

  artifactRules = "build/dokka/htmlMultiModule/** => pages.zip"

  steps {
    script {
      name = "Drop SNAPSHOT word for deploy"
      scriptContent = """
                if [ %teamcity.build.branch.is_default% == "true" ]; then
                  CURRENT_VERSION="$(sed -E s/^v?//g <<<%release.tag%)"
                	sed -i -E "s/^version=.+(-SNAPSHOT)?/version=${'$'}CURRENT_VERSION/gi" ./gradle.properties
                fi
            """.trimIndent()
      dockerImage = "alpine"
    }

    gradle {
      name = "Build dokka html"
      tasks = "dokkaHtmlMultiModule"
    }
  }

  requirements {
    contains("docker.server.osType", "linux")
  }

  params {
    param("teamcity.vcsTrigger.runBuildInNewEmptyBranch", "true")
  }
})
