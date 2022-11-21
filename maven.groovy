/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def callMavenPipeline() {
    if(isUnix()) {
        echo 'Unix OS'
        sh "chmod +x mvnw"
        sh './mvnw clean compile -e'
    } else {
        echo 'Windows OS'
        bat 'mvnw clean compile -e'
    }
}

return this;