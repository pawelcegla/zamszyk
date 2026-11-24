pipeline {
  
  agent any

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
