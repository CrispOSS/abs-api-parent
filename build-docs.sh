#!/bin/bash

cd abs-api-docs/
mvn clean compile
cd ..
mv abs-api-docs/target/docs/reference/* docs/
