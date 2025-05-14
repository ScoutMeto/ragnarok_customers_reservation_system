document.addEventListener("DOMContentLoaded", function () {

    const apiBase = '/api';

    // Responzivní design
    function getResponsiveView() {
        const width = window.innerWidth;
        // if (width < 600) return 'timeGridDay';          // mobil
        if (width < 1024) return 'timeGridDay';      // tablet / menší notebook
        return 'dayGridMonth';                        // velká obrazovka
    }

    let editMode = "single"; // nebo "series"

    // ===== hromadné mazání =====
    let aimedMode = false;
    let aimedSelections = [];
    const aimedModal        = document.getElementById("aimedDeleteModal");
    const aimedCountEl      = document.getElementById("aimed-count");
    const aimedListEl       = document.getElementById("aimed-list");
    const aimedConfirmBtn   = document.getElementById("aimed-confirm");
    const aimedCancelBtn    = document.getElementById("aimed-cancel");
    const closeAimedBtn     = document.getElementById("closeAimedModal");
// ===========================

    // přidá/odstraní žlutou třídu podle ID
    function highlightAimedEvent(id) {
        const el = document.querySelector(`[data-event-id="${id}"]`);
        if (el) el.classList.add('aimed-selected');
    }
    function unhighlightAimedEvent(id) {
        const el = document.querySelector(`[data-event-id="${id}"]`);
        if (el) el.classList.remove('aimed-selected');
    }

    /**
     * Sundá žluté zvýraznění ze všech dosud vybraných lekcí
     * a vyčistí seznam aimedSelections.
     */
    function clearAimedSelection() {
        aimedSelections.forEach(id => unhighlightAimedEvent(id));
        aimedSelections = [];                // vyčistí pole
        updateAimedUI();                     // překreslí "Vybráno lekcí: ..." a seznam
    }

    // Načtení kalendáře
    const calendarEl = document.getElementById('calendar')
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: getResponsiveView(),
        locale: 'cs',
        firstDay: 1,       // pondělí (0 = neděle, 1 = pondělí, …)
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        buttonText: {
            today: 'dnes',
            month: 'měsíc',
            week:  'týden',
            day:   'den'
        },
        events: {
            url: '/api/loadAllTrainings',
            method: 'GET',
            failure: function() {
                alert('Chyba při načítání tréninků (calendar)!');
            }
        },

        // Zobrazení pro velké obrazovky
        eventDidMount: function (info) {
            const width = window.innerWidth;
            if (width >= 1024) {  // Pouze pro velké obrazovky
                const title = info.event.extendedProps.lessonName || '?';
                const coachNameForExtendedProps = info.event.extendedProps.coachName || '?';
                const capacity = info.event.extendedProps.numberOfFreeSlots;
                const reservations = info.event.extendedProps.numberOfReservations;

                const extraInfo = `\nNázev lekce: ${title} \nTrenér: ${coachNameForExtendedProps}\nKapacita lekce: ${capacity} / Rezervace: ${reservations}`;

                const tId = info.event.extendedProps.trainingId;
                    if (tId != null) {
                          info.el.setAttribute('data-event-id', tId);
                          // pokud už jsme v bulk-módu a tohle ID je v aimedSelections, znovu ho naimportujeme
                              if (aimedSelections.includes(tId)) {
                                info.el.classList.add('aimed-selected');
                              }
                        }
                    }
        },

        // Modální okno po kliknutí na lekci
        eventClick: function (info) {
            const width = window.innerWidth; // if (width < 1024) { // zobrazit modal na mobilu / tabletu

            // pokud jsme v bulk-delete módu, jen sbíráme ID a exit
            if (aimedMode) {
                const id = info.event.extendedProps.trainingId;
                // toggle výběru
                const idx = aimedSelections.indexOf(id);
                if (idx >= 0) {
                    aimedSelections.splice(idx, 1);
                    info.el.classList.remove("aimed-selected");
                } else {
                    aimedSelections.push(id);
                    info.el.classList.add("aimed-selected");
                }
                updateAimedUI();
                return;
            }

                const event = info.event;
                const props = event.extendedProps;
                window.selectedTrainingId = props.trainingId; // Uložím trainingId do globální proměnné pro pozdější použití
                window.selectedTrainingData  = { // Uložím obecná data o tréninku do globální proměnné pro pozdější použití
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
                    window.selectedReservationId = res.reservation_id; // ID rezervace globálně dostupné
                    window.selectedReservationData = res;             // Data celého objektu rezervace globálně dostupné

                    console.log("Vybrané ID rezervace:", window.selectedReservationId);

                    // Otevřeme vlastní modal s volbou
                    document.getElementById('reservationOptionsModal').style.display = 'block';

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
        },
    })


    // Zavírání modalů
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

    // Zavření modálního okna pro funkci "registrationNewAdmin"
    document.querySelector('.close-registration-admin-modal').addEventListener('click', function() {
        document.getElementById('registrationAdminModal').style.display = 'none';
    });

    // Zavření modální okna aimed modalu
    // document.querySelector(".close-aimed-modal").addEventListener("click", () => {
    //     cancelAimed();
    // });
    //
    // // Zrušení aimed modalu
    // document.getElementById("aimed-cancel").addEventListener("click", () => {
    //     cancelAimed();
    // });
    closeAimedBtn.addEventListener("click", () => {
        clearAimedSelection();
        closeAimed();
    });
    aimedCancelBtn.addEventListener("click", () => {
        clearAimedSelection();
        closeAimed();
    });

    function closeAimed(){
        aimedMode = false;
        aimedSelections = [];
        aimedModal.style.display = "none";
        document.getElementById("trainingActions").style.display = "block";
    }


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

    // === Vytvoření admin profilu ===
    document.getElementById("registrationAdminForm").addEventListener("submit", async function (e) {
        e.preventDefault();

        const data = {
            nickname: document.getElementById("nickname").value,
            adminEmail: document.getElementById("adminEmail").value,
            password: document.getElementById("adminPassword").value,
        };

        const response = await fetch('/api/registrationNewAdmin', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            alert('Profil nového admina vytvořen.');
            document.getElementById("registrationAdminModal").style.display = "none";
            calendar.refetchEvents(); // obnoví kalendář
        } else {
            alert('Chyba při vytváření profilu admina.');
        }
    });


    // === Úprava jednoho tréninku - reakce na btn ===
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

    // === Úprava série tréninků - reakce na btn ===
    document.getElementById("editTrainingsSeriesBtn").addEventListener("click", () => {
        editMode = "series";
        document.getElementById("editTrainingId").value = window.selectedTrainingId;
        document.getElementById("editTrainingModal").style.display = "block";
    });

    // === Vytvoření rezervace - reakce na btn ===
    document.getElementById("createReservationBtn").addEventListener("click", () => {
        // Předvyplní trainingId do formuláře
        document.getElementById("reservationTrainingId").value = window.selectedTrainingId;

        // Zobrazí modal
        document.getElementById("reservationModal").style.display = "block";
    });

    // === Editace rezervace - reakce na btn===
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

    // === Vymazání rezervace - reakce na btn ===
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


    // === Vymazání vybraných tréninků skrz aimed modal - reakce na btn ===
    document.getElementById("deleteAimedTrainingsBtn").addEventListener("click", () => {
        // 1) zavřeme hlavní modal
        document.getElementById("eventModal").style.display = "none";
        // 2) skryjeme tlačítka v něm
        document.getElementById("trainingActions").style.display = "none";

        // 3) zapneme bulk-delete mód
        aimedMode = true;
        aimedSelections = [ window.selectedTrainingId ];

        // zvýrazníme už první nakliknutý trénink
        highlightAimedEvent(window.selectedTrainingId);

        updateAimedUI();

        // 4) otevřeme nový panel
        aimedModal.style.display = "block";
    });


    // === Komplexní funkcionalita pro mazání vybraných tréninků (aimed) ===
    function cancelAimed() {
        aimedSelectionActive = false;
        // odznačit lekce
        calendar.getEvents().forEach(ev => {
            if (ev._def.ui.classNames.includes("aimed-selected")) {
                ev._def.ui.classNames = ev._def.ui.classNames.filter(c=>c!=="aimed-selected");
                ev.remove();
                calendar.addEvent(ev);
            }
        });
        document.getElementById("aimedTrainingsModal").style.display = "none";
        selectedAimed.clear();
    }

    // Potvrdit aimed mazání
    aimedConfirmBtn.addEventListener("click", async () => {
        // 1) Sestavíme správný tvar DTO
        const payload = {
            trainingDTOs: aimedSelections.map(id => ({ trainingId: id }))
        };

        // 2) Pošleme na správnou URL
        const resp = await fetch(`${apiBase}/deleteAimedTrainingsChosenInOverview`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ trainingIds: aimedSelections  })
        });

        if (resp.ok) {
            alert("Vybrané tréninky smazány");
            closeAimed();
            calendar.refetchEvents();
        } else {
            alert("Chyba při mazání: " + resp.status);
        }
    });

    function updateAimedUI(){
        aimedCountEl.textContent = aimedSelections.length;
        aimedListEl.innerHTML = aimedSelections
            .map(id => `<li>${id}</li>`)
            .join("");
        aimedConfirmBtn.disabled = aimedSelections.length === 0;
    }

    calendar.render()


    // === Vytvoření profilu nového admina - reakce na btn ===
    document.getElementById("registrationAdminBtn").addEventListener("click", () => {

        document.getElementById("registrationAdminModal").style.display = "block";
    });



    // === Logout metoda ===
    document.getElementById('logoutBtn').addEventListener('click', () => {
        fetch('/api/logoutAdmin', { method: 'POST' })
            .then(resp => {
                if (resp.ok)
                    window.location.href = '/index.html';
                else alert('Odhlášení se nezdařilo');
            })
            .catch(err => console.error('Logout error:', err));
    });

});

