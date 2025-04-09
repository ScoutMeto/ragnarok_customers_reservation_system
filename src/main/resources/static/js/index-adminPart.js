document.addEventListener("DOMContentLoaded", function () {

    // načtení kalendáře
    const calendarEl = document.getElementById('calendar')
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
        locale: 'cs',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        events: {
            url: '/api/loadAllTrainings',
            method: 'GET',
            failure: function() {
                alert('Chyba při načítání tréninků (calendar)!');
            }
        }
    })
    calendar.render()


    const apiBase = '/api';


    // // === Rezervace ===
    // window.getAllReservations = async function () {
    //     const res = await fetch(`${apiBase}/admin/getAllReservations`);
    //     const data = await res.json();
    //     console.log('Všechny rezervace:', data);
    //     alert(JSON.stringify(data));
    // };
    //
    // window.deleteReservation = async function () {
    //     const id = prompt("Zadej ID rezervace ke smazání:");
    //     if (id) {
    //         const res = await fetch(`${apiBase}/admin/deleteReservation/${id}`, { method: 'DELETE' });
    //         alert(res.ok ? 'Smazáno' : 'Chyba při mazání');
    //     }
    // };
    //
    // window.deleteAllReservationsForTraining = async function () {
    //     const trainingId = prompt("Zadej ID tréninku, pro který se mají rezervace smazat:");
    //     if (trainingId) {
    //         const res = await fetch(`${apiBase}/admin/deleteReservations/${trainingId}`, { method: 'DELETE' });
    //         alert(res.ok ? 'Všechny rezervace pro trénink smazány' : 'Chyba při mazání rezervací');
    //     }
    // };
    //
    // // === Tréninky ===
    // window.getTrainingsInDateRange = async function () {
    //     const from = prompt("Zadej počáteční datum (YYYY-MM-DD):");
    //     const to = prompt("Zadej koncové datum (YYYY-MM-DD):");
    //     if (from && to) {
    //         const res = await fetch(`${apiBase}/admin/getTrainings?from=${from}&to=${to}`);
    //         const data = await res.json();
    //         console.log('Tréninky v rozmezí:', data);
    //         alert(JSON.stringify(data));
    //     }
    // };
    //
    // window.getAllTrainings = async function () {
    //     const res = await fetch(`${apiBase}/admin/getAllTrainings`);
    //     const data = await res.json();
    //     console.log('Všechny tréninky:', data);
    //     alert(JSON.stringify(data));
    // };
    //
    // window.deleteTraining = async function () {
    //     const id = prompt("Zadej ID tréninku ke smazání:");
    //     if (id) {
    //         const res = await fetch(`${apiBase}/admin/deleteTraining/${id}`, { method: 'DELETE' });
    //         alert(res.ok ? 'Trénink smazán' : 'Chyba při mazání');
    //     }
    // };
    //
    // window.deleteTrainingsFromDate = async function () {
    //     const date = prompt("Zadej datum (YYYY-MM-DD), od kdy se mají tréninky mazat:");
    //     if (date) {
    //         const res = await fetch(`${apiBase}/admin/deleteTrainingsFrom/${date}`, { method: 'DELETE' });
    //         alert(res.ok ? 'Tréninky od daného data smazány' : 'Chyba při mazání');
    //     }
    // };
    //
    // // === Logout ===
    // window.logout = async function () {
    //     const res = await fetch(`${apiBase}/logoutAdmin`, { method: 'POST' });
    //     if (res.ok) {
    //         window.location.href = '/index.html';
    //     } else {
    //         alert("Odhlášení selhalo");
    //     }
    // };

    // === Vytvoření tréninku ===
    document.getElementById("trainingForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        console.log("Formulář pro vytvoření tréninku byl odeslán");


        const repeatWeeks = parseInt(document.getElementById("repeatEveryWeek").value) || 0;
        const repeatIntervalInDays = repeatWeeks > 0 ? 7 : 0;
        const numberOfCopyConcreteTraining = repeatWeeks;

        const data = {
            nameOfLesson: document.getElementById("nameOfLesson").value,
            coachName: document.getElementById("coachName").value,
            startOfCurrentLesson: document.getElementById("startOfCurrentLesson").value,
            endOfCurrentLesson: document.getElementById("endOfCurrentLesson").value,
            // dateOfCurrentLesson: document.getElementById("dateOfCurrentLesson").value,
            dateOfCurrentLesson: document.getElementById("startOfCurrentLesson").value,
            numberOfFreeSlots: parseInt(document.getElementById("capacity").value),
            repeatIntervalInDays: repeatIntervalInDays,
            numberOfCopyConcreteTraining: numberOfCopyConcreteTraining
        };

        const response = await fetch('/api/createNewTraining', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert('Trénink vytvořen');
        } else {
            alert('Chyba při vytváření tréninku');
        }
    });

    // === Vytvoření rezervace ===
    document.getElementById("reservationForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        const data = {
            name: document.getElementById("name").value,
            email: document.getElementById("email").value,
            phone: document.getElementById("phone").value,
            trainingId: parseInt(document.getElementById("trainingId").value),
            numberOfPeople: parseInt(document.getElementById("numberOfPeople").value),
        };

        const response = await fetch('/api/reservation', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert('Rezervace vytvořena');
        } else {
            alert('Chyba při vytváření rezervace');
        }
    });
