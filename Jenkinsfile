def gradle_script
pipeline {
    agent any
     environment {
        NEXUS_INSTANCE_ID = "nexus"
        NEXUS_REPOSITORY = "devops-usach-nexus"
        NEXUS_SERVER = "nexus:8081"
    }
    parameters {
        choice(name: 'Dependencies builder', choices: ['Maven', 'Gradle'], description: 'Pick builder tool')
    }
    stages {
        stage('Load Scripts') {
            steps {
                gradle_script = load 'gradle.groovy';
            }
        }
        stage('Build & Test') {
            when {
                expression{
                    params.name == 'Gradle';
                }
            }
            steps {
                script {
                    gradle_script.callGradlePipeline();
                }
            }
        }
    }
 }