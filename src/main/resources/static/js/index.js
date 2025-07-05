
document.addEventListener("DOMContentLoaded", function () {

    const apiBase = '/api';

    //responzivní design
    function getResponsiveView() {
        const width = window.innerWidth;
        // if (width < 600) return 'timeGridDay';          // mobil - ponecháno pro případ potřeby
        if (width < 1024) return 'listWeek';      // tablet / menší notebook / mobil
        return 'dayGridMonth';                        // velká obrazovka
    }

    // kalendář
    const calendarEl = document.getElementById("calendar");
    const calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: getResponsiveView(),
        locale: "cs",
        firstDay: 1,       // pondělí (0 = neděle, 1 = pondělí, …)
        headerToolbar: {
            left: "prev,next today",
            center: "title",
            right: "dayGridMonth,listWeek"
        },

        moreLinkText: function(n) {     //mnoho lekcí k zobrazení v kalendáři - český přepis oznámení
            return `+${n} další`;
        },
        noEventsText: 'Žádné lekce',        //žádné lekce - pro zobrazení listWeek; český přepis
        navLinks: true, // can click day/week names to navigate views
        dayMaxEvents: true, // allow "more" link when too many events

        // formát času u eventů při měsíčním zobrazení
        eventTimeFormat: {
            hour:   'numeric',   // "0", "1", … "23"
            minute: '2-digit',   // "00", "05", "30", …
            hour12: false        // 24h formát
        },

        // Výchozí nastavení času (scroll)
        scrollTime: "16:00:00",

        allDayText: "celý den",

        buttonText: {
            today: 'dnes',
            month: 'měsíc',
            // week:  'týden',
            // day:   'den',
            list: 'týden'
        },

        // height: 'auto',

        events: {
            url: "/api/loadAllTrainings",
            method: "GET",
            failure: () => alert("Chyba při načítání lekcí")
        },

        eventDidMount: function (info) {
            const props = info.event.extendedProps;
            // pokud je plno, přidáme červený rámeček
            if (props.numberOfReservations >= props.numberOfFreeSlots) {
                info.el.classList.add("full");
            }
        },

        eventClick: function (info) {

            document.querySelectorAll('.fc-popover').forEach(node => {
                node.remove();
            });

            const props = info.event.extendedProps;
            window.selectedTrainingId = props.trainingId;

            // naplníme modal
            document.getElementById("modal-title").textContent = info.event.title;
            document.getElementById("modal-coach").textContent = props.coachName;
            document.getElementById("modal-capacity").textContent = props.numberOfFreeSlots;
            document.getElementById("modal-reservations").textContent = props.numberOfReservations;

            // kontrola kapacity
            const createBtn = document.getElementById("createReservationBtn");
            const msg = document.getElementById("modal-message");
            if (props.numberOfReservations >= props.numberOfFreeSlots) {
                createBtn.classList.add("disabled");
                msg.style.display = "block";
            } else {
                createBtn.classList.remove("disabled");
                msg.style.display = "none";
            }

            document.getElementById("eventModal").style.display = "block";
        }
    });


    // // načtení respozivity
    // window.addEventListener('resize', () => {
    //     calendar.changeView(getResponsiveView());
    //
    //     calendar.render();
    // });

    // Zavírání detail-modalu
    document.querySelector(".close-button")
        .addEventListener("click", () => document.getElementById("eventModal").style.display = "none");

    // Otevření reservation‐modalu
    document.getElementById("createReservationBtn")
        .addEventListener("click", () => {
            document.getElementById("reservationTrainingId").value = window.selectedTrainingId;
            document.getElementById("reservationModal").style.display = "block";
        });

    // Zavírání reservation‐modalu
    document.querySelector(".close-reservation-modal")
        .addEventListener("click", () => document.getElementById("reservationModal").style.display = "none");

    // Odeslání formuláře rezervace
    document.getElementById("reservationForm")
        .addEventListener("submit", async function (e) {
            e.preventDefault();

            const capacity = parseInt(document.getElementById("modal-capacity").textContent);
            const booked   = parseInt(document.getElementById("modal-reservations").textContent);
            const requested = parseInt(document.getElementById("numberOfPeople").value);
            const available = capacity - booked;

            if (requested > available) {
                alert(`Maximálně lze rezervovat ${available} míst.`);
                return;
            }

            const data = {
                firstName: document.getElementById("firstName").value,
                secondName: document.getElementById("secondName").value,
                userEmail: document.getElementById("userEmail").value,
                telephoneNumber: document.getElementById("phone").value,
                trainingId: parseInt(document.getElementById("reservationTrainingId").value),
                numberOfBookedEntries: requested
            };

            const resp = await fetch("/api/createNewReservation", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(data)
            });

            if (resp.ok) {
                alert("Rezervace úspěšně vytvořena.");
                document.getElementById("reservationModal").style.display = "none";
                document.getElementById("eventModal").style.display = "none";
                calendar.refetchEvents();
            } else {
                alert("Chyba při vytváření rezervace.");
            }
        });

    calendar.render();

});

document.querySelector('.login-toggle').addEventListener('click', () => {
    document.getElementById('loginDropdown')
        .classList.toggle('open');
});

//login dropdown
// pokud chcete dropdown zavřít při kliknutí venku:
document.addEventListener('click', (e) => {
    const dropdown = document.getElementById('loginDropdown');
    const toggle   = document.querySelector('.login-toggle');
    if (!dropdown.contains(e.target) && !toggle.contains(e.target)) {
        dropdown.classList.remove('open');
    }
});

async function login() {
    const email = document.getElementById("emailAdmin").value;
    const password = document.getElementById("password").value;

    const res = await fetch('/api/loginAdmin', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            adminEmail: email,
            password: password
        })
    });

    if (res.ok) {
        window.location.href = '/index-adminPart.html';
    } else {
        alert('Přihlášení selhalo');
    }
}
