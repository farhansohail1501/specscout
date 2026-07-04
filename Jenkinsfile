pipeline {
    agent any

    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Clone') {
            steps {
                git branch: 'main', url: 'https://github.com/farhansohail1501/specscout.git'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean compile'
            }
        }

      stage('Test') {
            steps {
                withCredentials([string(credentialsId: 'mobileapi-key', variable: 'MOBILEAPI_KEY')]) {
                    sh './mvnw test -Dmobileapi.key=$MOBILEAPI_KEY'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube') {
                    sh './mvnw sonar:sonar -Dsonar.projectKey=specscout'
                }
            }
        }

        stage('Package') {
            steps {
                sh './mvnw package -DskipTests'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t specscoutacr1212.azurecr.io/specscout:$BUILD_NUMBER .'
            }
        }
    }
}