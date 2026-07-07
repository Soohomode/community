import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 10,        // 동시 접속 가상 사용자 10명
    duration: '30s', // 30초 동안 실행
};

export default function () {
    const res = http.get('http://localhost:8080/api/posts?page=0&size=10&sort=latest');

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(0.5);
}