pipeline {
    agent any
     environment {
        NEXUS_INSTANCE_ID = "nexus"
        NEXUS_REPOSITORY = "devops-usach-nexus"
        NEXUS_SERVER = "nexus:8081"
    }
    stages {
        stage('Build & Test') {
            steps {
                echo 'Source code compilation in progress.....'
                script {
                    if(isUnix()) {
                        echo 'Unix OS'
                        sh './gradlew clean build'
                    } else {
                        echo 'Windows OS'
                        bat 'gradlew clean build'
                    }
                }
                echo '.....Source code compilation completed'
            }
        }
        stage('sonar') {
            steps {
                echo 'Sonar scan in progress.....'
                withSonarQubeEnv(credentialsId: '22f7a5b8-3425-4d58-a9e9-2326e6749326', installationName: 'sonarqube') {
                    script {
                        if(isUnix()) {
                            echo 'Unix OS'
                            sh './gradlew sonarqube \
                                 -Dsonar.projectKey=ejemplo-gradle'
                        } else {
                            echo 'Windows OS'
                            bat 'gradlew sonarqube \
                                -Dsonar.projectKey=ejemplo-gradle'
                        }
                        echo '.....Sonar scan completed'
                    }
                }
            }
        }
        stage('Run & Test') {
            steps {
                script {
                        if(isUnix()) {
                            echo 'Unix OS'
                            echo 'Running in progress.....'
                            sh './gradlew bootRun'
                            echo 'Testing in progress.....'
                            response = sh """\$(curl --write-out '%{http_code}' --silent --output /dev/null 'http://localhost:8081/rest/mscovid/test?msg=testing')""";
                            if (response != '200') {
                                sh 'exit 1'
                            }
                        } else {
                            echo 'Windows OS'
                            bat 'gradlew bootRun'
                            response= bat """\$(curl --write-out '%{http_code}' --silent --output /dev/null 'http://localhost:8081/rest/mscovid/test?msg=testing')""";
                            if (response != '200') {
                                bat 'exit 1'
                            }
                        }
                        echo '.....Sonar scan completed'
                }
            }
        }
        stage("nexus") {
            steps {
                echo 'Uploading to nexus in progress.....'
                script {
                    gradlePackaging = 'jar';
                    gradleBuild = readProperties file: "gradle.build";
                    files = findFiles(glob: "build/libs/*.${gradlePackaging}");
                    echo """${files[0].name},
                            ${files[0].path},
                            ${files[0].directory},
                            ${files[0].length},
                            ${files[0].lastModified}"""
                    artifactPath = files[0].path;
                    artifactExists = fileExists artifactPath;
                    if(artifactExists) {
                        echo """File: ${artifactPath},
                              group: ${gradleBuild['group']},
                              version ${gradleBuild['version']}"""
                        nexusPublisher(
                            nexusInstanceId: NEXUS_INSTANCE_ID,
                            nexusRepositoryId: NEXUS_REPOSITORY,
                            packages: [
                                [
                                    $class: 'MavenPackage',
                                    mavenAssetList: [
                                        [classifier: '',
                                        extension: '',
                                        filePath: artifactPath]],
                                    mavenCoordinate:
                                        [artifactId: gradleBuild['project']['name'],
                                        groupId: gradleBuild['group'],
                                        packaging: gradlePackaging,
                                        version: gradleBuild['version']]
                                 ]
                            ]
                        )
                    echo '.....Artifact Uploaded successfully'
                    } else {
                        error "File: ${artifactPath}, could not be found";
                    }
                }
            }
        }
        stage('download & test') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'acd50057-3abc-4c5b-a062-758a404e0bb9',
                                                  usernameVariable: 'USER', passwordVariable: 'PASS')]) {
                    script {
                        echo "Downloading artifact from nexus"
                        gradleBuild = readProperties file: "gradle.build";
                        gradleVersion = gradleBuild['version'];
                        gradlePackaging = "jar";
                        groupId = gradleBuild['group'];
                        gradleArtifactId = gradleBuild['project']['name'];
                        echo """${gradleBuild['group']}""";
                        groupIdPath = groupId.replace(".", "/");
                        echo """${groupIdPath}""";
                        sh """curl -X GET -u $USER:$PASS http://${env.NEXUS_SERVER}/repository/${env.NEXUS_REPOSITORY}/${groupIdPath}/${gradleArtifactId}/${gradleVersion}/${gradleArtifactId}-${gradleVersion}.${gradlePackaging} -O"""
                    }
                }
            }
        }
    }
 }