#!/bin/sh
protoc actor.proto --java_out=../java/
protoc actor_plain.proto --java_out=../java/