// });


// Tabulka přehledu tréninků
// document.addEventListener("DOMContentLoaded", function () {
//     const apiBase = '/api';

    // === Přepínání týdnů ===
    let currentStartDate = getCurrentMonday();

    function getCurrentMonday() {
        const now = new Date();
        const day = now.getDay() || 7; // Neděle jako 7
        now.setHours(0, 0, 0, 0);
        now.setDate(now.getDate() - day + 1);
        return now;
    }

    function formatDate(date) {
        const localDate = new Date(date);
        localDate.setMinutes(localDate.getMinutes() - localDate.getTimezoneOffset());

        const pad = (n) => n.toString().padStart(2, '0');
        const year = localDate.getFullYear();
        const month = pad(localDate.getMonth() + 1);
        const day = pad(localDate.getDate());
        const hours = pad(localDate.getHours());
        const minutes = pad(localDate.getMinutes());

        return `${year}-${month}-${day}T${hours}:${minutes}`; // správný formát pro LocalDateTime
    }

    // async function loadTrainingsForWeek(startDate) {
    //     console.log("DEBUG: Spouští se loadTrainingsForWeek() s parametrem:", startDate);
    //
    //     const formattedDate = formatDate(startDate);
    //     console.log("Načítám tréninky pro týden od:", formattedDate);
    //
    //     const response = await fetch(`${apiBase}/loadAllTrainings?startDate=${formattedDate}`);
    //
    //     if (!response.ok) {
    //         console.error("Chyba při načítání tréninků:", response.status);
    //         return;
    //     }
    //
    //     const result = await response.json();
    //
    //     console.log("Získaná data z backendu:", result);
    //     renderTrainingTable(result.content);
    // }

    function renderTrainingTable(trainings) {
        console.log("DEBUG – Tréninky přijaté z backendu:", trainings);
        const table = document.getElementById("trainingTable");
        table.innerHTML = "";

        const hours = Array.from({ length: 13 }, (_, i) => i + 8); // 8:00 - 20:00
        const days = ["Pondělí", "Úterý", "Středa", "Čtvrtek", "Pátek", "Sobota", "Neděle"];

        // Hlavička
        const header = document.createElement("tr");
        header.appendChild(document.createElement("th")); // Prázdný roh
        days.forEach(day => {
            const th = document.createElement("th");
            th.textContent = day;
            header.appendChild(th);
        });
        table.appendChild(header);

        // Tělo tabulky
        hours.forEach(hour => {
            const row = document.createElement("tr");
            const timeCell = document.createElement("td");
            timeCell.textContent = `${hour}:00`;
            row.appendChild(timeCell);

            for (let dayIndex = 0; dayIndex < 7; dayIndex++) {
                const cell = document.createElement("td");
                const cellTrainings = trainings.filter(t => {
                    const trainingDate = new Date(t.dateOfCurrentLesson);
                    return trainingDate.getDay() === (dayIndex + 1) &&
                        trainingDate.getHours() === hour;
                });

                if (cellTrainings.length > 0) {
                    cellTrainings.forEach(t => {
                        const div = document.createElement("div");
                        div.className = "training-box " + getColorClass(t.nameOfLesson);
                        div.innerHTML = `<strong>${t.nameOfLesson}</strong><br>${t.startOfCurrentLesson.slice(11,16)} - ${t.endOfCurrentLesson.slice(11,16)}<br>${t.reservations.length}/${t.numberOfFreeSlots}`;
                        cell.appendChild(div);
                    });
                }

                row.appendChild(cell);
            }

            table.appendChild(row);
        });
    }

    function getColorClass(name) {
        const lower = name.toLowerCase();
        if (lower.includes("kettlebell")) return "type-kettlebell";
        if (lower.includes("jóga")) return "type-yoga";
        if (lower.includes("box")) return "type-box";
        return "type-default";
    }

    // Tlačítka pro posun týdne
    document.getElementById("prevWeek").addEventListener("click", () => {
        currentStartDate.setDate(currentStartDate.getDate() - 7);
        loadTrainingsForWeek(currentStartDate);
    });

    document.getElementById("nextWeek").addEventListener("click", () => {
        currentStartDate.setDate(currentStartDate.getDate() + 7);
        loadTrainingsForWeek(currentStartDate);
    });

    // Načíst aktuální týden při startu
    loadTrainingsForWeek(currentStartDate);
});