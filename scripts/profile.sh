#!/usr/bin/env bash

function find_idle_profile()
{
    RESPONSE_CODE=$(sudo curl -s -o /dev/null -w "%{http_code}" https://sparta-daln.shop/)

    if [ ${RESPONSE_CODE} -ge 400 ] # 400 보다 크면 (즉, 40x/50x 에러 모두 포함)
    then
        CURRENT_PROFILE=real2
    else
        CURRENT_PROFILE=$(sudo curl -s https://sparta-daln.shop/)
    fi

    if [ ${CURRENT_PROFILE} == real1 ]
    then
      IDLE_PROFILE=real2
    else
      IDLE_PROFILE=real1
    fi

    echo "${IDLE_PROFILE}"    # bash script는 return 기능이 없기 떄문에 echo를 통해서 출력하면 이 값을 클라이언트가 사용할 수 있습니다.
}
# 쉬고 있는 profile의 port 찾기
function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == real1 ]
    then
      echo "8081"   # 여기도 마찬가지로 return 기능의 느낌
    else
      echo "8082"   # 여기도 마찬가지로 return 기능의 느낌
    fi
}