document.addEventListener("DOMContentLoaded", function() {
        const calendar = document.getElementById("calendar");
        const calendarMonth = document.getElementById("calendarMonth");
        const bookingTimesContainer = document.getElementById("bookingTimes");

        let currentMonth = new Date().getMonth();
        let currentYear = new Date().getFullYear();

        function renderCalendar() {
        	console.log("Rendering calendar for:", currentYear, currentMonth);
            // 달력 내용 초기화
            calendar.innerHTML = "";
            
            // 달력에 표시할 월의 첫 날과 마지막 날짜 가져오기
            const firstDay = new Date(currentYear, currentMonth, 1).getDay();
            const daysInMonth = new Date(currentYear, currentMonth + 1, 0).getDate();

            // 현재 월과 연도 표시
            calendarMonth.innerText = new Date(currentYear, currentMonth).toLocaleString('ko-KR', { month: 'long', year: 'numeric' });
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
                dayCell.onclick = () => selectDate(day);
                calendar.appendChild(dayCell);
            }
        }
        function selectDate(day) {
			console.log("  >  "+calendarMonth.innerText );
			console.log('day: ', day);
			
			// 'yyyy-MM-dd' 형식으로 변환
			var yyyymmdd = calendarMonth.innerText + day ;
			yyyymmdd = yyyymmdd.replace("년","-").replace("월","-").replace(" ","");
			
			// reservDate 입력 필드를 querySelector로 직접 선택
			const reservDateInput = document.querySelector("input[name='reservDate']");
			if (reservDateInput) {
				reservDateInput.value = yyyymmdd;
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
            

            // 예매 가능한 시간 표시 (예제 데이터)
            /*const selectedDate = new Date(currentYear, currentMonth, day);
            displayBookingTimes(selectedDate);*/
        }

        /*function displayBookingTimes(date) {
            const availableTimes = ["10:00 AM", "2:00 PM", "4:00 PM"];
            bookingTimesContainer.innerHTML = `<strong>Available Times for ${date.toLocaleDateString("ko-KR")}:</strong> ${availableTimes.join(', ')}`;
        }*/

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