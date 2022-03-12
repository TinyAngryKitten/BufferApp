#Deployment to firebase
first run gradle build, then run "firebase deploy" after removing references to local dependencies in package.json under functions/

to update environment variables locally run "firebase functions:config:get > .runtimeconfig.json" in the function/  folder

to set environment variables, run "firebase functions:config:set accounts.some_account=somevalue"