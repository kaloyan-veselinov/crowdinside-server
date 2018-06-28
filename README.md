# crowdinside-server

This repository contains a collection of indoor localization modules described in CrowdInside \[1] and UPTIME \[2].

## Modules

All relevant JARs are located in the `jars` directory.

### Data Exporter

This module parses the raw data produced by the Android application and exports:
- raw data for each sensor in separate CSV files
- a CSV file with aggregated data

#### Usage
```
java -jar data-exporter.jar dataset.JSON 1000 walk
```
Specifies both aggregation time and the user's gait (used in the training phase for ML).

```
java -jar data-exporter.jar dataset.JSON 1000 
```
Specifies only aggregation time (when labels are useless)

```
java -jar data-exporter.jar dataset.JSON
```
Uses default aggregation time (500 ms) and no labeling.

### Step Detector

This modules identifies steps and the user's gait during each step.

#### Usage

```
java -jar step-detector.jar dataset.JSON
```

The program outputs to the console the gait for each identified step.

### Anchor Point Extraction

This module detects anchor points (elevator, standing, walking, stairs).

#### Usage

```
java -jar inertial-anchor.jar dataset.JSON
```

Outputs to the console the detected anchor point for each aggregation time

## References

\[1] Alzantot, Moustafa, and Moustafa Youssef. "Crowdinside: automatic construction of indoor floorplans." Proceedings of the 20th International Conference on Advances in Geographic Information Systems. ACM, 2012. 

\[2] Alzantot, Moustafa, and Moustafa Youssef. "UPTIME: Ubiquitous pedestrian tracking using mobile phones." Wireless Communications and Networking Conference (WCNC), 2012 IEEE. IEEE, 2012.
