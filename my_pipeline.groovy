#!/usr/bin/env groovy

final def pipelineSdkVersion = 'master'

pipeline {
    agent any
    options {
        timeout(time: 120, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
        skipDefaultCheckout()
    }
    stages {
        stage('Init') {
            steps {
                library "s4sdk-pipeline-library@${pipelineSdkVersion}"
                stageInitS4sdkPipeline script: this
                abortOldBuilds script: this
            }
        }

        stage('Build') {
            parallel {
                stage("Backend") { steps { stageBuildBackend script: this } }
                stage("Frontend") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.FRONT_END_BUILD } }
                    steps { stageBuildFrontend script: this }
                }
            }
        }

        stage('Local Tests') {
            parallel {
                stage("Static Code Checks") { steps { stageStaticCodeChecks script: this } }
                stage("Backend Unit Tests") { steps { stageUnitTests script: this } }
                stage("Backend Integration Tests") { steps { stageIntegrationTests script: this } }
                stage("Frontend Unit Tests") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.FRONT_END_TESTS } }
                    steps { stageFrontendUnitTests script: this }
                }
                stage("Node Security Platform Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.NODE_SECURITY_SCAN } }
                    steps { stageNodeSecurityPlatform script: this }
                }
            }
        }
}
