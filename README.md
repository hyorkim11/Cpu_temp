# Cpu_Temp

## Synopsis
This is an Android project built for a university research.
The application scans the device and return a few basic information about the device's temperature information.

## Explanation
"CpuInfo.java" implements the SensorEventListener to capture the temperature values.
"MainActivity.java" is a simple testing implementation to pull the classes together.
"ScanPath.java" is a manual url parser to check the device's built-in internal urls for system sensor values.
"TempService.java" is a service class that fetches the sensor values from the device every second (default)

## Installation
Simply copy the .java files into your Android project.

## Dependencies
This project has no external dependencies
