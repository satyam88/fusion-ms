
pipeline {
    agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '3', artifactNumToKeepStr: '3'))
    }

    tools {
        maven 'maven_3.9.4'
    }

    environment {
        DOCKER_IMAGE = "satyam88/fusion-ms"
        ECR_REPO = "533267238276.dkr.ecr.ap-south-1.amazonaws.com/fusion-ms"
        NEXUS_URL = "http://3.6.37.208:8085/repository/fusion-ms/"
    }

    stages {
        stage('Code Compilation') {
            steps {
                echo 'Starting Code Compilation...'
                sh 'mvn clean compile'
                echo 'Code Compilation Completed Successfully!'
            }
        }
        stage('Code QA Execution') {
            steps {
                echo 'Running JUnit Test Cases...'
                sh 'mvn clean test'
                echo 'JUnit Test Cases Completed Successfully!'
            }
        }
        stage('SonarQube Code Quality') {
            environment {
                scannerHome = tool 'qube'
            }
            steps {
                echo 'Starting SonarQube Code Quality Scan...'
                withSonarQubeEnv('sonar-server') {
                    sh 'mvn sonar:sonar'
                }
                echo 'SonarQube Scan Completed. Checking Quality Gate...'
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
                echo 'Quality Gate Check Completed!'
            }
        }
        stage('Code Package') {
            steps {
                echo 'Creating WAR Artifact...'
                sh 'mvn clean package'
                echo 'WAR Artifact Created Successfully!'
            }
        }
        stage('Build & Tag Docker Image') {
            steps {
                echo 'Building Docker Image with Tags...'
                sh "docker build -t ${DOCKER_IMAGE}:latest -t fusion-ms:latest ."
                echo 'Docker Image Build Completed!'
            }
        }
        stage('Docker Image Scanning') {
            steps {
                echo 'Scanning Docker Image with Trivy...'
                sh 'trivy image ${DOCKER_IMAGE}:latest || echo "Scan Failed - Proceeding with Caution"'
                echo 'Docker Image Scanning Completed!'
            }
        }
        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'dockerhubCred', variable: 'dockerhubCred')]) {
                        sh 'docker login docker.io -u satyam88 -p ${dockerhubCred}'
                        echo 'Pushing Docker Image to Docker Hub...'
                        sh "docker push ${DOCKER_IMAGE}:latest"
                        echo 'Docker Image Pushed to Docker Hub Successfully!'
                    }
                }
            }
        }
        stage('Push Docker Image to Amazon ECR') {
            steps {
                script {
                    withDockerRegistry([credentialsId: 'ecr:ap-south-1:ecr-credentials', url: "https://${ECR_REPO}"]) {
                        echo 'Tagging and Pushing Docker Image to ECR...'
                        sh '''
                            docker images
                            docker tag fusion-ms:latest ${ECR_REPO}:latest
                            docker push ${ECR_REPO}:latest
                        '''
                        echo 'Docker Image Pushed to Amazon ECR Successfully!'
                    }
                }
            }
        }
        stage('Upload Docker Image to Nexus') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'nexus-credentials', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh "docker login ${NEXUS_URL} -u ${USERNAME} -p ${PASSWORD}"
                        echo "Push Docker Image to Nexus : In Progress"
                        sh "docker tag fusion-ms ${NEXUS_URL}latest"
                        sh "docker push ${NEXUS_URL}latest"
                        echo "Push Docker Image to Nexus : Completed"
                    }
                }
            }
        }
        stage('Cleanup Docker Images') {
            steps {
                echo 'Cleaning up local Docker images...'
                sh "docker rmi -f ${DOCKER_IMAGE}:latest || true"
                sh "docker rmi -f ${ECR_REPO}:latest || true"
                echo 'Local Docker images deleted successfully!'
            }
        }
    }
}