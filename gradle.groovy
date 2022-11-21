/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def buildStep() {
    if(isUnix()) {
        echo 'Unix OS'
        sh "chmod +x gradlew"
        sh './gradlew clean build'
    } else {
        echo 'Windows OS'
        bat 'gradlew clean build'
    }
}
def sonarStep() {
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
def runAndTestStep() {
    if(isUnix()) {
        echo 'Unix OS'
        echo 'Running in progress.....'
        sh './gradlew bootRun &'
        sh 'sleep 5'
        echo 'Testing in progress.....'
        def response = sh(script: "echo \$(curl --write-out '%{http_code}' --silent --output /dev/null http://localhost:8081/rest/mscovid/test?msg=testing)", returnStdout: true);
        if(response.trim() != '200') {
            echo "status ${response}"
            sh 'exit 1'
        }
        echo '.....Testing completed'
    } else {
        echo 'Windows OS'
        bat 'gradlew bootRun'
        bat 'timeout /t 5'
        response = bat """\$(curl --write-out '%{http_code}' --silent --output /dev/null 'http://localhost:8081/rest/mscovid/test?msg=testing')""";
        if (response.trim() != '200') {
            echo "status ${response}"
            bat 'exit /b 1'
        }
        echo '.....Testing completed'
    }
    echo '.....Running completed'
}
def uploadNexusStep() {
    echo 'Uploading to nexus in progress.....'
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
                                    [artifactId: 'DevOpsUsach2020',
                                     groupId: 'com.devopsusach2020',
                                     packaging: gradlePackaging,
                                     version: '0.0.1']
                        ]
                    ]
            )
            echo '.....Artifact Uploaded successfully'
        } else {
            error "File: ${artifactPath}, could not be found";
    }
}
def downloadAndTestNexusStep() {
    echo "Downloading artifact from nexus....."
    gradleBuild = readProperties file: "gradle.build";
    gradleVersion = '0.0.1';
    gradlePackaging = "jar";
    groupId = 'com.devopsusach2020';
    gradleArtifactId = 'DevOpsUsach2020';
    echo """${gradleBuild['group']}""";
    groupIdPath = groupId.replace(".", "/");
    echo """${groupIdPath}""";
    sh """curl -X GET -u $USER:$PASS http://${env.NEXUS_SERVER}/repository/${env.NEXUS_REPOSITORY}/${groupIdPath}/${gradleArtifactId}/${gradleVersion}/${gradleArtifactId}-${gradleVersion}.${gradlePackaging} -O"""
    echo ".....artifact downloaded successfully"
}
return this;