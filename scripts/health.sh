#!/bin/bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)

echo "> Health Check Start!"
echo "> IDLE_PORT: $IDLE_PORT"
echo "> curl -s https://sparta-daln.shop:$IDLE_PORT/profile"
sleep 10

for RETRY_COUNT in {1..10}
do
  RESPONSE=$(curl -s https://sparta-daln.shop:${IDLE_PORT}/profile)
  UP_COUNT=$(echo ${RESPONSE} | grep 'prof' | wc -l)

  if [ ${UP_COUNT} -ge 1 ]
  then
      echo "> Health check 성공"
      switch_proxy
      sleep 3
      break
  else
      echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
      echo "> Health check: ${RESPONSE}"
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> Health check 실패. "
    echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done