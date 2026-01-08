pipeline {
  agent any

  options {
    timestamps()
  }

  environment {
    CORE_DOCKER_DIR = "/opt/educeasy/core"
    GATEWAY_DOCKER_DIR = "/opt/educeasy/gateway"
    FRONT_DOCKER_DIR = "/opt/educeasy/front"

    MAIN_DIR = "/opt/educeasy"
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

    stage('Build Front dist') {
      steps {
        sh '''
          set -e
          test -d "$WORKSPACE/06_UI/educeasy-ui"
          test -f "$WORKSPACE/06_UI/educeasy-ui/package.json"
          test -f "$WORKSPACE/06_UI/educeasy-ui/package-lock.json"

          docker run --rm -v "$WORKSPACE/06_UI/educeasy-ui":/app -w /app node:20-alpine sh -lc "npm ci && npm run build"

          test -d "$WORKSPACE/06_UI/educeasy-ui/dist"
        '''
      }
    }

    stage('Copy artifacts to /opt/educeasy') {
      steps {
        sh '''
          set -e

          CORE_JAR=$(ls -1 "$WORKSPACE"/07_Metier/educeasy-core/target/*.jar | head -n 1)
          GATEWAY_JAR=$(ls -1 "$WORKSPACE"/07_Metier/educeasy-gateway/target/*.jar | head -n 1)

          echo "Copy core jar -> /opt/educeasy/core/app.jar"
		  mkdir -p /opt/educeasy/core/config
		  cp -f "$WORKSPACE/07_Metier/educeasy-core/config/application.yaml" /opt/educeasy/core/config/application.yaml
          cp -f "$CORE_JAR" /opt/educeasy/core/app.jar

          echo "Copy gateway jar -> /opt/educeasy/gateway/app.jar"
          mkdir -p /opt/educeasy/gateway/config
		  cp -f "$WORKSPACE/07_Metier/educeasy-gateway/config/application.yaml" /opt/educeasy/gateway/config/application.yaml
          cp -f "$GATEWAY_JAR" /opt/educeasy/gateway/app.jar

          echo "Copy front dist -> /opt/educeasy/front/dist"
          rm -rf /opt/educeasy/front/dist
          mkdir -p /opt/educeasy/front/dist
          cp -r "$WORKSPACE/06_UI/educeasy-ui/dist/"* /opt/educeasy/front/dist/
        '''
      }
    }

    stage('Build Docker images (VM Dockerfiles)') {
      steps {
        sh '''
          set -e
          test -f "$CORE_DOCKER_DIR/Dockerfile"
          test -f "$GATEWAY_DOCKER_DIR/Dockerfile"
          test -f "$FRONT_DOCKER_DIR/Dockerfile"

          docker build -t educeasy-core:latest "$CORE_DOCKER_DIR"
          docker build -t educeasy-gateway:latest "$GATEWAY_DOCKER_DIR"
          docker build -t educeasy-front:latest "$FRONT_DOCKER_DIR"
        '''
      }
    }

    stage('Deploy (docker compose)') {
      steps {
        sh '''
          set -e
          cd "$MAIN_DIR"
          docker compose up -d --remove-orphans
          docker compose ps
        '''
      }
    }
  }
}
