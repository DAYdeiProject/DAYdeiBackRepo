#!/bin/bash
#
#ABSPATH=$(readlink -f $0)
#ABSDIR=$(dirname $ABSPATH)
#source ${ABSDIR}/profile.sh
#source ${ABSDIR}/switch.sh
#
#IDLE_PORT=$(find_idle_port)
#
#echo "> Health Check Start!"
#echo "> IDLE_PORT: $IDLE_PORT"
#echo "> curl -s https://sparta-daln.shop:$IDLE_PORT/profile"
#sleep 10
#
#for RETRY_COUNT in {1..10}
#do
#  RESPONSE=$(curl -s https://sparta-daln.shop:${IDLE_PORT}/profile)
#  UP_COUNT=$(echo ${RESPONSE} | grep 'prof' | wc -l)
#
#  if [ ${UP_COUNT} -ge 1 ]
#  then
#      echo "> Health check 성공"
#      switch_proxy
#      sleep 3
#      break
#  else
#      echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
#      echo "> Health check: ${RESPONSE}"
#  fi
#
#  if [ ${RETRY_COUNT} -eq 10 ]
#  then
#    echo "> Health check 실패. "
#    echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
#    exit 1
#  fi
#
#  echo "> Health check 연결 실패. 재시도..."
#  sleep 10
#done



# service_url.inc 에서 현재 서비스를 하고 있는 WAS의 포트 번호 가져오기
CURRENT_PORT=$(cat /home/ubuntu/service_url.inc | grep -Po '[0-9]+' | tail -1)
TARGET_PORT=0

if [ ${CURRENT_PORT} -eq 8081 ]; then
    TARGET_PORT=8082
elif [ ${CURRENT_PORT} -eq 8082 ]; then
    TARGET_PORT=8081
else
    echo "> No WAS is connected to nginx"
    exit 1
fi

# 위 커맨드들을 통해 현재 타겟포트 가져오기

echo "> Start health check of WAS at 'http://127.0.0.1:${TARGET_PORT}' ..."

# 아래 커맨드들을 새로 열린 서버가 정상적으로 작동하는지 확인

# 해당 커맨드들을 10번씩 반복
for RETRY_COUNT in 1 2 3
do
    echo "> #${RETRY_COUNT} trying..."
    # 테스트할 API 주소를 통해 http 상태 코드 가져오기                                               /api/maininfo
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}"  http://127.0.0.1:${TARGET_PORT}/profile)

	# RESPONSE_CODE의 http 상태가 200번인 경우
    if [ ${RESPONSE_CODE} -eq 200 ]; then
        echo "> New WAS successfully running"
        exit 0
    elif [ ${RETRY_COUNT} -eq 10 ]; then
        echo "> Health check failed."
        exit 1
    fi
    # 아직 열려있지 않았다면 sleep
    sleep 15
done