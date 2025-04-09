async function login() {
    const email = document.getElementById("email").value;
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

//nová rezervace
document.getElementById("reservationForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const data = {
        name: document.getElementById("name").value,
        email: document.getElementById("emailReservation").value,
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