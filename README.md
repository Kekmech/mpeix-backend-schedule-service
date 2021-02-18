# MpeiX Schedule microservice

MpeiX Android client is [here](https://github.com/tonykolomeytsev/mpeiapp).

## Development Setup

Setup development environment from [common](https://github.com/Kekmech/mpeix-backend-common) repository

### IntelliJ IDEA

Run `RunDev` configuration from folder `.dev`

### Gradle
Run `run` task
```bash
./gradlew -Dlogback.configurationFile=.dev/logback-dev.xml run --args="-config=.dev/application-dev.conf"
```

## License

mpeix-schedule is licensed under the MIT license.

See [LICENSE](LICENSE) for the full license text.
