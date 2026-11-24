pipeline {
    agent any

    tools {
        jdk 'JDK27'
    }
    
    stages {
        stage('Build') {
            steps {
                sh './mvnw clean package'
            }
        }
    }
}
