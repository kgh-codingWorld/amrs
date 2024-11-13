$(document).ready(function() {
    var exhibitionList = $('#exhibitionData').data('exhibitionlist');
    console.log('result : ' + exhibitionList);
});


/*
var numOfRows = 10;  // 한 번의 요청에서 가져올 데이터 수
var pageNo = 1;      // 요청할 페이지 번호

var xhr = new XMLHttpRequest();
var url = 'http://localhost/api/exhibitionList';  // JSON 응답을 반환하는 API 엔드포인트

// 백엔드에서 numOfRows와 pageNo를 처리할 수 있으므로 URL에 추가하지 않음
xhr.open('GET', url + '?numOfRows=' + numOfRows + '&pageNo=' + pageNo);  // queryParams 포함

xhr.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
        var resultDiv = document.getElementById('result');
        resultDiv.innerHTML = '';

        console.log("응답 데이터:", this.responseText);

        try {
            var responseData = JSON.parse(this.responseText);
            console.log("파싱된 응답 데이터:", responseData);

            // 응답 데이터에서 resultCode와 resultMsg를 올바른 경로로 추출
            var resultCode = responseData.response.header.resultCode || "결과 코드 없음";
            var resultMsg = responseData.response.header.resultMsg || "결과 메시지 없음";

            resultDiv.innerHTML += '<p>결과 코드: ' + resultCode + '</p>';
            resultDiv.innerHTML += '<p>결과 메시지: ' + resultMsg + '</p>';

            // items 배열 추출 경로 수정: response.body.items.item
            var items = responseData.response.body.items.item;

            if (items && Array.isArray(items)) {
                items.forEach(function(item) {
                    var title = item.TITLE || "제목 없음";
                    var collectedDate = item.COLLECTED_DATE || "날짜 없음";
                    var description = item.DESCRIPTION || "설명 없음";

                    resultDiv.innerHTML += '<h2>제목: ' + title + '</h2>';
                    resultDiv.innerHTML += '<p>수집 날짜: ' + collectedDate + '</p>';
                    resultDiv.innerHTML += '<p>설명: ' + description + '</p>';
                    resultDiv.innerHTML += '<hr>';
                });
            } else {
                resultDiv.innerHTML += '<p>아이템이 존재하지 않거나 올바르지 않은 형식입니다.</p>';
            }
        } catch (e) {
            console.error("응답 처리 중 오류 발생:", e);
            resultDiv.innerHTML += '<p>응답 처리 중 오류가 발생했습니다.</p>';
        }
    }
};

xhr.send();
*/
