How to build using maven:
1. download https://github.com/mosabua/maven-android-sdk-deployer/
2. build an artefact of android sdk: mvn install -P 4.2
3. run maven build of lj-menu-demo


DEPLOYMENT

Prerequisites:
- the target device must be rooted
- the option to allow installation of apps from unknown sources must be enabled in Security settings

Steps:
1. copy system-controller.apk to /system/app
2. reboot device
3. install menu.apk
4. launch Menu application
5. superuser request will be shown -> grant the root permissions for the system-controller FOREVER (needed to prevent superuser requests in the future)
