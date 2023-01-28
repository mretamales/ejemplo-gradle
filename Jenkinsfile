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
        }
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
        }
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
