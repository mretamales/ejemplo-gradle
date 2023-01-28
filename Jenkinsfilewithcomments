def gradle_script
def maven_script

pipeline {
    agent any
     environment {
        NEXUS_INSTANCE_ID = "nexus"
        NEXUS_REPOSITORY = "devops-usach-nexus"
        NEXUS_SERVER = "nexus:8081"
    }
    parameters {
        choice(name: 'Dependencies_Builder', choices: ['maven', 'gradle'], description: 'Select builder tool: ')
    }
    stages {
        stage('Load Scripts') {
            steps {
                script {
                    gradle_script = load 'gradle.groovy';
                    maven_script = load 'maven.groovy';
                }
            }
        }
        stage('Build & Test (Gradle)') {
            when {
                expression {
                    params.Dependencies_Builder == 'gradle'
                }
            }
            steps {
                script {
                    gradle_script.buildStep();
                }
            }
        }
        stage('Build & Test (Maven)') {
            when {
                expression {
                    params.Dependencies_Builder == 'maven'
                }
            }
            steps {
                script {
                    maven_script.buildStep();
                }
            }
        }"""
        stage('Sonar (Gradle)') {
            when {
                expression {
                    params.Dependencies_Builder == 'gradle'
                }
            }
            steps {
                withSonarQubeEnv(credentialsId: '22f7a5b8-3425-4d58-a9e9-2326e6749326', installationName: 'sonarqube') {
                    script {
                        gradle_script.sonarStep();
                    }
                }
            }
        }
        stage('Sonar (Maven)') {
            when {
                expression {
                    params.Dependencies_Builder == 'maven'
                }
            }
            steps {
                withSonarQubeEnv(credentialsId: '22f7a5b8-3425-4d58-a9e9-2326e6749326', installationName: 'sonarqube') {
                    script {
                        maven_script.sonarStep();
                    }
                }
            }
        }"""
        stage('Run & Test (Gradle)') {
            when {
                expression {
                    params.Dependencies_Builder == 'gradle'
                }
            }
            steps {
                script {
                    gradle_script.runAndTestStep();
                }
            }
        }
        stage('Run & Test (Maven)') {
            when {
                expression {
                    params.Dependencies_Builder == 'maven'
                }
            }
            steps {
                script {
                    maven_script.runAndTestStep();
                }
            }
        }"""
        stage('Upload Nexus (Gradle)') {
            when {
                expression {
                    params.Dependencies_Builder == 'gradle'
                }
            }
            steps {
                script {
                    gradle_script.uploadNexusStep();
                }
            }
        }
        stage('Upload Nexus (Maven)') {
            when {
                expression {
                    params.Dependencies_Builder == 'maven'
                }
            }
            steps {
                script {
                    maven_script.uploadNexusStep();
                }
            }
        }
        stage('Download & Test Nexus (Gradle)') {
            when {
                expression {
                    params.Dependencies_Builder == 'gradle'
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'acd50057-3abc-4c5b-a062-758a404e0bb9',
                        usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    script {
                        gradle_script.downloadAndTestNexusStep();
                    }
                }
            }
        }
        stage('Download & Test Nexus (Maven)') {
            when {
                expression {
                    params.Dependencies_Builder == 'maven'
                }
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'acd50057-3abc-4c5b-a062-758a404e0bb9',
                        usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    script {
                        maven_script.downloadAndTestNexusStep();
                    }
                }
            }
        }
        stage('notification') {
            steps {
               slackSend channel: 'C04A9BDSUFM', failOnError: true, message: "${env.CHANGE_AUTHOR} ${env.JOB_NAME} params.Dependencies_Builder"
               slackSend channel: 'C04A9BDSUFM', message: "${env.CHANGE_AUTHOR} ${env.JOB_NAME} params.Dependencies_Builder"
            }
        } """
        stage('Build Image (Maven)') {
            when {
                expression {
                    params.Dependencies_Builder == 'maven'
                }
            }
            steps {
                script {
                    maven_script.buildDockerImage();
                }
            }
        }
        stage('Push Image (Maven)') {
            when {
                expression {
                    params.Dependencies_Builder == 'maven'
                }
            }
            steps {
                withDockerRegistry([ credentialsId: "dockerhubaccount", url: "" ]) {
                    script {
                        maven_script.pushDockerImageToDockerHub();
                    }
                }
            }
        }
    }
 }
