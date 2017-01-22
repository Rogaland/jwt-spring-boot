#!groovy

node {
    currentBuild.result = "SUCCESS"

    try {
        stage('checkout') {
            checkout scm
        }

        stage('build') {
            sh './mvnw clean install'
        }

        stage('deploy') {
        }
    }

    catch (err) {
        currentBuild.result = "FAILURE"
        throw err
    }
}