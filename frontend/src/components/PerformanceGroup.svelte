<script>
  export let performanceGroup = {
    city: '',
    problemNumber: '',
    ageGroup: '',
    stage: '',
    part: '',
    league: '',
    performances: [{
      name: "Poznań",
      time: "1:00:00",
      place: "Poznań",
      date: "2020-01-01",
      distance: "42.195",
      url: "https://www.poznanmarathon.pl/"}]
  };
</script>

<div class="performance-group">
  <header>
    <h2>{performanceGroup.city}</h2>
    <p>Problem Number: {performanceGroup.problemNumber}</p>
    <p>Age Group: {performanceGroup.ageGroup}</p>
    <p>Stage: {performanceGroup.stage}</p>
    <p>Part: {performanceGroup.part}</p>
    <p>League: {performanceGroup.league}</p>
  </header>

  <table>
    <thead>
    <tr>
      <th>Team Name</th>
      <th>Performance Hour</th>
      <th>Spontan Hour</th>
      <th>Performance Day</th>
      <th>Spontan Day</th>
    </tr>
    </thead>
    <tbody>
    <!--{#each performanceGroup.performances as performance}-->
    <!--  <tr>-->
    <!--    <td>{performance.team}</td>-->
    <!--    <td>{performance.performance}</td>-->
    <!--    <td>{performance.spontan}</td>-->
    <!--    <td>{performance.performanceDay}</td>-->
    <!--    <td>{performance.spontanDay}</td>-->
    <!--  </tr>-->
    <!--{/each}-->
    </tbody>
  </table>
</div>

<style>
  .performance-group {
    background-color: #292B2D;
    border-radius: 8px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    padding: 1rem;
    margin-bottom: 1rem;
  }

  header {
    background-color: #FF8800;
    border-radius: 4px;
    padding: 0.5rem 1rem;
    margin-bottom: 1rem;
  }

  h2 {
    color: #883600;
    margin-bottom: 0.5rem;
  }

  p {
    color: #883600;
    margin: 0;
  }

  table {
    width: 100%;
    border-collapse: collapse;
  }

  th, td {
    border: 1px solid #FFB662;
    padding: 0.5rem;
    text-align: left;
  }

  th {
    background-color: #C86B00;
    color: #292B2D;
  }

  tbody tr:nth-child(odd) {
    background-color: #3C3E40;
  }

  tbody tr:nth-child(even) {
    background-color: #292B2D;
  }

  tbody tr:hover {
    background-color: #883600;
    color: #FCC573;
  }
</style>
