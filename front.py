async function getCropRecommendations() {
  // Get values from your form
  const state = document.getElementById('state').value;
  const district = document.getElementById('district').value;
  const season = document.getElementById('season').value;
  const area = document.getElementById('area').value;

  try {
    const response = await fetch('http://localhost:8080/recommend', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        state: state,
        district: district,
        season: season,
        area: parseFloat(area),
        top_n: 5
      }),
    });

    const data = await response.json();
    
    if (data.status === 'success') {
      // Display recommendations
      displayRecommendations(data.recommendations);
    } else {
      // Show error message
      alert(`Error: ${data.message}`);
    }
  } catch (error) {
    console.error('Error:', error);
    alert('An error occurred while fetching recommendations');
  }
}

function displayRecommendations(recommendations) {
  const resultsDiv = document.getElementById('results');
  resultsDiv.innerHTML = '<h2>Recommended Crops</h2>';
  
  const table = document.createElement('table');
  table.innerHTML = `
    <tr>
      <th>Crop</th>
      <th>Predicted Yield (tonnes/hectare)</th>
    </tr>
  `;
  
  recommendations.forEach(rec => {
    const row = document.createElement('tr');
    row.innerHTML = `
      <td>${rec.crop}</td>
      <td>${rec.predicted_yield}</td>
    `;
    table.appendChild(row);
  });
  
  resultsDiv.appendChild(table);
}

// Populate dropdown menus when the page loads
async function populateDropdowns() {
  // Fetch states
  const statesResponse = await fetch('http://localhost:8080/states');
  const statesData = await statesResponse.json();
  
  const stateSelect = document.getElementById('state');
  statesData.states.forEach(state => {
    const option = document.createElement('option');
    option.value = state;
    option.textContent = state;
    stateSelect.appendChild(option);
  });
  
  // Handle state change to populate districts
  stateSelect.addEventListener('change', async () => {
    const districtsResponse = await fetch(`http://localhost:8080/districts/${stateSelect.value}`);
    const districtsData = await districtsResponse.json();
    
    const districtSelect = document.getElementById('district');
    districtSelect.innerHTML = '<option value="">Select District</option>';
    
    districtsData.districts.forEach(district => {
      const option = document.createElement('option');
      option.value = district;
      option.textContent = district;
      districtSelect.appendChild(option);
    });
  });
  
  // Fetch seasons
  const seasonsResponse = await fetch('http://localhost:8080/seasons');
  const seasonsData = await seasonsResponse.json();
  
  const seasonSelect = document.getElementById('season');
  seasonsData.seasons.forEach(season => {
    const option = document.createElement('option');
    option.value = season;
    option.textContent = season;
    seasonSelect.appendChild(option);
  });
}

// When the page loads
window.onload = populateDropdowns;