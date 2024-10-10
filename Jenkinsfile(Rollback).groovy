pipeline {
    agent any

    parameters {
        booleanParam(name: 'ROLLBACK', defaultValue: false, description: 'Enable rollback to a specific commit')
        string(name: 'COMMIT_SHA', defaultValue: '', description: 'Commit SHA to rollback to (leave empty for the latest commit)')
    }

    stages {
        stage('Manual Approval for Rollback') {
            when {
                expression { params.ROLLBACK == true }
            }
            steps {
                script {
                    timeout(time: 5, unit: 'MINUTES') {
                        input message: "Are you sure you want to rollback to commit ${params.COMMIT_SHA}?", ok: 'Proceed with Rollback'
                    }
                }
            }
        }

        stage('Checkout') {
            steps {
                script {
                    if (params.ROLLBACK && params.COMMIT_SHA) {
                        // Rollback to the specified commit SHA
                        echo "Rolling back to commit: ${params.COMMIT_SHA}"
                        checkout([
                            $class: 'GitSCM', 
                            branches: [[name: params.COMMIT_SHA]], 
                            doGenerateSubmoduleConfigurations: false, 
                            extensions: [], 
                            submoduleCfg: [], 
                            userRemoteConfigs: [[url: 'https://github.com/your-repo.git']]
                        ])
                    } else {
                        // Default behavior: checkout the latest commit from the branch
                        echo "Deploying the latest commit"
                        checkout scm
                    }
                }
            }
        }

        stage('Build') {
            steps {
                echo "Building the application..."
                // Add your build steps here
            }
        }

        stage('Deploy') {
            steps {
                echo "Deploying the application..."
                // Add your deploy steps here
            }
        }

        stage('Post-Deployment') {
            steps {
                echo "Post-deployment steps..."
                // Add any post-deployment checks or notifications here
            }
        }
    }
}
