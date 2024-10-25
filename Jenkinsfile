pipeline {

	agent any

    options {
        buildDiscarder(logRotator(numToKeepStr: '3', artifactNumToKeepStr: '3'))
    }

    tools {
        maven 'maven_3.9.4'
    }

    stages {
        stage('Code Compilation') {
            steps {
                echo 'Code Compilation is In Progress!'
                sh 'mvn clean compile'
                echo 'Code Compilation is Completed Successfully!'
            }
        }
        stage('Code QA Execution') {
            steps {
                echo 'JUnit Test Case Check in Progress!'
                sh 'mvn clean test'
                echo 'JUnit Test Case Check Completed!'
            }
        }
        stage('Code Package') {
            steps {
                echo 'Creating WAR Artifact'
                sh 'mvn clean package'
                echo 'Artifact Creation Completed'
            }
        }
        stage('Building & Tag Docker Image') {
            steps {
                echo "Starting Building Docker Image"
                sh "docker build -t fusion-ms ."
                echo 'Docker Image Build Completed'
            }
        }
        stage('Docker Image Scanning') {
            steps {
                echo 'Docker Image Scanning Started'
                sh 'docker --version'
                echo 'Docker Image Scanning Started'
            }
        }
        stage('Docker push to Docker Hub') {
            steps {
                script {
                    withDockerRegistry([credentialsId: 'docker.io', url: 'https://index.docker.io/v1/', credentials: [$class: 'UsernamePasswordMultiBinding', credentialsId: "${DOCKER_HUB_CRED}", usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD']]) {
                        echo "Push Docker Image to DockerHub: In Progress"
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        sh "docker push ${imageName}"
                        echo "Push Docker Image to DockerHub: Completed"
                    }
                }
            }
        }
        stage('Docker Image Push to Amazon ECR') {
            steps {
                script {
                    echo "Tagging the Docker Image: In Progress"
                    def ecrImageName = "559220132560.dkr.ecr.ap-south-1.amazonaws.com/booking.com:dev-booking-v.1.${BUILD_NUMBER}"
                    sh "docker tag ${imageName} ${ecrImageName}"
                    echo "Tagging the Docker Image: Completed"

                    withDockerRegistry([credentialsId: 'ecr:ap-south-1:ecr-credentials', url: "https://559220132560.dkr.ecr.ap-south-1.amazonaws.com"]) {
                        echo "Push Docker Image to ECR: In Progress"
                        sh "docker push ${ecrImageName}"
                        echo "Push Docker Image to ECR: Completed"
                    }
                }
            }
        }
    }
}