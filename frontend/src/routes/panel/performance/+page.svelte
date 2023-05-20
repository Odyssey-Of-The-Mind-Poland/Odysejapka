<script>
  import PerformanceGroup from "../../../components/PerformanceGroup.svelte";

  let group = {
    problemNumber: "poznan",
    performances: [{
      name: "Poznań",
      time: "1:00:00",
      place: "Poznań",
      date: "2020-01-01",
      distance: "42.195",
      url: "https://www.poznanmarathon.pl/"}]
  }
</script>

<PerformanceGroup performanceGroup={{group}}/>