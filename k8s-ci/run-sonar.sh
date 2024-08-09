env | grep -i -e SONAR -e GIT_BRANCH > env_file

#echo "======= FULL ENV ======="
#env
#echo "========================="
echo "======= FILTERED ENV ======="
cat env_file
echo "========================="

while getopts p: flag
do
    case "${flag}" in
        p) pr=${OPTARG};;
    esac
done

if [ -z $pr ]
  then
    echo "If you want to run sonar for PR pass '-p 1' else, if want to run sonar for master or release Tag pass '-p 0'"
else
  if [ $pr -eq 1 ]
    then
        GIT_PR_KEY=$(echo $PWD | rev | cut -d- -f1  | rev); echo "GIT_PR_KEY=$GIT_PR_KEY" >> .env;
        docker run --env-file env_file --rm -w /workspace -v "${PWD}:/workspace" -v "/.m2:/.m2" maven:3.8.6-eclipse-temurin-17 mvn -s settings.xml -Dformats=HTML,JSON,JUNIT -Dsonar.projectVersion=${GIT_COMMIT_ID} -Dsonar.projectKey=${APP_NAME} -Dsonar.projectName=${APP_NAME} -Dsonar.java.target=17 -Dsonar.java.source=17 -Dsonar.scm.disabled=true -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN} -Dsonar.pullrequest.branch=${GIT_BRANCH} -Dsonar.pullrequest.key=${GIT_PR_KEY} -Dsonar.externalIssuesReportPaths=./horusec_analysis.json sonar:sonar
    else
        docker run --env-file env_file --rm -w /workspace -v "${PWD}:/workspace" -v "/.m2:/.m2" maven:3.8.6-eclipse-temurin-17 mvn -s settings.xml -Dformats=HTML,JSON,JUNIT -Dsonar.projectVersion=${GIT_COMMIT_ID} -Dsonar.projectKey=${APP_NAME} -Dsonar.projectName=${APP_NAME} -Dsonar.java.target=17 -Dsonar.java.source=17 -Dsonar.scm.disabled=true -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_AUTH_TOKEN} -Dsonar.externalIssuesReportPaths=./horusec_analysis.json sonar:sonar
  fi
fi

