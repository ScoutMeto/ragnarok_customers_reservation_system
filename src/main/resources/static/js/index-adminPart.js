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
                // Uložíme trainingId do globální proměnné pro pozdější použití
                window.selectedTrainingId = props.trainingId;

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
        document.getElementById('editFirstName').value = reservation.firstName;
        document.getElementById('editSecondName').value = reservation.secondName;
        document.getElementById('editEmail').value = reservation.userEmail;
        document.getElementById('editPhone').value = reservation.telephoneNumber;
        document.getElementById('editPeople').value = reservation.numberOfBookedEntries;

        document.getElementById('reservationEditModal').style.display = 'block';
    }







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




    // === Vytvoření tréninku ===
    document.getElementById("trainingForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        console.log("Formulář pro vytvoření tréninku byl odeslán");


        // const repeatWeeks = parseInt(document.getElementById("repeatEveryWeek").value) || 0;
        // const repeatIntervalInDays = repeatWeeks > 0 ? 7 : 0;
        // const numberOfCopyConcreteTraining = repeatWeeks;

        const data = {
            nameOfLesson: document.getElementById("nameOfLesson").value,
            coachName: document.getElementById("coachName").value,
            startOfCurrentLesson: document.getElementById("startOfCurrentLesson").value,
            endOfCurrentLesson: document.getElementById("endOfCurrentLesson").value,
            // dateOfCurrentLesson: document.getElementById("dateOfCurrentLesson").value,
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

    //Mazání tréninku
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

    // Mazání tréninků
    document.getElementById("deleteTrainingsSeriesBtn").addEventListener("click", async () => {

        const trainingId = window.selectedTrainingId;
        if (confirm("Opravdu chceš smazat tento trénink a všechny jeho opakování + rezervace?")) {
            await fetch(`/api/deleteTrainingsChosenInOverview/${trainingId}`, { method: 'DELETE' });
            calendar.refetchEvents();
            document.getElementById("eventModal").style.display = "none";
        }
    });

    //Úprava jednoho tréninku
    document.getElementById("editTrainingForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        const trainingId = window.selectedTrainingId;

        const data = {
            nameOfLesson: document.getElementById("editNameOfLesson").value,
            coachName: document.getElementById("editCoachName").value,
            numberOfFreeSlots: parseInt(document.getElementById("editCapacity").value),
            startOfCurrentLesson: document.getElementById("editStartOfCurrentLesson").value,
            endOfCurrentLesson: document.getElementById("editEndOfCurrentLesson").value,
            dateOfCurrentLesson: document.getElementById("editStartOfCurrentLesson").value,

        };

        console.log(JSON.stringify(data));
        try {
            const response = await fetch(`/api/editTrainingChosenInOverview/${trainingId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert("Trénink byl úspěšně upraven.");
                document.getElementById("editTrainingModal").style.display = "none";
                calendar.refetchEvents();
            } else {
                alert("Chyba při úpravě tréninku.");
            }
        } catch (error) {
            console.error("Chyba při úpravě tréninku:", error);
            alert("Nastala chyba při komunikaci se serverem.");
        }
    });


    // Úprava série tréninků
    document.getElementById("editTrainingForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        const trainingId = window.selectedTrainingId;

        const data = {
            nameOfLesson: document.getElementById("editNameOfLesson").value,
            coachName: document.getElementById("editCoachName").value,
            numberOfFreeSlots: parseInt(document.getElementById("editCapacity").value),
            startOfCurrentLesson: document.getElementById("editStartOfCurrentLesson").value,
            endOfCurrentLesson: document.getElementById("editEndOfCurrentLesson").value,
            dateOfCurrentLesson: document.getElementById("editStartOfCurrentLesson").value,

        };

        try {
            const response = await fetch(`/api/editAllPlanned/${trainingId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                alert("Tréninky byly úspěšně upraveny.");
                document.getElementById("editTrainingModal").style.display = "none";
                calendar.refetchEvents();
            } else {
                alert("Chyba při úpravě tréninků.");
            }
        } catch (error) {
            console.error("Chyba při úpravě tréninků:", error);
            alert("Nastala chyba při komunikaci se serverem.");
        }
    });

    // Úprava jednoho tréninku
    document.getElementById("editTrainingBtn").addEventListener("click", () => {
        document.getElementById("editTrainingId").value = window.selectedTrainingId;

        document.getElementById("editTrainingModal").style.display = "block";
    });

    // Úprava série tréninků
    document.getElementById("editTrainingsSeriesBtn").addEventListener("click", () => {

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



    calendar.render()





    //editReservation, deleteReservation - zatím chybí


});