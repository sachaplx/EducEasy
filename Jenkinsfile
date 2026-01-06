pipeline {
  agent any

  options {
    timestamps()
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build educeasy-core') {
      steps {
        dir('07_Metier/educeasy-core') {
          sh 'mvn -DskipTests clean package'
        }
      }
      post {
        success {
          archiveArtifacts artifacts: '07_Metier/educeasy-core/target/*.jar', fingerprint: true
        }
      }
    }

    stage('Build educeasy-gateway') {
      steps {
        dir('07_Metier/educeasy-gateway') {
          sh 'mvn -DskipTests clean package'
        }
      }
      post {
        success {
          archiveArtifacts artifacts: '07_Metier/educeasy-gateway/target/*.jar', fingerprint: true
        }
      }
    }
  }
}
