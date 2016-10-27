# Fairphone 2 module tester
Android app that allows users to test the modules and their components inside the Fairphone 2.

```
FP2
|-- Display module
|   |-- Display
|   `-- Digitizer
|-- Battery
|-- Receiver module
|   |-- Headphone jack
|   |-- Proximity sensor
|   |-- LED
|   |-- Ear speaker
|   `-- Front camera
|-- Camera module
|   |-- Rear camera
|   `-- Flash
|-- Speaker module
|   |-- Vibration motor
|   |-- Rear speaker
|   |-- Microphone
|   `-- USB port
`-- Tranceiver module
    |-- SIM slots
    |-- microSD slot
    |-- Hardware buttons
    |-- GPS
    `-- Gyroscope
```
(incomplete)

## Versioning scheme

The app version (a.k.a Version Code) is based on the following scheme:

* Major version number
* Minor version number
* Release status number (snapshot/release candidate/public release)

The rules are as follow:

* The major version uses two digits
* The minor version uses two digits
* The release status uses two digits
* The major version should be bumped when introducing new features
* The minor version should be bumped whenever there is a (simple/hot) fix
* The release status must be 0 while developing (snapshot)
* The release status must be in [1;98] while testing releases (release candidate)
* The release status must be 99 for a public release

The readable app version (a.k.a Version Name) is based on the app version with the following schemes:

* Snapshots are named after <major version>.<minor version>-SNAPSHOT
* Release candidates are named after <major version>.<minor version>-RC<release status>
* Public releases are named after <major version>.<minor version>
