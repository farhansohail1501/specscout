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

        stage('Trivy Scan') {
            steps {
                sh 'trivy image --severity HIGH,CRITICAL --exit-code 0 specscoutacr1212.azurecr.io/specscout:$BUILD_NUMBER'
            }
        }

        stage('Push to ACR') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'acr-credentials', usernameVariable: 'ACR_USER', passwordVariable: 'ACR_PASS')]) {
                    sh 'echo $ACR_PASS | docker login specscoutacr1212.azurecr.io -u $ACR_USER --password-stdin'
                    sh 'docker push specscoutacr1212.azurecr.io/specscout:$BUILD_NUMBER'
                }
            }
        }

        stage('Deploy to AKS') {
            steps {
                sh 'kubectl apply -f k8s/'
                sh 'kubectl set image deployment/specscout-deployment specscout=specscoutacr1212.azurecr.io/specscout:$BUILD_NUMBER'
                sh 'kubectl rollout status deployment/specscout-deployment --timeout=180s'
            }
        }
    }
}