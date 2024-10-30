pipeline {
    agent any

    parameters {

        separator(name: "GITHUB_CONFIGURATION", sectionHeader: "GITHUB CONFIGURATION");
        string(name: 'GITHUB_URL', defaultValue: "https://github.com/chuflay29/M7G2Tarea1.git", description: 'Github url project');
        string(name: 'GITHUB_BRANCH', defaultValue: 'Api2', description: 'Github branch name');
        
        separator(name: "BUILD_CONFIGURATION_ANGULAR", sectionHeader: "BUILD CONFIGURATION ANGULAR");
        string(name: 'SOLUTION_NAME', defaultValue: "M7Tarea1.sln", description: '.NET Solution');
        string(name: 'ANGULAR_PROJECT_FOLDER_NAME', defaultValue: "m7tarea1.client", description: 'Angular Web project folder');
        string(name: 'ANGULAR_PROJECT_FILE_NAME', defaultValue: "m7tarea1.client.esproj", description: 'Angular Web project name');
        string(name: 'OUTPUT_FOLDER', defaultValue: "OutputScripts", description: 'Output folder');
        choice(name: 'ENVIRONMENT', choices: ['Release','Debug'], description: 'Environment type');
        choice(name: 'RUN_NPM_INSTALL', choices: ['No','Yes'], description: 'Selector to run npm install');
        choice(name: 'COPY_WEB_CONFIG_FILE', choices: ['No','Yes'], description: 'Selector to copy web config file');
        string(name: 'ANGULAR_SITE_NAME', defaultValue: "WebVentas", description: 'Angular Web project site name');
        string(name: 'ANGULAR_SITE_PATH', defaultValue: "C:\\inetpub\\wwwroot\\WebVentas", description: 'Angular Web project site path');
        string(name: 'MSBUILD_PATH', defaultValue: "C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\MSBuild\\Current\\Bin\\MSBuild.exe", description: "Msbuild path");
        string(name: 'NODEJS_PATH', defaultValue: "C:\\Users\\User\\AppData\\Roaming\\nvm\\v20.13.1", description: 'NodeJS path');
    }

    stages {
        stage('Checkout Project') {
            steps {
                git(url: "${GITHUB_URL}", branch: "${GITHUB_BRANCH}")
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

        stage('Build Web Project') {
            steps {
                script {
                    String command = "& \"" + "${MSBUILD_PATH}" + "\" -t:Build -p:Configuration="+ "${ENVIRONMENT}" + " \"" + "${WORKSPACE}" + "\\" + "${ANGULAR_PROJECT_FOLDER_NAME}" + "\\" + "${ANGULAR_PROJECT_FILE_NAME}" + "\"";
                    println(command);

                    String output = powershell(returnStdout:true, script:command).trim();
                    println(output);

                    if ("${RUN_NPM_INSTALL}" == 'yes') {
                        command = "& Set-Location -Path " + "\"" +"${WORKSPACE}" + "\\" + "${ANGULAR_PROJECT_FOLDER_NAME}" + "\"" + ";" + " npm install ";
                        println(command);

                        output = powershell(returnStdout:true, script:command).trim();
                        println(output);
                    }
                    else {
                        println("npm install NOT executed.");
                    }

                    String configuration = "development";
                    if ("${ENVIRONMENT}" == 'Release') {
                        configuration = "production";
                    }

                    String outputFilePath = "${WORKSPACE}" + "\\" + "${OUTPUT_FOLDER}" + "\\" + "${BUILD_NUMBER}" + "\\" + "${ENVIRONMENT}" + "\\" + "${ANGULAR_PROJECT_FOLDER_NAME}";
                    println("outputFilePath: " + outputFilePath);

                    command = "& Set-Location -Path " + "\"" +"${WORKSPACE}" + "\\" + "${ANGULAR_PROJECT_FOLDER_NAME}" + "\"" + ";" + " ng build --configuration=" + configuration + " --output-path=" + outputFilePath;
                    println(command);

                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);
                }
            }
        }

        stage('Deploy Web Project') {
            steps {
                script {
                    String command = "Import-Module WebAdministration";
                    println(command);
                    String output = powershell(returnStdout:true, script:command).trim();
                    println(output);
                    
                    command = "Stop-WebSite -Name " + "\"" + "${ANGULAR_SITE_NAME}" + "\"";
                    println(command);
                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);

                    command = "Remove-Item -Path " + "\"" + "${ANGULAR_SITE_PATH}" + "\\*" + "\"" + " -Recurse -Force -Exclude web.config";
                    println(command);
                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);

                    String webBuildPath = "${WORKSPACE}" + "\\" + "${OUTPUT_FOLDER}" + "\\" + "${BUILD_NUMBER}" + "\\" + "${ENVIRONMENT}" + "\\" + "${ANGULAR_PROJECT_FOLDER_NAME}";
                    println("webBuildPath: " + webBuildPath);
                    command = "Copy-Item -Path " + " \"" + webBuildPath + "\\*" + "\"" + " -Destination "+ "\"" + "${ANGULAR_SITE_PATH}" + "\"" + " -Exclude browser";
                    println(command);
                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);

                    command = "Copy-Item -Path " + " \"" + webBuildPath + "\\browser\\*" + "\"" + " -Destination "+ "\"" + "${ANGULAR_SITE_PATH}" + "\"";
                    println(command);
                    output = powershell(returnStdout:true, script:command).trim();
                    println(output);

                    if ("${COPY_WEB_CONFIG_FILE}" == 'Yes') {
                        String webConfigPath= "${WORKSPACE}" + "\\" + "FE.web.config"; 
                        command = "Copy-Item -Path " + " \"" + webConfigPath + "\"" + " -Destination "+ "\"" + "${ANGULAR_SITE_PATH}" + "\\" + "web.config" + "\"" + " -Force";
                        println(command);
                        output = powershell(returnStdout:true, script:command).trim();
                        println(output);
                    } else {
                        println("web.config NOT copied");
                    }

                    command = "Start-WebSite -Name " + "\"" + "${ANGULAR_SITE_NAME}" + "\"";
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