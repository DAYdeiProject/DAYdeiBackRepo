#!/usr/bin/env bash

PROJECT_ROOT="/home/ubuntu/app"
JAR_FILE="$PROJECT_ROOT/spring-webapp.jar"

DEPLOY_LOG="$PROJECT_ROOT/deploy.log"

TIME_NOW=$(date +%c)

# 현재 구동 중인 애플리케이션 pid 확인
CURRENT_PID=$(pgrep -f $JAR_FILE)

# 프로세스가 켜져 있으면 종료
if [ -z $CURRENT_PID ]; then
  echo "$TIME_NOW > 현재 실행중인 애플리케이션이 없습니다" >> $DEPLOY_LOG
else
  echo "$TIME_NOW > 실행중인 $CURRENT_PID 애플리케이션 종료 " >> $DEPLOY_LOG
  kill -15 $CURRENT_PID
fi

#ABSPATH=$(readlink -f $0)
#ABSDIR=$(dirname $ABSPATH)
#source ${ABSDIR}/profile.sh
#
#IDLE_PORT=$(find_idle_port)
#
#echo "> $IDLE_PORT 에서 구동중인 애플리케이션 pid 확인"
#IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})
#
#if [ -z ${IDLE_PID} ]
#then
#  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
#else
#  echo "> kill -15 $IDLE_PID"
#  kill -15 ${IDLE_PID}
#  sleep 5
#fi