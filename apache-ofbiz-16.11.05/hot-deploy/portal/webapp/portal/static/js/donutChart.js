$(function(){
     (function apcDetailInCount(){
     $.ajax({
      url:"getAPCDetailsInCount",
      type:"POST",
      success : function(res){
        renderPatternChartData(res.data);
        renderChartSolutionDesignData(res.data);
      },
      error:function(res){
        console.log(res);
      }});
     })();
})
function renderPatternChartData(data){
var ctx = document.getElementById("dashboardPatternChart").getContext('2d');
var myChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
        datasets: [{
            data: [data.approvedBasePatternCount, data.underDevelopmentPatternCount, data.createdBasePatternCount], // Specify the data values array
            borderColor: ['#8900ff', '#6f00ff','#0092ff'], // Add custom color border
            backgroundColor: ['#8900ff', '#6f00ff','#0092ff'], // Add custom color background (Points and Fill)
            borderWidth: 1 // Specify bar border width
        }],
            labels: ["Available Patterns", "Under Development Pattern", "Created Patterns"]
        },
    options: {
      responsive: true, // Instruct chart js to respond nicely.
      maintainAspectRatio: false, // Add to prevent default behaviour of full-width/height
           legend: {
            position: 'right',
            align:'center',
            fullWidth: true,
                      labels: {
                          // This more specific font property overrides the global property
                          fontColor: 'black',
                          boxWidth:10
                      }
                  }
    }
});
}


function renderChartSolutionDesignData(data){
var ctx = document.getElementById("dashboardSolutionChart").getContext('2d');
var myChart = new Chart(ctx, {
    type: 'doughnut',

    data: {

        datasets: [{
            data: [data.createdSolutionDesignCount,  data.underDevelopmentSolutionCount, data.approvedSolutionDesignCount], // Specify the data values array
            borderColor: ['#6f00ff', '#0092ff','#00c5ff'], // Add custom color border
            backgroundColor: ['#6f00ff', '#0092ff','#00c5ff'], // Add custom color background (Points and Fill)
            borderWidth: 1 // Specify bar border width
        }],
          labels: ["Available Solution Designs", "Under Development Solution Design", "Created Solution Designs"]
        },
    options: {
      responsive: true, // Instruct chart js to respond nicely.
      maintainAspectRatio: false, // Add to prevent default behaviour of full-width/height
           legend: {
            position: 'right',
            align:'center',
            fullWidth: true,
                      labels: {
                          // This more specific font property overrides the global property
                          fontColor: 'black',
                          boxWidth:10
                      }
                  }
    }
});
}