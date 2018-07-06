#!/usr/bin/env groovy 
node {
    deleteDir()
    //def rootDir = pwd()
    //load "${rootDir}/my_pipeline.groovy"
    sh "git clone --depth 1 https://github.com/ritu2593/SAP-library.git pipelines"
   load './pipelines/s4sdk-pipeline.groovy'
     
}
