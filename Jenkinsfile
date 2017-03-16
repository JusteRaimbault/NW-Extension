pipeline {

  agent any

  def SBT = "${tool name: 'sbt-0.13.13', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'}/bin/sbt -Dsbt.log.noformat=true"

  stages {
    stage('Test') {
      step([$class: 'GitHubSetCommitStatusBuilder'])
      checkout scm
      sh "${SBT} compile"
      sh "${SBT} test"
    }
  }

  post {
    always {
      step([
          $class: 'GitHubCommitStatusSetter',
          errorHandlers: [[$class: 'ShallowAnyErrorHandler']],
          statusResultSource: [
              $class: 'ConditionalStatusResultSource',
              results: [
                  [$class: 'BetterThanOrEqualBuildResult', result: 'SUCCESS', state: 'SUCCESS', message: 'Build succeeded'],
                  [$class: 'BetterThanOrEqualBuildResult', result: 'FAILURE', state: 'FAILURE', message: 'Build failed'],
                  [$class: 'AnyBuildResult', state: 'FAILURE', message: 'Build errored!']
              ]
          ]
      ])
    }
  }
}
