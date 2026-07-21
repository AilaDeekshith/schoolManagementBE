pipeline {
    agent any

    environment {
        AWS_REGION       = 'us-east-1'
        AWS_ACCOUNT_ID   = '063903862154'                 // TODO: replace with your account ID
        ECR_REPO         = 'schoolmanagementbe'
        IMAGE_TAG        = "${env.BUILD_NUMBER}"
        ECR_URI          = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPO}"
        ECS_CLUSTER      = 'schoolManagement-cluster'
        ECS_SERVICE      = 'schoolManagement-task-service-gnmqbd5o'
        TASK_FAMILY      = 'schoolManagement-task'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Unit Test') {
            steps {
                sh 'chmod +x ./gradlew'
                sh './gradlew clean test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh './gradlew bootJar -x test'
            }
        }

        stage('Build Docker Image') {
            steps {
                sh "docker build -t ${ECR_REPO}:${IMAGE_TAG} ."
            }
        }

        stage('Push to ECR') {
            steps {
                sh """
                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_URI}
                    docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_URI}:${IMAGE_TAG}
                    docker tag ${ECR_REPO}:${IMAGE_TAG} ${ECR_URI}:latest
                    docker push ${ECR_URI}:${IMAGE_TAG}
                    docker push ${ECR_URI}:latest
                """
            }
        }

        stage('Deploy to ECS') {
                    steps {
                        script {
                            // Fetch current task definition, swap in the new image, register a new revision
                            sh """
                                aws ecs describe-task-definition --task-definition ${TASK_FAMILY} \
                                  --region ${AWS_REGION} \
                                  --query 'taskDefinition' --output json > current-task-def.json

                                jq --arg IMAGE "${ECR_URI}:${IMAGE_TAG}" \
                                  '.containerDefinitions[0].image = \$IMAGE
                                   | del(.taskDefinitionArn, .revision, .status, .requiresAttributes,
                                         .compatibilities, .registeredAt, .registeredBy)' \
                                  current-task-def.json > new-task-def.json

                                aws ecs register-task-definition --region ${AWS_REGION} --cli-input-json file://new-task-def.json

                                aws ecs update-service \
                                  --region ${AWS_REGION} \
                                  --cluster ${ECS_CLUSTER} \
                                  --service ${ECS_SERVICE} \
                                  --task-definition ${TASK_FAMILY} \
                                  --force-new-deployment
                            """
                        }
                    }
                }

        stage('Verify Deployment') {
                    steps {
                        sh """
                            aws ecs wait services-stable --region ${AWS_REGION} --cluster ${ECS_CLUSTER} --services ${ECS_SERVICE}
                        """
                    }
        }
    }

    post {
        success {
            echo "Deployment succeeded: ${ECR_URI}:${IMAGE_TAG} is live on ECS."
        }
        failure {
            echo "Pipeline failed. Check the stage logs above."
        }
        always {
            sh 'docker system prune -f || true'
        }
    }
}