$(document).ready(function() {
    var exhibitionList = $('#exhibitionData').data('exhibitionList');
    console.log('result : ' + exhibitionList);
    
    let itemsPerPage = 6; // 페이지당 아이템 개수
    let currentPage = 1;
    let exhibitionItems = $(".exhibition-item"); // 모든 전시 아이템 가져오기
    let totalPages = Math.ceil(exhibitionItems.length / itemsPerPage); // 전체 페이지 계산
    
    function showPage(page) {
        exhibitionItems.hide(); // 모든 아이템 숨기기
        let start = (page - 1) * itemsPerPage;
        let end = start + itemsPerPage;
        exhibitionItems.slice(start, end).fadeIn(); // 해당 페이지의 아이템 표시
    }

    function updatePaginationButtons() {
        $("#prevPage").toggle(currentPage > 1);
        $("#nextPage").toggle(currentPage < totalPages);
    }

    $("#prevPage").click(function () {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
            updatePaginationButtons();
        }
    });
    
    $("#nextPage").click(function () {
        if (currentPage < totalPages) {
            currentPage++;
            showPage(currentPage);
            updatePaginationButtons();
        }
    });

    // 초기 로딩
    showPage(currentPage);
    updatePaginationButtons();
});