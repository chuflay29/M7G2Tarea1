pipeline {
    agent any

    parameters {

        separator(name: "GITHUB_CONFIGURATION", sectionHeader: "GITHUB CONFIGURATION");
        string(name: 'GITHUB_URL', defaultValue: "https://github.com/chuflay29/M7G2Tarea1.git", description: 'Github url project');
        string(name: 'GITHUB_BRANCH', defaultValue: 'Api2', description: 'Github branch name');

        separator(name: "BUILD_CONFIGURATION_API", sectionHeader: "BUILD CONFIGURATION API");
        string(name: 'DOTNET_VERSION', defaultValue: "8.0", description: '.NET version');
        string(name: 'SOLUTION_NAME', defaultValue: "M7Tarea1.sln", description: '.NET Solution');
        string(name: 'API_PROJECT_FOLDER_NAME', defaultValue: "M7Tarea1.Server", description: 'API project .NET folder');
        string(name: 'API_PROJECT_FILE_NAME', defaultValue: "M7Tarea1.Server.csproj", description: 'API project .NET');
        string(name: 'OUTPUT_FOLDER', defaultValue: "OutputScripts", description: 'Output folder');
        choice(name: 'ENVIRONMENT', choices: ['Release','Debug'], description: 'Environment type');
        choice(name: 'RESTORE_NUGGETS', choices: ['No','Yes'], description: 'Selector to run restore nuggets');
        choice(name: 'COPY_WEB_CONFIG_FILE', choices: ['No','Yes'], description: 'Selector to copy web config file');
        string(name: 'API_SITE_NAME', defaultValue: "ApiVentas", description: 'Api site name');
        string(name: 'API_SITE_PATH', defaultValue: "C:\\inetpub\\wwwroot\\ApiVentas", description: 'Api site Path');
        string(name: 'MSBUILD_PATH', defaultValue: "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\MSBuild\\Current\\Bin\\MSBuild.exe", description: "Msbuild path");
    }

    stages {
        stage('Checkout Project') {
            steps {
                git(url: "${GITHUB_URL}", branch: "${GITHUB_BRANCH}")
            }
        }

        stage('Restore Nuggets') {
            steps {
                script {
                    if ("${RESTORE_NUGGETS}" == 'Yes') {

                        String projectFilePath = "${WORKSPACE}" + "\\" + "${API_PROJECT_FOLDER_NAME}" + "\\" + "${API_PROJECT_FILE_NAME}";
                        println("projectFilePath: " + projectFilePath);

                        String command = "& \"" + "${MSBUILD_PATH}" + "\" -t:Restore" + " \"" + projectFilePath + "\"";
                        println(command);

                        String output = powershell(returnStdout:true, script:command).trim();
                        println(output);
                    }
                    else {
                        println("Restore Nuggets NOT executed");
                    }
                }
            }
        }

        stage('Create Output Folder'){
            steps {
                script {
                    String OUTPUT_FOLDER = "${WORKSPACE}" + "\\" + "${OUTPUT_FOLDER}" + "\\" + "${BUILD_NUMBER}" + "\\" + "${ENVIRONMENT}";
                    println("OUTPUT_FOLDER" + OUTPUT_FOLDER);

                    if(!fileExists(OUTPUT_FOLDER)){
                        String command = "New-Item -ItemType Directory -Force -Path " + OUTPUT_FOLDER;
                        String output = powershell(returnStdout:true, script:command).trim();
                        println(output);
                    }
                }
            }
        }

        stage('Build API Project') {
            steps {
                script {
                    String projectFilePath = "${WORKSPACE}" + "\\" + "${API_PROJECT_FOLDER_NAME}" + "\\" + "${API_PROJECT_FILE_NAME}";
                    println("projectFilePath: " + projectFilePath);

                    String outputFilePath = "${WORKSPACE}" + "\\" + "${OUTPUT_FOLDER}" + "\\" + "${BUILD_NUMBER}" + "\\" + "${ENVIRONMENT}" + "\\" + "${API_PROJECT_FOLDER_NAME}";
                    println("outputFilePath: " + outputFilePath);

                    String command = "& \"" + "${MSBUILD_PATH}" + "\" -t:Build -p:TargetFrameworkVersion=v" + "${DOTNET_VERSION}" + " -p:Configuration=" + "${ENVIRONMENT}" + " \"" + projectFilePath + "\"" + " -p:OutputPath=" + outputFilePath;
                    println(command);

                    String output = powershell(returnStdout:true, script:command).trim();
                    println(output);
                }
            }
        }

        stage('Deploy API Project') {
            steps {
                script {


                    String command = "Import-Module WebAdministration";
                    println(command);
                    String output = powershell(returnStdout:true, script:command).trim();
                    println(output);
                    
                    command = "Stop-WebSite -Name " + "\"" + "${API_SITE_NAME}" + "\"";
                    println(command);
                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);

                    command = "Remove-Item -Path " + "\"" + "${API_SITE_PATH}" + "\\*" + "\"" + " -Recurse -Force -Exclude web.config";
                    println(command);
                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);

                    String apiBuildPath = "${WORKSPACE}" + "\\" + "${OUTPUT_FOLDER}" + "\\" + "${BUILD_NUMBER}" + "\\" + "${ENVIRONMENT}" + "\\" + "${API_PROJECT_FOLDER_NAME}";
                    println("apiBuildPath: " + apiBuildPath);
                    command = "Copy-Item -Path " + " \"" + apiBuildPath + "\\*" + "\"" + " -Destination "+ "\"" + "${API_SITE_PATH}" + "\"" + " -Recurse -Container";
                    println(command);
                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);

                    if ("${COPY_WEB_CONFIG_FILE}" == 'Yes') {
                        String webConfigPath= "${WORKSPACE}" + "\\" + "BE.web.config"; 
                        command = "Copy-Item -Path " + " \"" + webConfigPath + "\"" + " -Destination "+ "\"" + "${API_SITE_PATH}" + "\\" + "web.config" +"\"" + " -Force";
                        println(command);
                        output = powershell(returnStdout:true, script:command).trim();
                        println(output);
                    } else {
                        println("web.config NOT copied");
                    }

                    command = "Start-WebSite -Name " + "\"" + "${API_SITE_NAME}" + "\"";
                    println(command);
                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);
                }
            }
        }

        stage('Push artifacts') {
            steps {
                script {
                    def timestamp = new java.text.SimpleDateFormat('yyyMMddhhmmss');
                    String today = timestamp.format(new Date());
                    String filesPath = "${OUTPUT_FOLDER}" + "\\" + "${BUILD_NUMBER}";
                    String outputFilePath = "${WORKSPACE}" + "\\" + "${OUTPUT_FOLDER}" + "\\" + "${BUILD_NUMBER}" + "\\" + "${ENVIRONMENT}";
                    String outputPath = outputFilePath + "\\" + today + "_" + "${BUILD_NUMBER}" + "_outputfiles.zip";
                    String artifactPath = "${OUTPUT_FOLDER}" + "\\" + "${BUILD_NUMBER}" + "\\" + "${ENVIRONMENT}" + "\\" + today + "_" + "${BUILD_NUMBER}" + "_outputFiles.zip";

                    zip zipFile: outputPath, archive: false, dir: filesPath;
                    archiveArtifacts artifacts: artifactPath, caseSensitive:false;
                }
            }
        }
    }
}