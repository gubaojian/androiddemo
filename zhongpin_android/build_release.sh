rm -rf *.apk
./gradlew assembleRelease
mv ./app/build/outputs/apk/release/*.apk  ./
