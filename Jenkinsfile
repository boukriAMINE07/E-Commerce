def mvn(String args, String dir) {
    sh "mvn ${args} -f ${dir}/pom.xml"
}

def dockerBuildPush(String dir) {
    def app_version = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout -f ${dir}/pom.xml", returnStdout: true).trim()
    def app_name = sh(script: "mvn help:evaluate -Dexpression=project.artifactId -q -DforceStdout -f ${dir}/pom.xml", returnStdout: true).trim()
    def image_name = "mouradtals/${app_name}:${app_version}"
    sh "docker build -t ${image_name} ${dir}"
    sh "docker push ${image_name}"
}

pipeline {
    agent any

    tools {
        maven 'MAVEN_HOME'
    }

    stages {
        stage('Pull code from repository') {
            steps {
                checkout scm
            }
        }

        stage('Build project') {
            steps {
                mvn 'clean install -DskipTests', '.'
            }
        }

        stage('SonarQube analysis') {
            steps {
                script {
                    def scannerHome = tool 'SonarQube';
                        withSonarQubeEnv('SonarServer') {
                            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ecommerce -Dsonar.projectName='ecommerce' -Dsonar.exclusions=**/*.java"
                        }
                }
            }
        }

        stage('Wait for SonarQube analysis to complete') {
            steps {
                waitForQualityGate abortPipeline: true
            }
        }

        stage('Build Docker images') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'DOCKER_CREDS', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
                        sh "docker login -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
                        dockerBuildPush('.')
                    }
                }
            }
        }


        stage('Deploy using Docker Compose') {
            steps {
                sh "docker-compose -f ${env.WORKSPACE}/docker-compose.yml up -d"
            }
        }
    }
}