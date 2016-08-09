#!/bin/bash
gradle clean
gradle shadowJar
cp -f ./build/libs/*.jar ./
