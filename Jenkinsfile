def gradle_script
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
                }
            }
        }
        stage('Build & Test') {
            when {
                expression {
                    params.Dependencies_Builder == 'gradle'
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