function initMap() {
    const map = new google.maps.Map(document.getElementById("map"), {
        zoom: 12,
        center: { lat: 0, lng: 0 }, // Set initial center
    });

    // Fetch sensor data from the backend
    fetch('http://localhost:8080/api/v1/data')
        .then(response => response.json())
        .then(data => {
            data.forEach(sensor => {
                addMarker(map, { lat: sensor.latitude, lng: sensor.longitude },
                    `Temperature: ${sensor.temperature}, Gas Voltage: ${sensor.gasVoltage}`);
            });
        })
        .catch(error => console.error('Error fetching data:', error));
}
