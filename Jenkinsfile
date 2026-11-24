pipeline {
  
  agent any
    
  stages {
    stage("Build against JDK's") {
      matrix {
        axes {
          axis {
            name 'JDK_VERSION'
            values 'JDK25', 'JDK27'
          }
        }
        tools {
          jdk "${JDK_VERSION}"
        }
        stages {
          stage('Build') {
            steps {
              sh 'java --version'
              sh './mvnw --version'
              sh './mvnw clean package'
            }
          }
        }
        post {
          always {
            junit '**/target/surefire-reports/TEST-*.xml'
          }
        }
      }
    }
  }
}
