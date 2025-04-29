document.addEventListener("DOMContentLoaded", function () {

    const apiBase = '/api';

    //responzivní design
    function getResponsiveView() {
        const width = window.innerWidth;
        if (width < 600) return 'timeGridDay';          // mobil
        if (width < 1024) return 'timeGridWeek';      // tablet / menší notebook
        return 'dayGridMonth';                        // velká obrazovka
    }

    let editMode = "single"; // nebo "series"

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
                // Uložíme trainingId do globální proměnné pro pozdější použití
                window.selectedTrainingId = props.trainingId;
                window.selectedTrainingData  = {
                lessonName: props.lessonName,
                coachName: props.coachName,
                start: info.event.start,
                end: info.event.end,
                capacity: props.numberOfFreeSlots
                };

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
                    window.selectedReservationId = res.reservation_id; // <<< TADY uložíš ID rezervace globálně - přístupné dál pro další metody, které ho potřebují
                    window.selectedReservationData = res;             // Uložíme celý objekt rezervace

                    console.log("Vybrané ID rezervace:", window.selectedReservationId);

                    // Otevřeme vlastní modal s volbou
                    document.getElementById('reservationOptionsModal').style.display = 'block';

                    // if (confirm("Upravit nebo smazat rezervaci?")) {
                    //     // Otevřít modální okno s předvyplněnými daty
                    //     document.getElementById('editReservationId').value = selectedReservationId;
                    //     document.getElementById('editFirstName').value = res.firstName;
                    //     document.getElementById('editSecondName').value = res.secondName;
                    //     document.getElementById('editEmail').value = res.userEmail;
                    //     document.getElementById('editPhone').value = res.telephoneNumber;
                    //     document.getElementById('editPeople').value = res.numberOfBookedEntries;
                    //
                    //     document.getElementById('reservationEditModal').style.display = 'block';
                    // } else if (confirm("Chcete smazat tuto rezervaci?")) {
                    //     if (confirm("Opravdu chcete smazat rezervaci?")) {
                    //         fetch(`/api/deleteReservation/${selectedReservationId}`, {
                    //             method: 'DELETE'
                    //         })
                    //             .then(response => {
                    //                 if (response.ok) {
                    //                     alert('Rezervace smazána');
                    //                     calendar.refetchEvents();
                    //                 } else {
                    //                     alert('Chyba při mazání rezervace');
                    //                 }
                    //             });
                    //     }
                    // }
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







    // Zavření modálního okna (obecně)
    document.querySelector('.close-button').addEventListener('click', function () {
        document.getElementById('eventModal').style.display = 'none';
    });

    // Zavření modálního okno pro funkci "createNewTraining"
    document.querySelector('.close-training-modal').addEventListener('click', function () {
        document.getElementById("trainingModal").style.display = 'none';
    });

    // Zavření modálního okno pro funkci "reservationEdit"
    document.querySelector('#reservationEditModal .close-reservation-modal').onclick = () => {
        document.getElementById('reservationEditModal').style.display = 'none';
    };

    // Zavření modálního okno pro funkci "createNewReservation"
    document.querySelector('#reservationModal .close-reservation-modal').onclick = () => {
        document.getElementById('reservationModal').style.display = 'none';
    };

    // Zavření modálního okno pro funkci "editTraining"
    document.querySelector('#editTrainingModal .close-edit-training-modal').onclick = () => {
        document.getElementById('editTrainingModal').style.display = 'none';
    };

    // Zavření modálního okna pro funkci "editReservation"
    document.querySelector('.close-options-modal').addEventListener('click', function() {
        document.getElementById('reservationOptionsModal').style.display = 'none';
    });

    // Zavírání modálního okna úpravy rezervace ????
    // document.querySelectorAll(".close-reservation-modal").forEach(btn => {
    //     btn.addEventListener("click", () => {
    //         document.getElementById("reservationEditModal").style.display = "none";
    //     });
    // });




    // === Vytvoření tréninku ===
    document.getElementById("trainingForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        console.log("Formulář pro vytvoření tréninku byl odeslán");

        const data = {
            nameOfLesson: document.getElementById("nameOfLesson").value,
            coachName: document.getElementById("coachName").value,
            startOfCurrentLesson: document.getElementById("startOfCurrentLesson").value,
            endOfCurrentLesson: document.getElementById("endOfCurrentLesson").value,
            dateOfCurrentLesson: document.getElementById("startOfCurrentLesson").value,
            numberOfFreeSlots: parseInt(document.getElementById("capacity").value),
            repeatIntervalInDays: 7,
            numberOfCopyConcreteTraining: parseInt(document.getElementById("repeatEveryWeek").value) || 0
        };

        console.log("Data odeslána na backend:", JSON.stringify(data));

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
            firstName: document.getElementById("firstName").value,
            secondName: document.getElementById("secondName").value,
            userEmail: document.getElementById("email").value,
            telephoneNumber: document.getElementById("phone").value,
            trainingId: parseInt(document.getElementById("reservationTrainingId").value),
            numberOfBookedEntries: parseInt(document.getElementById("numberOfPeople").value),
        };

        const response = await fetch('/api/createNewReservation', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert('Rezervace vytvořena');
            document.getElementById("reservationModal").style.display = "none";
            calendar.refetchEvents(); // obnoví kalendář
        } else {
            alert('Chyba při vytváření rezervace');
        }
    });

    // === Mazání tréninku ===
    document.getElementById("deleteTrainingBtn").addEventListener("click", async function () {
        const trainingId = window.selectedTrainingId;

        if (!trainingId) {
            alert("Nebyl vybrán žádný trénink k odstranění.");
            return;
        }

        const confirmed = confirm("Opravdu chceš tento trénink smazat? Tím smažeš i všechny jeho rezervace.");
        if (!confirmed) return;

        try {
            const response = await fetch(`/api/deleteTrainingChosenInOverview/${trainingId}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                alert("Trénink úspěšně smazán.");
                document.getElementById("reservationModal").style.display = "none";
                calendar.refetchEvents(); // aktualizace FullCalendaru
            } else {
                alert("Chyba při mazání tréninku.");
            }
        } catch (error) {
            console.error("Chyba při komunikaci s backendem:", error);
            alert("Došlo k chybě při mazání.");
        }
    });

    // === Mazání tréninků ===
    document.getElementById("deleteTrainingsSeriesBtn").addEventListener("click", async () => {

        const trainingId = window.selectedTrainingId;
        if (confirm("Opravdu chceš smazat tento trénink a všechny jeho opakování + rezervace?")) {
            await fetch(`/api/deleteTrainingsChosenInOverview/${trainingId}`, { method: 'DELETE' });
            calendar.refetchEvents();
            document.getElementById("eventModal").style.display = "none";
        }
    });

    // === Úprava jednoho tréninku nebo série ===
    document.getElementById("editTrainingForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        const trainingId = window.selectedTrainingId;

        const data = {
            nameOfLesson: document.getElementById("editNameOfLesson").value,
            coachName: document.getElementById("editCoachName").value,
            numberOfFreeSlots: parseInt(document.getElementById("editCapacity").value),
            startOfCurrentLesson: document.getElementById("editStartOfCurrentLesson").value,
            endOfCurrentLesson: document.getElementById("editEndOfCurrentLesson").value,
            dateOfCurrentLesson: document.getElementById("editStartOfCurrentLesson").value
        };

        let endpoint = "/api/editTrainingChosenInOverview/" + trainingId;
        if (editMode === "series") {
            endpoint = "/api/editAllPlanned/" + trainingId;
        }

        try {
            const response = await fetch(endpoint, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert(editMode === "single" ? "Trénink upraven." : "Série tréninků upravena.");
                document.getElementById("editTrainingModal").style.display = "none";
                calendar.refetchEvents();
            } else {
                alert("Chyba při úpravě " + (editMode === "single" ? "tréninku." : "série."));
            }
        } catch (error) {
            console.error("Chyba při odeslání:", error);
            alert("Nastala chyba při komunikaci se serverem.");
        }
    });

// === Úprava rezervace ===
    document.getElementById("editReservationForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        const data = {
            firstName: document.getElementById("editFirstName").value,
            secondName: document.getElementById("editSecondName").value,
            userEmail: document.getElementById("editEmail").value,
            telephoneNumber: document.getElementById("editPhone").value,
            numberOfBookedEntries: parseInt(document.getElementById("editPeople").value),
            trainingPassedOrDeleted: false // Předpokládáme false
        };

        const response = await fetch(`/api/editReservation/${window.selectedReservationId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert('Rezervace upravena');
            document.getElementById("reservationEditModal").style.display = "none";
            document.getElementById("eventModal").style.display = "none";
            calendar.refetchEvents();
        } else {
            alert('Chyba při úpravě rezervace');
        }
    });


    // Úprava jednoho tréninku
    document.getElementById("editTrainingBtn").addEventListener("click", () => {
        editMode = "single";

        const data = window.selectedTrainingData || {};

        document.getElementById("editNameOfLesson").value = data.lessonName || "";
        document.getElementById("editCoachName").value = data.coachName || "";
        document.getElementById("editStartOfCurrentLesson").value = data.start ? new Date(data.start).toISOString().slice(0,16) : "";
        document.getElementById("editEndOfCurrentLesson").value = data.end ? new Date(data.end).toISOString().slice(0,16) : "";
        document.getElementById("editCapacity").value = data.capacity || "";

        document.getElementById("editTrainingId").value = window.selectedTrainingId;
        document.getElementById("editTrainingModal").style.display = "block";
    });

    // Úprava série tréninků
    document.getElementById("editTrainingsSeriesBtn").addEventListener("click", () => {
        editMode = "series";
        document.getElementById("editTrainingId").value = window.selectedTrainingId;
        document.getElementById("editTrainingModal").style.display = "block";
    });

    // Vytvoření rezervace
    document.getElementById("createReservationBtn").addEventListener("click", () => {
        // Předvyplní trainingId do formuláře
        document.getElementById("reservationTrainingId").value = window.selectedTrainingId;

        // Zobrazí modal
        document.getElementById("reservationModal").style.display = "block";
    });

    // Editace rezervace
    document.getElementById("editReservationBtn").addEventListener("click", () => {
        // Zavři volbu
        document.getElementById('reservationOptionsModal').style.display = 'none';
        const res = window.selectedReservationData;

        // Předvyplň formulář na editaci
        document.getElementById('editFirstName').value = res.firstName; // naplnit z uloženého objektu
        document.getElementById('editSecondName').value = res.secondName;
        document.getElementById('editEmail').value = res.userEmail;
        document.getElementById('editPhone').value = res.telephoneNumber;
        document.getElementById('editPeople').value = res.numberOfBookedEntries;

        // Zavřeme rozcestník a otevřeme editaci
        document.getElementById('reservationOptionsModal').style.display = 'none';
        document.getElementById('reservationEditModal').style.display = 'block';
    });

    // Vymazání rezervace
    document.getElementById("deleteReservationBtn").addEventListener("click", async () => {
        // Zavři volbu
        document.getElementById('reservationOptionsModal').style.display = 'none';

            try {
                const response = await fetch(`/api/deleteReservation/${window.selectedReservationId}`, {
                    method: 'DELETE'
                });

                if (response.ok) {
                    alert('Rezervace smazána.');
                    calendar.refetchEvents();
                    document.getElementById('eventModal').style.display = 'none';
                } else {
                    alert('Chyba při mazání rezervace.');
                }
            } catch (error) {
                console.error("Chyba:", error);
            }
    });


    calendar.render()

    // Logout metoda
    document.getElementById('logoutBtn').addEventListener('click', () => {
        fetch('/api/logoutAdmin', { method: 'POST' })
            .then(resp => {
                if (resp.ok)
                    window.location.href = '/index.html';
                else          alert('Odhlášení se nezdařilo');
            })
            .catch(err => console.error('Logout error:', err));
    });

});

