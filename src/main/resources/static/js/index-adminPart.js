document.addEventListener("DOMContentLoaded", function () {

    const apiBase = '/api';

    //responzivní design
    function getResponsiveView() {
        const width = window.innerWidth;
        if (width < 600) return 'timeGridDay';          // mobil
        if (width < 1024) return 'timeGridWeek';      // tablet / menší notebook
        return 'dayGridMonth';                        // velká obrazovka
    }


    // načtení kalendáře
    const calendarEl = document.getElementById('calendar')
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: getResponsiveView(),
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
        },

        // zobrazení pro velké obrazovky
        eventDidMount: function (info) {
            const width = window.innerWidth;
            if (width >= 1024) {  // Pouze pro velké obrazovky
                const title = info.event.extendedProps.lessonName || '?';
                const coachNameForExtendedProps = info.event.extendedProps.coachName || '?';
                const capacity = info.event.extendedProps.numberOfFreeSlots;
                const reservations = info.event.extendedProps.numberOfReservations;

                const extraInfo = `\nNázev lekce: ${title} \nTrenér: ${coachNameForExtendedProps}\nKapacita lekce: ${capacity} / Rezervace: ${reservations}`;

                const titleEl = info.el.querySelector('.fc-event-title');
                if (titleEl) {
                    titleEl.innerText += extraInfo;
                }
            }
        },

        // Modální okno po kliknutí na lekci
        eventClick: function (info) {
            const width = window.innerWidth;
            // if (width < 1024) { // zobrazit modal na mobilu / tabletu
                const event = info.event;
                const props = event.extendedProps;

                document.getElementById('modal-title').textContent = event.title || "Neznámý název";
                document.getElementById('modal-coach').textContent = props.coachName || "neuvedeno";
                document.getElementById('modal-capacity').textContent = props.numberOfFreeSlots ?? "neuvedeno";
                document.getElementById('modal-reservations').textContent = props.numberOfReservations ?? "neuvedeno";
                document.getElementById('modal-start').textContent = new Date(event.start).toLocaleString();
                document.getElementById('modal-end').textContent = new Date(event.end).toLocaleString();

                document.getElementById('eventModal').style.display = 'block';
            // }


            // Načti rezervace
            const reservations = props.reservations || [];

            const listContainer = document.getElementById("reservationsContainer");
            listContainer.innerHTML = ""; // reset

            if (reservations.length === 0) {
                listContainer.innerHTML = "<i>Žádné rezervace</i>";
            }

            reservations.forEach(res => {
                const li = document.createElement("li");
                li.textContent = `${res.firstName} ${res.secondName}, ${res.userEmail}, ${res.telephoneNumber}, ${res.numberOfBookedEntries} (počet osob)`;
                li.style.cursor = "pointer";
                li.addEventListener("click", () => {
                    if (confirm("Upravit nebo smazat rezervaci?")) {
                        // TODO: Otevři modal s úpravou nebo potvrzením smazání
                        alert(`TODO: Implementace úpravy nebo mazání rezervace ID: ${res.reservationId}`);
                    }
                });
                listContainer.appendChild(li);
            });

            document.getElementById('eventModal').style.display = 'block';

        },

        // Modální okno pro "createNewTraining"
        dateClick: function(info) {
            // Zobraz modal
            const modal = document.getElementById("trainingModal"); // id modálního okna
            modal.style.display = "block";

            // Předvyplnění data a času
            const startInput = document.getElementById("startOfCurrentLesson");
            const endInput = document.getElementById("endOfCurrentLesson");

            const clickedDate = new Date(info.dateStr);
            const defaultEnd = new Date(clickedDate);
            defaultEnd.setHours(clickedDate.getHours() + 1);

            startInput.value = clickedDate.toISOString().slice(0, 16); // yyyy-MM-ddTHH:mm
            endInput.value = defaultEnd.toISOString().slice(0, 16);
        }
    })

    // Funkce pro otevření modalu pro úpravu rezervace
    function openReservationEditModal(reservation) {
        document.getElementById('editReservationId').value = reservation.reservationId;
        document.getElementById('editName').value = reservation.firstName;
        document.getElementById('editSurname').value = reservation.secondName;
        document.getElementById('editEmail').value = reservation.userEmail;
        document.getElementById('editPhone').value = reservation.telephoneNumber;
        document.getElementById('editPeople').value = reservation.numberOfBookedEntries;

        document.getElementById('reservationEditModal').style.display = 'block';
    }




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


    // Zavření modálního okna
    document.querySelector('.close-button').addEventListener('click', function () {
        document.getElementById('eventModal').style.display = 'none';
    });

    // Zavření modálního okno pro funkci "createNewTraining"
    document.querySelector('.close-training-modal').addEventListener('click', function () {
        document.getElementById("trainingModal").style.display = 'none';
    });

    // Zavření modálního okno pro funkci "reservationEdit"
    document.querySelector('.close-reservation-modal').onclick = () => {
        document.getElementById('reservationEditModal').style.display = 'none';
    };

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
            document.getElementById("trainingModal").style.display = "none";
            calendar.refetchEvents();  // ← Zde obnovíš kalendář s novým tréninkem
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



    calendar.render()


    // Tlačítka pro správu tréninku
    document.getElementById("deleteTrainingBtn").addEventListener("click", async () => {
        const trainingId = window.selectedTrainingId;
        if (confirm("Opravdu chceš smazat tento trénink a jeho rezervace?")) {
            await fetch(`/api/deleteTrainingChosenInOverview/${trainingId}`, { method: 'DELETE' });
            calendar.refetchEvents();
            document.getElementById("eventModal").style.display = "none";
        }
    });

    document.getElementById("deleteTrainingsSeriesBtn").addEventListener("click", async () => {
        const trainingId = window.selectedTrainingId;
        if (confirm("Opravdu chceš smazat tento trénink a všechny jeho opakování + rezervace?")) {
            await fetch(`/api/deleteTrainingsChosenInOverview/${trainingId}`, { method: 'DELETE' });
            calendar.refetchEvents();
            document.getElementById("eventModal").style.display = "none";
        }
    });

    document.getElementById("editTrainingBtn").addEventListener("click", () => {
        alert("TODO: Otevři modální okno pro úpravu jednoho tréninku.");
        // TODO: připrav formulář pro úpravu (další modal)
    });

    document.getElementById("editTrainingsSeriesBtn").addEventListener("click", () => {
        alert("TODO: Otevři modální okno pro úpravu série tréninků.");
        // TODO: připrav formulář pro úpravu série
    });

    document.getElementById("createReservationBtn").addEventListener("click", () => {
        alert("TODO: Otevři formulář pro vytvoření rezervace k tomuto tréninku.");
        // TODO: otevři modal s předvyplněným trainingId
    });
});