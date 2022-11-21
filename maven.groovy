/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def buildStep() {
    echo 'Source code compilation in progress.....'
    if(isUnix()) {
        echo 'Unix OS'
        sh "chmod +x mvnw"
        sh './mvnw clean compile -e'
    } else {
        echo 'Windows OS'
        bat 'mvnw clean compile -e'
    }
    echo '.....Source code compilation completed'
}
def sonarStep() {
    if(isUnix()) {
        echo 'Unix OS'
        sh './mvnw clean verify sonar:sonar \
             -Dsonar.projectKey=example-maven2'
    } else {
        echo 'Windows OS'
        bat 'mvnw clean verify sonar:sonar \
            -Dsonar.projectKey=example-maven2'
    }
    echo '.....Sonar scan completed'
}
def runAndTestStep() {
    echo 'Source code packaging in progress.....'
    if(isUnix()) {
        echo 'Unix OS'
        sh './mvnw spring-boot:run &'
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
        bat 'mvnw spring-boot:run &'
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
    pom = readMavenPom file: "pom.xml";
    files = findFiles(glob: "build/*.${pom.packaging}");
    echo """${files[0].name},
            ${files[0].path},
            ${files[0].directory},
            ${files[0].length},
            ${files[0].lastModified}"""
    artifactPath = files[0].path;
    artifactExists = fileExists artifactPath;
    if(artifactExists) {
        echo """File: ${artifactPath},
                          group: ${pom.groupId},
                          packaging: ${pom.packaging},
                          version ${pom.version}"""
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
                                [artifactId: pom.artifactId,
                                 groupId: pom.groupId,
                                 packaging: pom.packaging,
                                 version: pom.version]
                    ]
                ]
        )
        echo '.....Artifact Uploaded successfully'
    } else {
        error "File: ${artifactPath}, could not be found";
    }
}
def downloadAndTestNexusStep() {
    echo "Downloading artifact from nexus"
    pom = readMavenPom file: "pom.xml";
    groupId = pom.groupId;
    echo """${pom.groupId}""";
    groupIdPath = groupId.replace(".", "/");
    echo """${groupIdPath}""";
    sh """curl -X GET -u $USER:$PASS http://${env.NEXUS_SERVER}/repository/${env.NEXUS_REPOSITORY}/${groupIdPath}/${pom.artifactId}/${pom.version}/${pom.artifactId}-${pom.version}.${pom.packaging} -O"""
    echo ".....artifact downloaded successfully"
}
return this;