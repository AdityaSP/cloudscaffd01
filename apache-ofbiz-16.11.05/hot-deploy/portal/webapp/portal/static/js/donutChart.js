$(function () {
    (function apcDetailInCount() {
        $.ajax({
            url: "getAPCDetailsInCount",
            type: "POST",
            success: function (res) {
                renderPatternChartData(res.data);
                renderChartSolutionDesignData(res.data);
            },
            error: function (res) {
                console.log(res);
            }
        });
    })();
})
function renderPatternChartData(data) {
    var ctx = document.getElementById("dashboardPatternChart").getContext('2d');
    if (data.underDevelopmentPatternCount == 0 && data.underDevelopmentSolutionCount == 0 && data.approvedBasePatternCount == 0
        && data.createdBasePatternCount == 0 && data.createdSolutionDesignCount == 0 && data.approvedSolutionDesignCount == 0) {

        $('#patternCountChart').hide();

    } else {

        var myChart = new Chart(ctx, {
            type: 'doughnut',
            data: {

                datasets: [{
                    data: [data.approvedBasePatternCount, data.underDevelopmentPatternCount, data.createdBasePatternCount],
                    borderColor: ['#8900ff', '#6f00ff', '#0092ff'],
                    backgroundColor: ['#8900ff', '#6f00ff', '#0092ff'],
                    borderWidth: 1
                }],
                labels: ["Available Patterns", "Under Development Pattern", "Created Patterns"]
            },
            options: {
                onClick: function (e) {
                    var activePoints = myChart.getElementsAtEvent(e);
                    var selectedIndex = activePoints[0]._index;
                    let status = activePoints[0]._view.label, count = this.data.datasets[0].data[selectedIndex],
                        type = activePoints[0]._chart.titleBlock.options.text;


                    let queryStr = `type=${type}&status=${status}`;
                    //        console.log(queryStr);
                    window.location.href = `productAPC?${window.btoa(queryStr)}`;
                },
                title: {
                    position: 'top',
                    display: true,
                    text: "Pattern Details",
                },
                responsive: true,
                maintainAspectRatio: false,
                legend: {
                    position: 'right',
                    align: 'center',
                    fullWidth: true,
                    labels: {
                        fontColor: 'black',
                        boxWidth: 10
                    }
                }
            }
        });
    }
}

function renderChartSolutionDesignData(data) {
    var ctx = document.getElementById("dashboardSolutionChart").getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'doughnut',
        data: {

            datasets: [{
                data: [data.approvedSolutionDesignCount, data.underDevelopmentSolutionCount, data.createdSolutionDesignCount],
                borderColor: ['#6f00ff', '#0092ff', '#00c5ff'],
                backgroundColor: ['#6f00ff', '#0092ff', '#00c5ff'],
                borderWidth: 1
            }],
            labels: ["Available Solution Designs", "Under Development Solution Design", "Created Solution Designs"]
        },
        options: {

            onClick: function (e) {
                var activePoints = myChart.getElementsAtEvent(e);
                var selectedIndex = activePoints[0]._index;
                let status = activePoints[0]._view.label, count = this.data.datasets[0].data[selectedIndex],
                    type = activePoints[0]._chart.titleBlock.options.text;


                let queryStr = `type=${type}&status=${status}`;
                window.location.href = `productAPC?${window.btoa(queryStr)}`;
            },

            title: {
                position: 'top',

                display: true,
                text: "Solution Design Details",
            },
            responsive: true,
            maintainAspectRatio: false,
            legend: {
                position: 'right',
                align: 'center',
                fullWidth: true,
                labels: {
                    fontColor: 'black',
                    boxWidth: 10
                }
            },

        }
    });
}