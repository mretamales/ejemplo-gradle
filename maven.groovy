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

return this;