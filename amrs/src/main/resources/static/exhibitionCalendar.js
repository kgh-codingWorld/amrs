// html 로드 시 DOMContentLoaded 이벤트 실행
document.addEventListener("DOMContentLoaded", function() {
        const calendar = document.getElementById("calendar");
        const calendarMonth = document.getElementById("calendarMonth");

        let currentMonth = new Date().getMonth();
        let currentYear = new Date().getFullYear();
		const today = new Date();

		// 초기 달력 생성
        function renderCalendar() {
        	console.log("Rendering calendar for:", currentYear, currentMonth);
            calendar.innerHTML = ""; // 기존 달력 초기화
            
            // 달력에 표시할 월의 첫 날과 마지막 날짜 가져오기
            const firstDay = new Date(currentYear, currentMonth, 1).getDay(); // 첫 날의 요일(0~6)
            const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate(); // 해당 월의 총 일 수

            // 현재 월과 연도 표시
            const formattedMonth = String(currentMonth + 1).padStart(2, '0'); // 두 자리 숫자로 변환
            calendarMonth.innerText = `${currentYear}년${formattedMonth}월`;
            
            // 이전 달의 빈 칸 채우기
            for (let i = 0; i < firstDay; i++) {
                const emptyCell = document.createElement("div");
                emptyCell.className = "empty";
                calendar.appendChild(emptyCell);
            }

            // 현재 월의 날짜 채우기
            for (let day = 1; day <= daysInMonth; day++) {
                const dayCell = document.createElement("div");
                dayCell.className = "day";
                dayCell.innerText = day;
                
                const dateObj = new Date(currentYear, currentMonth, day);
                
                // 오늘보다 과거 날짜인 경우 클릭 비활성화
	            if (dateObj < today.setHours(0, 0, 0, 0)) {
	                dayCell.classList.add("disabled"); // 비활성화 스타일 적용
	            } else {
	                dayCell.onclick = () => selectDate(day); // 날짜 클릭 이벤트 추가
	            }
                
                calendar.appendChild(dayCell);
            }
        }
        
        // 날짜 선택 시 동작
        function selectDate(day) {
			console.log("  >  "+calendarMonth.innerText );
			console.log('day: ', day);
			
			// 두 자리 형식으로 변환
			const formattedDay = day.toString().padStart(2,'0');
			
			// 'yyyy-MM-dd' 형식으로 변환
			let yyyymmdd = calendarMonth.innerText.replace("년", "-").replace("월","").trim();
			yyyymmdd = `${yyyymmdd}-${formattedDay}`;
			
			// reservDate 입력 필드를 querySelector로 직접 선택
			// 예약 날짜 입력 필드에 값 설정
			const reservDateInput = document.querySelector("input[name='reservDate']");
			if (reservDateInput) {
				reservDateInput.value = yyyymmdd; // ex) 2025-02-13
			}
			else {
				console.error("reservDate 입력 필드를 찾을 수 없습니다.")
			}
            // 이전 선택 초기화
            document.querySelectorAll(".day.selected").forEach(el => el.classList.remove("selected"));
            
            // 선택한 날짜 강조
            const selectedDay = Array.from(calendar.children).find(el => el.innerText == day);
            if (selectedDay) {
            	selectedDay.classList.add("selected");
            }
            

        }

        function prevMonth() {
            currentMonth = currentMonth === 0 ? 11 : currentMonth - 1;
            currentYear = currentMonth === 11 ? currentYear - 1 : currentYear;
            renderCalendar();
        }

        function nextMonth() {
            currentMonth = currentMonth === 11 ? 0 : currentMonth + 1;
            currentYear = currentMonth === 0 ? currentYear + 1 : currentYear;
            renderCalendar();
        }

        // 초기화 시 달력 렌더링
        renderCalendar();

        // 이벤트 핸들러 연결
        document.querySelector(".prev-month-btn").onclick = prevMonth;
        document.querySelector(".next-month-btn").onclick = nextMonth;
    });