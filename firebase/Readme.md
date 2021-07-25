#Deployment to firebase
run "firebase deploy" after removing references to local dependencies in package.json under functions/

to update environment variables locally run "firebase functions:config:get > .runtimeconfig.json" in the function/  folder
