pipeline {
  
    agent any

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
